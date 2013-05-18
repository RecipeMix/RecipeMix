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

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Named;
import recipemix.beans.GroupEJB;
import recipemix.models.Groups;


/**
 *
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Named("groupConverter")
public class GroupConverter implements Converter{
    
@EJB
private GroupEJB groupsEJB;
    
    /**
     * returns a group after giving it a parameter key
     * @param facesContext
     * @param component
     * @param submittedValue
     * @return 
     */
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
                
                    int number = Integer.parseInt(submittedValue);
                    Groups g=groupsEJB.findGroup(number);
                    return g;
  
            } catch(NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid group"));
            }
        }

    }

    /**
     * returns the group as a string
     * @param facesContext
     * @param component
     * @param value
     * @return 
     */
    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((Groups) value).getGroupID());
        }
    }

    
}
