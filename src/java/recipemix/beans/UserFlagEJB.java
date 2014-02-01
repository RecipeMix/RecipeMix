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
 * *Enterprise JavaBean belonging to user flag entity
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Stateless
public class UserFlagEJB {
    
    @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;

    public List<UserFlag> findUserFlags() {
        TypedQuery<UserFlag> query = em.createNamedQuery("findAllUserFlags", UserFlag.class);
        return query.getResultList();
    }

    public UserFlag findByUserAndOffender(Users user, Users offender) {
        String offenderUserName = offender.getUserName();
        String userName = user.getUserName();
        TypedQuery<UserFlag> query =
                em.createQuery("SELECT flag "
                + "FROM UserFlag flag JOIN flag.flagger flagger JOIN flag.flaggedUser user"
                + " WHERE flagger.userName = :userName AND user.userName = :offenderUserName", UserFlag.class);
        query.setParameter("userName", userName);
        query.setParameter("offenderUserName", offenderUserName);
        UserFlag flag;
        try {
            flag = query.getSingleResult();
        } catch (NoResultException nre) {
            flag = null;
        }
        return flag;
    }
    
    /**
     * creates and persists the flag to the db
     * @param flag
     * @return 
     */
    public UserFlag createUserFlag(UserFlag flag) {
        try{
            em.persist(flag);
            em.flush();
        }catch(EntityExistsException e){
            // do nothing...
        }
        return flag;
    }
}
