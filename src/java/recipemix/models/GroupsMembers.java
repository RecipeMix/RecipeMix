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
 * The GroupsMembers entity holds member role information 
 * @author Steven Paz <steve.a.paz@gmail.com>
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */

@Entity
@Table(name ="GROUPS_MEMBERS")
@XmlRootElement
public class GroupsMembers implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private int memberID;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "MEMBER_ROLE", nullable=false)
    private GroupRole role;
    
    @Column(nullable=false)
    boolean isConfirmed;
    
    @ManyToOne
    @JoinColumn (nullable = false)
    private Groups group;
    
    @ManyToOne
    @JoinColumn (nullable = false)
    private Users member;

    // ======================================
    // =            Constructors            =
    // ======================================
    
    /**
     * Creates a simple instance of GroupsMembers
     */
    public GroupsMembers() {
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================

    public GroupRole getRole() {
        return role;
    }

    public void setRole(GroupRole role) {
        this.role = role;
    }

    public Groups getGroup() {
        return group;
    }

    public void setGroup(Groups group) {
        this.group = group;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public Users getMember() {
        return member;
    }

    public void setMember(Users member) {
        this.member = member;
    }

    public boolean isIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }
    
    
}