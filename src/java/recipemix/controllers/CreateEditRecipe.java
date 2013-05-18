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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import recipemix.beans.IngredientEJB;
import recipemix.beans.RecipeEJB;
import recipemix.beans.TagEJB;
import recipemix.models.Ingredient;
import recipemix.models.RatingValue;
import recipemix.models.Recipe;
import recipemix.models.RecipeIngredient;
import recipemix.models.RecipeRating;
import recipemix.models.RecipeStep;
import recipemix.models.Tag;
import recipemix.models.Unit;
import recipemix.models.Users;

/**
 *
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@Named
@ConversationScoped
public class CreateEditRecipe implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("CreateRecipe");
    private static final String ingredPlaceholder = "Click here to enter a name.";
    private static final String stepPlaceholder = "Click here to enter a description.";
    //=================================================
    //=                 EJBs                          =
    //=================================================
    @EJB
    private RecipeEJB recipeEJB;
    @EJB
    private IngredientEJB ingredientsEJB;
    @EJB
    private TagEJB tagEJB;
    //=================================================
    //=                 Other Beans                   =
    //=================================================
    @Inject
    private UserIdentity ui;
    @Inject
    private RequestParameterManager rpm;
    @Inject
    private Conversation conversation;
    private List<RecipeIngredient> ingredients;
    private List<RecipeStep> steps;
    private Recipe recipe;
    private RecipeIngredient newIngredient;
    private RecipeStep newStep;
    private RecipeIngredient ingredientToRemove;
    private RecipeStep stepToRemove;
    private String inputTags;
    // Need math for these
    private int prepHours;
    private int prepMinutes;
    private int cookHours;
    private int cookMinutes;
    private boolean editMode;

    @PostConstruct
    public void init() {
        String value = rpm.get("recipe");

        // New recipe
        if (value == null) {
            recipe = new Recipe();
            newIngredient = new RecipeIngredient();
            newStep = new RecipeStep();
            newStep.setStepDescription(stepPlaceholder);
            ingredients = new ArrayList<RecipeIngredient>();
            steps = new ArrayList<RecipeStep>();
            this.addIngredient();
            this.addStep();
            editMode = false;
        } else {
            recipe = recipeEJB.findRecipe(Integer.parseInt(value));
            if (recipe != null) {
                editMode = true;
                ingredients = recipe.getIngredients();
                steps = recipe.getInstructions();

                prepHours = recipe.getPrepTime() / 60; // whole number
                prepMinutes = recipe.getPrepTime() % 60; // remainder minutes
                cookHours = recipe.getCookTime() / 60; // whole number
                cookMinutes = recipe.getCookTime() % 60; // remainder minutes

                // Build the string of comma-delimited current tags
                inputTags = "";
                List<Tag> tags = recipe.getTags();
                if (!tags.isEmpty()) {
                    for (Iterator<Tag> it = tags.iterator(); it.hasNext();) {
                        Tag t = it.next();
                        if (it.hasNext()) { // more to go
                            inputTags += t.getTagName() + ", ";
                        } else if (!it.hasNext()) { // last one
                            inputTags += t.getTagName();
                        }
                    }
                }//end if
            }//end recipe != null
        }
    }// end method

    /* Will be called once - during bean initialization */
    public void initConversation() {
        if (!FacesContext.getCurrentInstance().isPostback()
                && conversation.isTransient()) {

            conversation.begin();
            conversation.setTimeout(900000);
        }
    }

    /**
     * returns the boolean if the list of ingredients or steps is empty or 
     * @return true if they are not empty else false
     */
    private boolean clearEmpty() {
        boolean proceed = true; // can proceed with edit or create
        //Throw out empty ingredients
        ListIterator<RecipeIngredient> i = ingredients.listIterator();
        while (i.hasNext()) {
            RecipeIngredient ri = i.next();
            String name = ri.getIngredient().getIngredientName();
            Float amt = ri.getIngredientAmount();
            if (name == null || name.equals("") || name.equals(CreateEditRecipe.ingredPlaceholder) || amt == 0f) {
                i.remove();
            }
        }

        //Throw out empty instructions
        ListIterator<RecipeStep> it = steps.listIterator();
        while (it.hasNext()) {
            RecipeStep step = it.next();
            String desc = step.getStepDescription();
            if (desc == null || desc.equals(CreateEditRecipe.stepPlaceholder) || desc.equals("")) {
                it.remove();
            }
        }

        if (ingredients.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Ingredients", "You didn't specify any ingredients!"));
            proceed = false;
        }
        if (steps.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Instructions", "You didn't specify any instructions!"));
            proceed = false;
        }
        
        if(!proceed){
            addIngredient();
            addStep();
        }
        
        return proceed;
    }

    /**
     * sets the recipe fields to be created to a recipe entity
     */
    private void prepareRecipe() {
        recipe.setCreator(ui.getUser());
        recipe.setCookTime(this.cookHours * 60 + this.cookMinutes);
        recipe.setPrepTime(this.prepHours * 60 + this.prepMinutes);

        //Redo the step numbers
        int counter = 1;
        for (RecipeStep rs : steps) {
            rs.setStepNumber(counter);
            counter++;
        }

        //createIngredient() creates Ingredient entities if they don't already exist
        for (RecipeIngredient ri : ingredients) {
            ri.setIngredient(ingredientsEJB.createIngredient(ri.getIngredient().getIngredientName()));
        }

        recipe.setInstructions(steps);
        recipe.setIngredients(ingredients);
        recipe.setTags(tagEJB.splitTags(inputTags));

        // User automatically has 5 stars for his own NEW recipe
        if (!editMode) {
            RecipeRating rating = new RecipeRating();
            rating.setRatingDate(new Date().getTime());
            rating.setRatingValue(RatingValue.FIVE_STARS);
            Users u = ui.getUser();
            rating.setRater(u);
            rating.setRecipe(recipe);
            u.getRatings().add(rating);
            List<RecipeRating> ratings = new ArrayList<RecipeRating>();
            ratings.add(rating);
            recipe.setRatings(ratings);
        }
    }

    /**
     * This method calls the create recipe method in the EJB
     *
     * @return "success" if creation was successful, null otherwise
     */
    public String doCreateRecipe() {
        String result = "";
        boolean proceed = clearEmpty();
        if (proceed) {
            prepareRecipe();
            try {
                recipe = recipeEJB.createRecipe(recipe);
                if (recipe != null) {
                    result = "success";
                }
            } catch (javax.ejb.EJBAccessException ejbae) {
                result = "forbidden";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only registered users can create recipes."));
            }

            if (!conversation.isTransient()) {
                conversation.end();
            }
        }
        return result;
    }

    /**
     * This method does the edit
     *
     * @return "success" if edit was successful, null otherwise
     */
    public String doEditRecipe() {
        String result = "failure";
        boolean proceed = clearEmpty();
        if (proceed) {
            try {
                prepareRecipe();
                recipe = recipeEJB.editRecipe(recipe);
                if (recipe != null) {
                    result = "success";
                    // increment the weight on each tag
                }
            } catch (javax.ejb.EJBAccessException ejbae) {
                result = "forbidden";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only registered users can create recipes."));
            }
            if (!conversation.isTransient()) {
                conversation.end();
            }
        }
        return result;
    }

    /**
     * This method does the edit
     *
     * @return "success" if edit was successful, null otherwise
     */
    public String doDeleteRecipe() {
        String result = "failure";
        try {
            recipeEJB.removeRecipe(recipe);
            if (recipeEJB.findRecipe(recipe.getRecipeId()) == null) {
                result = "success";
            }
        } catch (javax.ejb.EJBAccessException ejbae) {
            result = "forbidden";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only registered users can create recipes."));
        }
        if (!conversation.isTransient()) {
            conversation.end();
        }
        return result;
    }

    /**
     * Method to be called to add a new empty ingredient
     */
    public void addIngredient() {
        Ingredient i = new Ingredient();
        i.setIngredientName(ingredPlaceholder);
        RecipeIngredient ri = new RecipeIngredient();
        ri.setIngredient(i);
        if (ingredients.isEmpty()) {
            this.ingredients.add(ri);
        } else {
            RecipeIngredient previous = ingredients.get(ingredients.size() - 1);
            String iName = previous.getIngredient().getIngredientName();
            if (previous.getIngredientAmount() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Amount invalid", "Please enter an ingredient amount!"));
            } else if (!iName.equals(ingredPlaceholder) && !iName.equals("") && iName != null) {
                this.ingredients.add(ri);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Name invalid", "Please enter an ingredient name!"));
            }
        }
    }

    /**
     * Method to be called to add a new empty ingredient
     */
    public void addStep() {
        RecipeStep step = new RecipeStep();
        step.setStepDescription(stepPlaceholder);
        if (steps.isEmpty()) {
            step.setStepNumber(1);
            steps.add(newStep);
        } else {
            int currentNumSteps = steps.size();
            String desc = steps.get(currentNumSteps - 1).getStepDescription();
            if (!desc.equals("") && !desc.equals(stepPlaceholder) && desc != null) {
                step.setStepNumber(currentNumSteps + 1);
                steps.add(step);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid", "Please enter a step description!"));
            }
        }
    }
    
    /**
     * removes the step from the list of recipe steps
     */
    public void removeStep() {
        this.steps.remove(this.stepToRemove);
        // Fix numbers
        Iterator i = steps.listIterator();
        int counter = 1;
        while (i.hasNext()) {
            RecipeStep s = (RecipeStep) i.next();
            s.setStepNumber(counter);
            counter++;
        }
    }

    /**
     * This method gets the recipe stored in the bean for creation
     *
     * @return the Recipe that is pending creation
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * This method sets the recipe stored in the bean for creation
     *
     * @param recipe the recipe to be included
     */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    /**
     * returns the prepHours
     * @return int prephours
     */
    public int getPrepHours() {
        return prepHours;
    }

    /**
     * sets the prep hours 
     * @param prepHours integer new prepHours
     */
    public void setPrepHours(int prepHours) {
        this.prepHours = prepHours;
    }

    /**
     * returns the preparation minutes
     * @return int prepMinutes
     */
    public int getPrepMinutes() {
        return prepMinutes;
    }

    /**
     * sets the prepMinutes of the recipe
     * @param prepMinutes int new prep minutes
     */
    public void setPrepMinutes(int prepMinutes) {
        this.prepMinutes = prepMinutes;
    }

    /**
     * returns the cook hours
     * @return int cookhours
     */
    public int getCookHours() {
        return cookHours;
    }

    /**
     * sets the cook hours for this recipe
     * @param cookHours new int cook hours
     */
    public void setCookHours(int cookHours) {
        this.cookHours = cookHours;
    }

    /**
     * returns the cook minutes for this recipe
     * @return 
     */
    public int getCookMinutes() {
        return cookMinutes;
    }

    public void setCookMinutes(int cookMinutes) {
        this.cookMinutes = cookMinutes;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public RecipeIngredient getNewIngredient() {
        return newIngredient;
    }

    public void setNewIngredient(RecipeIngredient newIngredient) {
        this.newIngredient = newIngredient;
    }

    public RecipeIngredient getIngredientToRemove() {
        return ingredientToRemove;
    }

    public void setIngredientToRemove(RecipeIngredient ingredientToRemove) {
        this.ingredientToRemove = ingredientToRemove;
    }

    public void removeIngredient() {
        this.ingredients.remove(this.ingredientToRemove);
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RecipeStep> steps) {
        this.steps = steps;
    }

    public RecipeStep getStepToRemove() {
        return stepToRemove;
    }

    public void setStepToRemove(RecipeStep stepToRemove) {
        this.stepToRemove = stepToRemove;
    }

    public RecipeStep getNewStep() {
        return newStep;
    }

    public void setNewStep(RecipeStep newStep) {
        this.newStep = newStep;
    }

    public Unit[] getUnitLabels() {
        return Unit.values();
    }

    public String getInputTags() {
        return inputTags;
    }

    public void setInputTags(String inputTags) {
        this.inputTags = inputTags;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
