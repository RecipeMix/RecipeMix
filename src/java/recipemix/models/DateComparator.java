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

import java.util.Comparator;

/**
 * Custom comparator used for sorting recipes
 * @author Steven Paz <steve.a.paz@gmail.com>
 *  
 */
public class DateComparator implements Comparator<Object> {

    /**
     * compares two objects which can be recipe, group, or user
     * @param o1 object  
     * @param o2 object
     * @return int 1 if true -1 if false
     */
    @Override
    public int compare(Object o1, Object o2) {
        if(o1 instanceof Recipe){
            Recipe r1 = (Recipe)o1;
            Recipe r2 = (Recipe)o2;
            return -1 * r1.getRecipeCreatedTimestamp().compareTo(r2.getRecipeCreatedTimestamp());
        }
        else if(o1 instanceof Groups){
            Groups g1 = (Groups)o1;
            Groups g2 = (Groups)o2;
            return -1 * g1.getGroupCreatedTimestamp().compareTo(g2.getGroupCreatedTimestamp());
        }
        else if(o1 instanceof Users){
            Users u1 = (Users)o1;
            Users u2 = (Users)o2;
            return -1 * u1.getDateRegistered().compareTo(u2.getDateRegistered());
        }
        else{
            return 0;
        }
    }
}