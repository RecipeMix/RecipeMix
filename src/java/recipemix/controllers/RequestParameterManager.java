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
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * Application scoped bean used to get the value in
 * a request paramater map
 * 
 * Example: "recipe", "groupID", "username", etc.
 * 
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@Named
@ApplicationScoped
public class RequestParameterManager implements Serializable {
    
    /**
     * returns the key passed as a parameter from the xhtml page converted as a string
     * @param key id used in the xhtml
     * @return string parameter
     */
    public String get(String key) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
	}
    
}
