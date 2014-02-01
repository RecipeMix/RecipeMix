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
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The User entity holds the information of a registered user.
 * A User is associated with comments, groups, image, newsPost, professional, recipes,
 * recipeRating, review, and securityGroup.
 * @author Alex Chavez <alex@alexchavez.net>
 * @author Jairo Lopez <jairo.lope00@gmail.com>
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@Entity(name="Users")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
@NamedQueries({
    @NamedQuery(name = Users.FIND_USERS_SINCE_DATE, query = "SELECT u FROM Users u WHERE u.dateRegistered > :cutOffDate"),
    @NamedQuery(name = Users.FIND_USERS_BY_LIKE_NAME, query = "SELECT u FROM Users u WHERE LOWER (u.userName) LIKE :userLikeName OR LOWER (u.firstName) LIKE :userLikeName ")
})
@XmlRootElement
public class Users implements Serializable {
    // ===============================================
    // =            Queries                          =
    // ===============================================
    public static final String FIND_USERS_BY_LIKE_NAME = "findUsersByLikeName";
    public static final String FIND_USERS_SINCE_DATE = "findUsersSinceDate";

    // ======================================
    // =             Attributes             =
    // ======================================

    @Column(length = 32, nullable = false)
    private String password;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Id
    @Column(name = "USER_NAME", nullable = false)
    private String userName;

    @Column(name = "DATE_REGISTERED")
    private Timestamp dateRegistered;

    @Column(length = 1500)
    private String about;
    
    @Column(length = 32, nullable = false)
    private String email;
   
    @OneToMany(mappedBy="member")
    private Set<GroupsMembers> userGroups;

    @ManyToMany(mappedBy="users", cascade=CascadeType.ALL)
    private Collection<SecurityGroup> securityGroups;
    
    @ManyToMany
    @JoinTable(name="USERS_FAVORITES",
          joinColumns={@JoinColumn (name = "USER_NAME", referencedColumnName = "USER_NAME")},
          inverseJoinColumns={@JoinColumn(name="RECIPE_ID", referencedColumnName = "RECIPE_ID")})
    private List<Recipe> favorites;
    
    
    @OneToMany (mappedBy = "creator", cascade=CascadeType.ALL)
    private List<Recipe> recipes;
    
    @OneToOne
    private ProfessionalInfo professionalInfo;
    
    @OneToMany (mappedBy = "commenter")
    private List<Comment> comments;
    
    @OneToMany (mappedBy = "reviewer")
    private List<Review> reviews;
    
    @OneToOne(orphanRemoval = true)
    @JoinColumn (name = "IMAGEID")
    private Image image;
    
    @OneToMany (mappedBy = "poster")
    private List<NewsPost> reviewsnewsPosts;
    
    @OneToMany(mappedBy = "rater", cascade=CascadeType.ALL)
    private List<RecipeRating> ratings;
    
    @OneToMany(mappedBy = "flaggedUser", cascade = CascadeType.ALL)
    private List<UserFlag> flags;
    
    @Column
    private long views;
    
    // Calculated attribute
    @Column
    double flagRatio;
    
    // ======================================
    // =            Constructors            =
    // ======================================

