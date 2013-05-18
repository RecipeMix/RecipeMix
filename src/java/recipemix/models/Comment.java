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
 * The Comment entity will save any comments and reviews
 * that a user or professional will create; each comment/review
 * is tied to a User and a Recipe.
 * @author Alex Chavez <alex@alexchavez.net>
 */
@Entity
@Table(name = "COMMENT")
@XmlRootElement
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;
    // ======================================
    // =             Attributes             =
    // ======================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Integer commentId;

    @Column(name = "BODY", nullable = false, length = 5000)
    private String body;
    
    // Not a temporal type because those are a headache to deal with
    @Column(name = "DATE_COMMENTED", nullable = false)
    private long dateCommented;

    @ManyToOne
    @JoinColumns({
        @JoinColumn (name = "RECIPE_ID", referencedColumnName = "RECIPE_ID"),
        @JoinColumn (name = "RECIPE_NAME", referencedColumnName = "RECIPE_NAME")
    })
    private Recipe recipe;

    @ManyToOne
    @JoinColumns({
        @JoinColumn (name = "GROUP_ID", referencedColumnName = "GROUP_ID"),
        @JoinColumn (name = "GROUP_NAME", referencedColumnName = "GROUP_NAME")
    })
    private Groups group;
    
    @ManyToOne
    @JoinColumn(name = "USER_NAME")
    private Users commenter;

    // ======================================
    // =            Constructors            =
    // ======================================

    /**
    * Initializes an instance of a comment.
    */
    public Comment() {
        this.body = "";
    }

    /**
    * Initialized an instance of a comment with some text.
    * @param [body] the  main content of a comment.
    */
    public Comment(String body) {
        this.body = body;
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================

    /**
    * Gets the ID of a comment or review.
    * @return the ID of the comment/review to be returned.
    */
    public Integer getCommentId() {
        return commentId;
    }

    /**
    * Gets the body of a comment.
    * @return the body (or message) of a comment/review.
    */
    public String getBody() {
        return body;
    }

    /**
    * Sets the body of a comment or review.
    * @param [body] the new body to be set.
    */
    public void setBody(String body) {
        this.body = body;
    }

    /**
    * Gets an instance of the Recipe objets.
    * @return the instance of a Recipe object
    */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
    * Sets the instance of an existing Recipe object.
    * @param [recipe] the Recipe object to set.
    */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    /**
    * Gets the user who created the comment/review.
    * @return the User object of the user who created the comment/review.
    */
    public Users getCommenter() {
        return commenter;
    }

    /**
    * Sets the user who has created a comment/review.
    * @param [commenter] the User object of the user who created the comment/review.
    */
    public void setCommenter(Users commenter) {
        this.commenter = commenter;
    }

    /**
     * retuns the group where the comment has been posted
     * @return groups 
     */
    public Groups getGroup() {
        return group;
    }

    /**
     * sets the group owner of this comment
     * @param group Group to be set as owner
     */
    public void setGroup(Groups group) {
        this.group = group;
    }

    /**
     * returns the date in long form when the date was commented
     * @return long of date
     */
    public long getDateCommented() {
        return dateCommented;
    }

    /**
     * sets the long depicting the date when the comment was posted
     * @param dateCommented to be set as the new dateCommented
     */
    public void setDateCommented(long dateCommented) {
        this.dateCommented = dateCommented;
    }
}
