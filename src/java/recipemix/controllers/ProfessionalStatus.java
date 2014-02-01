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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import recipemix.beans.RecipeEJB;
import recipemix.beans.ReviewEJB;
import recipemix.beans.UsersEJB;
import recipemix.models.ProfessionalInfo;
import recipemix.models.Recipe;
import recipemix.models.Review;
import recipemix.models.SecurityGroup;
import recipemix.models.Users;

/**
 * This bean is used for various Professional functions
 *
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@Named
@RequestScoped
public class ProfessionalStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("ViewProfile");
    @EJB
    private UsersEJB userEJB;
    @EJB
    private RecipeEJB recipeEJB;
    @EJB
    private ReviewEJB reviewEJB;
    @Inject
    private UserIdentity ui;
    @Inject
    private RequestParameterManager rpm;
    // Information for Professional Info
    private Users user;
    private String employer;
    private String request;
    private int yearsOfExperience;
    private Recipe recipe; // for submitting reviews
    private Review review;

    public ProfessionalStatus() {
        review = new Review();
    }

    @PostConstruct
    public void init() {
        if (ui.isIsUserAuthenticated()) {
            user = ui.getUser();

            String value = rpm.get("recipe");
            if (value != null) {
                recipe = recipeEJB.findRecipe(Integer.parseInt(value));
            }

            if (recipe != null) {
                List<Review> reviews = user.getReviews();
                for (Review r : reviews) {
                    if (r.getRecipe().getRecipeId() == recipe.getRecipeId()) {
                        review = r;
                        break;
                    }
                }
            }
        }
    }

    /**
     * This method is used to submit a new review
     */
    public void submitReview() {
        review.setReviewer(user);
        review.setDateReviewed(new Date().getTime());
        review.setRecipe(recipe);
        user.getReviews().add(review);
        recipe.getReviews().add(review);
        review = reviewEJB.createReview(review);

        if (review != null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Success!", "Thank you for submitting this review!"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Failure!", "Something went wrong."));
        }
    }

    /**
     * removes the review from the recipe 
     */
    public void deleteReview() {
        if (review.getReviewer() != null && review.getId() != null && review.getRecipe() != null) {
            reviewEJB.removeReview(review);
        }
    }

    /**
     * Action used to apply for professional status
     *
     * @return - success if the application was successful, failure otherwise
     */
    public String applyForProfessionalStatus() {
        String result = "failure";
        if (user != null) {
            ProfessionalInfo pInfo = new ProfessionalInfo();
            if (employer != null && !employer.equals("")) {
                pInfo.setEmployer(employer);
            }
            pInfo.setYearsOfExperience(yearsOfExperience);
            pInfo.setConfirmationRequest(request);
            pInfo.setCorrespondingUser(user);
            pInfo = userEJB.createProfessionalInfo(pInfo);

            if (pInfo != null) {
                result = "success";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Success", "Your request has been submitted."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Failure", "Your request was not submitted. Have you already submitted a request?"));
            }
        } // end isAuthenticated
        return result;
    }

    /**
     * Returns true if the user is a *confirmed* professional
     *
     * @return - true if the user is a professional, false otherwise
     */
    public boolean isIsProfessional() {
        boolean result = false;
        if (user != null) {
            for (SecurityGroup sg : user.getSecurityGroups()) {
                if (sg.getSecurityGroupName().equals("professional")) {
                    result = true;
                    break;
                }
            }
        }//end user != null
        return result;
    }//end isIsProfessional

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}