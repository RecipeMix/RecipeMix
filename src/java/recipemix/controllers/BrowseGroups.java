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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import recipemix.beans.GroupEJB;
import recipemix.models.Groups;
import recipemix.models.Users;

/**
 *
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@Named
@SessionScoped
public class BrowseGroups implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("BrowseGroups");
    //=================================================
    //=                 EJBs                          =
    //=================================================
    @EJB
    private GroupEJB groupEJB;
    
    //=================================================
    //=             Other Beans                       =
    //=================================================
    @Inject
    private UserIdentity ui;
    @Inject
    private RequestParameterManager qm;
    
    private String browseBy;
    private String displayTitle;
    private List<Groups> groups;
    private LazyGroupDataModel lazyModel;
    
    /**
     *
     */
    @PostConstruct
    public void init() {
        lazyModel = new LazyGroupDataModel(groupEJB);
    }

    /**
     * This method returns a list of the 10 most recent groups in the last 3 months
     * @return the list
     */
    public List<Groups> getRecentGroups() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -3);
        
        //TO DO : return recent Groups
        return null;
    }

    /**
     * This method returns the groups the user created
     * @return 
     */
    public List<Groups> getGroupsCreated() {
        Users user = ui.getUser();
        if (user != null) {
            groups = groupEJB.getGroupsCreated(user);
        }
        return groups;
    }
    
    /**
     * TO DO: change this so that it returns groups the user manages
     * This method returns the groups the user manages
     * @return 
     */
    public List<Groups> getGroupsManaged() {
        Users user = ui.getUser();
        if (user != null) {
            groups = groupEJB.getGroupsCreated(user);
        }
        return groups;
    }

    /**
     * Returns the local list of groups
     * @return 
     */
    public List<Groups> getGroups() {
        browseBy = qm.get("by");
        if (browseBy != null && !browseBy.equals("")) {
            if (browseBy.equals("all")) {
                // TO DO: Get recent groups
            } else {
                // TO DO: Ge Groups by 'browseBy'
            }
            determineTitle();
        }
        return groups;
    }

    public void setGroups(List<Groups> groups) {
        this.groups = groups;
    }

    public String getBrowseBy() {
        return browseBy;
    }

    public void setBrowseBy(String browseBy) {
        this.browseBy = browseBy;
    }

    /**
     * this chooses a title depending on what the navigation case is
     */
    private void determineTitle() {
        
        if (browseBy.equals("all")) {
            displayTitle = "All Recipes";
        } else if (browseBy.equals("maincourses")) {
            displayTitle = "Main Courses";
        } else if (browseBy.equals("dinner")) {
            displayTitle = "Dinner";
        } else if (browseBy.equals("breakfast")) {
            displayTitle = "Breakfast";
        } else if (browseBy.equals("lunch")) {
            displayTitle = "Lunch";
        } else if (browseBy.equals("sidedishes")) {
            displayTitle = "Side Dishes";
        } else if (browseBy.equals("desserts")) {
            displayTitle = "Desserts";
        } else if (browseBy.equals("beverages")) {
            displayTitle = "Beverages";
        } else if (browseBy.equals("appetizers")) {
            displayTitle = "Appetizers";
        }
    }

    /**
     * returns the title of the browsing
     * @return 
     */
    public String getDisplayTitle() {
        return displayTitle;
    }

    /**
     * sets the display title of this browse group
     * @param displayTitle 
     */
    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    /**
     * returns the lazy model of this browse group
     * @return 
     */
    public LazyGroupDataModel getLazyModel() {
        return lazyModel;
    }

    /**
     * sets the lazy model for this browse group
     * @param lazyModel 
     */
    public void setLazyModel(LazyGroupDataModel lazyModel) {
        this.lazyModel = lazyModel;
    }
    
    
}
