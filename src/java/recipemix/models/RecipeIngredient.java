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

/**
 * The RecipeIngredient entity holds the needed information for an ingredient to
 * be used in a recipe. Each RecipeIngredient is associated with an Ingredient
 * object.
 *
 * @author Alex Chavez <alex@alexchavez.net>
 * @author Gustavo Rosas <grgustavorosas@gmail.com>
 */
@Embeddable
@Access(AccessType.FIELD)
public class RecipeIngredient implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================
    private static final long serialVersionUID = 1L;
    @JoinColumn(name = "INGREDIENT_ID")
    private Ingredient ingredient;
    @Column(name = "AMOUNT", nullable = false)
    private float ingredientAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "UNIT")
    private Unit measurementUnit;

    // ======================================
    // =            Constructors            =
    // ======================================
    /**
     * Creates a simple instance of RecipeIngredient
     */
    public RecipeIngredient() {
        this.ingredient = new Ingredient();
        this.measurementUnit = Unit.none;
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================
    /**
     * Returns ingredient of this RecipeIngredient
     *
     * @return Ingredient object
     */
    public Ingredient getIngredient() {
        return ingredient;
    }

    /**
     * Sets ingredient of RecipeIngredient to ingredient
     *
     * @param ingredient Ingredient object to be set to ingredient of
     * RecipeIngredient
     */
    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    /**
     * Returns the amount of RecipeIngredient
     *
     * @return float value of amount
     */
    public float getIngredientAmount() {
        return ingredientAmount;
    }

    /**
     * Sets the amount of the RecipeIngredient to ingredientAmount
     *
     * @param ingredientAmount new float value for ingredient amount for
     * RecipeIngredient
     */
    public void setIngredientAmount(float ingredientAmount) {
        this.ingredientAmount = ingredientAmount;
    }

    /**
     * Sets the unit of measurement for RecipeIngredient to measurementUnit
     *
     * @param measurementUnit Unit of measurement
     */
    public void setMeasurementUnit(Unit measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    /**
     * Returns the unit of measurement for the ingredient amount
     *
     * @return measurement unit for RecipeIngredient
     */
    public Unit getMeasurementUnit() {
        return measurementUnit;
    }

    /**
     * returns the recipeIngredient as a string
     * @return string value of recipeIngredient
     */
    @Override
    public String toString() {
        String s = "";
        Float f = ingredientAmount;
        String ingredName = ingredient.getIngredientName();
        if (f % 1 == 0) { // whole number, don't show decimal
            s += f.intValue();
        } else {
            s += f;

        }
        if (measurementUnit == Unit.none) {
            s = s + " " + ingredName;
        } else {
            s = s + " " + measurementUnit + " " + ingredName;
        }
        
        return s;
    }
}
