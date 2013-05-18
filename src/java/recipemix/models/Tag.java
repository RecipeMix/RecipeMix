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
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The Tag entity holds only the name of the tag. Tag is associated with 
 * recipes and groups.
 * @author Gustavo Rosas <grgustavorosas@gmail.com>
 */
@Entity
@Table(name = "TAG")
@XmlRootElement
public class Tag implements Serializable{

    // ======================================
    // =             Attributes             =
    // ======================================

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "TAG_NAME" , nullable = false, length = 50)
    private String tagName;
    
    // ======================================
    // =             Constructor            =
    // ======================================

    /**
     * Initializes a simple instance of Tag
     */
    public Tag() {
    }

    /**
     * Initializes an instance of Tag with tagName
     * @param tagName string for tag name
     */
    public Tag(String tagName) {
        this.tagName = tagName;
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================

    /**
     * Returns tagName of tag
     * @return string tagName of tag
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Sets the tag name to tagName
     * @param tagName new string for tag name
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    
}
