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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.tagcloud.DefaultTagCloudItem;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudModel;
import recipemix.beans.GroupEJB;
import recipemix.beans.ImageEJB;
import recipemix.beans.TagEJB;
import recipemix.models.Comment;
import recipemix.models.GroupRole;
import recipemix.models.Groups;
import recipemix.models.GroupsMembers;
import recipemix.models.Image;
import recipemix.models.Recipe;
import recipemix.models.Tag;
import recipemix.models.Users;

/**
 * This bean is used to view a group's profile
 *
 * @author Jairo Lopez <jairolopez00@gmail.com>
 */
@ManagedBean(name="groupProfile")
@ViewScoped
public class GroupProfile implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("GroupProfile");
    // =======================================
    // =              EJBS                   =
    // =======================================
    @EJB
    private GroupEJB groupEJB;
    @EJB
    private TagEJB tagEJB;
    @EJB
    private ImageEJB imageEJB;
    // =======================================
    // =            Other Beans              =
    // =======================================
    @Inject
    private RequestParameterManager rpm;
    @Inject
    private UserIdentity ui;
    // =======================================
    // =            Fields                   =
    // =======================================
    private TagCloudModel tags;
    private Groups existingGroup; // the group being viewed
    private Users user; // the user making the request
    private Recipe recipe; // used to add a recipe to the group
    private Comment delComment; // Comment to be deleted by event
    private List<Users> members;
    private List<Recipe> recipeMembers;
    private LazyCommentDataModel commentModel;
    private Comment newComment;
    private String joinButtonText;
    private String description;
    private String caption;

    /**
     * This method is called AFTER the bean is constructed
     */
    @PostConstruct
    private void init() {
        // init some fields
        tags = new DefaultTagCloudModel();
        newComment = new Comment();
        newComment.setBody("Enter your comment here... if you dare.");

        String gid = rpm.get("groupID");
        if (gid != null) {
            try {
                Integer groupID = Integer.parseInt(gid);
                existingGroup = groupEJB.findGroup(groupID);
                this.commentModel = new LazyCommentDataModel(existingGroup, groupEJB);
                // Get list of CONFIRMED members
                Set<GroupsMembers> gm = existingGroup.getMembers();
                recipeMembers = existingGroup.getRecipes();
                members = new ArrayList<Users>();
                newComment = new Comment();
                for (GroupsMembers memberEntry : gm) {
                    if (memberEntry.isIsConfirmed()) {
                        members.add(memberEntry.getMember());
                    }
                }

                // Get the group's tags
                List<Tag> checkTags = new ArrayList<Tag>(existingGroup.getTags());
                if (checkTags.isEmpty()) {
                    this.tags.addTag(new DefaultTagCloudItem("#tagMe", 1));
                } else {
                    for (Tag t : checkTags) {
                        String url = "search.xhtml?searchArg=" + t.getTagName();
                        int weight = tagEJB.getWeight(t); // not working
                        this.tags.addTag(new DefaultTagCloudItem(t.getTagName(), url, weight));
                    }
                }
            } catch (NumberFormatException nfe) {
                logger.log(Level.SEVERE, "GroupProfile: groupID request param was not formatted correctly");
            }
        } else {
            this.tags.addTag(new DefaultTagCloudItem("#tagMe", 1));
        }

        if (ui.isIsUserAuthenticated()) {
            user = ui.getUser();
        }
    }

     /**
     * creates the folder and uploads the image file to the local files
     * @param event
     * @return 
     */
    public void createGroupImage(FileUploadEvent event) {
        String destination = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        Image groupImage = existingGroup.getImage();
        Integer groupID = this.existingGroup.getGroupID();
        File file = new File(destination + "uploads" + File.separator + "groups" + File.separator + groupID);
        String abspath = file.getAbsolutePath() + File.separator;
        if (!file.exists()) {
            if (file.mkdirs());
        }
        //new name of the image
        String imageNameType;
        if (event.getFile().getContentType().equalsIgnoreCase("image/jpeg")) {
            imageNameType = groupID + ".jpeg";
        } else if (event.getFile().getContentType().equalsIgnoreCase("image/gif")) {
            imageNameType = groupID + ".gif";
        } else {
            imageNameType = groupID + ".png";
        }
        String newImagePath = "/uploads/groups/" + groupID + "/" + imageNameType;
        // Do what you want with the file        
        try {
            copyFile(abspath, imageNameType, event.getFile().getInputstream());
            groupImage.setCaption("This is the " + this.existingGroup.getGroupName() + "'s group picture.");
            groupImage.setDescription(imageNameType);
            groupImage.setGroups(this.existingGroup);
            groupImage.setImagePath(newImagePath);
            groupImage.setImageName(imageNameType);
            groupImage = this.imageEJB.editImage(groupImage);
            this.existingGroup.setImage(groupImage);
            this.existingGroup = this.groupEJB.editGroup(this.existingGroup);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Success!", "Your image was uploaded successfully."));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used by createGroupImage to write to the disk
     * @param destination - the destination folder
     * @param fileName - the filename on disk
     * @param in - InputStream
     */
    public void copyFile(String destination, String fileName, InputStream in) {
        try {
            // write the inputStream to a FileOutputStream
            OutputStream out = new FileOutputStream(new File(destination + fileName));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    
    /**
     * Returns the Group
     *
     * @return the Group
     */
    public Groups getExistingGroup() {
        return existingGroup;
    }

    /**
     * Sets the authenticated User being edited
     *
     * @param existingGroup
     */
    public void setExistingGroup(Groups existingGroup) {
        this.existingGroup = existingGroup;
    }

    /**
     * Adds a user to the Group
     */
    public void addUserToGroup(ActionEvent e) {
        user = ui.getUser();
        if (user != null && existingGroup != null) {
            // Create the association table entry (as a MEMBER)
            // Must set both sides of the relationship
            Set<GroupsMembers> gm = existingGroup.getMembers();
            GroupsMembers memberEntry = new GroupsMembers();
            memberEntry.setMember(user);
            memberEntry.setRole(GroupRole.MEMBER);
            memberEntry.setGroup(existingGroup);

            if (existingGroup.isRestricted()) {
                memberEntry.setIsConfirmed(false);
            } else {
                memberEntry.setIsConfirmed(true);
            }
            gm.add(memberEntry);
            existingGroup.setMembers(gm);
            existingGroup = groupEJB.editGroup(existingGroup);

            // refresh the list of members
            members = new ArrayList<Users>();
            Iterator<GroupsMembers> i = gm.iterator();
            while (i.hasNext()) {
                members.add(i.next().getMember());
            }
            if (existingGroup.isRestricted()) {
                FacesMessage msg = new FacesMessage("Success!", "Request to join sent.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage("Success!", "Thank you for joining!");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } else {
            logger.log(Level.WARNING, "JoinGroup: User or Group resolved to NULL!");
            FacesMessage msg = new FacesMessage("Failure!", "Join failed!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * Add a recipe to the group
     */
    public void addRecipeToGroup() {
        if (recipe != null && existingGroup != null) {
            //TO DO: code here
        } else {
            logger.log(Level.WARNING, "JoinGroup: Recipe or Group resolved to NULL!");
        }
    }

    /**
     * Returns a user's eligibility to join status for a group
     *
     * @return - true if eligible, false otherwise
     */
    public boolean isEligibleToJoin() {
        if (ui.isIsUserAuthenticated() && existingGroup != null) {
            return !groupEJB.checkRole(existingGroup, user, GroupRole.OWNER)
                    && !groupEJB.checkRole(existingGroup, user, GroupRole.MODERATOR)
                    && !groupEJB.checkRole(existingGroup, user, GroupRole.MEMBER);
        } else {
            return false;
        }
    }

    /**
     * Creates a comment on the group page
     *
     * @return - the navigation case for post comment creation
     */
    public String doCreateComment() {
        Users commenter = ui.getUser();
        try {
            newComment.setGroup(existingGroup);
            newComment.setCommenter(commenter);
            newComment.setDateCommented(new Date().getTime());
            existingGroup.getComments().add(newComment);
            groupEJB.editGroup(existingGroup);
            newComment = new Comment();
            newComment.setBody("Enter your comment here... if you dare.");
        } catch (javax.ejb.EJBAccessException ejbae) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only registered users can post comments."));
        }
        return "/group.xhtml?groupID=" + rpm.get("groupID");
    }

    /**
     * Returns true if the group has comments, false otherwise
     *
     * @return boolean hasComments
     */
    public boolean hasComments() {
        boolean hasComments = false;
        if (existingGroup != null) {
            hasComments = existingGroup.getComments() != null && existingGroup.getComments().size() > 0;
        }
        return hasComments;
    }

    /**
     * Getter for group's members
     *
     * @return - the list of members
     */
    public List<Users> getMembers() {
        return members;
    }

    /**
     * Getter for local new comment
     *
     * @return - the new comment
     */
    public Comment getNewComment() {
        return newComment;
    }

    /**
     * getter for recipeMembers
     *
     * @return list of recipes in group
     */
    public List<Recipe> getRecipeMembers() {
        return recipeMembers;
    }

    /**
     * Setter for recipeMembers
     *
     * @param recipeMembers
     */
    public void setRecipeMembers(List<Recipe> recipeMembers) {
        this.recipeMembers = recipeMembers;
    }

    /**
     * Returns true if the Group has recipes
     *
     * @return - whether or not to render Recipes
     */
    public boolean getRecipeRendered() {
        boolean temp;
        try {
            if (existingGroup.getRecipes().isEmpty()) {
                temp = false;
            } else {
                temp = true;
            }
        } catch (Exception e) {
            temp = false;
        }
        return temp;
    }

    /**
     * Setter for new comment
     *
     * @param newComment
     */
    public void setNewComment(Comment newComment) {
        this.newComment = newComment;
    }

    /**
     * Returns a user's edit capability for this group
     *
     * @return - true if moderator or owner, false otherwise
     */
    public boolean isCanEdit() {
        if (ui.isIsUserAuthenticated() && existingGroup != null) {
            boolean one = groupEJB.checkRole(existingGroup, user, GroupRole.OWNER);
            boolean two = groupEJB.checkRole(existingGroup, user, GroupRole.MODERATOR);
            return one || two;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the user is the owner for this group
     *
     * @return boolean
     */
    public boolean isIsOwner() {
        if (ui.isIsUserAuthenticated() && existingGroup != null) {
            return groupEJB.checkRole(existingGroup, user, GroupRole.OWNER);
        } else {
            return false;
        }
    }

    /**
     * Deletes a comment
     */
    public void deleteComment() {
        if (ui.isIsUserAuthenticated()) {
            Users u = ui.getUser();
            if (isCanEdit() || ui.isIsAdmin()) {
                existingGroup.getComments().remove(delComment);
                try {
                    groupEJB.editGroup(existingGroup);
                } catch (javax.ejb.EJBAccessException ejbae) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only registered users can post comments."));
                }
            }
        }
    }

    /**
     * Returns the text for the join button
     *
     * @return "Return to Join" if restricted, "Join Group" otherwise
     */
    public String getJoinButtonText() {
        if (existingGroup.isRestricted()) {
            return "Request To Join";
        } else {
            return "Join Group";
        }
    }

    public Comment getDelComment() {
        return delComment;
    }

    public void setDelComment(Comment delComment) {
        this.delComment = delComment;
    }

    public TagCloudModel getTags() {
        return tags;
    }

    public void setTags(TagCloudModel tags) {
        this.tags = tags;
    }
    
    public boolean isIsConfirmedMember(){
        boolean result = false;
        if(existingGroup != null && ui.isIsUserAuthenticated()){
            Users u = ui.getUser();
            for(GroupsMembers gm : existingGroup.getMembers()){
                String mName = gm.getMember().getUserName();
                if(mName.equals(u.getUserName())
                        && gm.isIsConfirmed()){
                    result = true;
                }
            }//end for
        }// end
        return result;
    }

    public void setJoinButtonText(String joinButtonText) {
        this.joinButtonText = joinButtonText;
    }

    public LazyCommentDataModel getCommentModel() {
        return commentModel;
    }

    public void setCommentModel(LazyCommentDataModel commentModel) {
        this.commentModel = commentModel;
    }
    
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * sets the caption and description fields for an group's image
     */
    public void setImageFields(){
        Image groupImage = existingGroup.getImage();
        groupImage.setCaption(caption);
        groupImage.setDescription(description);
        this.existingGroup.setImage(groupImage);
        this.existingGroup = groupEJB.editGroup(existingGroup);
        imageEJB.editImage(groupImage);
    }
}
