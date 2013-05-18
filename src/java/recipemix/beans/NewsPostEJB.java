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

import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import recipemix.models.NewsPost;

/**
 **Enterprise JavaBean belonging to news post entity
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Stateless
@DeclareRoles("siteModerator")
public class NewsPostEJB {
    @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;
    
     public List<NewsPost> findNewsPosts() {
        TypedQuery<NewsPost> query = em.createNamedQuery("findAllNewsPosts", NewsPost.class);
        List<NewsPost> results = query.getResultList();
        return query.getResultList();
    }
     
    @RolesAllowed("siteModerator")
    public NewsPost createNewsPost (NewsPost newsPost){
        em.persist(newsPost);
        return newsPost;
    }
    
    @RolesAllowed("siteModerator")
    public NewsPost editNewsPost(NewsPost newsPost) {
        em.merge(newsPost);
        return newsPost;
    }
    
    @RolesAllowed("siteModerator")
    public void removeNewsPost (NewsPost newsPost){
        em.remove(em.merge(newsPost));
    }
    
    public NewsPost findNewsPost (Integer id){
        return em.find(NewsPost.class,id);
    }

    

}

