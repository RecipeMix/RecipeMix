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
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.event.RateEvent;
import recipemix.beans.RecipeEJB;
import recipemix.beans.RecipeRatingEJB;
import recipemix.beans.UsersEJB;
import recipemix.models.RatingValue;
import recipemix.models.Recipe;
import recipemix.models.RecipeRating;
import recipemix.models.Users;

/**
 * This bean is used to rate a recipe
 *
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@Named
@RequestScoped
public class RatingBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("RatingBean");
    private Users rater; // the user
    private Recipe recipe;
    private RecipeRating finalRating;
    private Integer recipeID;
    private String userName;
    private Integer rating;
    @EJB
    private UsersEJB usersEJB;
    @EJB
    private RecipeEJB recipeEJB;
    @EJB
    private RecipeRatingEJB ratingEJB;

    /**
     * Creates a new instance of RatingBean
     */
    public RatingBean() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        userName = request.getRemoteUser();
        Map<String, String> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        recipeID = Integer.parseInt(requestMap.get("recipe"));
    }

    @PostConstruct
    public void init() {
        if (userName != null) {
            rater = usersEJB.findUser(userName);
        }
        recipe = recipeEJB.findRecipe(recipeID);
    }

    /**
     * This event listener is used for rating
     *
     * @param rateEvent the event
     */
    public void onrate(RateEvent rateEvent) {
        if (rating != null && rating > 0 && rating <= 5) {
            this.finalRating = new RecipeRating();
            this.finalRating.setRater(rater);
            this.finalRating.setRecipe(recipe);
            this.finalRating.setRatingDate(new Date().getTime());

            switch (rating) {
                case 1:
                    this.finalRating.setRatingValue(RatingValue.ONE_STAR);
                    break;
                case 2:
                    this.finalRating.setRatingValue(RatingValue.TWO_STARS);
                    break;
                case 3:
                    this.finalRating.setRatingValue(RatingValue.THREE_STARS);
                    break;
                case 4:
                    this.finalRating.setRatingValue(RatingValue.FOUR_STARS);
                    break;
                case 5:
                    this.finalRating.setRatingValue(RatingValue.FIVE_STARS);
                    break;
            }// end switch
            this.finalRating = ratingEJB.createRecipeRating(finalRating);
        }//endif
    }

    /**
     * For canceling a rating
     */
    public void oncancel() {
        ratingEJB.removeRecipeRating(finalRating);
    }

    /**
     * Getter for rating
     *
     * @return the Integer rating
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * Setter for rating
     *
     * @param rating Integer
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
