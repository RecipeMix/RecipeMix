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
import java.util.Date;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import recipemix.beans.RecipeEJB;
import recipemix.beans.UsersEJB;
import recipemix.models.Country;
import recipemix.models.Recipe;
import recipemix.models.Users;

/**
 * This bean is used to edit an authenticated user's profile
 *
 * @author Steven Paz <steve.a.paz@gmail.com>
 * @author Gustvo Rosas <grgustavorosas@gmail.com>
 */
@Named
@RequestScoped
public class EditProfile implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("EditProfile");
    private Users existingUser; // the user
    /* Temporary storage */
    private String userName;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private Country country;
    private Integer postalCode;
    private Date dob;
    private String about;
    @EJB
    private UsersEJB usersEJB;
    @EJB
    private RecipeEJB recipeEJB;
    @Inject
    private UserIdentity ui;
    @Inject
    private RequestParameterManager rpm;

    @PostConstruct
    public void init() {
        if (ui.isIsUserAuthenticated()) {
            existingUser = ui.getUser();
        }
        this.firstName = existingUser.getFirstName();
        this.lastName = existingUser.getLastName();
        this.email = existingUser.getEmail();
        this.postalCode = existingUser.getPostalCode();
        this.dob = new Date(existingUser.getDateOfBirth());
        this.about = existingUser.getAbout();
        this.country = existingUser.getCountry();
    }

    /**
     * Attempts to save new information for a user
     *
     * @return result string
     */
    public String save() {
        String result = "failure";
        try {
            //Give the User object the new values
            if (firstName != null && !firstName.equals("")) {
                this.existingUser.setFirstName(firstName);
            }

            if (lastName != null && !lastName.equals("")) {
                this.existingUser.setLastName(lastName);
            }
            if (email != null && !email.equals("")) {
                this.existingUser.setEmail(email);
            }
            if (dob != null) {
                this.existingUser.setDateOfBirth(dob.getTime());
            }
            if (postalCode != null && postalCode != 0) {
                this.existingUser.setPostalCode(postalCode);
            }
            //Make sure password isn't null or blank !
            if (this.password != null && !this.password.equals("")) {
                existingUser.setPassword(recipemix.controllers.MD5Hash.hashPassword(this.password));
            }
            this.existingUser.setAbout(about);

            //Attempt the edit
            existingUser = usersEJB.editUser(existingUser);
            result = "success";


            //}
        } catch (javax.ejb.EJBAccessException ejbae) {
            result = "forbidden";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only registered users can create/edit profiles."));
        }
        return result;
    }

    /**
     * adds a recipe to your favorites 
     * @param e 
     */
    public void addFavorite(ActionEvent e) {
        if (existingUser != null) {
            int recipeID = (Integer) e.getComponent().getAttributes().get("recipe");
            Recipe recipe = recipeEJB.findRecipe(recipeID);
            if (!existingUser.getFavorites().contains(recipe)) {
                existingUser.getFavorites().add(recipe);
                usersEJB.editUser(existingUser);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Success", "Added favorite."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Failed", "You've already favorited this."));
            }
        }
    }

    /**
     * removes a recipe from your favorites
     */
    public void removeFavorite() {
        if (existingUser != null) {
            String value = rpm.get("recipe");
            if (value != null) {
                int recipeID = Integer.parseInt(value);
                Recipe recipe = recipeEJB.findRecipe(recipeID);
                existingUser.getFavorites().remove(recipe);
                if (!existingUser.getFavorites().contains(recipe)) {
                    FacesMessage msg = new FacesMessage("Success", "Removed from favorites.");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {
                    FacesMessage msg = new FacesMessage("Failure", "Woops... Server error.");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            }
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
     * Sets the authenticated User being edited
     *
     * @param existingUser
     */
    public void setExistingUser(Users existingUser) {
        this.existingUser = existingUser;
    }

    /**
     * Returns local copy of the User's first name
     *
     * @return the User's firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the local copy of the User's first name
     *
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns local copy of the User's last name
     *
     * @return the User's lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the local copy of the User's first name
     *
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns local copy of the User's password
     *
     * @return the String password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets local copy of the User's password
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     */
    public Country getCountry() {
        return country;
    }

    /**
     *
     * @param country
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     *
     * @return
     */
    public Integer getPostalCode() {
        return postalCode;
    }

    /**
     *
     * @param postalCode
     */
    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
