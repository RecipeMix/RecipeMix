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

import recipemix.models.RecipeRating;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import recipemix.models.Recipe;
import recipemix.models.Users;

/**
 **Enterprise JavaBean belonging to recipe rating entity
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Stateless
public class RecipeRatingEJB {

    @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;

    public List<RecipeRating> findRecipeRatings() {
        TypedQuery<RecipeRating> query = em.createNamedQuery("findAllRecipeRatings", RecipeRating.class);
        return query.getResultList();
    }

    public RecipeRating findByUserAndRecipe(Users user, Recipe recipe) {
        Integer recipeID = recipe.getRecipeId();
        String userName = user.getUserName();
        TypedQuery<RecipeRating> query =
                em.createQuery("SELECT rating "
                + "FROM RecipeRating rating JOIN rating.rater rater JOIN rating.recipe recipe"
                + " WHERE rater.userName = :userName AND recipe.recipeId = :recipeID", RecipeRating.class);
        query.setParameter("userName", userName);
        query.setParameter("recipeID", recipeID);
        RecipeRating rating;
        try {
            rating = query.getSingleResult();
        } catch (NoResultException nre) {
            rating = null;
        }
        return rating;
    }

    public RecipeRating createRecipeRating(RecipeRating rRating) {
        em.persist(rRating);
        em.flush();
        return rRating;
    }

    public RecipeRating editRecipeRating(RecipeRating rRating) {
        em.merge(rRating);
        em.flush();
        return rRating;
    }

    public void removeRecipeRating(RecipeRating rRating) {
        em.remove(em.merge(rRating));
        em.flush();
    }

    public RecipeRating findRecipeRating(Integer id) {
        return em.find(RecipeRating.class, id);
    }
    
    /**
     * Used to count the number of comments for the recipe in the system
     * @return - the total number of comments
     */
    public int countTotalRatings(Recipe recipe){
        Query q = em.createQuery("SELECT COUNT(rt) FROM Recipe r JOIN r.ratings rt WHERE rt.recipe = :recipe");
        q.setParameter("recipe", recipe);
        int result = 0;
        try{
            result = ((Long)q.getSingleResult()).intValue();
        } catch(NoResultException nre){
            result = 0;
        }
        return result;
    }
}
