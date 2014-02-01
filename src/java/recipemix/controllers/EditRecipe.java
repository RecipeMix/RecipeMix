///*
// *  Copyright (C) 2013 Alex Chavez <alex@alexchavez.net>, Jairo Lopez <jlopez@csulbmaes.org>,
// *  Steven Paz <steve.a.paz@gmail.com>, Gustavo Rosas <gustavoscrib@yahoo.com>
// *
// *  RecipeMix ALL RIGHTS RESERVED
// *
// *  This program is distributed to users in the hope that it will be useful,
// *  but WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.



// */ DEPRECATED - see CreateEditRecipe




//package recipemix.controllers;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.Serializable;
//import java.util.Iterator;
//import java.util.List;
//import java.util.ListIterator;
//import java.util.Random;
//import java.util.logging.Logger;
//import javax.annotation.PostConstruct;
//import javax.ejb.EJB;
//import javax.enterprise.context.Conversation;
//import javax.enterprise.context.ConversationScoped;
//import javax.faces.application.FacesMessage;
//import javax.faces.context.FacesContext;
//import javax.inject.Inject;
//import javax.inject.Named;
//import org.primefaces.event.FileUploadEvent;
//import org.primefaces.model.UploadedFile;
//import recipemix.beans.ImageEJB;
//import recipemix.beans.IngredientEJB;
//import recipemix.beans.RecipeEJB;
//import recipemix.beans.TagEJB;
//import recipemix.models.Image;
//import recipemix.models.Recipe;
//import recipemix.models.RecipeIngredient;
//import recipemix.models.RecipeStep;
//import recipemix.models.Tag;
//import recipemix.models.Unit;
//
///**
// *
// * @author Jairo Lopez <jairo.lopez00@gmail.com>
// * @author Steven Paz <steve.a.paz@gmail.com>
// */
//@Named
//@ConversationScoped
//public class EditRecipe implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//    private static final Logger logger = Logger.getLogger("EditRecipe");
//    //=================================================
//    //=                 EJBs                          =
//    //=================================================
//    @EJB
//    private RecipeEJB recipeEJB;
//    @EJB
//    private IngredientEJB ingredientsEJB;
//    @EJB
//    private TagEJB tagEJB;
//    @EJB
//    private ImageEJB imageEJB;
//    //=================================================
//    //=                 Other Beans                   =
//    //=================================================
//    @Inject
//    private UserIdentity ui;
//    @Inject
//    private Conversation conversation;
//    @Inject
//    RequestParameterManager qm;
//    
//    // The Recipe being edited
//    private Recipe recipe;
//    // Running lists of ingredients and steps
//    private List<RecipeIngredient> ingredients;
//    private List<RecipeStep> steps;
//    
//    // the comma-delimited string input for tags
//    private String inputTags;
//    
//    // used for editing/deleting steps and ingredients
//    private RecipeIngredient newIngredient;
//    private RecipeStep newStep;
//    private RecipeIngredient ingredientToRemove;
//    private RecipeStep stepToRemove;
//    
//    // Need math for these - cook and prep time
//    private int prepHours;
//    private int prepMinutes;
//    private int cookHours;
//    private int cookMinutes;
//    
//    private UploadedFile file;
//
//    //For image uploads
//    Random random = new Random();
//    
//    /**
//     * Creates a new instance of RecipeController
//     */
//    public EditRecipe() {
//        newIngredient = new RecipeIngredient();
//        newStep = new RecipeStep();
//    }
//
//    @PostConstruct
//    /**
//     * Populate with recipe's current values
//     */
//    public void init() {
//        String value = qm.get("recipe"); // get the recipe ID as a string
//        if (value != null) {
//            recipe = recipeEJB.findRecipe(Integer.parseInt(value));
//        }
//        if (recipe != null) {
//            ingredients = recipe.getIngredients();
//            steps = recipe.getInstructions();
//            
//            prepHours = recipe.getPrepTime() / 60; // whole number
//            prepMinutes = recipe.getPrepTime() % 60; // remainder minutes
//            cookHours = recipe.getCookTime() / 60; // whole number
//            cookMinutes = recipe.getCookTime() % 60; // remainder minutes
//
//            // Build the string of comma-delimited current tags
//            inputTags = "";
//            List<Tag> tags = recipe.getTags();
//            if (!tags.isEmpty()) {
//                ListIterator i = tags.listIterator();
//                do {
//                    inputTags += ((Tag) i.next()).getTagName();
//                    // add a comma if there's more
//                    if(i.hasNext()){
//                        inputTags += ", ";
//                    }
//                }while(i.hasNext());
//            }
//        } else {
//            // TODO: Faces redirect to error?
//        }
//    }
//
//    /* Will be called once - during bean initialization */
//    public void initConversation() {
//        if (!FacesContext.getCurrentInstance().isPostback()
//                && conversation.isTransient()) {
//
//            conversation.begin();
//            conversation.setTimeout(900000); // 15 minutes
//        }
//    }
//    
//    /**
//     * FileUploadEventListener for image upload in edit recipe
//     * @param event 
//     */
//    public void createRecipeImage(FileUploadEvent event) {
//        Image newImage = new Image();
//        String destination = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
//        Integer recipeID = this.recipe.getRecipeId();
//        File file = new File(destination+"uploads"+File.separator+"recipe"+File.separator+recipeID);
//        String abspath = file.getAbsolutePath()+ File.separator;
//        if(!file.exists()){
//            if(file.mkdirs());
//        }
//        //new name of the image
//        //TODO: start naming at 1 and then check if the next number
//        //  is being used as the tile of another FILE in the same folder
//        String newImageName = String.valueOf(random.nextInt(10000)) + ".jpeg";
//        // Do what you want with the file        
//        String newImagePath = "/uploads/recipe/"+recipeID+"/"+newImageName;
//        try {
//            copyFile(abspath,newImageName, event.getFile().getInputstream());            
//            newImage = this.imageEJB.createImage(newImage);
//            newImage.setCaption("This is the "+this.recipe.getRecipeName()+"'s recipe picture.");
//            newImage.setRecipe(this.recipe);
//            newImage.setImagePath(newImagePath);
//            newImage.setImageName(newImageName);
//            this.imageEJB.editImage(newImage);
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Success!", "Your image was uploaded successfully."));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    
//    public void copyFile(String destination, String fileName, InputStream in) {
//        try {
//            // write the inputStream to a FileOutputStream
//            OutputStream out = new FileOutputStream(new File(destination + fileName));
//            int read = 0;
//            byte[] bytes = new byte[1024];
//            while ((read = in.read(bytes)) != -1) {
//                out.write(bytes, 0, read);
//            }
//            in.close();
//            out.flush();
//            out.close();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    /**
//     * This method does the edit
//     *
//     * @return "success" if edit was successful, null otherwise
//     */
//    public String doEditRecipe() {
//        String result = "failure";
//        try {
//            recipe.setCookTime(this.cookHours * 60 + this.cookMinutes);
//            recipe.setPrepTime(this.prepHours * 60 + this.prepMinutes);
//
//            //Create the Ingredient entities if they don't already exist
//            Iterator i = this.ingredients.listIterator();
//            while (i.hasNext()) {
//                RecipeIngredient ri = (RecipeIngredient) i.next();
//                ri.setIngredient(ingredientsEJB.createIngredient(ri.getIngredient().getIngredientName()));
//            }
//            //Create the Recipe entity
//            recipe.setInstructions(steps);
//            recipe.setIngredients(ingredients);
//            recipe.setTags(tagEJB.splitTags(inputTags));
//            recipe = recipeEJB.editRecipe(recipe);
//            if (recipe != null) {
//                result = "success";  // better to return success to notify user that Recipe was created
//                // increment the weight on each tag
//            }
//        } catch (javax.ejb.EJBAccessException ejbae) {
//            result = "forbidden";
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only registered users can create recipes."));
//        }
//        if (conversation.isTransient()) {
//            conversation.end();
//        }
//        return result;
//    }
//    
//    /**
//     * Method to be called to add a new empty ingredient
//     */
//    public void addIngredient() {
//        this.ingredients.add(this.newIngredient);
//        this.newIngredient = new RecipeIngredient();
//    }
//
//    /**
//     * Method to be called to add a new empty ingredient
//     */
//    public void addStep() {
//        newStep.setStepNumber(this.steps.size() + 1);
//        steps.add(newStep);
//        newStep = new RecipeStep(); // reset
//    }
//    
//    public void removeIngredient() {
//        this.ingredients.remove(this.ingredientToRemove);
//    }
//
//    public void removeStep() {
//        this.steps.remove(this.stepToRemove);
//        // Fix numbers
//        Iterator i = steps.listIterator();
//        int counter = 1;
//        while(i.hasNext()){
//            RecipeStep s = (RecipeStep)i.next();
//            s.setStepNumber(counter);
//            counter++;
//        }
//    }
//
//    public int getPrepHours() {
//        return prepHours;
//    }
//
//    public void setPrepHours(int prepHours) {
//        this.prepHours = prepHours;
//    }
//
//    public int getPrepMinutes() {
//        return prepMinutes;
//    }
//
//    public void setPrepMinutes(int prepMinutes) {
//        this.prepMinutes = prepMinutes;
//    }
//
//    public int getCookHours() {
//        return cookHours;
//    }
//
//    public void setCookHours(int cookHours) {
//        this.cookHours = cookHours;
//    }
//
//    public int getCookMinutes() {
//        return cookMinutes;
//    }
//
//    public void setCookMinutes(int cookMinutes) {
//        this.cookMinutes = cookMinutes;
//    }
//
//    public List<RecipeIngredient> getIngredients() {
//        return ingredients;
//    }
//
//    public void setIngredients(List<RecipeIngredient> ingredients) {
//        this.ingredients = ingredients;
//    }
//
//    public RecipeIngredient getIngredientToRemove() {
//        return ingredientToRemove;
//    }
//
//    public void setIngredientToRemove(RecipeIngredient ingredientToRemove) {
//        this.ingredientToRemove = ingredientToRemove;
//    }
//
//    public List<RecipeStep> getSteps() {
//        return steps;
//    }
//
//    public void setSteps(List<RecipeStep> steps) {
//        this.steps = steps;
//    }
//
//    public RecipeStep getStepToRemove() {
//        return stepToRemove;
//    }
//
//    public void setStepToRemove(RecipeStep stepToRemove) {
//        this.stepToRemove = stepToRemove;
//    }
//
//    public Unit[] getUnitLabels() {
//        return Unit.values();
//    }
//
//    public Recipe getRecipe() {
//        return recipe;
//    }
//
//    public void setRecipe(Recipe recipe) {
//        this.recipe = recipe;
//    }
//
//    public RecipeIngredient getNewIngredient() {
//        return newIngredient;
//    }
//
//    public void setNewIngredient(RecipeIngredient newIngredient) {
//        this.newIngredient = newIngredient;
//    }
//
//    public RecipeStep getNewStep() {
//        return newStep;
//    }
//
//    public void setNewStep(RecipeStep newStep) {
//        this.newStep = newStep;
//    }
//
//    public String getInputTags() {
//        return inputTags;
//    }
//
//    public void setInputTags(String inputTags) {
//        this.inputTags = inputTags;
//    }
//}
