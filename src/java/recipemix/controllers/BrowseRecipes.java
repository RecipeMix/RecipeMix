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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import recipemix.beans.RecipeEJB;
import recipemix.models.Recipe;
import recipemix.models.Users;
import org.primefaces.json.*;

/**
 *
 * DEPRECATED: SEE PAGINATION BEAN
 * 
 * @author Steven Paz <steve.a.paz@gmail.com>
 * @author Alex Chavez <alex@alexchavez.net>
 */
@Named
@SessionScoped
public class BrowseRecipes implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("BrowseRecipes");
    //=================================================
    //=                 EJBs                          =
    //=================================================
    @EJB
    private RecipeEJB recipeEJB;
    @Inject
    private UserIdentity ui;
    @Inject
    private RequestParameterManager qm;
    private String browseBy;
    private String displayTitle;
    private List<Recipe> recipes;
    private static final int PAGE_SIZE = 19;
    private Map<Integer, ArrayList<String>> dataSource;
    private int currentPageNumber; // used for gridster pagination
    private LazyRecipeDataModel lazyModel;
    
    @PostConstruct
    public void init() {
        currentPageNumber = 1;
        lazyModel = new LazyRecipeDataModel(recipeEJB
                );
    }

    /**
     * returns the list of the most recent recipes
     * @return List of recipes
     */
    public List<Recipe> getRecentRecipes() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -3);
        return recipeEJB.getRecipesSince(c.getTime());
    }

    /**
     * returns the list of recipes belonging to a given user
     * @return List of recipes
     */
    public List<Recipe> getMyRecipes() {
        Users user = ui.getUser();
        List<Recipe> recipes = null;
        if (user != null) {
            recipes = recipeEJB.getRecipes(user.getUserName());
        }
        return recipes;
    }
    
    /**
     * returns the name of recipe to be used with gridster
     * @return String name of recipe
     */
    public String getRecipesForGridster() {
        int page = this.currentPageNumber;
        if (dataSource == null) {
            // key = page number
            // Array = array with each element containing a recipe id and name
            dataSource = new HashMap<Integer, ArrayList<String>>();
            getRecipes();
            int dataSize = recipes.size();

            if (dataSize > PAGE_SIZE) {
                int currentPage = 1;
                ArrayList<String> temp = new ArrayList<String>();
                
                for (int i = 0; i < dataSize; i++) {
                    String tempRecipeId = recipes.get(i).getRecipeId().toString();
                    String tempRecipeName = recipes.get(i).getRecipeName();
                    
                    if (tempRecipeId != null) {
                        String tempStr = tempRecipeId + "," + tempRecipeName;
                        temp.add(tempStr);
                    }
                    
                    if (i % PAGE_SIZE == 0) {
                        dataSource.put(currentPage, temp);
                        currentPage++;
                        temp = new ArrayList<String>();
                    }
                } // end for-loop
                
            } else {
                ArrayList<String> tmp = new ArrayList<String>();
                
                for (Recipe r: recipes) {
                    String tempRecipeId = r.getRecipeId().toString();
                    String tempRecipeName = r.getRecipeName();
                    
                    if (tempRecipeId != null) {
                        String tempStr = tempRecipeId + "," + tempRecipeName;
                        tmp.add(tempStr);
                    }  
                } //end for-loop
                
                dataSource.put(1, tmp); // no pagination needed
            }
        }
        
        JSONObject recipesListJSONObject = new JSONObject();        

        try {
            String strStr = buildDelimitedString(dataSource.get(page), "|");
            recipesListJSONObject.put(String.valueOf(page), strStr);
        } catch (JSONException ex) {
            Logger.getLogger(BrowseRecipes.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return recipesListJSONObject.toString();
    }
    
    /**
     * builds a string with a given delimeter
     * @param dataSource collection of objects
     * @param deilimeter string to be plcaed between the objects
     * @return 
     */
    public String buildDelimitedString(Collection dataSource, String deilimeter) {
        StringBuilder strStr = new StringBuilder();
        for (Object str : dataSource) {
            strStr.append(str);
            strStr.append(deilimeter);
        }
        return strStr.length() > 0 ? strStr.substring(0, strStr.length() - 1): "";
    }
    
    /**
     * returns the list of recipes filtered
     * @return List of filtered recipes
     */
    public List<Recipe> getRecipes() {
        browseBy = qm.get("by");
        if (browseBy != null && !browseBy.equals("")) {
            if (browseBy.equals("all")) {
                recipes = getRecentRecipes();
            } else {
                recipes = recipeEJB.getRecipesByTag(browseBy);
            }
            determineTitle();
        }
        return recipes;
    }

    /**
     * sets the list of recipes to browse
     * @param recipes List of recipes
     */
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    /**
     * returns the filter to be browsed by
     * @return string browseBy
     */
    public String getBrowseBy() {
        return browseBy;
    }

    /**
     * set the filter to be browsed by
     * @param browseBy String
     */
    public void setBrowseBy(String browseBy) {
        this.browseBy = browseBy;
    }

    /**
     * determines the category of the browse
     */
    private void determineTitle() {
        if (browseBy.equals("all")) {
            displayTitle = "All Recipes";
        } else if (browseBy.equals("maincourses")) {
            displayTitle = "Main Courses";
        } else if (browseBy.equals("dinner")) {
            displayTitle = "Dinner";
        } else if (browseBy.equals("breakfast")) {
            displayTitle = "Breakfast";
        } else if (browseBy.equals("lunch")) {
            displayTitle = "Lunch";
        } else if (browseBy.equals("sidedishes")) {
            displayTitle = "Side Dishes";
        } else if (browseBy.equals("desserts")) {
            displayTitle = "Desserts";
        } else if (browseBy.equals("beverages")) {
            displayTitle = "Beverages";
        } else if (browseBy.equals("appetizers")) {
            displayTitle = "Appetizers";
        }
    }

    /**
     * returns the title to be displayed
     * @return string displyTitle
     */
    public String getDisplayTitle() {
        return displayTitle;
    }

    /**
     * sets the title to be displayed
     * @param displayTitle string displayTitle
     */
    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }
    /**
     * returns the lazy model for browsing recipe
     * @return lazyModel
     */
    public LazyRecipeDataModel getLazyModel() {
        return lazyModel;
    }

    /**
     * sets the lazy model for browsing recipe
     * @param lazyModel new LazyRecipeModel
     */
    public void setLazyModel(LazyRecipeDataModel lazyModel) {
        this.lazyModel = lazyModel;
    }
}
