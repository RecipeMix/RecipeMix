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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.json.JSONException;
import org.primefaces.model.tagcloud.DefaultTagCloudItem;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudModel;
import recipemix.beans.*;
import recipemix.models.*;

/**
 * This bean is used to view a recipe
 *
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@ManagedBean(name="viewRecipeBean")
@ViewScoped
public class ViewRecipeBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("ViewRecipeBean");
    
    // =======================================
    // =            EJBs                     =
    // =======================================
    @EJB
    private RecipeEJB recipesEJB;
    @EJB
    private RecipeRatingEJB ratingEJB;
    @EJB
    private TagEJB tagEJB;
    @EJB 
    private RecipeFlagEJB flagEJB;
    @EJB
    private NutritionixEJB nutritionixBean;
    @EJB
    private ImageEJB imageEJB;
    @EJB
    private CommentEJB commentEJB;
    
    // =======================================
    // =            Other Beans              =
    // =======================================
    @Inject
    private RequestParameterManager qm;
    @Inject
    private UserIdentity ui;
    @Inject
    private Search search;
    @Inject
    private ProfessionalStatus professionalStatus;
    
    // =======================================
    // =            Fields                   =
    // =======================================
    private TagCloudModel tags;
    private Comment newComment;
    private Comment deleteComment;
    private RecipeRating newRating;
    private Integer rating;
    private List<Recipe> relatedRecipes;
    private Recipe recipe; // the recipe being viewed
    private LazyCommentDataModel commentModel;
    private Users user;//user making request
    private int totalRatings;
    private String caption;
    private String description;
    
    // =======================================
    // =         Nutrionix Data              =
    // =======================================
    private double calciumPercentage;
    private double ironPercentage;
    private double vitaminAPercentage;
    private double vitaminCPercentage;
    private int calories;
    private int caloriesFromFat;
    private int cholesterol;
    private int dietaryFiber;
    private int protein;
    private int saturatedFat;
    private int sodium;
    private int sugars;
    private int totalCarbohydrates;
    private int totalFat;
    private int transFat;

    /**
     * This method is called AFTER the bean is constructed 
     */
    @PostConstruct
    private void init() {
        if(!FacesContext.getCurrentInstance().isPostback()){
            // TODO: view counter ++
        }
        // Get the recipe
        String value = qm.get("recipe");
        if (value != null) {
            recipe = recipesEJB.findRecipe(Integer.parseInt(value));
        }
        
        // init some fields
        tags = new DefaultTagCloudModel();
        newComment = new Comment();
        newRating = new RecipeRating();
        tags = new DefaultTagCloudModel();
        
        if (recipe != null) {
            this.commentModel = new LazyCommentDataModel(recipe, recipesEJB);
            this.totalRatings = ratingEJB.countTotalRatings(recipe);
            Users user = ui.getUser();
            getNutritionixIngredientInfo(recipe);
            recipesEJB.incrementViews(recipe);

            // Get the recipe's tags
            ArrayList<Tag> tagList = new ArrayList<Tag>(recipe.getTags());
            if (!tagList.isEmpty()) {
                ListIterator i = tagList.listIterator();
                while (i.hasNext()) {
                    Tag t = (Tag) i.next();
                    String url = "search.xhtml?searchArg=" + t.getTagName();
                    this.tags.addTag(new DefaultTagCloudItem(t.getTagName(), url, tagEJB.getWeight(t)));
                }
            } else {
                this.tags.addTag(new DefaultTagCloudItem("#tagMe", 1));
            }

            // Get related recipes
            relatedRecipes = search.getSearchRecipes(recipe);

        } //end if recipe is null
        else {
            this.tags.addTag(new DefaultTagCloudItem("#tagMe", 1));
        }
        if (ui.isIsUserAuthenticated()) {
            user = ui.getUser();
        }
    }
    
     /**
     * creates the folder and uploads the image file to the local files
     * @param event
     * @return 
     */
    public void createRecipeImage(FileUploadEvent event) {
        Image newImage = new Image();
        String destination = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        String getParam = qm.get("recipeId");
        Integer recipeID = Integer.parseInt(getParam);
        this.recipe = recipesEJB.findRecipe(recipeID);
        File file = new File(destination+"uploads"+File.separator+"recipe"+File.separator+recipeID);
        String abspath = file.getAbsolutePath()+ File.separator;
        if(!file.exists()){
            if(file.mkdirs());
        }
        //new name of the image
        List <Image> recipeImages = this.recipe.getImageGallery();
        Integer count = recipeImages.size();

        if(this.recipe.getImageGallery().get(0).getImagePath().equalsIgnoreCase("/resources/images/recipe_placeholder.png") && count == 1){
            imageEJB.removeImage(this.recipe.getImageGallery().get(0));
            this.recipe.getImageGallery().remove(0);
        }else {
            count++;
        }
        String newImageName;
        if (event.getFile().getContentType().equalsIgnoreCase("image/jpeg")) {
            newImageName = count + ".jpeg";
        } else if (event.getFile().getContentType().equalsIgnoreCase("image/gif")) {
            newImageName = count + ".gif";
        } else {
            newImageName = count + ".png";
        }
        // Do what you want with the file        
        String newImagePath = "/uploads/recipe/"+recipeID+"/"+newImageName;
        try {
            copyFile(abspath,newImageName, event.getFile().getInputstream());            
            newImage = this.imageEJB.createImage(newImage);
            newImage.setCaption("This is the "+this.recipe.getRecipeName()+"'s recipe picture.");
            newImage.setDescription(newImageName);
            newImage.setRecipe(this.recipe);
            newImage.setImagePath(newImagePath);
            newImage.setImageName(newImageName);
            newImage = this.imageEJB.editImage(newImage);
            this.recipe.getImageGallery().add(newImage);
            this.recipe = recipesEJB.editRecipe(recipe);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Success!", "Your image was uploaded successfully."));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * copy the uploaded file to the destination bit by bit
     * @param destination string path of the destination folder
     * @param fileName string name of the output file
     * @param in inputStream of the file being uploaded
     */
    public void copyFile(String destination, String fileName, InputStream in) {
        try {
            // write the inputStream to a FileOutputStream
            OutputStream out = new FileOutputStream(new File(destination + fileName));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * retreives information from the nutritionix api
     * @param curRecipe 
     */
    public void getNutritionixIngredientInfo(Recipe curRecipe) {

        ArrayList<NutritionixInfo> nutritionixIngredientList = new ArrayList<NutritionixInfo>();
        List<RecipeIngredient> ingredients = this.recipe.getIngredients();

        try {

            // Use the Nutritionix API to lookup nutrition facts info for each one of our ingredients
            for (RecipeIngredient ingredient : ingredients) {
                String ingredientName = ingredient.getIngredient().getIngredientName();
                nutritionixIngredientList.add(nutritionixBean.searchForIngredient(ingredientName, 0, 1));
            }

            // Calculate and set the nutrition attributes for this class
            for (RecipeIngredient ingredient : ingredients) {
                int servings = Math.round(ingredient.getIngredientAmount());
                for (NutritionixInfo ni : nutritionixIngredientList) {
                    this.setCalories(servings * (this.getCalories() + ni.getNfCalories()));
                    this.setCaloriesFromFat(servings * (this.getCaloriesFromFat() + ni.getNfCaloriesFromFat()));
                    this.setCholesterol(servings * (this.getCholesterol() + ni.getNfCholesterol()));
                    this.setDietaryFiber(servings * (this.getDietaryFiber() + ni.getNfDietaryFiber()));
                    this.setProtein(servings * (this.getProtein() + ni.getNfProtein()));
                    this.setSaturatedFat(servings * (this.getSaturatedFat() + ni.getNfSaturatedFat()));
                    this.setSodium(servings * (this.getSodium() + ni.getNfSodium()));
                    this.setSugars(servings * (this.getSugars() + ni.getNfSugars()));
                    this.setTotalCarbohydrates(servings * (this.getTotalCarbohydrates() + ni.getNfTotalCarbohydrate()));
                    this.setTotalFat(servings * (this.getTotalFat() + ni.getNfTotalFat()));
                    this.setTransFat(servings * (this.getTransFat() + ni.getNfTransFat()));

                    this.setCalciumPercentage(servings * (this.getCalciumPercentage() + ni.getNfCalciumDv()));
                    this.setIronPercentage(servings * (this.getIronPercentage() + ni.getNfIronDv()));
                    this.setVitaminAPercentage(servings * (this.getVitaminAPercentage() + ni.getNfVitaminADv()));
                    this.setVitaminCPercentage(servings * (this.getVitaminCPercentage() + ni.getNfVitaminCDv()));
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(ViewRecipeBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ViewRecipeBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(ViewRecipeBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is intended to be used as an actionListener for rating
     */
    public void handleRating() {
        Users rater = ui.getUser();
        newRating = ratingEJB.findByUserAndRecipe(rater, recipe);
        boolean edit = false; // determine whether we need to edit

        // No rating for this user exists
        if (newRating == null && rating > 0 && rating <= 5) {
            newRating = new RecipeRating();
            newRating.setRater(rater);
            newRating.setRecipe(recipe);
            newRating.setRatingDate(new Date().getTime());
        } // A rating exists
        else {
            edit = true;
        }

        switch (rating) {
            case 1:
                this.newRating.setRatingValue(RatingValue.ONE_STAR);
                break;
            case 2:
                this.newRating.setRatingValue(RatingValue.TWO_STARS);
                break;
            case 3:
                this.newRating.setRatingValue(RatingValue.THREE_STARS);
                break;
            case 4:
                this.newRating.setRatingValue(RatingValue.FOUR_STARS);
                break;
            case 5:
                this.newRating.setRatingValue(RatingValue.FIVE_STARS);
                break;
        }// end switch

        if (edit) {
            this.newRating = ratingEJB.editRecipeRating(newRating);
        } else {
            this.newRating = ratingEJB.createRecipeRating(newRating);
        }
    }

    /**
     * For canceling a rating event
     */
    public void oncancel() {
        Users user = ui.getUser();
        newRating = ratingEJB.findByUserAndRecipe(user, recipe);

        if (newRating != null) {
            ratingEJB.removeRecipeRating(newRating);
        }
    }

    /**
     * Creates a comment and redirects back to the recipe page
     *
     * @return the string navigation outcome
     */
    public String doCreateComment() {
        Users commenter = ui.getUser();
        try {
            this.newComment.setRecipe(this.recipe);
            this.newComment.setCommenter(commenter);
            this.newComment.setDateCommented(new Date().getTime());
            List<Comment> c = recipe.getComments();
            c.add(newComment);
            this.recipe.setComments(c);
            recipesEJB.editRecipe(recipe);
        } catch (javax.ejb.EJBAccessException ejbae) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only registered users can post comments."));
        }
        return "/recipe.xhtml?recipe=" + qm.get("recipe");
    }

    /**
     * deletes a comment
     */
    public void doDeleteComment() {
        if (ui.isIsUserAuthenticated()) {
            Users u = ui.getUser();
            if (isEditAuthorized() || ui.isIsAdmin()) {
                try {
                    recipesEJB.removeCommentFromRecipe(recipe, deleteComment);
                    this.commentModel = new LazyCommentDataModel(recipe, recipesEJB);
                } catch (javax.ejb.EJBAccessException ejbae) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only registered users can post comments."));
                }
            }
        }
    }

    /**
     * returns the comment to delete
     * @return 
     */
    public Comment getDeleteComment() {
        return deleteComment;
    }

    /**
     * sets the comment to delete
     * @param deleteComment comment to delete
     */
    public void setDeleteComment(Comment deleteComment) {
        this.deleteComment = deleteComment;
    }
    
    /**
     * returns the new comment that has been submitted
     * @return Comment newComment
     */
    public Comment getNewComment() {
        return newComment;
    }

    /**
     * sets the new comment to newComment
     * @param newComment 
     */
    public void setNewComment(Comment newComment) {
        this.newComment = newComment;
    }

    /**
     * Returns the recipe being viewed
     *
     * @return the Recipe
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * Returns the model that contains the tags for the recipe Use this for
     * PrimeFaces tagCloud component
     *
     * @return the TagCloudModel
     */
    public TagCloudModel getTags() {
        return tags;
    }

    /**
     * Returns true if the recipe has comments, false otherwise
     *
     * @return boolean hasComments
     */
    public boolean hasComments() {
        boolean hasComments = false;
        if (recipe != null) {
            hasComments = this.recipe.getComments() != null && this.recipe.getComments().size() > 0;
        }
        return hasComments;
    }

    /**
     * Used to build a string that contains time in hours, if needed, and
     * minutes
     *
     * @param minutes
     * @return a human-readable String of hours + minutes from the given total
     * minutes
     */
    private String readableTime(Integer minutes) {
        String time = "";
        if (minutes / 60.0 > 1) {
            time = (minutes / 60) + " hrs, "
                    + (minutes % 60) + " min";
        } else {
            time = minutes + " min";
        }
        return time;
    }

    /**
     * Returns a human-readable string representation of the cook time
     *
     * @return String cook time
     */
    public String getReadableCookTime() {
        if (recipe != null) {
            return readableTime(recipe.getCookTime());
        } else {
            return "";
        }
    }

    /**
     * Returns a human-readable string representation of the prep time
     *
     * @return String prep time
     */
    public String getReadablePrepTime() {
        if (recipe != null) {
            return readableTime(recipe.getPrepTime());
        } else {
            return "";
        }
    }

    /**
     * Returns a human-readable string representation of the total time
     *
     * @return String total time
     */
    public String getTotalTime() {
        if (recipe != null) {
            return readableTime(recipe.getCookTime() + recipe.getPrepTime());
        } else {
            return "";
        }
    }

    public boolean isEditAuthorized() {
        Users user = ui.getUser();
        boolean editAuthorized = false;
        if (user != null && recipe != null) {
            editAuthorized = recipe.getCreator().getUserName().equals(user.getUserName())
                    || ui.isIsAdmin();
        }
        return editAuthorized;
    }

    /**
     * returns the rating of this recipe
     * @return integer rating from 1-5
     */
    public Integer getRating() {
        if (recipe != null && ui.isIsUserAuthenticated()) {
            RecipeRating temp = ratingEJB.findByUserAndRecipe(ui.getUser(), recipe);
            if (temp != null) {
                rating = temp.getRatingValue().getValue();
            }
        }
        return rating;
    }

    /**
     * Sets the rating of the recipe being displayed
     * @param rating 
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    /**
     * returns the list of recipes that are related to the current recipe
     * @return 
     */
    public List<Recipe> getRelatedRecipes() {
        return relatedRecipes;
    }

    /**
     * sets the relatedRecipes to a new list of related recipes
     * @param relatedRecipes new list of related recipes
     */
    public void setRelatedRecipes(List<Recipe> relatedRecipes) {
        this.relatedRecipes = relatedRecipes;
    }
    
    /**
     * checks if the related recipes list is empty
     * @return true if it is populated
     *          false if it is not populated or null
     */
    public boolean shouldRenderRelated() {
        if (relatedRecipes != null) {
            return !this.relatedRecipes.isEmpty();
        } else {
            return false;
        }
    }

    /**
     * checks if the user is able to upload
     * @return true if user is authorized
     *          false if the user is not
     */         
    public boolean shouldRenderUpload() {
        if (recipe != null && ui.isIsUserAuthenticated()) {
            return recipe.getCreator().getUserName().equals(ui.getUser().getUserName());
        } else {
            return false;
        }
    }

    /**
     *  returns true if it has been reviewed and false if it has not been
     * reviewed by a professional;
     */
    public boolean isHasAlreadyReviewed() {
        boolean result = false;
        if (ui.isIsUserAuthenticated() && professionalStatus.isIsProfessional()) {
            Users user = ui.getUser();
            if (recipe != null) {
                for (Review rev : recipe.getReviews()) {
                    if (rev.getReviewer().getUserName().equals(user.getUserName())) {
                        result = true;
                        break;
                    }
                }//end for
            }
        }// end value != null
        return result;
    }

    public String getReadableIngredientsList() {
        if (recipe != null) {
            String ingredientsListString = "";
            List<RecipeIngredient> ingredients = this.recipe.getIngredients();
            int ingredientsCount = ingredients.size();
            int i = 1;
            for (RecipeIngredient ingredient : ingredients) {
                if (i != ingredientsCount) {
                    ingredientsListString += ingredient.getIngredient().getIngredientName() + ", ";
                } else {
                    ingredientsListString += ingredient.getIngredient().getIngredientName();
                }
                i++;
            }
            return ingredientsListString;
        } else {
            return "";
        }
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getCaloriesFromFat() {
        return caloriesFromFat;
    }

    public void setCaloriesFromFat(int caloriesFromFat) {
        this.caloriesFromFat = caloriesFromFat;
    }

    public int getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(int totalFat) {
        this.totalFat = totalFat;
    }

    public int getSaturatedFat() {
        return saturatedFat;
    }

    public void setSaturatedFat(int saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    public int getTransFat() {
        return transFat;
    }

    public void setTransFat(int transFat) {
        this.transFat = transFat;
    }

    public int getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(int cholesterol) {
        this.cholesterol = cholesterol;
    }

    public int getTotalCarbohydrates() {
        return totalCarbohydrates;
    }

    public void setTotalCarbohydrates(int totalCarbohydrates) {
        this.totalCarbohydrates = totalCarbohydrates;
    }

    public int getDietaryFiber() {
        return dietaryFiber;
    }

    public void setDietaryFiber(int dietaryFiber) {
        this.dietaryFiber = dietaryFiber;
    }

    public int getSugars() {
        return sugars;
    }

    public void setSugars(int sugars) {
        this.sugars = sugars;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public double getVitaminAPercentage() {
        return vitaminAPercentage;
    }

    public void setVitaminAPercentage(double vitaminAPercentage) {
        this.vitaminAPercentage = vitaminAPercentage;
    }

    public double getVitaminCPercentage() {
        return vitaminCPercentage;
    }

    public void setVitaminCPercentage(double vitaminCPercentage) {
        this.vitaminCPercentage = vitaminCPercentage;
    }

    public double getCalciumPercentage() {
        return calciumPercentage;
    }

    public void setCalciumPercentage(double calciumPercentage) {
        this.calciumPercentage = calciumPercentage;
    }

    public double getIronPercentage() {
        return ironPercentage;
    }

    public void setIronPercentage(double ironPercentage) {
        this.ironPercentage = ironPercentage;
    }

    public int getSodium() {
        return sodium;
    }

    public void setSodium(int sodium) {
        this.sodium = sodium;
    }
    
    /**
     * Returns the total number of images for this recipe
     * @return - the gallery size
     */
    public int getGallerySize(){
        int size = 0;
        if(recipe != null){
            size = recipe.getImageGallery().size();
        }
        return size;
    }
   
    /**
     * Returns true if the user has already favorited this recipe, false otherwise
     * @return true if already favorited, false otherwise
     */
    public boolean isAlreadyFavorited(){
        if(ui.isIsUserAuthenticated()){
            Users user = ui.getUser();
            if(user.getFavorites() != null){
                return user.getFavorites().contains(recipe);
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    /**
     * Return the Lazy data model for comments
     * @return 
     */
    public LazyCommentDataModel getCommentModel() {
        return commentModel;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
    
    
     /**
     * Returns true if the user is the creator for this recipe
     *
     * @return boolean
     */
    public boolean isIsCreator() {
        if (ui.isIsUserAuthenticated() && recipe != null) {
            if(recipe.getCreator() == user)
                return true;
            else
                return false;
        } else {
            return false;
        }
    }  
}
