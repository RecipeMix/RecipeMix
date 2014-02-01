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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import recipemix.beans.GroupEJB;
import recipemix.beans.RecipeEJB;
import recipemix.beans.UsersEJB;
import recipemix.models.Groups;
import recipemix.models.Recipe;
import recipemix.models.Users;

/**
 * This bean is used for featured recipes and groups
 *
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@Named
@ApplicationScoped
public class Featured implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("ViewProfile");
    private List<Recipe> featuredRecipes;
    private List<Groups> featuredGroups;
    private List<Users> featuredUsers;
    private boolean useRandomRecipes;
    private boolean useRandomGroups;
    private boolean useRandomUsers;
    @EJB
    private RecipeEJB recipeEJB;
    @EJB
    private GroupEJB groupEJB;
    @EJB
    private UsersEJB userEJB;
    private Recipe[] recipesSelectedByMod;
    private Groups[] groupsSelectedByMod;

    @PostConstruct
    public void init() {
        // At application start, use random recipes, users, and groups
        useRandomRecipes = true;
        useRandomGroups = true;
        useRandomUsers = true;

        getRandomFeatured(useRandomRecipes, useRandomGroups, useRandomUsers);

    }

    /**
     * returns the list of featured recipes
     * @return 
     */
    public List<Recipe> getFeaturedRecipes() {
        if (recipesSelectedByMod == null || useRandomRecipes) {
            getRandomFeatured(true, false, false);
            return featuredRecipes;
        } else {
            return new ArrayList<Recipe>(Arrays.asList(recipesSelectedByMod));
        }
    }

    public void setFeaturedRecipes(List<Recipe> featuredRecipes) {
        if (featuredRecipes != null) {
            this.featuredRecipes = featuredRecipes;
            this.useRandomRecipes = false;
        }
    }

    public List<Groups> getFeaturedGroups() {
        if (useRandomGroups || this.featuredGroups == null || this.featuredGroups.isEmpty()) {
            getRandomFeatured(false, true, false);
        }
        return featuredGroups;
    }

    public void setFeaturedGroups(List<Groups> featuredGroups) {
        this.featuredGroups = featuredGroups;
    }

    public List<Users> getFeaturedUsers() {
        if (useRandomUsers || this.featuredUsers == null || this.featuredUsers.isEmpty()) {
            getRandomFeatured(false, false, true);
        }
        return featuredUsers;
    }

    public void setFeaturedUsers(List<Users> featuredUsers) {
        this.featuredUsers = featuredUsers;
    }

    /**
     * Will set the lists of recipes, groups, or users to random values
     * depending on parameters
     *
     * @param useRandomRecipes
     * @param useRandomGroups
     * @param useRandomUsers
     */
    private void getRandomFeatured(boolean useRandomRecipes, boolean useRandomGroups, boolean useRandomUsers) {
        Random random = new Random();
        if (useRandomRecipes) {
            random.setSeed(new Date().getTime());
            //Get range for Recipes
            int recipeRange = recipeEJB.getHighestRecipeID() + 1;

            if (recipeRange <= 6) {
                featuredRecipes = recipeEJB.findRecipes();
                Collections.shuffle(featuredRecipes, random);
            } else {
                for (int i = 0; i < 5; i++) {
                    int recipeID;
                    // Only one result
                    if (recipeRange <= 0) {
                        recipeID = 1;
                    } else {
                        recipeID = random.nextInt(recipeRange); // nextInt is exclusive of upper range, so add 1
                    }

                    // Just one
                    if (recipeID != 0) {
                        Recipe result = recipeEJB.findRecipe(recipeID);
                        if (result != null) {
                            featuredRecipes = new ArrayList<Recipe>();
                            featuredRecipes.add(result);
                        }
                    }
                }
            }
        }

        if (useRandomGroups) {
            //Seed using current system time
            random.setSeed(new Date().getTime());

            //Get range for Groups
            int groupRange = groupEJB.getHighestGroupID();

            if (groupRange <= 5) {
                featuredGroups = groupEJB.findGroups();
                Collections.shuffle(featuredGroups, random);
            } else {
                for (int i = 0; i < 5; i++) {
                    int groupID;
                    // Only one result
                    if (groupRange <= 0) {
                        groupID = 1;
                    } else {
                        groupID = random.nextInt(groupRange + 1);
                    }
                    // Just one
                    if (groupID != 0) {
                        Groups result = groupEJB.findGroup(groupID);
                        if (result != null) {
                            featuredGroups = new ArrayList<Groups>();
                            featuredGroups.add(result);
                        }
                    }
                }
            }
        }

        if (useRandomUsers) {
            // Help from:
            // http://stackoverflow.com/questions/124671/picking-a-random-element-from-a-set
            // http://stackoverflow.com/questions/969573/java-random-collection
            // Seed using current system time
            random.setSeed(new Date().getTime());

            //Inefficient, but JPQL can't select a random subset of rows
            List<Users> users = userEJB.findUsers();
            featuredUsers = new ArrayList<Users>();

            if (!users.isEmpty() && users.size() > 5) {
                Collections.shuffle(users);
                featuredUsers = users.subList(0, 5);

            } else if (!users.isEmpty() && users.size() > 0) {
                Collections.shuffle(users, random);
                featuredUsers = users;
            }
        }
    }

    public Recipe[] getRecipesSelectedByMod() {
        return recipesSelectedByMod;
    }

    public Groups[] getGroupsSelectedByMod() {
        return groupsSelectedByMod;
    }

    public void setGroupsSelectedByMod(Groups[] groupsSelectedByMod) {
        if (groupsSelectedByMod != null) {
            this.useRandomRecipes = false;
        }
        this.groupsSelectedByMod = groupsSelectedByMod;
    }

    public void setRecipesSelectedByMod(Recipe[] recipesSelectedByMod) {
        if (recipesSelectedByMod != null) {
            this.useRandomRecipes = false;
        }
        this.recipesSelectedByMod = recipesSelectedByMod;
    }

    public void clearSelectedRecipes() {
        recipesSelectedByMod = null;
        this.useRandomRecipes = true;
    }

    public void clearSelectedGroups() {
        groupsSelectedByMod = null;
        this.useRandomGroups = true;
    }
}
