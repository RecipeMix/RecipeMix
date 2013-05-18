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
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import recipemix.models.Comment;
import recipemix.models.DateComparator;
import recipemix.models.GroupRole;
import recipemix.models.Groups;
import recipemix.models.GroupsMembers;
import recipemix.models.Users;

/**
 **Enterprise JavaBean belonging to groups entity
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Stateless
public class GroupEJB {

    @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;

    /**
     * Returns all groups - should only be used in development TODO: Remove
     * before final presentation
     *
     * @return - the list of all groups
     */
    public List<Groups> findGroups() {
        TypedQuery<Groups> query = em.createNamedQuery("findAllGroups", Groups.class);
        return query.getResultList();
    }

    /**
     * Returns all groups the given user has created
     *
     * @param user
     * @return - the list of groups the user created
     */
    public List<Groups> getGroupsCreated(Users user) {
        TypedQuery<Groups> query = em.createNamedQuery(Groups.FIND_GROUPS_CREATED, Groups.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    /**
     * Returns the 15 most recent recipes since the cutoff date
     *
     * @param cutoff Date
     * @return List of Groups
     */
    public List<Groups> getGroupsSince(Date cutoff, int start, int end) {
        TypedQuery<Groups> query = em.createNamedQuery("findGroupsSinceDate", Groups.class).setParameter("cutOffDate", cutoff);
        query.setMaxResults(end + 1 - start);
        query.setFirstResult(start);
        List<Groups> result = query.getResultList();
        Collections.sort(result, new DateComparator());
        return result;
    }

    /**
     * @param userName
     * @return - a list of groups the user is in
     */
    public List<Groups> getGroupMembership(Users user) {
        TypedQuery<Groups> query = em.createNamedQuery(Groups.FIND_GROUPS_MEMBERSHIP, Groups.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    /**
     * @param userName
     * @return - a list of groups the user is in
     */
    public List<Groups> getGroupMembership(Users user, int start, int end) {
        TypedQuery<Groups> query = em.createNamedQuery(Groups.FIND_GROUPS_MEMBERSHIP, Groups.class);
        query.setParameter("user", user);
        query.setMaxResults(end + 1 - start);
        query.setFirstResult(start);
        return query.getResultList();
    }

    /**
     *
     * @param userName
     * @return
     */
    public List<Groups> getGroupsManaged(Users user) {
        List<Groups> results = getGroupsCreated(user);
        TypedQuery<Groups> query = em.createNamedQuery(Groups.FIND_GROUPS_MANAGED, Groups.class);
        query.setParameter("user", user);
        query.setParameter("role", GroupRole.MODERATOR);
        List<Groups> managed = query.getResultList();

        for (Groups g : managed) {
            if (!results.contains(g)) {
                results.add(g);
            }
        }
        return results;
    }

    /**
     *
     * @param userName
     * @return
     */
    public List<Groups> getGroupsManaged(Users user, int start, int end) {
        List<Groups> results = getGroupsCreated(user);
        TypedQuery<Groups> query = em.createNamedQuery(Groups.FIND_GROUPS_MANAGED, Groups.class);
        query.setParameter("user", user);
        query.setParameter("role", GroupRole.MODERATOR);
        List<Groups> managed = query.getResultList();

        for (Groups g : managed) {
            if (!results.contains(g)) {
                results.add(g);
            }
        }

        if (results.size() < end) {
            return results;
        } else {
            return results.subList(start, end);
        }
    }

    /**
     * Finds the group entity given a group name Will return only the first
     * result
     *
     * @param groupName
     * @return - the group
     */
    public Groups findGroup(String groupName) {
        TypedQuery<Groups> query = em.createNamedQuery(Groups.FIND_GROUPS_BY_NAME, Groups.class)
                .setParameter("groupName", groupName);

        Groups g;
        try {
            g = query.getSingleResult();
        } catch (NoResultException nre) {
            g = null;
        }
        return g;
    }

    /**
     * This returns a list of groups with similar names to groupName
     *
     * @param groupName
     * @return list of like groups
     */
    public List<Groups> findLikeGroups(String groupName) {
        String groupNamePattern = "%" + groupName + "%";
        TypedQuery<Groups> query = em.createNamedQuery(Groups.FIND_GROUPS_LIKE_NAME, Groups.class)
                .setParameter("groupNamePattern", groupNamePattern);
        List<Groups> results = query.getResultList();

        return results;
    }

    /**
     * This finds group objects based on their associated tags
     *
     * @param tagName
     * @return list of groups
     */
    public List<Groups> findGroupsByTag(String tagName) {
        String tagLikeName = "%" + tagName + "%";
        TypedQuery<Groups> query = em.createNamedQuery(Groups.FIND_GROUPS_BY_TAG, Groups.class)
                .setParameter("tagLikeName", tagLikeName);
        List<Groups> results = query.getResultList();

        return results;
    }

    public Set<GroupsMembers> getGroupUsers(int groupID) {
        Groups g = findGroup(groupID);
        return g.getMembers();
    }

    public Groups createGroup(Groups group) {

        em.merge(group);
        return group;
    }

    public Groups editGroup(Groups group) {
        em.merge(group);
        return group;
    }

    public void confirmMember(GroupsMembers gm) {
        gm.setIsConfirmed(true);
        em.merge(gm);
        em.flush();
    }

    public void removeMembers(Groups g, Users u) {
        for (Iterator<GroupsMembers> it = g.getMembers().iterator(); it.hasNext();) {
            GroupsMembers gm = it.next();
            if (gm.getMember().getUserName().equals(u.getUserName())) {
                gm.getMember().getUserGroups().remove(gm); // remove from user side
                it.remove(); // remove from group side
                em.remove(em.merge(gm)); // remove join table entry
            }
        }
        em.merge(g);
        em.flush();
    }

    public void removeGroup(Groups group) {
        // First remove the association table entry
        for (Iterator<GroupsMembers> it = group.getMembers().iterator(); it.hasNext();) {
            GroupsMembers gm = it.next();
            gm.getMember().getUserGroups().remove(gm);
            it.remove();
            em.remove(em.merge(gm));
        }
        em.remove(em.merge(group));
    }

    public Groups findGroup(Integer id) {
        return em.find(Groups.class, id);
    }

    public void updateRole(Groups g, Users u, GroupRole r) {
        if (g != null && u != null) {
            for (GroupsMembers gm : g.getMembers()) {
                Users member = gm.getMember();
                if (member.getUserName().equals(u.getUserName())
                        && !checkRole(g, u, r)) {
                    gm.setRole(r);
                    em.merge(gm);
                }
            }
        }
        em.flush();
    }

    public boolean checkRole(Groups g, Users u, GroupRole r) {
        boolean positive = false;
        if (u != null && g != null) {
            Set<GroupsMembers> gm = g.getMembers();
            Iterator<GroupsMembers> i = gm.iterator();
            while (i.hasNext()) {
                GroupsMembers m = i.next();
                Users user = m.getMember();
                if (user.getUserName().equals(u.getUserName()) && m.getRole() == r) {
                    positive = true;
                }
            }
        }
        return positive;
    }

    /**
     * Will return the biggest Group ID in the database Especially useful for
     * randomly choosing a group ID
     *
     * @return - the highest group ID integer
     */
    public int getHighestGroupID() {
        int result = 0;
        Query q = em.createQuery("SELECT MAX(g.groupID) FROM Groups g");
        try {
            Object o = q.getSingleResult();
            if (o != null) {
                result = ((Integer) o).intValue();
            } else {
                result = 0;
            }
        } catch (NoResultException nre) {
            result = 0;
        }
        return result;
    }

    /**
     * Used to count the number of groups in the system
     *
     * @return - the total number of groups in the database
     */
    public int countTotalGroups() {
        Query q = em.createQuery("SELECT COUNT(g) FROM Groups g");
        int result = 0;
        try {
            result = ((Long) q.getSingleResult()).intValue();
        } catch (NoResultException nre) {
            result = 0;
        }
        return result;
    }
    
    /**
     * Used to count the number of groups in the system
     *
     * @return - the total number of groups in the database
     */
    public int countTotalGroupsSince(Date d) {
        Query q = em.createQuery("SELECT COUNT(g) FROM Groups g WHERE g.groupCreatedTimestamp > :cutoff");
        q.setParameter("cutoff", d);
        int result = 0;
        try {
            result = ((Long) q.getSingleResult()).intValue();
        } catch (NoResultException nre) {
            result = 0;
        }
        return result;
    }

    /**
     * Used to count the number of groups in the system
     *
     * @return - the total number of groups in the database
     */
    public int countPendingMembers(Groups g) {
        Query q = em.createNamedQuery(Groups.COUNT_PENDING_MEMBERS);
        q.setParameter("group", g);
        int result = 0;
        try {
            result = ((Long) q.getSingleResult()).intValue();
        } catch (NoResultException nre) {
            result = 0;
        }
        return result;
    }
    
    

    /**
     * Returns the number of records that will be used with lazy loading /
     * pagination
     *
     * @param namedQueryName
     * @param start
     * @param end
     * @return List
     */
    public List findWithNamedQuery(String namedQueryName, int start, int end) {
        TypedQuery query = this.em.createNamedQuery(namedQueryName, Groups.class);
        query.setMaxResults(end - start);
        query.setFirstResult(start);
        return query.getResultList();
    }

    /**
     * Returns the number of records that will be used with lazy loading /
     * pagination
     *
     * @param namedQueryName
     * @param start
     * @param end
     * @return List
     */
    public List findWithNamedQuery(String namedQueryName, Groups g, int start, int end) {
        TypedQuery query = this.em.createNamedQuery(namedQueryName, Groups.class);
        query.setParameter("group", g);
        query.setMaxResults(end - start);
        query.setFirstResult(start);
        return query.getResultList();
    }

    /**
     * Returns the number of records that will be used with lazy loading
     * pagination
     *
     * @param namedQueryName
     * @param start
     * @param end
     * @return List
     */
    public List<Comment> getComments(Groups group, int start, int end) {
        TypedQuery query = this.em.createNamedQuery(Groups.FIND_ALL_COMMENTS, Comment.class);
        query.setParameter("group", group);
        query.setMaxResults(end - start);
        query.setFirstResult(start);
        return query.getResultList();
    }

    /**
     * Used to count the number of comments for the group in the system
     *
     * @return - the total number of comments
     */
    public int countTotalComments(Groups group) {
        Query q = em.createQuery("SELECT COUNT(c) FROM Groups g JOIN g.comments c WHERE c.group = :group");
        q.setParameter("group", group);
        int result = 0;
        try {
            result = ((Long) q.getSingleResult()).intValue();
        } catch (NoResultException nre) {
            result = 0;
        }
        return result;
    }

    /* This method can be used to get any groups whose flag count is greater
     * than or equal to the specified count
     *
     * @param count
     * @return the list of flagged groups to inspect
     */
    public List<Groups> getGroupsByFlagCount(Integer count) {
        // returns a List of object arrays
        // each array contains the groupID and its flag count

        List<Object[]> results = em.createQuery(
                "SELECT g, COUNT(f)"
                + " FROM Groups g LEFT JOIN g.flags f "
                + "GROUP BY g").getResultList();

        List<Groups> groups = new ArrayList<Groups>();
        if (results != null) {
            for (Object[] o : results) {
                if ((Long) o[1] >= count) {
                    groups.add((Groups) o[0]);
                }
            }
        }
        return groups;
    }
}
