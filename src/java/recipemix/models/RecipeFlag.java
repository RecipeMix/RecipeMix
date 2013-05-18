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
 * This table is a join table between recipes and users that stores the times that 
 * recipes have been flagged by users
 * @author Alex Chavez <alex@alexchavez.net>
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Entity
@Table(name = "RECIPE_FLAG")
@IdClass(RecipeFlagId.class)
@XmlRootElement
public class RecipeFlag implements Serializable {
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @ManyToOne
    @JoinColumn (name = "RECIPE_ID")
    private Recipe flaggedRecipe;

    @Id
    @ManyToOne
    @JoinColumn(name = "USER_NAME")
    private Users flagger;
    
    @Column(name = "FLAGGING_DATE")
    private long flaggingDate;

    // ======================================
    // =          Getters & Setters         =
    // ======================================
    
    /**
     * returns the flagged recipe's id
     * @return integer value of the flagged recipe
     */
    public Recipe getFlaggedRecipe() {
        return flaggedRecipe;
    }

    /**
     * sets the flagged recipe's id to flaggedRecipe
     * @param flaggedRecipe integer id of the recipe flagged
     */
    public void setFlaggedRecipe(Recipe flaggedRecipe) {
        this.flaggedRecipe = flaggedRecipe;
    }
    
    /**
     * returns the username of the user who flagged the recipe
     * @return string username of flagger
     */
    public Users getFlagger() {
        return flagger;
    }

    /**
     * sets flagger to the username of the user who flagge the recipe
     * @param flagger string value of username of the user who flagged the recipe
     */
    public void setFlagger(Users flagger) {
        this.flagger = flagger;
    }
    
    /**
     * returns the date when the flag was done
     * @return date glaggingDate as a long integer
     */
    public long getFlaggingDate() {
        return flaggingDate;
    }

    /**
     * sets the flagging date to flaggingDate
     * @param flaggingDate date when the flagging took place as a long integer
     */
    public void setFlaggingDate(long flaggingDate) {
        this.flaggingDate = flaggingDate;
    }
}
