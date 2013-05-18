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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import recipemix.models.Ingredient;

/**
 **Enterprise JavaBean belonging to ingredient entity
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Stateless
public class IngredientEJB {
    @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;
    
    public Ingredient createIngredient(Ingredient i){
        em.persist(i);
        return i;
    }
    
    public Ingredient createIngredient(String ingredientName){
        TypedQuery<Ingredient> query = 
                em.createNamedQuery("findIngredientByName", Ingredient.class)
                .setParameter("searchName", ingredientName);
        Ingredient i;
        try{
            i = query.getSingleResult();
            i.setCount(i.getCount()+1);
        }catch(NoResultException nre){
            i = new Ingredient();
            i.setIngredientName(ingredientName);
            i.setCount(1);
            em.persist(i);
        }
        return i;
    }
    
}
