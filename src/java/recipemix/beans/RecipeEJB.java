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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import recipemix.models.Comment;
import recipemix.models.Image;
import recipemix.models.Recipe;

/**
 **Enterprise JavaBean belonging to recipe entity
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Stateless
public class RecipeEJB {

    @EJB
    ImageEJB imageEJB;
    @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;

    /**
     * Returns the 15 most recent recipes
     *
     * @return all the recipes in the database
     */
    public List<Recipe> findRecipes() {
        TypedQuery<Recipe> query = em.createNamedQuery("findAllRecipes", Recipe.class);
        List<Recipe> result = query.getResultList();
        Collections.sort(result, new DateComparator());
        return result;
    }

    /**
     * Returns the 15 most recent recipes since the cutoff date
     *
     * @param cutoff Date
     * @return List of Recipes
     */
    public List<Recipe> getRecipesSince(Date cutoff) {
        TypedQuery<Recipe> query = em.createNamedQuery("findRecipesSinceDate", Recipe.class).setParameter("cutOffDate", cutoff);
        List<Recipe> result = query.getResultList();
        Collections.sort(result, new DateComparator());
        if (result.size() > 15) {
            return result.subList(0, 15);
        } else {
            return result;
        }
    }

    /**
     * Returns the 15 most recent recipes since the cutoff date
     *
     * @param cutoff Date
     * @return List of Recipes
     */
    public List<Recipe> getRecipesSince(Date cutoff, int start, int end) {
        TypedQuery<Recipe> query = em.createQuery(
                "SELECT r FROM Recipe r WHERE r.recipeCreatedTimestamp > :cutOffDate ORDER BY r.recipeCreatedTimestamp", 
                Recipe.class).setParameter("cutOffDate", cutoff);
        List<Recipe> result = query.getResultList();
        query.setMaxResults(end + 1 - start);
        query.setFirstResult(start);
        result = query.getResultList();
        Collections.sort(result, new DateComparator());
        return query.getResultList();
    }

    /**
     * Returns the list of recipes for the given username
     *
     * @param userName String
     * @return the List of Recipes
     */
    public List<Recipe> getRecipes(String userName) {
        TypedQuery<Recipe> query = em.createNamedQuery("findRecipesByUsername", Recipe.class).setParameter("userName", userName);
        List<Recipe> result = query.getResultList();
        Collections.sort(result, new DateComparator());
        return result;
    }

    /**
     *
     * @param recipeName String
     * @return a list of recipes of the same name
     */
    public List<Recipe> getRecipesByName(String recipeName) {
        TypedQuery<Recipe> query = em.createNamedQuery("findRecipesByName", Recipe.class).setParameter("recipeName", recipeName);
        List<Recipe> result = query.getResultList();
        Collections.sort(result, new DateComparator());
        return result;
    }

    /**
     *
     * @param recipeName String
     * @return a list of recipes with names similar to recipeName
     */
    public List<Recipe> getRecipesByLikeName(String recipeName) {
        String recipeLikeName = "%" + recipeName + "%";
        TypedQuery<Recipe> query = em.createNamedQuery("findRecipesByLikeName", Recipe.class).setParameter("recipeLikeName", recipeLikeName);
        List<Recipe> result = query.getResultList();
        Collections.sort(result, new DateComparator());
        return result;
    }

    /**
     *
     * @param tagName
     * @return a list of recipes associated with tag
     */
    public List<Recipe> getRecipesByTag(String tagName) {
        String tagLikeName = "%" + tagName + "%";
        TypedQuery<Recipe> query = em.createNamedQuery("findRecipesByTag", Recipe.class).setParameter("tagLikeName", tagLikeName);
        List<Recipe> result = query.getResultList();
        Collections.sort(result, new DateComparator());
        return result;
    }

    /**
     * This method can be used to get any recipes whose flag count is greater
     * than or equal to the specified count
     *
     * @param count
     * @return the list of flagged recipes to inspect
     */
    public List<Recipe> getFlaggedRecipes(double threshold) {
        // returns a List of object arrays
        // each array contains the recipeID and its flags

        TypedQuery query = em.createQuery(
                "SELECT r FROM Recipe r WHERE r.flagRatio >= :threshold", Recipe.class);
        query.setParameter("threshold", threshold);
        return query.getResultList();

    }

    /**
     * Attempts to persist a Recipe
     *
     * @param recipe
     * @return the recipe if successful, null otherwise
     */
    public Recipe createRecipe(Recipe recipe) {
        try {
            Image i = new Image();
            i.setRecipe(recipe);
            i.setCaption("Default recipe picture");
            i.setImageName("Default Image Title");
            i.setImagePath("/resources/images/recipe_placeholder.png");
            i = imageEJB.createImage(i);
            List<Image> gallery = new ArrayList<Image>();
            gallery.add(i);
            recipe.setImageGallery(gallery);
            em.merge(recipe);
            em.flush();
        } catch (EntityExistsException eee) {
            System.out.println(eee.getMessage());
            recipe = null;
        }
        return recipe;
    }

    /**
     * Edits a recipe
     *
     * @param recipe
     * @return the recipe if successful
     */
    public Recipe editRecipe(Recipe recipe) {
        em.merge(recipe);
        return recipe;
    }

    /**
     *
     * @param recipe
     */
    public void removeRecipe(Recipe recipe) {
        em.remove(em.merge(recipe));
        em.flush();
    }

    /**
     *
     * @param id
     * @return
     */
    public Recipe findRecipe(Integer id) {
        return em.find(Recipe.class, id);
    }

    /**
     * Custom comparator used for sorting recipes
     */
    public class DateComparator implements Comparator<Recipe> {

        @Override
        public int compare(Recipe o1, Recipe o2) {
            return -1 * o1.getRecipeCreatedTimestamp().compareTo(o2.getRecipeCreatedTimestamp());
        }
    }

    /**
     * Will return the biggest Recipe ID in the database Especially useful for
     * randomly choosing a recipe ID
     *
     * @return - the highest recipe ID integer
     */
    public int getHighestRecipeID() {
        int result = 0;
        Query q = em.createQuery("SELECT MAX(r.recipeId) FROM Recipe r");
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
     * Used to count the number of recipes in the system
     *
     * @return - the total number of recipes in the database
     */
    public int countTotalRecipes(Date cutoff) {
        Query q = em.createQuery("SELECT COUNT(r) FROM Recipe r WHERE r.recipeCreatedTimestamp > :cutOffDate");
        q.setParameter("cutOffDate", cutoff);
        int result = 0;
        try {
            result = ((Long) q.getSingleResult()).intValue();
        } catch (NoResultException nre) {
            result = 0;
        }
        return result;
    }

    /**
     * Used to count the number of recipes in the system by tag
     *
     * @return - the total number of recipes in the database by tag
     */
    public int countTotalRecipesByTag(String tagName) {
        Query q = em.createQuery("SELECT COUNT(r) "
                + "FROM Recipe r JOIN r.tags t WHERE t.tagName = :tag");
        q.setParameter("tag", tagName);
        int result = 0;
        try {
            result = ((Long) q.getSingleResult()).intValue();
        } catch (NoResultException nre) {
            result = 0;
        }
        return result;
    }

    /**
     * Used to count the number of comments for the recipe in the system
     *
     * @return - the total number of comments
     */
    public int countTotalComments(Recipe recipe) {
        Query q = em.createQuery("SELECT COUNT(c) FROM Recipe r JOIN r.comments c WHERE c.recipe = :recipe");
        q.setParameter("recipe", recipe);
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
        TypedQuery query = this.em.createNamedQuery(namedQueryName, Recipe.class);
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
    public List<Comment> getComments(Recipe recipe, int start, int end) {
        TypedQuery query = this.em.createNamedQuery(Recipe.FIND_ALL_COMMENTS, Comment.class);
        query.setParameter("recipe", recipe);
        query.setMaxResults(end - start);
        query.setFirstResult(start);
        return query.getResultList();
    }

    /**
     * Returns a subset of the requested query
     *
     * @param tagName
     * @param start
     * @param end
     * @return
     */
    public List<Recipe> getRecipesByTag(String tagName, int start, int end) {
        String tagLikeName = "%" + tagName + "%";
        TypedQuery<Recipe> query = em.createNamedQuery("findRecipesByTag", Recipe.class).setParameter("tagLikeName", tagLikeName);
        query.setMaxResults(end - start);
        query.setFirstResult(start);
        List<Recipe> result = query.getResultList();
        Collections.sort(result, new DateComparator());
        return result;
    }

    public void incrementViews(Recipe r) {
        long count = r.getViews();
        if (count >= 0) {
            count++;
            r.setViews(count);
            Double flags = new Double(r.getFlags().size());
            double temp=flags/count;
            r.setFlagRatio(temp);
            em.merge(r);
            em.flush();
        }
    }
    
    public void removeCommentFromRecipe(Recipe recipe, Comment comment){
        if(recipe.getComments().contains(comment)){
            recipe.getComments().remove(comment);
            em.merge(recipe);
            em.flush();
        }
    }
}
