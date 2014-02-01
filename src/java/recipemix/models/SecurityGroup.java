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
import java.util.Collection;
import java.util.HashSet;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The SecurityGroup entity holds the users that belong to a security group.
 * These security groups are used to identify the authority that a user has
 * on functionalities of the web application.
 * @author Alex Chavez <alex@alexchavez.net>
 */
@Entity
@Table(name = "SECURITY_GROUP")
@NamedQueries ({
    @NamedQuery(name = SecurityGroup.FIND_GROUP_BY_USERNAME, query = "SELECT g FROM SecurityGroup g WHERE g.securityGroupName = :groupname")
})
@XmlRootElement
public class SecurityGroup implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================
    
    private static final long serialVersionUID = 1L;
    public static final String FIND_GROUP_BY_USERNAME = "findGroupByName";

    @Id
    @Column(name = "SECURITY_GROUP_NAME")
    private String securityGroupName;

    @Column(name = "SECURITY_GROUP_DESCRIPTION")
    private String securityGroupDescription;
    //needed
    @ManyToMany
    @JoinTable(name="SECURITY_GROUPS_USERS",
          joinColumns=@JoinColumn(name="GROUP_NAME"),
          inverseJoinColumns=@JoinColumn(name="USER_NAME"))
    private Collection<Users> users;

    // ======================================
    // =            Constructors            =
    // ======================================
    
    /**
     * Initializes a simple instance of SecurityGroup
     */
    public SecurityGroup() {
        users = new HashSet<Users>();
    }

    /**
     * Initializes an instance of SecurityGroup with groupName
     * @param groupName string for the group name
     */
    public SecurityGroup(String groupName) {
        this.securityGroupName = groupName;
        this.securityGroupDescription = null;
        users = new HashSet<Users>();
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================
    
    /**
     * Returns the description of the security group
     * @return securityGroupDescription of tag
     */
    public String getSecurityGroupDescription() {
        return securityGroupDescription;
    }

    /**
     * Sets the security group's description to description
     * @param description new string for security group description
     */
    public void setDescription(String description) {
        this.securityGroupDescription = description;
    }

    /**
     * Returns the name of the security group
     * @return string name of securityGroup
     */
    public String getSecurityGroupName() {
        return securityGroupName;
    }

    /**
     * Sets the name of the security group to groupname
     * @param groupname string new name of securityGroup
     */
    public void setSecurityGroupName(String groupname) {
        this.securityGroupName = groupname;
    }

    /**
     * Returns the users associated with this security group
     * @return collection of users
     */
    @XmlTransient
    public Collection<Users> getUsers() {
        return users;
    }

    /**
     * Sets the users of the security group to users
     * @param users collection of users
     */
    public void setUsers(Collection<Users> users) {
        this.users = users;
    }

    /**
     * Adds a user to the security group of users users
     * @param user to be added to the collection of users
     */
    public void addUser(Users user) {
        this.users.add(user);
    }

    /**
     * Hashes the security group name
     * @return hash of the security group name
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (securityGroupName != null ? securityGroupName.hashCode() : 0);
        return hash;
    }

    /**
     * Check the equality of object to this security group
     * @param object to be compared to this security group
     * @return boolean true if object is equal to this security group
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SecurityGroup)) {
            return false;
        }
        SecurityGroup other = (SecurityGroup) object;
        if ((this.securityGroupName == null && other.securityGroupName != null) || (this.securityGroupName != null && !this.securityGroupName.equals(other.securityGroupName))) {
            return false;
        }
        return true;
    }

    /**
     * Returns security group as a string
     * @return security name
     */
    @Override
    public String toString() {
        return "SecurityGroup[securityGroupName=" + securityGroupName + "]";
    }

}
