/*
 * Copyright (C) 2013 Alex Chavez <alex@alexchavez.net>, Jairo Lopez <jlopez@csulbmaes.org>,
 * Steven Paz <steve.a.paz@gmail.com>, Gustavo Rosas <gustavoscrib@yahoo.com>
 * 
 * RecipeMix ALL RIGHTS RESERVED
 * 
 * This program is distributed to users in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package recipemix.beans;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.*;
import recipemix.models.*;

/**
 * *Enterprise JavaBean belonging to group flag entity
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Stateless
public class GroupFlagEJB {
    
    @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;

    public List<GroupFlag> findGroupFlags() {
        TypedQuery<GroupFlag> query = em.createNamedQuery("findAllGroupFlags", GroupFlag.class);
        return query.getResultList();
    }

    public GroupFlag findByUserAndGroup(Users user, Groups group) {
        Integer groupID = group.getGroupID();
        String userName = user.getUserName();
        TypedQuery<GroupFlag> query =
                em.createQuery("SELECT flag "
                + "FROM GroupFlag flag JOIN flag.flagger flagger JOIN flag.flaggedGroup flaggedGroup"
                + " WHERE flagger.userName = :userName AND flaggedGroup.groupID = :groupID", GroupFlag.class);
        query.setParameter("userName", userName);
        query.setParameter("groupID", groupID);
        GroupFlag flag;
        try {
            flag = query.getSingleResult();
        } catch (NoResultException nre) {
            flag = null;
        }
        return flag;
    }
    
    

    public GroupFlag createGroupFlag(GroupFlag flag) {
        try{
            em.persist(flag);
            em.flush();
        }catch(EntityExistsException e){
            // do nothing...
        }
        return flag;
    }
}
