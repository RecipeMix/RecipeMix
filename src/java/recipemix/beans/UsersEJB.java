/*
 *  Copyright (C) 2013 Alex Chavez <alex@alexchavez.net>, Jairo Lopez <jlopez@csulbmaes.org>,
 *  Steven Paz <steve.a.paz@gmail.com>, Gustavo Rosas <gustavoscrib@yahoo.com>
 *
 *  RecipeMix ALL RIGHTS RESERVED
 *
 *  This program is distributed to users in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package recipemix.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import recipemix.controllers.exceptions.UserExistsException;
import recipemix.models.GroupRole;
import recipemix.models.GroupsMembers;
import recipemix.models.Image;
import recipemix.models.ProfessionalInfo;
import recipemix.models.Recipe;
import recipemix.models.RecipeRating;
import recipemix.models.SecurityGroup;
import recipemix.models.Users;

/**
 **Enterprise JavaBean belonging to users entity
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Stateless
@DeclareRoles("admin")
public class UsersEJB {

    @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;
    @EJB
    private ImageEJB imageEJB;

    /**
     * returns the list of users
     * @return 
     */
    public List<Users> findUsers() {
        Query query = em.createQuery("SELECT u FROM Users u");
        return query.getResultList();
    }

    /**
     * returns the users with the give email
     * @param email
     * @return 
     */
    public Users findUserByEmail(String email) {
        TypedQuery query = em.createQuery("SELECT u FROM Users u WHERE u.email=:email", Users.class);
        query.setParameter("email", email);
        try {
            return (Users) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * creates a user and returns the created user entity
     * @param member
     * @return 
     */
    public Users createUser(Users member) {
        em.persist(member);
        em.flush();
        return member;
    }

    /**
     * edits the user given with the new fields
     * @param member
     * @return the same user entity updated
     */
    public Users editUser(Users member) {
        em.merge(member);
        em.flush();
        return member;
    }

    /**
     * removes a user and it's fields from the database
     * @param member 
     */
    public void removeUser(Users member) {
        // First remove the association table entries
        for (Iterator<GroupsMembers> it = member.getUserGroups().iterator(); it.hasNext();) {
            GroupsMembers gm = it.next();
            if (gm.getRole() == GroupRole.OWNER) {
                // Assign new owner to group
                GroupsMembers newOwner;
                try {
                    TypedQuery q = em.createQuery("SELECT gm FROM GroupsMembers gm WHERE gm.group = :group AND gm.role = :role", 
                            GroupsMembers.class);
                    q.setParameter("group", gm.getGroup());
                    q.setParameter("role", GroupRole.MODERATOR);
                    newOwner = (GroupsMembers) q.getSingleResult();
                    newOwner.setRole(GroupRole.OWNER);
                    gm.getGroup().setOwner(newOwner.getMember());
                }catch (NoResultException nre){
                    gm.getGroup().setOwner(null);
                }
            }
            // Remove member entry from the group side
            gm.getGroup().getMembers().remove(gm);
            it.remove();
            em.remove(em.merge(gm));
        }

        for (Recipe r : member.getRecipes()) {
            r.setCreator(null);
            em.merge(r);
        }
        for (Iterator<RecipeRating> it = member.getRatings().iterator(); it.hasNext();) {
            RecipeRating rr = it.next(); // get the rating entity
            rr.getRecipe().getRatings().remove(rr); // remove rating from the recipe
            it.remove(); // remove from user
            em.merge(rr);
            em.remove(em.merge(rr));
        }

        em.remove(em.merge(member));
        em.flush();
    }

    /**
     * registers users so they are able to log in
     * @param user
     * @param defaultGroupName
     * @throws UserExistsException
     * @throws PersistenceException 
     */
    public void registerUser(Users user, String defaultGroupName) throws UserExistsException, PersistenceException {
        // 1: Use security EJB to add username/password to security
        // 2: if successful, then add as a registered user
        
        TypedQuery q = em.createQuery("SELECT u FROM Users u WHERE LOWER(u.userName) = :name", Users.class);
        q.setParameter("name", user.getUserName().toLowerCase());
        List<Users> checkUserName = q.getResultList();
        Users checkEmail = this.findUserByEmail(user.getEmail());

        if (checkUserName.isEmpty() && checkEmail == null) {
            Image i = new Image();
            i.setImageOwner(user);
            i.setCaption("Default profile picture");
            i.setImagePath("/resources/images/profile_placeholder.jpeg");
            i = imageEJB.createImage(i);
            user.setImage(i);
            SecurityGroup group = em.find(SecurityGroup.class, defaultGroupName);
            if (group == null) {
                group = new SecurityGroup(defaultGroupName); // TODO: create group if it doesn't exist?
            }
            user.addsecurityGroup(group);
            group.addUser(user);
            em.persist(user);
            em.flush();
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "User exists: {0}", user.getUserName());
            throw new UserExistsException();
        }
    }

    public Users findUser(String username) {
        return em.find(Users.class, username);
    }

    /**
     * returns the list of users who have similar usernames as the one given
     * @param userName
     * @return 
     */
    public List<Users> getUsersByLikeName(String userName) {
        String userLikeName = "%" + userName + "%";
        TypedQuery<Users> query = em.createNamedQuery("findUsersByLikeName", Users.class).setParameter("userLikeName", userLikeName);
        List<Users> result = query.getResultList();
        return result;
    }

    /**
     * creates the information of a professional
     * @param prof
     * @return 
     */
    public ProfessionalInfo createProfessionalInfo(ProfessionalInfo prof) {
        try {
            em.persist(prof);
        } catch (EntityExistsException e) {
            return null;
        }
        return prof;
    }

    /**
     * This method can be used to get any users whose flag count is greater than
     * or equal to the specified count
     *
     * @param count
     * @return the list of flagged users to inspect
     */
    public List<Users> getUsersByFlagCount(Integer count) {
        // returns a List of object arrays
        // each array contains the userName and its flag count

        List<Object[]> results = em.createQuery(
                "SELECT u, COUNT(f)"
                + " FROM Users u LEFT JOIN u.flags f "
                + "GROUP BY u").getResultList();

        List<Users> users = new ArrayList<Users>();
        if (results != null) {
            for (Object[] o : results) {
                if ((Long) o[1] >= count) {
                    users.add((Users) o[0]);
                }
            }
        }
        return users;
    }

    @RolesAllowed("admin")
    public void makeSiteMod(Users u) {
        SecurityGroup modGroup = em.find(SecurityGroup.class, "siteModerator");
        SecurityGroup memberGroup = em.find(SecurityGroup.class, "user");
        u.getSecurityGroups().remove(memberGroup);
        u.getSecurityGroups().add(modGroup);
        modGroup.getUsers().add(u);
        memberGroup.getUsers().remove(u);
        em.merge(u);
    }

    public SecurityGroup findSecurityGroup(String groupName) {
        return em.find(SecurityGroup.class, groupName);
    }
}
