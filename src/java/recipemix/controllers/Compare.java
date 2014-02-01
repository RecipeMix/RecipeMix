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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import recipemix.beans.RecipeEJB;
import recipemix.models.Recipe;

/**
 *
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@Named
@SessionScoped
public class Compare implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("BrowseGroups");
    //=================================================
    //=                 EJBs                          =
    //=================================================
    @EJB
    private RecipeEJB recipeEJB;
    //=================================================
    //=             Other Beans                       =
    //=================================================
    @Inject
    private UserIdentity ui;
    @Inject
    private RequestParameterManager qm;
    private List<Recipe> compare; // list of running recipes

    /**
     *
     */
    @PostConstruct
    public void init() {
        compare = new ArrayList<Recipe>();
    }

    public List<Recipe> getCompare() {
        List<Recipe> temp = new ArrayList<Recipe>();
        for(Recipe r : compare){
            temp.add(r);
        }
        return temp;
    }

    public void setCompare(List<Recipe> compare) {
        this.compare = compare;
    }

    public void addToComparison(ActionEvent e) {
        Integer recipeID = (Integer) e.getComponent().getAttributes().get("recipe");

        if (recipeID != null) {
            if (compare.isEmpty()) {
                compare.add(recipeEJB.findRecipe(recipeID));
            } else {
                boolean result = true;
                for (Recipe r : compare){
                    if(r.getRecipeId() == recipeID){
                        result = false;
                        break;
                    }
                }
                if(result){
                    compare.add(recipeEJB.findRecipe(recipeID));
                }
            }
        }
    }

    public void removeFromComparison(ActionEvent e) {
        Integer recipeID = (Integer) e.getComponent().getAttributes().get("recipe");

        if (recipeID != null) {
            for (Iterator<Recipe> it = compare.iterator(); it.hasNext();) {
                Recipe r = it.next();
                if (r.getRecipeId() == recipeID) {
                    it.remove();
                }
            }
        }
    }
    
    public boolean getReady(){
        return this.compare.size() >= 2;
    }
    
}
