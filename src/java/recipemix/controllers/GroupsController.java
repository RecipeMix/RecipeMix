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
package recipemix.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import org.primefaces.model.DualListModel;
import recipemix.beans.GroupEJB;
import recipemix.beans.TagEJB;
import recipemix.beans.UsersEJB;
import recipemix.models.GroupRole;
import recipemix.models.Groups;
import recipemix.models.GroupsMembers;
import recipemix.models.Image;
import recipemix.models.Tag;
import recipemix.models.Users;

/**
 * This bean is used to Create, Edit, and Delete Groups
 *
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@ManagedBean(name = "groupsController")
@ViewScoped
public class GroupsController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("GroupsController");
    // =======================================
    // =              EJBS                   =
    // =======================================
    @EJB
    private GroupEJB groupEJB;
    @EJB
    private TagEJB tagEJB;
    @EJB
    private UsersEJB userEJB;
    // =======================================
    // =          Other Beans                =
    // =======================================
    @Inject
    RequestParameterManager rpm;
    @Inject
    UserIdentity ui;
    // =======================================
    // =             Fields                  =
    // =======================================
    private Groups group;           // holds a new group
    private List<Groups> groups;    // holds recent groups
    private String inputTags;       // comma-delimited string of tags
    private boolean editMode;       // in edit mode or not?
    private int id;                // used to fix a null pointer exception...
    private DualListModel<Users> membersPromoteModel;   // used in the picklist
    private DualListModel<Users> membersRemoveModel;   // used in the picklist
    private LazyUserDataModel lazyPendingUsers;
    GroupsMembers[] membersToConfirm;

    @PostConstruct
    public void init() {
        groups = new ArrayList<Groups>();
        // Logged in?
        if (ui.isIsUserAuthenticated()) {
            Users user = ui.getUser();
            String value = rpm.get("groupID");
            //Edit mode
            if (value != null) {
                group = groupEJB.findGroup(Integer.parseInt(value));
                if (group != null) {
                    if (lazyPendingUsers == null) {
                        lazyPendingUsers = new LazyUserDataModel(group, groupEJB);
                    }

                    preparePickLists();

                    if (groupEJB.checkRole(group, user, GroupRole.OWNER)
                            || groupEJB.checkRole(group, user, GroupRole.MODERATOR)) {
                        editMode = true;
                        id = group.getGroupID(); // fix for f:param null exception

                        // Build the tags
                        inputTags = "";
                        List<Tag> tags = group.getTags();
                        if (!tags.isEmpty()) {
                            for (Iterator<Tag> it = tags.iterator(); it.hasNext();) {
                                Tag t = it.next();
                                if (it.hasNext()) { // more to go
                                    inputTags += t.getTagName() + ", ";
                                } else if (!it.hasNext()) { // last one
                                    inputTags += t.getTagName();
                                }
                            }
                        }//end if
                    }
                }
            } // Create mode
            else {
                editMode = false;
                id = 0; // Fix for null pointer exception
                group = new Groups();
                group.setRestricted(false); // defaults
                group.setPrivacy(false);
            }
        }
    }//end init

    private void preparePickLists() {
        // Set the dual model for members and moderators
        this.membersPromoteModel = new DualListModel<Users>();
        List<Users> members = new ArrayList<Users>(); // source
        List<Users> moderators = new ArrayList<Users>(); // target
        group = groupEJB.findGroup(group.getGroupID());
        
        for (GroupsMembers gm : this.group.getMembers()) {
            if (gm.getRole() == GroupRole.MODERATOR
                    && gm.isIsConfirmed()) {
                moderators.add(gm.getMember());
            } else if (gm.getRole() == GroupRole.MEMBER
                    && gm.isIsConfirmed()) {
                members.add(gm.getMember());
            }
            // Don't add owner
        }
        this.membersPromoteModel.setSource(members);
        this.membersPromoteModel.setTarget(moderators);


        // Set the dual model for deleting users
        this.membersRemoveModel = new DualListModel<Users>();
        this.membersRemoveModel.setTarget(new ArrayList<Users>());

        List<Users> current = new ArrayList<Users>();
        for (GroupsMembers gm : group.getMembers()) {
            // show only confirmed members that are not the owner
            if (gm.isIsConfirmed() && gm.getRole() != GroupRole.OWNER) {
                current.add(gm.getMember());
            }
        }
        this.membersRemoveModel.setSource(current);
    }

    /**
     * This method calls the create group method in the EJB
     *
     *
     * @return "success" if creation was successful, null otherwise
     */
    public String doCreateGroups() {
        String result = "failure";
        Users owner = ui.getUser();
        try {
            //First things first
            group.setOwner(owner);

            //Create the association table entry
            GroupsMembers ownerEntry = new GroupsMembers();
            ownerEntry.setMember(owner);
            ownerEntry.setRole(GroupRole.OWNER);
            ownerEntry.setGroup(group);
            ownerEntry.setIsConfirmed(true);

            //Add entry to group
            Set<GroupsMembers> members = new HashSet<GroupsMembers>();
            members.add(ownerEntry);
            group.setMembers(members);

            //Add entry to owner
            owner.getUserGroups().add(ownerEntry);

            //Add the default picutre
            Image i = new Image();
            i.setGroups(group);
            i.setImageName("Default image");
            i.setCaption("Default group picture");
            i.setImagePath("/resources/images/group_placeholder.jpg");

            group.setImage(i);

            //Add the tags
            group.setTags(tagEJB.splitTags(inputTags));
            group = groupEJB.createGroup(group);

            // if created, refresh the list of groups
            if (group != null) {
                result = "success";
            }
        } catch (javax.ejb.EJBAccessException ejbae) {
            result = "forbidden";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only registered users can make groups."));
        }
        return result;
    }

    /**
     * Edits the group
     *
     * @return "success" if edit was successful, null otherwise
     */
    public String doEditGroup() {
        String result = "failure";
        Users owner = ui.getUser();
        try {
            //Add the tags
            group.setTags(tagEJB.splitTags(inputTags));
            group = groupEJB.editGroup(group);

            // if created, refresh the list of groups
            if (group != null) {
                result = "success";
            }
        } catch (javax.ejb.EJBAccessException ejbae) {
            result = "forbidden";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only registered users can edit groups."));
        }
        return result;
    }

    /**
     * Delete the group
     *
     * @return "success" if delete was successful, null otherwise
     */
    public String doDeleteGroup() {
        String result = "failure";
        Users owner = ui.getUser();
        if (groupEJB.checkRole(group, owner, GroupRole.OWNER) || ui.isIsAdmin()) {
            try {
                groupEJB.removeGroup(group);
                if (groupEJB.findGroup(group.getGroupID()) == null) {
                    result = "success";
                    try {
                        FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
                    } catch (IOException ex) {
                        Logger.getLogger(GroupsController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (javax.ejb.EJBAccessException ejbae) {
                result = "forbidden";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only registered users can edit groups."));
            }
        }
        return result;
    }

    /**
     * Returns true if the user is the owner for this group
     *
     * @return boolean
     */
    public boolean isIsOwner() {
        if (ui.isIsUserAuthenticated() && group != null && id != 0) {
            Users user = ui.getUser();
            return groupEJB.checkRole(group, user, GroupRole.OWNER);
        } else {
            return false;
        }
    }

    /**
     * Getter for the new group
     *
     * @return - the new Group
     */
    public Groups getGroup() {
        return group;
    }

    /**
     * Setter for the new group
     *
     * @param group
     */
    public void setGroup(Groups group) {
        this.group = group;
    }

    /**
     * The list of groups the user has created
     *
     * @return - the list of groups the user created
     */
    public List<Groups> getGroupsUserCreated() {
        Users user = ui.getUser();

        if (user != null) {
            groups = groupEJB.getGroupsCreated(user);
            List<Groups> managed = getGroupsUserManages();

            for (Groups g : managed) {
                if (!groups.contains(g)) {
                    groups.add(g);
                }
            }
        }
        return groups;
    }

    /**
     * Returns the Groups the User is a member of
     *
     * @return - the List of Groups the User is in
     */
    public List<Groups> getGroupsUserIsIn() {
        Users user = ui.getUser();

        if (user != null) {
            groups = groupEJB.getGroupMembership(user);
        }
        return groups;
    }

    /**
     * Returns the Groups the User Manages
     *
     * @return - the List of Groups the user manages
     */
    public List<Groups> getGroupsUserManages() {
        Users user = ui.getUser();

        if (user != null) {
            groups = groupEJB.getGroupsManaged(user);
        }
        return groups;
    }

    /**
     * Returns all Groups WARNING: Should not be returned to front end
     *
     * @return
     */
    public List<Groups> getAllGroups() {
        return groupEJB.findGroups();
    }

    /**
     * Returns the string of input tags to be edited (delimited)
     *
     * @return
     */
    public String getInputTags() {
        return inputTags;
    }

    /**
     * Sets the input tags (delimited)
     *
     * @param inputTags
     */
    public void setInputTags(String inputTags) {
        this.inputTags = inputTags;
    }

    /**
     * Returns true if currently editing a recipe
     *
     * @return
     */
    public boolean isEditMode() {
        return editMode;
    }

    /**
     * This is part of a fix for a nullpointer Exception Holds the ID of the
     * Group if it exists Holds '0' if it doesn't
     *
     * @return
     */
    public int getId() {
        return id;
    }

    public DualListModel<Users> getMembersPromoteModel() {
        return membersPromoteModel;
    }

    public void setMembersPromoteModel(DualListModel<Users> membersPromoteModel) {
        this.membersPromoteModel = membersPromoteModel;
    }

    public boolean isIsModerator() {
        boolean result = false;
        if (group != null && ui.isIsUserAuthenticated() && id != 0) {
            Users user = ui.getUser();

            for (GroupsMembers memberEntry : group.getMembers()) {
                if (memberEntry.getMember().getUserName().equals(user.getUserName())) {
                    if (groupEJB.checkRole(group, user, GroupRole.MODERATOR)
                            || groupEJB.checkRole(group, user, GroupRole.OWNER)) {
                        result = true;
                    } // end check roles
                }// check username
            }//end for
        }
        return result;
    }

    public LazyUserDataModel getLazyPendingUsers() {
        return lazyPendingUsers;
    }

    public void setLazyPendingUsers(LazyUserDataModel lazyPendingUsers) {
        this.lazyPendingUsers = lazyPendingUsers;
    }

    public GroupsMembers[] getMembersToConfirm() {
        return membersToConfirm;
    }

    public void setMembersToConfirm(GroupsMembers[] membersToConfirm) {
        this.membersToConfirm = membersToConfirm;
    }

    /**
     * Promotes users in the membersToPromote List to MODERATOR
     */
    public void doPromoteMembers() {
        if (group != null) {
            // For each member in the moderator list
            //  if they are not a moderator, change their role to moderator
            for (Users u : membersPromoteModel.getTarget()) {
                groupEJB.updateRole(group, u, GroupRole.MODERATOR);
            }// end promote to moderator

            for (Users u : membersPromoteModel.getSource()) {
                groupEJB.updateRole(group, u, GroupRole.MEMBER);
            }// end demote to member
        }
        preparePickLists();
        FacesMessage msg = new FacesMessage("Success", "Member roles changed.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * Promotes users in the membersToPromote List to MODERATOR
     */
    public void doDeleteMembers() {
        if (group != null) {
            for (Iterator<Users> it = membersRemoveModel.getTarget().iterator(); it.hasNext();) {
                Users u = it.next();
                groupEJB.removeMembers(group, u);
                it.remove();
            }
        }
        preparePickLists();
        FacesMessage msg = new FacesMessage("Success", "Member(s) deleted.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void doConfirmNewMembers() {
        if (this.membersToConfirm != null) {
            for (GroupsMembers entry : this.membersToConfirm) {
                groupEJB.confirmMember(entry);
            }
        }
        FacesMessage msg = new FacesMessage("Success", "Users confirmed.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        this.preparePickLists();
    }

    public void rejectNewMember(ActionEvent e) {
        String username = (String) e.getComponent().getAttributes().get("user");
        for (Iterator<GroupsMembers> it = this.group.getMembers().iterator(); it.hasNext();) {
            GroupsMembers gm = it.next();
            if (gm.getMember().getUserName().equals(username)) {
                groupEJB.removeMembers(group, gm.getMember());
                it.remove();
            }
        }
    }

    public boolean isHasUsersToConfirm() {
        boolean result = false;
        if (group != null) {
            result = groupEJB.countPendingMembers(group) > 0;
        }
        return result;
    }

    public DualListModel<Users> getMembersRemoveModel() {
        return membersRemoveModel;
    }

    public void setMembersRemoveModel(DualListModel<Users> membersRemoveModel) {
        this.membersRemoveModel = membersRemoveModel;
    }
}
