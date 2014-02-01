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
import recipemix.beans.NewsPostEJB;
import recipemix.models.NewsPost;

/**
 *
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Named
@RequestScoped
public class News implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("News");
    @EJB
    private NewsPostEJB newsPostEJB;
    @Inject
    private UserIdentity ui;
    @Inject
    private RequestParameterManager rpm;
    private NewsPost newNewsPost;// holds a new recipe
    private NewsPost existingPost;
    private List<NewsPost> newsPosts; // retreive news posts

    /**
     *
     */
    @PostConstruct
    public void init() {
        String postID = rpm.get("newsID");
        if(postID != null){
            existingPost = newsPostEJB.findNewsPost(Integer.parseInt(postID));
        }
        newNewsPost = new NewsPost();
        newsPosts = newsPostEJB.findNewsPosts();
    }

    /**
     * This method calls the create recipe method in the EJB
     *
     * @return "success" if creation was successful, null otherwise
     */
    public String doCreateNewsPost() {
        String result = "failure";
        try {
            if (ui.isIsUserAuthenticated() && ui.isIsSiteMod()) {
            }
            newNewsPost.setPoster(ui.getUser());
            newNewsPost.setPostDate(new Date().getTime());
            newNewsPost = newsPostEJB.createNewsPost(newNewsPost);
            if (newNewsPost != null) {
                newsPosts = newsPostEJB.findNewsPosts();
                result = "success";
            }
        } catch (javax.ejb.EJBAccessException ejbae) {
            result = "forbidden";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only registered users can post News."));
        }
        return result;
    }

    public NewsPost getNewNewsPost() {
        return newNewsPost;
    }

    public void setNewNewsPost(NewsPost newNewsPost) {
        this.newNewsPost = newNewsPost;
    }

    public List<NewsPost> getNewsPosts() {
        return newsPosts;
    }

    public void setNewsPosts(List<NewsPost> newsPosts) {
        this.newsPosts = newsPosts;
    }

    public NewsPost getExistingPost() {
        return existingPost;
    }

    public void setExistingPost(NewsPost existingPost) {
        this.existingPost = existingPost;
    }
    
    
    
}