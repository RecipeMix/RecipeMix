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

import recipemix.models.Comment;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *Enterprise JavaBean belonging to comments entity
 * 
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Stateless
public class CommentEJB {
    @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;

    
  public List<Comment> findComments() {
        TypedQuery<Comment> query = em.createNamedQuery("findAllComments", Comment.class);
        return query.getResultList();
    }
    public Comment createComment (Comment comment){
        em.persist(comment);
        return comment;
    }
    
    public Comment editComment(Comment comment) {
        em.merge(comment);
        return comment;
    }
    
    public void removeComment (Comment comment){
        em.remove(em.merge(comment));
    }
    
    public Comment findComment (Integer id){
        return em.find(Comment.class,id);
    }

  
    
}
