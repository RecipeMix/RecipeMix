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
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import recipemix.models.Groups;
import recipemix.models.Recipe;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.model.DualListModel;
import recipemix.beans.GroupEJB;
import recipemix.beans.RecipeEJB;
import recipemix.models.Users;

/**
 *
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Named
@RequestScoped
public class RecipeToGroup implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("RecipeToGroup");
    
    // =======================================
    // =              EJBS                   =
    // =======================================
    @EJB
    private GroupEJB groupEJB;
    @EJB
    private RecipeEJB recipeEJB;
    
    // =======================================
    // =          Other Beans                =
    // =======================================
    @Inject
    RequestParameterManager rpm;
    @Inject
    private UserIdentity ui;
    
    // =======================================
    // =             Fields                  =
    // =======================================
    private Recipe recipeToBeAdded;
    private Users user;
    List<Groups> source = new ArrayList<Groups>();
    List<Groups> target = new ArrayList<Groups>();
    private DualListModel<Groups> groups;

    /*
     * Initializes bean to include user's identity and populates list with
     * groups they own and moderate.
     */
    @PostConstruct
    private void init() {
        if (ui.isIsUserAuthenticated()) {
            user = ui.getUser();
        }

        Set<Groups> groupsSet;
        try {
            groupsSet = new HashSet<Groups>(groupEJB.getGroupsCreated(user));
            groupsSet.addAll(groupEJB.getGroupsManaged(user));

            for (Groups g : groupsSet) {
                source.add(g);
            }
        } catch (Exception e) {
            System.out.println("No groups created or owned");
        }


        groups = new DualListModel<Groups>(source, target);

    }

    public DualListModel<Groups> getGroups() {
        return groups;
    }

    public void setGroups(DualListModel<Groups> groups) {
        this.groups = groups;
    }

    /* public void onTransfer(TransferEvent event) {
     StringBuilder builder = new StringBuilder();  
     for(Object item : event.getItems()) {  
     builder.append(((Groups) item).getGroupName()).append("<br />");  
     }  

     FacesMessage msg = new FacesMessage();
     msg.setSeverity(FacesMessage.SEVERITY_INFO);
     msg.setSummary("Items Transferred");
     msg.setDetail(builder.toString());

     FacesContext.getCurrentInstance().addMessage(null, msg);
     }*/
    public void addToGroup() {
        List<Recipe> recipes;
        String value = rpm.get("recipe");
        if (value != null) {
            recipeToBeAdded = recipeEJB.findRecipe(Integer.parseInt(value));
        }
        for (Groups g : groups.getTarget()) {
            recipes = g.getRecipes();
            if (!recipes.contains(recipeToBeAdded)) {
                recipes.add(recipeToBeAdded);
                g.setRecipes(recipes);
                groupEJB.editGroup(g);
                FacesMessage msg = new FacesMessage("Success", 
                        "Added " + recipeToBeAdded.getRecipeName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }
}
