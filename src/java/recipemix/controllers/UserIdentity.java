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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import recipemix.beans.UsersEJB;
import recipemix.models.SecurityGroup;
import recipemix.models.Users;

@Named
@SessionScoped
public class UserIdentity implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("SecurityBacking");
    private Users user; // used during registration and while session is active
    @EJB
    private UsersEJB usersEJB;

    /**
     * Creates a new instance of UserIdentity
     */
    public UserIdentity() {
        user = null;
    }

    /**
     * Determine if the user is authenticated and if so, make sure the session
     * scope includes the User object for the authenticated user
     *
     * @return true if the user making a request is authenticated, false
     * otherwise.
     */
    public boolean isIsUserAuthenticated() {
        boolean isAuthenticated = false;
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String userName = request.getRemoteUser();
        if (userName != null) {
            this.user = usersEJB.findUser(userName);
            isAuthenticated = (this.user != null);
            if (isAuthenticated) {
                logger.log(Level.SEVERE, "UserIdentiy::isUserAuthenticated: Changed session, so now userIdentiy object has user=authenticated user");
            }
        }

        return isAuthenticated;
    }

    /**
     * Logout the user and invalidate the session
     *
     * @return success if user is logged out and session invalidated, failure
     * otherwise.
     */
    public String logout() {
        String result = "failure";

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.logout();
            user = null;
            result = "success";
        } catch (ServletException ex) {
            logger.log(Level.SEVERE, null, ex);
        } finally {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }

        return result;
    }

    /**
     * @return the user
     */
    public Users getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(Users user) {
        this.user = user;
    }

    public boolean isIsAdmin() {
        boolean admin = false;
        if (this.user != null) {
            for (SecurityGroup s : user.getSecurityGroups()) {
                if(s.getSecurityGroupName().equals("admin")){
                    admin = true;
                    break;
                }
            }
        }
        return admin;
    }
    
    /**
     * validator for site moderator
     * @return true if user is site moderator
     *          false otherwise
     */
    public boolean isIsSiteMod(){
        boolean mod = false;
        if(this.user!=null){
            for(SecurityGroup sg: user.getSecurityGroups()){
                if(sg.getSecurityGroupName().equals("siteModerator")){
                    mod = true;
                }
            }
        }
        return mod;
    }
    
    /**
     * validator for a professional
     * @return  true if user is a professional
     *          false otherwise
     */
    public boolean isIsProfessional(){
        boolean pro = false;
        if(this.user!=null){
            pro = user.getProfessionalInfo() != null;
        }
        return pro;
    }
}
