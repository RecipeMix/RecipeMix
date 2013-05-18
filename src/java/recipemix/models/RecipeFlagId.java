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

/**
 * The RecipeFlagId class is a class that helps uniquely identify a
 * recipeFlag by combining the id of a recipe and the id of the
 * flagger (User).
 * @author Alex Chavez <alex@alexchavez.net>
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
public class RecipeFlagId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    private Integer flaggedRecipe; //should match Recipe PK's data type
    private String flagger;

    // ======================================
    // =          Getters & Setters         =
    // ======================================
    
    /**
     * returns the flagged recipe's id
     * @return integer value of the flagged recipe
     */
    public Integer getFlaggedRecipe() {
        return flaggedRecipe;
    }

    /**
     * sets the flagged recipe's id to flaggedRecipe
     * @param flaggedRecipe integer id of the recipe flagged
     */
    public void setFlaggedRecipe(Integer flaggedRecipe) {
        this.flaggedRecipe = flaggedRecipe;
    }
   
    /**
     * returns the username of the user who flagged the recipe
     * @return string username of flagger
     */
    public String getFlagger() {
        return flagger;
    }

    /**
     * sets flagger to the username of the user who flagge the recipe
     * @param flagger string value of username of the user who flagged the recipe
     */
    public void setFlagger(String flagger) {
        this.flagger = flagger;
    }
    
    /**
     * Hashes both the recipe and the flagger
     * @return hash of the recipe and flagger
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.flaggedRecipe != null ? this.flaggedRecipe.hashCode() : 0);
        hash = 67 * hash + (this.flagger != null ? this.flagger.hashCode() : 0);
        return hash;
    }

    /**
     * Checks the equality of obj to this RecipeFlagId
     * @param obj object to be compared to RecipeFlagId
     * @return boolean true if obj is equal to the RecipeFlagId
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RecipeFlagId other = (RecipeFlagId) obj;
        if ((this.flaggedRecipe == null) ? (other.flaggedRecipe != null) : !this.flaggedRecipe.equals(other.flaggedRecipe)) {
            return false;
        }
        if ((this.flagger == null) ? (other.flagger != null) : !this.flagger.equals(other.flagger)) {
            return false;
        }
        return true;
    }   
}
