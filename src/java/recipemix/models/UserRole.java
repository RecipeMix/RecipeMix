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
 * The UserRole embeddable entity holds a role.
 * @author Gustavo Rosas <grgustavorosas@gmail.com>
 */
@Embeddable @Access(AccessType.FIELD)
public class UserRole implements Serializable {
    
    // ======================================
    // =             Attributes             =
    // ======================================
    private static final long serialVersionUID = 1L;

    @Column (name = "USER_ROLE", nullable = false)
    private Role userRole;
    
    // ======================================
    // =            Constructors            =
    // ======================================
    
    /**
     * Initializes a simple instance of UserRole
     */
    public UserRole() {
    }
    
    // ======================================
    // =          Getters & Setters         =
    // ======================================
    
    /**
     * Returns userRole
     * @return Role userRole
     */
    public Role getUserRole() {
        return userRole;
    }

    /**
     * Sets the user role to userRole
     * @param userRole new Role for userRole
     */
    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }
    
}
