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
package recipemix.models;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The Recipe entity is the main recipe entity that will hold
 * all attributes, named queries, and related operations of a
 * recipe. Each instance of Recipe is also associated with Comment/Review,
 * Groups, Image, RecipeIngredient, RecipeStep, Tag, Unit, and Users.
 * @author Alex Chavez <alex@alexchavez.net>
 * @author Steven Paz <steve.a.paz@gmail.com>
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"RECIPE_NAME"}))
@NamedQueries({
    @NamedQuery(name = Recipe.FIND_ALL_RECIPES, query = "SELECT r FROM Recipe r"),
    @NamedQuery(name = Recipe.FIND_ALL_COMMENTS, query = "SELECT c FROM Recipe r JOIN r.comments c WHERE c.recipe = :recipe"),
    @NamedQuery(name = Recipe.FIND_RECIPES_BY_USERNAME, query= "SELECT r FROM Recipe r INNER JOIN r.creator c WHERE c.userName = :userName" ),
    @NamedQuery(name = Recipe.FIND_RECIPES_SINCE_DATE, query = "SELECT r FROM Recipe r WHERE r.recipeCreatedTimestamp > :cutOffDate"),
    @NamedQuery(name = Recipe.FIND_RECIPES_BY_NAME, query = "SELECT r FROM Recipe r WHERE LOWER (r.recipeName) = :recipeName"),
    @NamedQuery(name = Recipe.FIND_RECIPES_BY_LIKE_NAME, query = "SELECT r FROM Recipe r WHERE LOWER (r.recipeName) LIKE :recipeLikeName"),
    @NamedQuery(name = Recipe.FIND_RECIPES_BY_TAG, query = "SELECT r FROM Recipe r INNER JOIN r.tags t WHERE t.tagName LIKE :tagLikeName"),
    @NamedQuery(name = Recipe.FIND_RECIPES_BY_GROUP_NAME, query = "SELECT r FROM Recipe r INNER JOIN r.groups g WHERE g.groupName = :groupName")
})
@XmlRootElement
public class Recipe implements Serializable {
    private static final long serialVersionUID = 1L;
    // ===============================================
    // =            Queries                          =
    // ===============================================
    public static final String FIND_ALL_RECIPES = "findAllRecipes";
    public static final String FIND_ALL_COMMENTS = "findAllRecipeComments";
    public static final String FIND_RECIPES_BY_USERNAME = "findRecipesByUsername";
    public static final String FIND_RECIPES_SINCE_DATE = "findRecipesSinceDate";
    public static final String FIND_RECIPES_BY_NAME = "findRecipesByName";
    public static final String FIND_RECIPES_BY_LIKE_NAME = "findRecipesByLikeName";
    public static final String FIND_RECIPES_BY_TAG = "findRecipesByTag";
    public static final String FIND_RECIPES_BY_GROUP_NAME = "findRecipesByGroupName";

    // ======================================
    // =             Attributes             =
    // ======================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECIPE_ID")
    private Integer recipeId;

    @Column(name = "RECIPE_NAME", nullable = false, length = 50)
    private String recipeName;

    @Column(name = "RECIPE_CREATED_TIMESTAMP")
    private Timestamp recipeCreatedTimestamp;

    @Column(name = "RECIPE_DESCRIPTION", length = 1500)
    private String recipeDescription;

    @Column(name = "PREP_TIME")
    private int prepTime;

    @Column(name = "COOK_TIME")
    private int cookTime;

    @Column(name = "SERVINGS", nullable = false, length = 10)
    private int servings;
    
    @Column
    private long views;
    
    // Calculate attribute
    @Column
    double flagRatio;

    @OneToMany(cascade=CascadeType.MERGE)
    @JoinTable (name="Tag_Recipe", 
            joinColumns=@JoinColumn(name="RECIPE_ID", referencedColumnName="RECIPE_ID"),
            inverseJoinColumns={@JoinColumn(name="TAG_NAME", referencedColumnName="TAG_NAME")})
    private List<Tag> tags;
    
    @ManyToMany(mappedBy = "favorites")
    private List<Users> favoriters;
 
