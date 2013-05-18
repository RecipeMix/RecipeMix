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
import java.util.ArrayList;
import javax.persistence.*;

/**
 * The RecipeStep embeddable entity holds information for a step in a recipe.
 * The RecipeStep is embedded and associated with the Recipe entity.
 * @author Alex Chavez <alex@alexchavez.net>
 */
@Embeddable @Access(AccessType.FIELD)
public class RecipeStep implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================

    private static final long serialVersionUID = 1L;

    @Column(name = "STEP_NUMBER", nullable = false)
    private Integer stepNumber;

    @Column(name = "STEP_DESCRIPTION", nullable = false, length = 1500)
    private String stepDescription;
    //needed
    @OneToOne(optional = true)
    @JoinColumn(name="imageStep")
    private Image image;

    // ======================================
    // =            Constructors            =
    // ======================================

    /**
     * Initializes a simple instance of RecipeStep
     */
    public RecipeStep() {
        this.stepDescription = "";
        this.stepNumber = 1;
    }

    /**
     * Initializes an instance of RecipeStep with stepNumber and stepDescription
     * @param stepNumber integer step number
     * @param stepDescription string description of step
     */
    public RecipeStep(Integer stepNumber, String stepDescription) {
        this.stepNumber = stepNumber;
        this.stepDescription = stepDescription;
    }

    /**
     * Initializes an instance of RecipeStep with stepNumber, stepDescription, and imagesPath
     * @param  integer step number
     * @param stepDescription string description of step
     * @param imagesPath arrayList of paths to images
     */
    public RecipeStep(Integer stepNumber, String stepDescription, ArrayList<String> imagesPath) {
        this.stepNumber = stepNumber;
        this.stepDescription = stepDescription;
        //this.imagesPath = imagesPath;
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================

    /**
     * Returns the description of the step
     * @return step description of recipeStep
     */
    public String getStepDescription() {
        return stepDescription;
    }

    /**
     * Sets the description of recipe Step to stepDescription
     * @param stepDescription new string for step description
     */
    public void setStepDescription(String stepDescription) {
        this.stepDescription = stepDescription;
    }

    /**
     * Returns the step number of recipe Step
     * @return integer stepNumber of recipeStep
     */
    public Integer getStepNumber() {
        return stepNumber;
    }

    /**
     * Sets the step number of recipe step to stepNumber
     * @param stepNumber new integer for step number
     */
    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    /**
     * Returns the image of the step
     * @return Image for the step
     */
    public Image getImage() {
        return image;
    }

    /**
     * Sets the image for the recipe step to image
     * @param image new Image for the recipe step
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Returns the recipeStep entity as a string
     * @return string value of recipeStep
     */
    @Override
    public String toString(){
        return stepNumber + ". " + stepDescription;
    }
    
}