    /**
     * Initializes a simple instance of User
     */
    public Users() {
        securityGroups = new HashSet<SecurityGroup>();
        this.dateRegistered = new Timestamp(System.currentTimeMillis());
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================

    /**
     * Returns first name of the user
     * @return string first name of user
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Compares two Users and returns the boolean  validator
     * @param other
     * @return true if the two usernames are the same
     *          false if they are not the same usernames
     */
    public boolean equals(Users other) { 
        return userName.equals(other.userName);
    }

    /**
     * Sets the user's first name to firstName
     * @param firstName new first name string  
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns user's last name
     * @return string last Name of user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name to lastName
     * @param lastName new last name string
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns encrypted password
     * @return string password of user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's current password to password
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the user's username
     * @return string userName of user
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user's current username to userName
     * @param userName new username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns the about section for the user
     * @return string about of user
     */
    public String getAbout() {
        return about;
    }

    /**
     * Sets the about section of user to about
     * @param about new about for user
     */
    public void setAbout(String about) {
        this.about = about;
    }

    /**
     * Checks to see if the User is valid
     * @param confirmPassword
     * @return true if valid, false otherwise
     */
    public boolean isInformationValid() {
        return (firstName != null && 
                lastName != null && 
                password != null);
    }

    /**
     * Returns the collection of a security groups
     * @return collection of securityGroup
     */
    @XmlTransient
    public Collection<SecurityGroup> getSecurityGroups() {
        return securityGroups;
    }

    /**
     * Sets the security groups to a new collection of securityGroups
     * @param securityGroups a new collection of securityGroup
     */
    public void setSecurityGroups(Collection<SecurityGroup> securityGroups) {
        this.securityGroups = securityGroups;
    }

    /**
     * Add a group to the user's set of groups
     * @param group to be added
     */
    public void addsecurityGroup(SecurityGroup group) {
        this.securityGroups.add(group);
    }

    /**
     * Returns the users email
     * @return email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user to email
     * @param email string email of user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the groups for the user
     * @return Set of GroupsMembers
     */
    @XmlTransient
    public Set<GroupsMembers> getUserGroups() {
        return userGroups;
    }

    /**
     * Sets the user groups of the user to userGroups
     * @param userGroups Sett of user groups of user
     */
    public void setUserGroups(Set<GroupsMembers> userGroups) {
        this.userGroups = userGroups;
    }

    /**
     * Returns the recipes for the user
     * @return List of recipes of user
     */
    @XmlTransient
    public List<Recipe> getRecipes() {
        return recipes;
    }

    /**
     * Sets the recipes of the user to receipes
     * @param recipes List of recipes of user
     */
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
    
    /**
     * Returns the comments of the user
     * @return list of comments of user
     */
    @XmlTransient
    public List<Comment> getComments() {
        return comments;
    }

    /**
     * Sets the comments of the user to comments
     * @param comments list of comments of user
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Returns the reviews of the user
     * @return List of reviews of user
     */
    @XmlTransient
    public List<Review> getReviews() {
        return reviews;
    }

    /**
     * Sets the reviews of the user to reviews
     * @param reviews list of reviews of user
     */
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    /**
     * Returns the image of the user
     * @return Image image of user
     */
    public Image getImage() {
        return image;
    }

    /**
     * Sets the image of the user to image
     * @param image Image of user
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Returns the news posts of the user
     * @return List of news post of user
     */
    @XmlTransient
    public List<NewsPost> getReviewsnewsPosts() {
        return reviewsnewsPosts;
    }

    /**
     * Sets the news posts of the user to reviewsnewsPost
     * @param reviewsnewsPost List of news posts of user
     */
    public void setReviewsnewsPosts(List<NewsPost> reviewsnewsPosts) {
        this.reviewsnewsPosts = reviewsnewsPosts;
    }

    /**
     * Returns the ratings the user has given to recipes
     * @return List of recipe ratings of user
     */
    @XmlTransient
    public List<RecipeRating> getRatings() {
        return ratings;
    }

    /**
     * Sets the ratings the user has given to ratings
     * @param ratings list of ratings of user
     */
    public void setRatings(List<RecipeRating> ratings) {
        this.ratings = ratings;
    }

    /**
     * Returns the professional info of the user if any
     * @return professionalInfo of user
     */
    public ProfessionalInfo getProfessionalInfo() {
        return professionalInfo;
    }

    /**
     * Sets the professional information of the user to professionalMamber
     * @param professionalInfo information of user
     */
    public void setProfessionalInfo(ProfessionalInfo professionalInfo) {
        this.professionalInfo = professionalInfo;
    }

    /**
     * Returns this user's favorite recipes
     * @return the list of favorites
     */
    @XmlTransient
    public List<Recipe> getFavorites() {
        return favorites;
    }

    /**
     * Sets this user's favorite recipes
     * @param favorites 
     */
    public void setFavorites(List<Recipe> favorites) {
        this.favorites = favorites;
    }

    /**
     * returns the flags of the user as a list
     * @return List of flags
     */
    public List<UserFlag> getFlags() {
        return flags;
    }

    /**
     * Sets the current user's list of flags to the new list of flags
     * @param flags List of flags to replace the current list
     */
    public void setFlags(List<UserFlag> flags) {
        this.flags = flags;
    }

    /**
     * Returns a time with the date of when the user registered
     * @return the date when the user registered
     */
    public Timestamp getDateRegistered() {
        return dateRegistered;
    }

    /**
     * Sets the the date the user registered to dateRegistered
     * @param dateRegistered the new time of when the user registered
     */
    public void setDateRegistered(Timestamp dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    /**
     * returns the number of times that the people have visited the user's profile
     * @return 
     */
    public long getViews() {
        return views;
    }

    /**
     * Sets the number of views to the new number of views
     * @param views long integer to replace the current views count
     */
    public void setViews(long views) {
        this.views = views;
    }

    /**
     * returns the ratio of flags over the total views of the user
     * @return the flag ration
     */
    public double getFlagRatio() {
        return flagRatio;
    }

    /**
     * sets the flag ratio to the new flag ratio
     * @param flagRatio the new flag ration
     */
    public void setFlagRatio(double flagRatio) {
        this.flagRatio = flagRatio;
    }
}