    @ElementCollection
    @CollectionTable(name="RECIPE_STEP",
            joinColumns=@JoinColumn(name="RECIPE_ID"))
    private List<RecipeStep> instructions;

    @ElementCollection
    @CollectionTable(name="RECIPE_INGREDIENTS",
            joinColumns=@JoinColumn(name="RECIPE_ID"))
    private List<RecipeIngredient> ingredients;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<RecipeRating> ratings;
    
    @OneToMany(mappedBy = "flaggedRecipe", cascade = CascadeType.ALL)
    private List<RecipeFlag> flags;
    
    @OneToOne
    @JoinColumn(name = "USER_NAME", nullable = true)
    private Users creator;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany (mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<Image> imageGallery;
    
    @ManyToMany (mappedBy = "recipes", cascade = CascadeType.ALL)
    private List<Groups> groups;

    // ======================================
    // =            Constructors            =
    // ======================================

    /**
     * Default constructor; instantiates a new instance of recipe with
     * the current time.
     */
    public Recipe() {
        this.recipeCreatedTimestamp = new Timestamp(System.currentTimeMillis());
        this.ingredients = new ArrayList<RecipeIngredient>();
        this.instructions = new ArrayList<RecipeStep>();
        this.flagRatio = 0;
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================

    /**
    * Gets the ID of a recipe.
    * @return the ID of a recipe.
    */
    public Integer getRecipeId() {
        return this.recipeId;
    }

    /**
     * Sets the ID of a recipe
     * @param recipeId 
     */
    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    /**
     * Returns the name of the recipe
     * @return the string name of the recipe
     */
    public String getRecipeName() {
        return recipeName;
    }

    /**
     * Sets the name of the recipe
     * @param [recipeName] the new string name of the recipe
     */
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    /**
     * Returns the string description of the Recipe
     * @return the string description
     */
    public String getRecipeDescription() {
        return recipeDescription;
    }

    /**
     * Sets the string description of the Recipe
     * @param [recipeDescription] the new string name
     */
    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
    }

    /**
     * Gets the integer preparation time for the recipe
     * @return the int prep time
     */
    public int getPrepTime() {
        return prepTime;
    }

    /**
     * Sets the integer preparation time for the recipe
     * @param [prepTime] the new int prep time
     */
    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    /**
     * Gets the integer cook time for the recipe
     * @return the int cook time
     */
    public int getCookTime() {
        return cookTime;
    }

    /**
     * Sets the integer cook time for the recipe
     * @param [cookTime] the int cook time
     */
    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    /**
     * Gets the int number of servings
     * @return the int servings
     */
    public int getServings() {
        return servings;
    }

    /**
     * Sets the int number of servings
     * @param [servings] the new int number of servings
     */
    public void setServings(int servings) {
        this.servings = servings;
    }

    /**
    * Gets the user who created the recipe.
    * @return the User object of the user who created this recipe.
    */
    public Users getCreator() {
        return creator;
    }

    /**
    * Sets the user who created the recipe.
    * @param [creater] the User who created the recipe.
    */
    public void setCreator(Users creator) {
        this.creator = creator;
    }

    /**
    * Gets the timestamp of when the recipe was created.
    * @return the timestamp of the instance a recipe was created.
    */
    public Timestamp getRecipeCreatedTimestamp() {
        return recipeCreatedTimestamp;
    }

    /**
    * Sets the timestamp of a recipe when it is created.
    * @param [recipeCreatedTimestamp] the timestamp that will be set for a recipe.
    */
    public void setRecipeCreatedTimestamp(Timestamp recipeCreatedTimestamp) {
        this.recipeCreatedTimestamp = recipeCreatedTimestamp;
    }

    /**
    * Gets a list of tags for a recipe.
    * @return a list of recipe tags.
    */
    @XmlTransient
    public List<Tag> getTags() {
        return tags;
    }
    
    /**
    * Sets a list of tags for an associated recipe.
    * @param [tags] a list of tags of a recipe to be set.
    */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
    * Gets a list of instructions for a recipe.
    * @return a list of recipe instructions.
    */
    public List<RecipeStep> getInstructions() {
        return instructions;
    }

    /**
    * Sets a list of instructions for an associated recipe.
    * @param [instructions] a new list of instructions to be set for a recipe.
    */
    public void setInstructions(List<RecipeStep> instructions) {
        this.instructions = instructions;
    }

    /**
    * Gets a list of ingredients for an associated recipe.
    * @return a list of recipe ingredients.
    */
    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    /**
    * Sets a list of new instructions for an associated recipe.
    * @param [ingredients] the list of ingredients to be set.
    */
    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    /**
    * Gets a list of ratings for an associated recipe.
    * @return a list of recipe ratings.
    */
    @XmlTransient
    public List<RecipeRating> getRatings() {
        return ratings;
    }

    /**
    * Sets a new list of ratings for a recipe.
    * @param [ratings] a list of recipe ratings to be set.
    */
    public void setRatings(List<RecipeRating> ratings) {
        this.ratings = ratings;
    }

    /**
    * Gets a list of comments associated with a recipe.
    * @return a list of recipe comments.
    */
    @XmlTransient
    public List<Comment> getComments() {
        return comments;
    }

    /**
    * Sets a new list of comments for an associated recipe.
    * @param [comments] the list of recipe comments to be set.
    */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    /**
    * Gets a list of reviews for an associated recipe.
    * @return a list of recipe reviews.
    */
    @XmlTransient
    public List<Review> getReviews() {
        return reviews;
    }

    /**
    * Sets a new list of reviews for an associated recipe.
    * @param [reviews] the list of recipe reviews to be set.
    */
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    /**
    * Gets a list of Images for this recipe.
    * @return a list of Images
    */
    @XmlTransient
    public List<Image> getImageGallery() {
        return imageGallery;
    }

    /**
    * Sets a new list of Images for this recipe
    * @param [imageGallery] the list Images
    */
    public void setImageGallery(List<Image> imageGallery) {
        this.imageGallery = imageGallery;
    }

    /**
    * Gets a list of groups for an associated recipe.
    * @return a list of recipe groups.
    */
    @XmlTransient
    public List<Groups> getGroups() {
        return groups;
    }

    /**
    * Sets a list of groups for an associated recipe.
    * @param [groups] a list of recipe groups to be set.
    */
    public void setGroups(List<Groups> groups) {
        this.groups = groups;
    }
    
    /**
     * Assures that the recipe id does not return null
     * @return 
     */
    @Override
    public int hashCode(){
        if (recipeId == null) return 0;
        else return recipeId;
    }

    /**
     * returns the recipe entity as a string
     * @param obj the recipe object that will be expressed as a string
     * @return  string value of the recipe entity
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Recipe other = (Recipe) obj;
        if (this.recipeId != other.recipeId && (this.recipeId == null || !this.recipeId.equals(other.recipeId))) {
            return false;
        }
        return true;
    }

    /**
     * Returns the list of flags for this recipe
     * @return the list of flags
     */
    @XmlTransient
    public List<RecipeFlag> getFlags() {
        return flags;
    }

    /**
     * Sets the list of flags for this recipe
     * @param flags 
     */
    public void setFlags(List<RecipeFlag> flags) {
        this.flags = flags;
    }

    /**
     * Returns the list of users that have favorited this recipe
     * @return the list of favoriters
     */
    @XmlTransient
    public List<Users> getFavoriters() {
        return favoriters;
    }

    /**
     * Sets the list of users who have favorited this recipe
     * @param favoriters 
     */
    public void setFavoriters(List<Users> favoriters) {
        this.favoriters = favoriters;
    }

    /**
     * The number of page hits for this recipe
     * @return num views
     */
    public long getViews() {
        return views;
    }

    /**
     * Set the number of page hits
     * @param views 
     */
    public void setViews(long views) {
        this.views = views;
    }

    /**
     * returns the ratio of flags to total views of the page
     * @return double flagRatio
     */
    public double getFlagRatio() {
        return flagRatio;
    }

    /**
     * 
     * @param flagRatio 
     */
    public void setFlagRatio(double flagRatio) {
        this.flagRatio = flagRatio;
    }
    
}
