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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import recipemix.beans.ImageEJB;
import recipemix.beans.UsersEJB;
import recipemix.controllers.exceptions.UserExistsException;
import recipemix.models.Image;
import recipemix.models.Users;

/**
 *
 * @author Jairo Lopez (jairo.lopez00@gmail.com)
 */
@Named
@RequestScoped
public class Registration implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("SecurityBacking");
    private Users newUser; // used during registration and while session is active
    
    @EJB
    private UsersEJB usersEJB;
    @EJB
    private ImageEJB imageEJB;
    
    private static final String whitespace_chars = "["
            + "" // dummy empty string for homogeneity
            + "\\u0009" // CHARACTER TABULATION
            + "\\u000A" // LINE FEED (LF)
            + "\\u000B" // LINE TABULATION
            + "\\u000C" // FORM FEED (FF)
            + "\\u000D" // CARRIAGE RETURN (CR)
            + "\\u0020" // SPACE
            + "\\u0085" // NEXT LINE (NEL) 
            + "\\u00A0" // NO-BREAK SPACE
            + "\\u1680" // OGHAM SPACE MARK
            + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
            + "\\u2000" // EN QUAD 
            + "\\u2001" // EM QUAD 
            + "\\u2002" // EN SPACE
            + "\\u2003" // EM SPACE
            + "\\u2004" // THREE-PER-EM SPACE
            + "\\u2005" // FOUR-PER-EM SPACE
            + "\\u2006" // SIX-PER-EM SPACE
            + "\\u2007" // FIGURE SPACE
            + "\\u2008" // PUNCTUATION SPACE
            + "\\u2009" // THIN SPACE
            + "\\u200A" // HAIR SPACE
            + "\\u2028" // LINE SEPARATOR
            + "\\u2029" // PARAGRAPH SEPARATOR
            + "\\u202F" // NARROW NO-BREAK SPACE
            + "\\u205F" // MEDIUM MATHEMATICAL SPACE
            + "\\u3000" // IDEOGRAPHIC SPACE
            + "]";

    /**
     * Creates a new instance of Registration
     */
    public Registration() {
        newUser = new Users();
    }

    @PostConstruct
    public void init() {
        Image image = new Image();
        image.setCaption("Default profile picture.");
        image.setImageName("Default profile picture.");
    }

    /**
     * JSF Action that uses the information submitted in the registration page
     * to add user as a registered user.
     *
     * @return either failure, success, or register depending on successful
     * registration.
     */
    public String registerUser() {
        String result = "failure";
        if (newUser.isInformationValid()) {
            newUser.setPassword(recipemix.controllers.MD5Hash.hashPassword(newUser.getPassword()));
            try {
                usersEJB.registerUser(newUser, "user");
                result = "success";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Success!", "Thank you for registering."));
            } catch (UserExistsException uee) {
                logger.log(Level.SEVERE, null, uee);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("A user with that information already exists, try again."));
            } catch (Exception e) {
                if (e instanceof EJBException) {
                    logger.log(Level.SEVERE, null, e);
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("A user with that information already exists, try again."));
                } else {
                    logger.log(Level.SEVERE, null, e);
                    result = "failure";
                }
            }
        }

        return result;
    }

    /**
     * @return the user
     */
    public Users getNewUser() {
        return newUser;
    }

    /**
     * @param user the user to set
     */
    public void setNewUser(Users user) {
        this.newUser = user;
    }
}
