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
import javax.ejb.Stateless;
import javax.persistence.*;
import recipemix.models.*;

/**
 * *Enterprise JavaBean belonging to recipe flag entity
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 * @author Alex Chavez <alex@alexchavez.net>
 */
@Stateless
public class RecipeFlagEJB {
    
    @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;

    public List<RecipeFlag> findRecipeFlags() {
        TypedQuery<RecipeFlag> query = em.createNamedQuery("findAllRecipeFlags", RecipeFlag.class);
        return query.getResultList();
    }

    public RecipeFlag findByUserAndRecipe(Users user, Recipe recipe) {
        Integer recipeID = recipe.getRecipeId();
        String userName = user.getUserName();
        TypedQuery<RecipeFlag> query =
                em.createQuery("SELECT flag "
                + "FROM RecipeFlag flag JOIN flag.flagger flagger JOIN flag.flaggedRecipe recipe"
                + " WHERE flagger.userName = :userName AND recipe.recipeId = :recipeID", RecipeFlag.class);
        query.setParameter("userName", userName);
        query.setParameter("recipeID", recipeID);
        RecipeFlag flag;
        try {
            flag = query.getSingleResult();
        } catch (NoResultException nre) {
            flag = null;
        }
        return flag;
    }
    
    

    public RecipeFlag createRecipeFlag(RecipeFlag flag) {
        try{
            em.merge(flag);
            em.flush();
        }catch(EntityExistsException e){
            // do nothing...
        }
        return flag;
    }
}
