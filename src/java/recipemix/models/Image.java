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
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Image entity holds the path to an image that may be used by Recipes, 
 * groups, and users.
 * @author Gustavo Rosas <grgustavorosas@gmail.com>
 */

@Entity
@Table (name = "IMAGE")
@NamedQueries ({
    @NamedQuery (name = Image.FIND_ALL_IMAGES, query = "SELECT i FROM Image i"),
    @NamedQuery (name = Image.FIND_IMAGES_BY_RECIPE_ID, query = "SELECT i FROM Image i WHERE i.recipe.recipeId = :recipeId"),
    //the find image by group id query works
    @NamedQuery (name = Image.FIND_IMAGES_BY_GROUP_ID, query = "SELECT i FROM Image i WHERE i.groups.groupID = :groupID"),
    //the find image by username query works
    @NamedQuery (name = Image.FIND_IMAGE_BY_USERNAME, query = "SELECT i FROM Image i WHERE i.imageOwner.userName = :imageOwner"),
    //the find image by path query works
    @NamedQuery (name = Image.FIND_IMAGE_BY_PATH, query = "SELECT i FROM Image i WHERE i.imagePath = :imagePath")
})
@XmlRootElement
public class Image implements Serializable {
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    public static final String FIND_ALL_IMAGES = "findAllImages";
    public static final String FIND_IMAGE_BY_USERNAME = "findImageByUsername";
    public static final String FIND_IMAGES_BY_GROUP_ID = "findImagesByGroupId";
    public static final String FIND_IMAGES_BY_RECIPE_ID = "findImagesByRecipeId";
    public static final String FIND_IMAGE_BY_PATH = "findImageByPath";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Integer imageId;
    
    @Column (name = "IMAGE_PATH")
    private String imagePath;
    
    @Column
    private String imageName;

    @Column
    private String description;
    
    @Column (name = "CAPTION")
    private String caption;
    
    //needed
    @OneToOne(mappedBy = "image")
    private Groups groups;
    //needed
    @OneToOne(mappedBy = "image")
    private Users imageOwner;
    
    @ManyToOne
    @JoinColumn(name = "RECIPE_ID")
    private Recipe recipe;

    // ======================================
    // =            Constructors            =
    // ======================================
    
    /**
     * Initializes a simple instance of Image
     */
    public Image() {
        this.caption = "Default image";
    }
    
    // ======================================
    // =          Getters & Setters         =
    // ======================================

    public int getImageId() {
        return imageId;
    }    
    /**
     * Returns the image path to the image
     * @return imagePath for Image
     */
    public String getImagePath() {
        return imagePath;
    }
    
    /**
     * Sets the path to the image path
     * @param imagePath new string path to an image
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Returns the group associated with the image
     * @return groups of image
     */
    public Groups getGroups() {
        return groups;
    }

    /**
     * Sets the group of the image to groups
     * @param groups new Groups of Image
     */
    public void setGroups(Groups groups) {
        this.groups = groups;
    }

    /**
     * Returns the user who uses this image in their profile
     * @return imageOwner of Image
     */
    public Users getImageOwner() {
        return imageOwner;
    }

    /**
     * Sets user who uses this image as their own to imageOwner
     * @param imageOwner Users new imageOnwer of Image
     */
    public void setImageOwner(Users imageOwner) {
        this.imageOwner = imageOwner;
    }    

    /**
     * Returns the recipe which the image belongs to
     * @return recipe owner of the image
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * Sets the recipe that uses this image to be recipe
     * @param recipe new Recipe of this image
     */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    /**
     * Returns the caption that describes the image
     * @return caption of the image
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption of the image to caption
     * @param caption description of the image
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
    /**
     * Returns the name of the image
     * @return imageName String name of the image
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Sets the image name to imageName
     * @param imageName String new name of the image
     */
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    /**
     * sets the id of the image 
     * @param imageId 
     */
    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    /**
     * return the description of the image
     * @return String description of image
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the image to the new description
     * @param description string description for the image
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
