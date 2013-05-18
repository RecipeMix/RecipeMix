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
package recipemix.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import recipemix.beans.GroupEJB;
import recipemix.beans.RecipeEJB;
import recipemix.beans.UsersEJB;
import recipemix.models.Groups;
import recipemix.models.Recipe;
import recipemix.models.Tag;
import recipemix.models.Users;

/**
 *
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Named
@RequestScoped
public class Search implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("Search");
    private static final String whitespace_chars = "["
            + "" // dummy empty string for homogeneity
            + "\\u0009" // CHARACTER TABULATION
            + "\\u000A" // LINE FEED (LF)
            + "\\u000B" // LINE TABULATION
            + "\\u000C" // FORM FEED (FF)
            + "\\u000D" // CARRIAGE RETURN (CR)
            + "\\u0020" // SPACE
            + "\\u0085" // NEXT LINE (NEL) 
            + "\\u00A0" // NO-BREAK SPACE
            + "\\u1680" // OGHAM SPACE MARK
            + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
            + "\\u2000" // EN QUAD 
            + "\\u2001" // EM QUAD 
            + "\\u2002" // EN SPACE
            + "\\u2003" // EM SPACE
            + "\\u2004" // THREE-PER-EM SPACE
            + "\\u2005" // FOUR-PER-EM SPACE
            + "\\u2006" // SIX-PER-EM SPACE
            + "\\u2007" // FIGURE SPACE
            + "\\u2008" // PUNCTUATION SPACE
            + "\\u2009" // THIN SPACE
            + "\\u200A" // HAIR SPACE
            + "\\u2028" // LINE SEPARATOR
            + "\\u2029" // PARAGRAPH SEPARATOR
            + "\\u202F" // NARROW NO-BREAK SPACE
            + "\\u205F" // MEDIUM MATHEMATICAL SPACE
            + "\\u3000" // IDEOGRAPHIC SPACE
            + "]";
    private String searchValue;
    private String tempSearch;
    //=================================================
    //=                 EJBs                          =
    //=================================================
    @EJB
    private RecipeEJB recipeEJB;
    @EJB
    private UsersEJB usersEJB;
    @EJB
    private GroupEJB groupEJB;
    @Inject
    private RequestParameterManager qm;

    /**
     * Called at bean initialization
     *
     */
    @PostConstruct
    public void init() {
        String value = qm.get("searchArg");
        if (value != null) {
            searchValue = value;
            tempSearch = searchValue.replaceAll(whitespace_chars, "");
        } else {
            logger.log(Level.WARNING, "Search: init failed!");
        }
    }

    /**
     * Returns a List of Recipes given a search query parameter
     *
     * @param query - the Recipe to search with
     * @return - the List of recipes matching the query
     */
    public List<Recipe> getSearchRecipes(Recipe query) {
        this.searchValue = query.getRecipeName();
        List<Object> results = this.getResults("recipes");
        Set<Recipe> finalResults = new HashSet<Recipe>();
        for (Object o : results) {
            Recipe r = (Recipe) o;
            if (r.getRecipeId() != query.getRecipeId()) {
                finalResults.add((Recipe) o);
            }
        }
        //Search by tags
        for (Tag t : query.getTags()) {
            finalResults.addAll(recipeEJB.getRecipesByTag(t.getTagName()));
        }
        finalResults.remove(query);
        List<Object> setObjects = Arrays.asList(finalResults.toArray());
        List<Recipe> toReturn = new ArrayList<Recipe>();
        for(Object ob: setObjects){
            toReturn.add((Recipe)ob);
        }
        return toReturn;
    }

    /**
     * Method used to search for recipes, groups, and users
     *
     * @param searchFor - specifies what to search for
     * @return - the desired search results
     */
    private List<Object> getResults(String searchFor) {
        List<Object> results = null;

        // Search for Recipes
        if (searchFor.equals("recipes") && searchValue != null) {
            Set<Recipe> recipes = null;
            try {
                recipes = new HashSet<Recipe>(recipeEJB.getRecipesByLikeName(searchValue.toLowerCase()));
                recipes.addAll(recipeEJB.getRecipesByTag(tempSearch.toLowerCase()));
            } catch (Exception E) {
                System.out.println("No recipes matching search");
            }
            results = Arrays.asList(recipes.toArray());
        } // Search for Groups
        else if (searchFor.equals("groups") && searchValue != null) {
            Set<Groups> groups = null;
            try {
                groups = new HashSet<Groups>(groupEJB.findGroupsByTag(tempSearch.toLowerCase()));
                groups.addAll(groupEJB.findLikeGroups(searchValue.toLowerCase()));

            } catch (Exception E) {
                System.out.println("No groups matching search");
            }
            results = Arrays.asList(groups.toArray());
        } // Search for Users
        else if (searchFor.equals("users") && searchValue != null) {
            Set<Users> users = null;
            try {
                users = new HashSet<Users>(usersEJB.getUsersByLikeName(searchValue.toLowerCase()));
            } catch (Exception E) {
                System.out.println("No users matching search");
            }
            results = Arrays.asList(users.toArray());

        }
        return results;
    }

    /**
     * Returns the recipes matching the search query
     *
     * @return - the List of recipes
     */
    public List<Object> getSearchRecipes() {
        List<Object> results = getResults("recipes");
        return results;
    }

    /**
     * Returns the groups matching the search query
     *
     * @return - the List of groups
     */
    public List<Object> getSearchGroups() {
        List<Object> results = getResults("groups");
        return results;
    }

    /**
     * Returns the user that matches the search query
     *
     * @return - the User
     */
    public List<Object> getSearchUsers() {
        List<Object> results = getResults("users");
        return results;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }
    
    
}
