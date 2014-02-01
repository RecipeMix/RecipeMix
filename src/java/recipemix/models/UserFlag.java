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
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The user flag entity holds the information to identify the user who has been flagged
 * and the user who has flagged as well as the date expressed as a long integer
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Entity
@Table(name = "USER_FLAG")
@IdClass(UserFlagId.class)
@XmlRootElement
public class UserFlag implements Serializable {
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @ManyToOne
    @JoinColumn (name = "OFFENDER_USER_NAME")
    private Users flaggedUser;

    @Id
    @ManyToOne
    @JoinColumn(name = "FLAGGER_USER_NAME")
    private Users flagger;
    
    @Column(name = "FLAGGING_DATE")
    private long flaggingDate;

    // ======================================
    // =          Getters & Setters         =
    // ======================================
    public Users getFlaggedUser() {
        return flaggedUser;
    }

    public void setFlaggedUser(Users flaggedUser) {
        this.flaggedUser = flaggedUser;
    }
    
    public Users getFlagger() {
        return flagger;
    }

    public void setFlagger(Users flagger) {
        this.flagger = flagger;
    }
    
    public long getFlaggingDate() {
        return flaggingDate;
    }

    public void setFlaggingDate(long flaggingDate) {
        this.flaggingDate = flaggingDate;
    }
}