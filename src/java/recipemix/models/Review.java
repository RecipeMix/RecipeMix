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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Review entity will save any review that a professional will create
 * The Review is associated with a User and a Recipe.
 * @author Gustavo Rosas <grgustavorosas@gmail.com>
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@Entity
@Table
@XmlRootElement
public class Review implements Serializable {
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column (nullable = false, length = 100)
    private String title;
    
    //Longer than normal comments
    @Column (nullable = false, length = 5000)
    private String body;
    
    @ManyToOne
    @JoinColumns({
        @JoinColumn (name = "RECIPE_ID", referencedColumnName = "RECIPE_ID"),
        @JoinColumn (name = "RECIPE_NAME", referencedColumnName = "RECIPE_NAME")
    })
    private Recipe recipe;
    
    @ManyToOne
    @JoinColumn(name = "USER_NAME")
    private Users reviewer;
    
    @Column (name = "DATE_REVIEWED")
    private long dateReviewed;
    
    // ======================================
    // =            Constructors            =
    // ======================================
    
    /**
     * Initializes a simple instance of Review
     */
    public Review() {
    }
    
    // ======================================
    // =          Getters & Setters         =
    // ======================================

    /**
     * Returns review title
     * @return title of review
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of review to title
     * @param title new string title of review
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the body of the reviw
     * @return body of review
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the body of review to body
     * @param body new string body of review
     */
    public void setBody(String body) {
        this.body = body;
    }
    
    /**
     * Returns id of review
     * @return integer id of review
     */
    public Integer getId() {
        return id;
    }

    /**
     * Returns the recipe associated with this review
     * @return 
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * Sets the recipe for this review
     * @param recipe 
     */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    /**
     * Returns the User that made the review
     * @return 
     */
    public Users getReviewer() {
        return reviewer;
    }

    /**
     * Sets the User reviewer
     * @param reviewer 
     */
    public void setReviewer(Users reviewer) {
        this.reviewer = reviewer;
    }

    /**
     * Returns the (long) value of the Date reviewed
     * @return 
     */
    public long getDateReviewed() {
        return dateReviewed;
    }

    /**
     * Sets the date of the review
     * @param dateReviewed (long)
     */
    public void setDateReviewed(long dateReviewed) {
        this.dateReviewed = dateReviewed;
    }
    
    
    
}
