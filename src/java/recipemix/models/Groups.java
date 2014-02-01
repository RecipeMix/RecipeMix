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
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A Group is a community created by any user that allows
 * other users to join, share, and discuss various recipes.
 * @author Gustavo Rosas <grgustavorosas@gmail.com>
 * @author Alex Chavez <alex@alexchavez.net>
 * @author Steven Paz <steve.a.paz@gmail.com>
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Entity
@Table(name = "GROUPS")
@NamedQueries({
    @NamedQuery(name = Groups.FIND_ALL_GROUPS, query = "SELECT g FROM Groups g WHERE g.privacy = false"),
    @NamedQuery(name = Groups.FIND_ALL_COMMENTS, query = "SELECT c FROM Groups g JOIN g.comments c WHERE c.group = :group"),
    @NamedQuery(name = Groups.FIND_GROUPS_SINCE_DATE, query = "SELECT g FROM Groups g WHERE g.groupCreatedTimestamp > :cutOffDate"),
    @NamedQuery(name = Groups.FIND_GROUPS_CREATED, query = "SELECT g FROM Groups g WHERE g.owner = :user"),
    @NamedQuery(name = Groups.FIND_GROUPS_BY_NAME, query = "SELECT g FROM Groups g WHERE LOWER (g.groupName) = :groupName AND g.privacy = false"),
    @NamedQuery(name = Groups.FIND_GROUPS_BY_TAG, query = "SELECT g FROM Groups g JOIN g.tags t WHERE t.tagName LIKE :tagLikeName AND g.privacy = false"),
    @NamedQuery(name = Groups.FIND_GROUPS_MANAGED, query = "SELECT g FROM Groups g JOIN g.members m WHERE m.role = :role AND m.member = :user"),
    @NamedQuery(name = Groups.FIND_GROUPS_MEMBERSHIP, query = "SELECT g FROM Groups g JOIN g.members m WHERE m.member = :user"),
    @NamedQuery(name = Groups.FIND_PENDING_MEMBERS, query = "SELECT m FROM Groups g JOIN g.members m WHERE m.isConfirmed = false AND m.group = :group"),
    @NamedQuery(name = Groups.COUNT_PENDING_MEMBERS, query = "SELECT COUNT(m) FROM Groups g JOIN g.members m WHERE m.isConfirmed = false AND m.group = :group"),
    @NamedQuery(name = Groups.FIND_GROUPS_LIKE_NAME, query = "SELECT g FROM Groups g WHERE LOWER (g.groupName) LIKE :groupNamePattern AND g.privacy = false")
})
@XmlRootElement
public class Groups  implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================

    private static final long serialVersionUID = 1L;
    public static final String FIND_ALL_GROUPS = "findAllGroups";
    public static final String FIND_GROUPS_CREATED = "findGroupsCreatedByUser";
    public static final String FIND_GROUPS_MEMBERSHIP = "findGroupsByUser";
    public static final String FIND_GROUPS_BY_NAME = "findGroupsByName";
    public static final String FIND_GROUPS_BY_TAG = "findGroupsByTag";
    public static final String FIND_GROUPS_MANAGED = "findGroupsManaged";
    public static final String FIND_GROUPS_LIKE_NAME = "findGroupsLikeName";
    public static final String FIND_PENDING_MEMBERS = "findPendingMembers";
    public static final String COUNT_PENDING_MEMBERS = "countPendingMembers";
    public static final String FIND_GROUPS_SINCE_DATE = "findGroupsSinceDate";
    public static final String FIND_ALL_COMMENTS = "findAllGroupsComments";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_ID")
    private Integer groupID;

    @Column(name = "GROUP_NAME", nullable = false, length = 20, unique = true)
    private String groupName;
    
    @Column(nullable=false)
    private boolean privacy;
    
    @Column(nullable=false)
    private boolean restricted;

    @Column(name = "GROUP_CREATED_TIMESTAMP")
    private Timestamp groupCreatedTimestamp;
    
    @Column(name = "GROUP_DESCRIPTION", nullable = false, length = 2500)
    private String groupDescription;
    
    @Column
    private long views;
    
    // Calculate attribute
    @Column
    double flagRatio;
     
    @OneToMany(mappedBy = "group")
    private Set<GroupsMembers> members;

    @OneToOne
    @JoinColumn(name= "OWNER", nullable = false)
    private Users owner;

    @OneToMany(cascade=CascadeType.MERGE)
    @JoinTable (name="Tag_Group", 
            joinColumns=@JoinColumn(name="GROUP_ID", referencedColumnName="GROUP_ID"),
            inverseJoinColumns={@JoinColumn(name="TAG_NAME", referencedColumnName="TAG_NAME")})
    private List<Tag> tags;

    @OneToOne (orphanRemoval = true)
    @JoinColumn(name = "IMAGEID")
    private Image image;

    @ManyToMany
    @JoinTable(name="GROUP_RECIPE",
          joinColumns={@JoinColumn (name = "GROUP_ID", referencedColumnName = "GROUP_ID")},
          inverseJoinColumns={@JoinColumn(name="RECIPE_ID", referencedColumnName = "RECIPE_ID")})
    private List<Recipe> recipes;
    
    @OneToMany(mappedBy = "group", cascade=CascadeType.ALL)
    private List<Comment> comments;
    
    @OneToMany(mappedBy = "flaggedGroup", cascade = CascadeType.REMOVE)
    private List<GroupFlag> flags;

    
    // ======================================
    // =            Constructors            =
    // ======================================

    /**
    * Initializes a new instance of a group.
    */
    public Groups() {
        this.groupCreatedTimestamp = new Timestamp(System.currentTimeMillis());
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================

    /**
    * Gets the name of the group.
    * @return the group's name.
    */
    public String getGroupName() {
        return groupName;
    }

    /**
    * Gets the description of the group.
    * @return the description of the group.
    */
    public String getGroupDescription() {
        return groupDescription;
    }

    /**
    * Sets the name of the group.
    * @param [groupName] the new name of the group
    */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
    * Sets the description of the group.
    * @param [groupDescription] the new description of the group.
    */
    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    /**
    * Gets the ID of the group.
    * @return the group's ID.
    */
    public int getGroupID() {
        return groupID;
    }

    /**
    * Sets the ID of the group.
    * @param [groupID] the new ID of the group.
    */
    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    @XmlTransient
    public Set<GroupsMembers> getMembers() {
        return members;
    }

    public void setMembers(Set<GroupsMembers> members) {
        this.members = members;
    }

    /**
    * Gets a list of tags that are associated with a group.
    * @return a list of tags associated with a particular group.
    */
    @XmlTransient
    public List<Tag> getTags() {
        return tags;
    }

    /**
    * Sets a list of tags to associate the group with.
    * @param [tags] a list of tags to be set.
    */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
    * Gets a list of images that have been uploaded to a group's images gallery.
    * @return a list of images associated with a group.
    */
    public Image getImage() {
        return image;
    }

    /**
    * Sets a list of images to associate with a group's images gallery.
    * @param [image] a list of images to be set.
    */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
    * Gets a list of recipes that have been added or associated to a group.
    * @return a list of recipes.
    */
    @XmlTransient
    public List<Recipe> getRecipes() {
        return recipes;
    }

    /**
    * Sets a list of recipes to add or asssociate to a group.
    * @param [recipes] a list of recipes to set.
    */
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    /**
    * Gets the owner who created the group.
    * @return a User object of the group's owner.
    */
    public Users getOwner() {
        return owner;
    }

    /**
    * Sets a new owner of a group; to be used in case a user is deleted
    * or ownership of a group is transferred.
    * @param [owner] to new owner of a group to be set.
    */
    public void setOwner(Users owner) {
        this.owner = owner;
    }

    @XmlTransient
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }
    
    

    @Override
    public int hashCode(){
        if (groupID == null) return 0;
        else return groupID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Groups other = (Groups) obj;
        if (this.groupID != other.groupID && (this.groupID == null || !this.groupID.equals(other.groupID))) {
            return false;
        }
        return true;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public List<GroupFlag> getFlags() {
        return flags;
    }

    public void setFlags(List<GroupFlag> flags) {
        this.flags = flags;
    }

    public Timestamp getGroupCreatedTimestamp() {
        return groupCreatedTimestamp;
    }

    public void setGroupCreatedTimestamp(Timestamp groupCreatedTimestamp) {
        this.groupCreatedTimestamp = groupCreatedTimestamp;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public double getFlagRatio() {
        return flagRatio;
    }

    public void setFlagRatio(double flagRatio) {
        this.flagRatio = flagRatio;
    }
    
    
    
}
