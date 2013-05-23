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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import recipemix.beans.UsersEJB;
import recipemix.models.Groups;
import recipemix.models.GroupsMembers;
import recipemix.models.Users;

/**
 * This bean is used to edit an authenticated user's profile
 *
 * @author Steven Paz <steve.a.paz@gmail.com>
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Named
@RequestScoped
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("ViewProfile");
    private Users existingUser; // the user
    
    @Inject RequestParameterManager rpm;
    @Inject UserIdentity ui;
    @EJB UsersEJB usersEJB;

    /**
     * This method is called AFTER the bean is constructed This is needed
     * because the EJB is injected after construction
     */
    @PostConstruct
    private void init() {
        String value = rpm.get("user");
        if (value != null) {
            existingUser = usersEJB.findUser(value);
        }
    }

    /**
     * Returns the authenticated User
     *
     * @return the User
     */
    public Users getExistingUser() {
        return existingUser;
    }

    /**
     * allows for buttons to be rendered in a page
     * @return boolean 
     */
    public boolean getRenderButtons() {
        boolean render = false;
        if (ui.isIsUserAuthenticated()) {
            String uiUserName = ui.getUser().getUserName();
            try {
                if (uiUserName.equals(existingUser.getUserName())) {
                    render = true;
                }
            } catch (Exception e) {
                render = false;
            }
        }
        return render;
    }

    /**
     * deletes the profile of the user from the db
     */
    public void doDeleteUser() {
        if (existingUser != null) {
            if (existingUser.getUserName().equals(ui.getUser().getUserName())) {
                ui.logout();
            }
        }
        usersEJB.removeUser(existingUser);

    }

    /**
     * Sets the authenticated User being edited
     *
     * @param existingUser
     */
    public void setExistingUser(Users existingUser) {
        this.existingUser = existingUser;
    }

    /**
     * returns the groups which the user is a member of
     * @return 
     */
    public List<Groups> getUserGroups() {
        List<Groups> groups = new ArrayList<Groups>();
        if (existingUser != null) {
            Set<GroupsMembers> groupSet = existingUser.getUserGroups();

            for (GroupsMembers gm : groupSet) {
                if (gm.getMember().getUserName().equals(existingUser.getUserName())) {
                    groups.add(gm.getGroup());
                }
            }// end for
        }
        return groups;
    }

    /**
     * validator whether this user can modify the profile
     * @return 
     */
    public boolean isCanEdit() {
        if (ui.isIsUserAuthenticated() && existingUser != null) {
            if (existingUser.getUserName().equals(ui.getUser().getUserName())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isShowProfessionalBtn(){
        boolean pro = false;
        if(existingUser != null){
            pro = existingUser.getProfessionalInfo() != null;
        }
        return pro;
    }
    
//    public StreamedContent getProfilePicture() {
//        try{
//            File existing = new File(existingUser.getImage().getImagePath());
//            profilePicture = new DefaultStreamedContent(new FileInputStream(existing), "image/jpeg");
//        }catch (Exception e) {  
//            e.printStackTrace();  
//        }
//        return profilePicture;
//    }
}
