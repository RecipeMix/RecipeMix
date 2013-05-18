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
 * The RecipeRating entity holds the information for a rating.
 * The RecipeRating entity is associated with a user and a recipe
 * @author Alex Chavez <alex@alexchavez.net>
 */
@Entity
@Table(name = "RECIPE_RATING")
@XmlRootElement
public class RecipeRating implements Serializable{

    // ======================================
    // =             Attributes             =
    // ======================================

    private static final long serialVersionUID = 1L;
    
    // This was originally going to use a composite PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RATING_ID")
    private int ratingID;
    
    @ManyToOne
    @JoinColumn (name = "RECIPE_ID", nullable = false)
    private Recipe recipe;
    
    @ManyToOne
    @JoinColumn(name = "USER_ID",nullable = false)
    private Users rater;
    
    @Column(name = "RATING_TYPE")
    private String type;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "RATING_VALUE")
    private RatingValue ratingValue;
    
    @Column(name = "RATING_DATE")
    private long ratingDate;

    // ======================================
    // =          Getters & Setters         =
    // ======================================

    /**
     * Returns the rating date as long
     * @return long date of rating
     */
    public long getRatingDate() {
        return ratingDate;
    }

    /**
     * Sets the rating date to ratingDate
     * @param ratingDate new date of the the recipe rating
     */
    public void setRatingDate(long ratingDate) {
        this.ratingDate = ratingDate;
    }

    /**
     * Returns the recipe associated to this rating
     * @return recipe of the rating
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * Sets the recipe that is associated to the rating
     * @param recipe new recipe for the rating
     */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    /**
     * Returns the user that rated
     * @return User of the recipe rating
     */
    public Users getRater() {
        return rater;
    }

    /**
     * Sets the rater for the recipe rating
     * @param rater new user of the rating
     */
    public void setRater(Users rater) {
        this.rater = rater;
    }

    /**
     * Returns the type of the rating
     * @return string type of the rating
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the recipe rating type
     * @param type new string type of the recipe rating
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the value of the recipe rating
     * @return ratingValue of recipe rating
     */
    public RatingValue getRatingValue() {
        return ratingValue;
    }

    /**
     * Sets the value of the recipe rating
     * @param ratingValue of recipe rating
     */
    public void setRatingValue(RatingValue ratingValue) {
        this.ratingValue = ratingValue;
    }
    

}
