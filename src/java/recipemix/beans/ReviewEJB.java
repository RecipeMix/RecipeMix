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

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import recipemix.models.Review;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import recipemix.models.Recipe;

/**
 **Enterprise JavaBean belonging to review entity
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@Stateless
@DeclareRoles("siteModerator")
public class ReviewEJB {

    @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;

    @RolesAllowed("professional")
    public Review createReview(Review review) {
        em.merge(review);
        em.flush();
        return review;
    }

    @RolesAllowed("professional")
    public Review editReview(Review review) {
        em.merge(review);
        em.flush();
        return review;
    }

    @RolesAllowed({"profesional", "admin"})
    public void removeReview(Review review) {
        em.remove(em.merge(review));
        em.flush();
    }

    public Review findReview(Integer id) {
        em.flush();
        return em.find(Review.class, id);
    }

    /**
     * Used to count the number of comments for the recipe in the system
     *
     * @return - the total number of comments
     */
    public int countTotalReviews(Recipe recipe) {
        Query q = em.createQuery("SELECT COUNT(rv) FROM Recipe r JOIN r.reviews rv WHERE rv.recipe = :recipe");
        q.setParameter("recipe", recipe);
        int result = 0;
        try {
            result = ((Long) q.getSingleResult()).intValue();
        } catch (NoResultException nre) {
            result = 0;
        }
        return result;
    }
}
