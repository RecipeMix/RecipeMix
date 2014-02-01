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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import recipemix.beans.GroupEJB;
import recipemix.beans.RecipeEJB;
import recipemix.models.Groups;
import recipemix.models.Recipe;

/**
 * This bean is used to view recipes, lazily paginated
 *
 * @author Steven Paz <steve.a.paz@gmail.com>
 * @author Alex Chavez <alex@alexchavez.net>
 */
@ManagedBean(name = "paginationBean")
@ViewScoped
public class PaginationBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("PaginationBean");
    // =======================================
    // =            EJBs                     =
    // =======================================
    @EJB
    private RecipeEJB recipeEJB;
    @EJB
    private GroupEJB groupEJB;
    @Inject
    private RequestParameterManager qm;
    @Inject
    private UserIdentity ui;
    // Other fields -- Groups
    private String groupsListJSONObject;   // JSON to send back to client
    private List<Groups> groups;           // temporary storage
    // Other fields -- Recipes
    private String recipesListJSONObject;   // JSON to send back to client
    private List<Recipe> recipes;           // temporary storage
    // For both
    private static final int PAGE_SIZE = 19;
    private int currentPageNumber;
    private int numberOfPages;              // total number of pages (future upgrade)
    private int start;                      // start index of current page
    private Map<Integer, ArrayList<String>> dataSource;
    private String browseBy;
    private String displayTitle;
    boolean browsingGroups;
    // boolean browsingUsers; // for future use

    @PostConstruct
    public void init() {
        browseBy = qm.get("by");
        browsingGroups = determineTitle(); // sets page title and if browsing groups
        currentPageNumber = 1;
        start = 0;
        if (browsingGroups) {
            getGroupsForGridster();
        } else {
            getRecipesForGridster(); // will set the JSON object
        }
    }

    public String getGroupsForGridster() {
        // key = page number
        // Array = array with each element containing a recipe id and name
        dataSource = new HashMap<Integer, ArrayList<String>>();

        if (browseBy != null && !browseBy.equals("")) {
            if (browseBy.equals("groups")) {
                // Get groups created since 3 months ago
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.MONTH, -3);
                groups = groupEJB.getGroupsSince(c.getTime(), start, start + PAGE_SIZE - 1);
                //numberOfPages = (int) Math.ceil(recipeEJB.countTotalRecipes() / PAGE_SIZE);
                int total = groupEJB.countTotalGroupsSince(c.getTime());
                numberOfPages = (int)Math.ceil(total/ new Double(PAGE_SIZE));
            }
            else if(browseBy.equals("member") && ui.isIsUserAuthenticated()){
                groups = groupEJB.getGroupMembership(ui.getUser(), start , start + PAGE_SIZE - 1);
            }else if(browseBy.equals("managed")){
                groups = groupEJB.getGroupsManaged(ui.getUser(), start, start + PAGE_SIZE - 1);
            }
        }

        int dataSize = groups.size(); // should be equal to page size

        // One page is enough

        ArrayList<String> tmp = new ArrayList<String>();
        for (Groups g : groups) {
            String tempGroupId = Integer.toString(g.getGroupID());
            String tempGroupName = g.getGroupName();
            String tempImagePath = g.getImage().getImagePath();
            if (tempGroupId != null) {
                String tempStr = tempGroupId + "," + tempGroupName
                        + "," + tempImagePath;
                tmp.add(tempStr);
            }
        } //end for-loop

        dataSource.put(1, tmp); // no pagination needed

        JSONObject jsonObject = new JSONObject();

        try {
            String strStr = buildDelimitedString(dataSource.get(currentPageNumber), "|");
            jsonObject.put(String.valueOf(currentPageNumber), strStr);
        } catch (JSONException ex) {
            Logger.getLogger(PaginationBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.groupsListJSONObject = jsonObject.toString();
        return this.groupsListJSONObject;
    }

    public String getRecipesForGridster() {
        // key = page number
        // Array = array with each element containing a recipe id and name
        dataSource = new HashMap<Integer, ArrayList<String>>();

        // Get the recipes to return
        if (browseBy != null && !browseBy.equals("")) {
            if (browseBy.equals("all")) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.MONTH, -3);
                recipes = recipeEJB.getRecipesSince(c.getTime(), start , start + PAGE_SIZE - 1);
                int total = recipeEJB.countTotalRecipes(c.getTime());
                numberOfPages = (int)Math.ceil(total/ new Double(PAGE_SIZE));
            } else {
                recipes = recipeEJB.getRecipesByTag(browseBy, start, start + PAGE_SIZE - 1);
                int total = recipeEJB.countTotalRecipesByTag(browseBy);
                numberOfPages = (int)Math.ceil(total/ new Double(PAGE_SIZE));
            }
        }

        int dataSize = recipes.size();

        ArrayList<String> tmp = new ArrayList<String>();

        for (Recipe r : recipes) {
            String tempRecipeId = r.getRecipeId().toString();
            String tempRecipeName = r.getRecipeName();
            String tempImagePath = r.getImageGallery().get(0).getImagePath();
            if (tempRecipeId != null) {
                String tempStr = tempRecipeId + "," + tempRecipeName
                        + "," + tempImagePath;
                tmp.add(tempStr);
            }
        } //end for-loop

        dataSource.put(1, tmp); // no pagination needed


        JSONObject jsonObject = new JSONObject();

        try {
            String strStr = buildDelimitedString(dataSource.get(1), "|");
            jsonObject.put(String.valueOf(currentPageNumber), strStr);
        } catch (JSONException ex) {
            Logger.getLogger(BrowseRecipes.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.recipesListJSONObject = jsonObject.toString();
        return this.recipesListJSONObject;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public List<Recipe> getRecentRecipes() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -3);
        return recipeEJB.getRecipesSince(c.getTime());
    }

    public String buildDelimitedString(Collection dataSource, String deilimeter) {
        StringBuilder strStr = new StringBuilder();
        for (Object str : dataSource) {
            strStr.append(str);
            strStr.append(deilimeter);
        }
        String test = strStr.length() > 0 ? strStr.substring(0, strStr.length() - 1) : "";
        return test;
    }

    public void loadPreviousPage() {
        if (currentPageNumber > 1) {
            this.currentPageNumber--;
            start = start - PAGE_SIZE;
            if(this.browsingGroups){
                this.getGroupsForGridster();
            }
            else{
                this.getRecipesForGridster();
            }
        }
    }

    public void loadNextPage() {
        if (currentPageNumber < this.numberOfPages) {
            start = start + PAGE_SIZE;
            this.currentPageNumber++;
            if(this.browsingGroups){
                this.getGroupsForGridster();
            }
            else{
                this.getRecipesForGridster();
            }
        }
    }

    private boolean determineTitle() {
        boolean groups = false;
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
        } else if (browseBy.equals("groups")) {
            displayTitle = "All Groups";
            groups = true;
        } else if (browseBy.equals("member")) {
            displayTitle = "Groups You're In";
            groups = true;
        } else if (browseBy.equals("managed")) {
            displayTitle = "Groups You Manage";
            groups = true;
        }
        return groups;
    }

    public String getRecipesListJSONObject() {
        return recipesListJSONObject;
    }

    public void setRecipesListJSONObject(String recipesListJSONObject) {
        this.recipesListJSONObject = recipesListJSONObject;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public String getBrowseBy() {
        return browseBy;
    }

    public void setBrowseBy(String browseBy) {
        this.browseBy = browseBy;
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    public String getGroupsListJSONObject() {
        return groupsListJSONObject;
    }

    public void setGroupsListJSONObject(String groupsListJSONObject) {
        this.groupsListJSONObject = groupsListJSONObject;
    }
    
    
    
}
