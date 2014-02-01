/*
 * Copyright (C) 2013 Alex Chavez <alex@alexchavez.net>, Jairo Lopez <jlopez@csulbmaes.org>,
 * Steven Paz <steve.a.paz@gmail.com>, Gustavo Rosas <gustavoscrib@yahoo.com>
 * 
 * RecipeMix ALL RIGHTS RESERVED
 * 
 * This program is distributed to users in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package recipemix.models;

import java.io.Serializable;

/**
 * The GroupFlagId class is a class that helps uniquely identify a
 * groupFlag by combining the id of a group and the id of the
 * flagger (User).
 * 
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
public class GroupFlagId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    private Integer flaggedGroup; //should match Recipe PK's data type
    private String flagger;

    // ======================================
    // =          Getters & Setters         =
    // ======================================
    public Integer getFlaggedGroup() {
        return flaggedGroup;
    }

    public void setFlaggedGroup(Integer flaggedGroup) {
        this.flaggedGroup = flaggedGroup;
    }
    

    public String getFlagger() {
        return flagger;
    }

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
        hash = 67 * hash + (this.flaggedGroup != null ? this.flaggedGroup.hashCode() : 0);
        hash = 67 * hash + (this.flagger != null ? this.flagger.hashCode() : 0);
        return hash;
    }

    /**
     * Checks the equality of obj to this GroupFlagId
     * @param obj object to be compared to GroupFlagId
     * @return boolean true if obj is equal to the GroupFlagId
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GroupFlagId other = (GroupFlagId) obj;
        if ((this.flaggedGroup == null) ? (other.flaggedGroup != null) : !this.flaggedGroup.equals(other.flaggedGroup)) {
            return false;
        }
        if ((this.flagger == null) ? (other.flagger != null) : !this.flagger.equals(other.flagger)) {
            return false;
        }
        return true;
    }   
}
