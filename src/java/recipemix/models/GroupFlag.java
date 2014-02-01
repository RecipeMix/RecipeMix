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
 * 
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Entity
@Table(name = "GROUP_FLAG")
@IdClass(GroupFlagId.class)
@XmlRootElement
public class GroupFlag implements Serializable {
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @ManyToOne
    @JoinColumn (name = "GROUP_ID")
    private Groups flaggedGroup;

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
    /**
     * sets the flagging date to flaggingDate
     * @param flaggingDate date when the flagging took place as a long integer
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

    /**
     * returns the group that is flagged
     * @return Groups flaggedGroup
     */
    public Groups getFlaggedGroup() {
        return flaggedGroup;
    }

    /**
     * sets the flagged
     * @param flaggedGroup 
     */
    public void setFlaggedGroup(Groups flaggedGroup) {
        this.flaggedGroup = flaggedGroup;
    }

    
}
