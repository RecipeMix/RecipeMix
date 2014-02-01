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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Ingredient entity will hold the name and id of an ingredient.
 * Ingredient is used by RecipeIngredient.
 * @author Alex Chavez <alex@alexchavez.net>
 * @author Gustavo Rosas <grgustavorosas@gmail.com>
 */

@Entity
@Table(name = "INGREDIENT")
@NamedQueries({
    @NamedQuery(name = Ingredient.FIND_ALL_INGREDIENTS, query = "SELECT i FROM Ingredient i"),
    @NamedQuery(name = Ingredient.FIND_INGREDIENT_BY_NAME, query = "SELECT i FROM Ingredient i WHERE LOWER(i.ingredientName) = LOWER(:searchName)" )
})
@XmlRootElement
public class Ingredient implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INGREDIENT_ID")
    private Integer ingredientId;

    @Column(name = "INGREDIENT_NAME", nullable = false)
    private String ingredientName;
    
    @Column(nullable=false)
    private int count;
    // ===============================================
    // =            Queries                          =
    // ===============================================
    public static final String FIND_ALL_INGREDIENTS = "findAllIngredients";
    public static final String FIND_INGREDIENT_BY_NAME = "findIngredientByName";
    
    //TODO: "Alternate" ingredients for substitution
    
    // ======================================
    // =            Constructors            =
    // ======================================

    /**
     * Initializes a simple instance of Ingredient
     */
    public Ingredient() {

    }

    /**
     * constructor with parameter
     * @param ingredientName
     */
    public Ingredient(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================

    /**
     * returns the ingredientName
     * @return this.ingredientName
     */
    public String getIngredientName() {
        return ingredientName;
    }

    /**
     * The ingredientName of this entity will be changed to the parameter ingredientName
     * @param ingredientName
     */
    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    /**
     * Returns id of the ingredient
     * @return ingredientId of ingredient
     */
    public Integer getIngredientId() {
        return ingredientId;
    }

    /**
     * Sets the ingredient id of ingredient to IingredientId
     * @param ingredientId new integer of ingredientId of Ingredient
     */
    public void setIngredientId(Integer ingredientId) {
        this.ingredientId = ingredientId;
    }

    /**
     * returns the integer count of ingredient
     * @return int count
     */
    public int getCount() {
        return count;
    }

    /**
     * sets the count of the ingredient
     * @param count integer 
     */
    public void setCount(int count) {
        this.count = count;
    }
}
