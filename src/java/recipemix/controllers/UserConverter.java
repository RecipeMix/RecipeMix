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

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Named;
import recipemix.beans.UsersEJB;
import recipemix.models.Users;

/**
 * Used in for pickList component of PrimeFaces 3.5
 * Adapted from Jairo's GroupConverter
 *
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@Named
public class UserConverter implements Converter {

    @EJB
    private UsersEJB userEJB;

    /**
     * converts a parameter from the xhtml to a user entity
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
                Users u = userEJB.findUser(submittedValue);
                return u;

            } catch (NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid user"));
            }
        }

    }

    /**
     * returns the object as a string
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
            return String.valueOf(((Users) value).getUserName());
        }
    }
}
