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
 * The UserFlagId class is a class that helps uniquely identify a
 * userFlag by combining the id of a User (offender) and the id of the
 * flagger (User).
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
public class UserFlagId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    private String flaggedUser; //should match Users PK's data type
    private String flagger;

    // ======================================
    // =          Getters & Setters         =
    // ======================================
    
    /**
     * returns the username of the flagged user
     * @return flagged user username
     */
    public String getFlaggedUser() {
        return flaggedUser;
    }

    /**
     * Sets the user whom has been flagged
     * @param flaggedUser username of the flagged user
     */
    public void setFlaggedUser(String flaggedUser) {
        this.flaggedUser = flaggedUser;
    }
    
    /**
     * gets the username of the flagger
     * @return flagger username
     */
    public String getFlagger() {
        return flagger;
    }

    /**
     * sets the flagger to flagger
     * @param flagger String username of the flagger
     */
    public void setFlagger(String flagger) {
        this.flagger = flagger;
    }
    
    /**
     * Hashes both the flaggedUser and the flagger
     * @return hash of the flaggedUser and flagger
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.flaggedUser != null ? this.flaggedUser.hashCode() : 0);
        hash = 67 * hash + (this.flagger != null ? this.flagger.hashCode() : 0);
        return hash;
    }

    /**
     * Checks the equality of obj to this UserFlagId
     * @param obj object to be compared to UserFlagId
     * @return boolean true if obj is equal to the UserFlagId
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserFlagId other = (UserFlagId) obj;
        if ((this.flaggedUser == null) ? (other.flaggedUser != null) : !this.flaggedUser.equals(other.flaggedUser)) {
            return false;
        }
        if ((this.flagger == null) ? (other.flagger != null) : !this.flagger.equals(other.flagger)) {
            return false;
        }
        return true;
    }   
}
