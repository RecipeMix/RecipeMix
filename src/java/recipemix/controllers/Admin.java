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
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import recipemix.beans.UsersEJB;
import recipemix.models.SecurityGroup;
import recipemix.models.Users;

/**
 * This bean is used for various admin functions
 *
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@Named
@RequestScoped
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("Admin");
    private List<Users> siteModerators;
    private String userName;
    @EJB
    UsersEJB userEJB;
    @Inject
    UserIdentity ui;

    @PostConstruct
    public void init() {
        loadSiteModerators();
    }

    /**
     * finds the siteModerators among the users in the security group
     */
    private void loadSiteModerators(){
        SecurityGroup siteModGroup = userEJB.findSecurityGroup("siteModerator");
        if (siteModGroup != null) {
            siteModerators = new ArrayList<Users>(siteModGroup.getUsers());
        }else{
            siteModerators = new ArrayList<Users>();
        }
    }
    
    /**
     * allows a user to have site moderation privilages
     * @param e 
     */
    public void promoteToSiteMod(ActionEvent e) {
        if (ui.isIsAdmin()) {
            if (userName != null) {
                Users user = userEJB.findUser(userName);
                if (user != null) {
                    userEJB.makeSiteMod(user);
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO,"Success", "Promoted to site moderator."));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "User not found..."));
                }
                loadSiteModerators();
            }// end username not null
        }// end is admin
    }// end method

    /**
     * returns the site moderators lis
     * @return List siteModerators
     */
    public List<Users> getSiteModerators() {
        return siteModerators;
    }

    /**
     * sets the siteMiderators to the given list
     * @param siteModerators list of new site moderators
     */
    public void setSiteModerators(List<Users> siteModerators) {
        this.siteModerators = siteModerators;
    }

    /**
     * returns the username of thie admin
     * @return String username of admin
     */
    public String getUserName() {
        return userName;
    }

    /**
     * sets the username of this admin
     * @param userName 
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}