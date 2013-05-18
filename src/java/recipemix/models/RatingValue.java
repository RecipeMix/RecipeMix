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

/**
 * Enumerated values for the ratings.
 * @author Gustavo Rosas <grgustavorosas@gmail.com>
 */
public enum RatingValue {
    ONE_STAR(1), TWO_STARS(2), THREE_STARS(3),
    FOUR_STARS(4), FIVE_STARS(5);
    
    private final int value;
    
    /**
     * Initializes RatingValue with value
     * @param value integer value for a rating
     */
    private RatingValue(int value){
        this.value = value;
    }
    
    /**
     * Returns the value of the RatingValue
     * @return integer value of RatingValue
     */
    public Integer getValue(){
        return this.value;
    }
}
