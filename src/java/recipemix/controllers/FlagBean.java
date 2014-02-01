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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import recipemix.beans.GroupEJB;
import recipemix.beans.GroupFlagEJB;
import recipemix.beans.RecipeEJB;
import recipemix.beans.RecipeFlagEJB;
import recipemix.beans.UserFlagEJB;
import recipemix.beans.UsersEJB;
import recipemix.models.Recipe;
import recipemix.models.RecipeFlag;
import recipemix.models.GroupFlag;
import recipemix.models.Groups;
import recipemix.models.SecurityGroup;
import recipemix.models.UserFlag;
import recipemix.models.Users;

/**
 * This bean is used to flag a recipe for moderation.
 *
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 * @author Alex Chavez <alex@alexchavez.net>
 */
@Named
@RequestScoped
public class FlagBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private Users flagger; // the user
    private RecipeFlag newRecipeFlag;
    private GroupFlag newGroupFlag;
    private UserFlag newUserFlag;
    private List<Recipe> flaggedRecipes;
    private List<Groups> flaggedGroups;
    private List<Users> flaggedUsers;
    private Recipe recipe;
    private Groups group;
    private Users user;
    private Users offender;
    @EJB
    private UsersEJB usersEJB;
    @EJB
    private RecipeEJB recipeEJB;
    @EJB
    private GroupEJB groupEJB;
    @EJB
    private RecipeFlagEJB recipeFlagEJB;
    @EJB
    private GroupFlagEJB groupFlagEJB;
    @EJB
    private UserFlagEJB userFlagEJB;
    @Inject
    private UserIdentity ui;
    @Inject
    private RequestParameterManager rpm;

    @PostConstruct
    public void init() {
        if (ui.isIsUserAuthenticated()) {
            flagger = ui.getUser();
        }
    }//end init

    /**
     * This method is used to flag a recipe
     */
    public void flagObject() {
        int object = Integer.parseInt(rpm.get("object"));

        switch (object) {
            //object=1, Recipe
            case 1:
                String recipeID = rpm.get("recipe");
                if (recipeID != null) {
                    recipe = recipeEJB.findRecipe(Integer.parseInt(recipeID));
                    if (recipe != null) {
                        newRecipeFlag = recipeFlagEJB.findByUserAndRecipe(flagger, recipe);
                        if (newRecipeFlag == null) { // If no flags for this recipe exists
                            // Set flag attributes
                            newRecipeFlag = new RecipeFlag();
                            newRecipeFlag.setFlagger(flagger);
                            newRecipeFlag.setFlaggedRecipe(recipe);
                            newRecipeFlag.setFlaggingDate(new Date().getTime());
                            

                            // Create a new flag
                            newRecipeFlag = recipeFlagEJB.createRecipeFlag(newRecipeFlag);
                            if (newRecipeFlag != null) {
                                FacesContext.getCurrentInstance().addMessage(null,
                                        new FacesMessage("Success!", "Flag submitted."));
                            } else {
                                FacesContext.getCurrentInstance().addMessage(null,
                                        new FacesMessage("Error!", "Flag submit failed."));
                            }
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null,
                                    new FacesMessage("Error!", "You've already flagged this recipe."));
                        }
                    }// end recipe != null
                }//end recipeID != null
                break;

            //object=2, Group            
            case 2:
                String groupID = rpm.get("group");
                if (groupID != null) {
                            group = groupEJB.findGroup(Integer.parseInt(groupID));
                            if (group != null) {
                                newGroupFlag = groupFlagEJB.findByUserAndGroup(flagger, group);
                                if (newGroupFlag == null) { // If no flags for this group exists
                                    // Set flag attributes
                                    newGroupFlag = new GroupFlag();
                                    newGroupFlag.setFlaggedGroup(group);
                                    newGroupFlag.setFlagger(flagger);
                                    newGroupFlag.setFlaggingDate(new Date().getTime());

                                    // Create a new flag
                                    newGroupFlag = groupFlagEJB.createGroupFlag(newGroupFlag);
                                    if (newGroupFlag != null) {
                                        FacesContext.getCurrentInstance().addMessage(null,
                                                new FacesMessage("Success!", "Flag submitted."));
                                    } else {
                                        FacesContext.getCurrentInstance().addMessage(null,
                                                new FacesMessage("Error!", "Flag submit failed."));
                                    }
                                } else {
                                    FacesContext.getCurrentInstance().addMessage(null,
                                            new FacesMessage("Error!", "You've already flagged this group."));
                                }
                            }// end recipe != null
                        }//emd recipeID != null
                break;
            //object=3, User
            case 3:
                String offenderUserName = rpm.get("userName");
                if (offenderUserName != null) {
                            offender = usersEJB.findUser(offenderUserName);
                            if (offender != null) {
                                newUserFlag = userFlagEJB.findByUserAndOffender(flagger, offender);
                                if (newUserFlag == null) { // If no flags for this recipe exists
                                    // Set flag attributes
                                    newUserFlag = new UserFlag();
                                    newUserFlag.setFlaggedUser(offender);
                                    newUserFlag.setFlagger(flagger);
                                    newUserFlag.setFlaggingDate(new Date().getTime());

                                    // Create a new flag
                                    newUserFlag = userFlagEJB.createUserFlag(newUserFlag);
                                    if (newUserFlag != null) {
                                        FacesContext.getCurrentInstance().addMessage(null,
                                                new FacesMessage("Success!", "Flag submitted."));
                                    } else {
                                        FacesContext.getCurrentInstance().addMessage(null,
                                                new FacesMessage("Error!", "Flag submit failed."));
                                    }
                                } else {
                                    FacesContext.getCurrentInstance().addMessage(null,
                                            new FacesMessage("Error!", "You've already flagged this user."));
                                }
                            }// end recipe != null
                        }//emd recipeID != null
                break;
            default:
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Flag failed"));
                break;

        }
    }

    /**
     * Returns whether or not the user has already flagged the recipe
     *
     * @return - true if can flag, false otherwise
     */
    public boolean isCanFlagRecipe() {
        boolean result = false;
        String recipeID = rpm.get("recipe");
        if (recipeID != null) {
            recipe = recipeEJB.findRecipe(Integer.parseInt(recipeID));
            if (recipe != null && flagger != null) {
                RecipeFlag existingFlag = recipeFlagEJB.findByUserAndRecipe(flagger, recipe);
                result = (existingFlag != null);
            }
        }
        return result;
    }

    /**
     * Returns whether or not the user has already flagged the user profile
     *
     * @return - true if can flag, false otherwise
     */
    public boolean isCanFlagUser() {
        boolean result = false;
        String userName = rpm.get("user");
        if (userName != null) {
            user = usersEJB.findUser(userName);
            if (user != null && flagger != null) {
                UserFlag existingFlag = userFlagEJB.findByUserAndOffender(flagger, user);
                result = (existingFlag != null);
            }
        }
        return result;
    }
    
    /**
     * Returns whether or not the user has already flagged the group
     *
     * @return - true if can flag, false otherwise
     */
    public boolean isCanFlagGroup() {
        boolean result = false;
        String groupID = rpm.get("group");
        if (groupID != null) {
            group = groupEJB.findGroup(Integer.parseInt(groupID));
            if (group != null && flagger != null) {
                GroupFlag existingFlag = groupFlagEJB.findByUserAndGroup(flagger, group);
                result = (existingFlag != null);
            }
        }
        return result;
    }

    /**
     * This method returns the list of Recipes that need admin attention
     *
     * @return the list of flagged Recipes
     */
    public List<Recipe> getFlaggedRecipes() {
        // determine if the user is an admin
        boolean isAdmin = false;
        Collection<SecurityGroup> roles = flagger.getSecurityGroups();

        for (SecurityGroup sg : roles) {
            if (sg.getSecurityGroupName().equals("admin")) {
                isAdmin = true;
                break;
            }
        }

        if (isAdmin) {
            flaggedRecipes = recipeEJB.getFlaggedRecipes(0.2);
        }
        return flaggedRecipes;
    }

    public List<Groups> getFlaggedGroups() {
         // determine if the user is an admin
        boolean isAdmin = false;
        Collection<SecurityGroup> roles = flagger.getSecurityGroups();

        for (SecurityGroup sg : roles) {
            if (sg.getSecurityGroupName().equals("admin")) {
                isAdmin = true;
                break;
            }
        }

        if (isAdmin) {
            flaggedGroups = groupEJB.getGroupsByFlagCount(1);
        }
        return flaggedGroups;
    
    }

    public List<Users> getFlaggedUsers() {
        // determine if the user is an admin
        boolean isAdmin = false;
        Collection<SecurityGroup> roles = flagger.getSecurityGroups();

        for (SecurityGroup sg : roles) {
            if (sg.getSecurityGroupName().equals("admin")) {
                isAdmin = true;
                break;
            }
        }

        if (isAdmin) {
            flaggedUsers = usersEJB.getUsersByFlagCount(1);
        }
        return flaggedUsers;
    }
}
