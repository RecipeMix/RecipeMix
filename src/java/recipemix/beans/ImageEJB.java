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
package recipemix.beans;

import java.beans.*;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import recipemix.models.Image;
import recipemix.models.Users;

/**
 **Enterprise JavaBean belonging to image entity
 * @author Gustavo Rosas <grgustavorosas@gmail.com>
 */
@Stateless
public class ImageEJB {
    @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;
    
    public Image createImage(Image cimage){
        em.persist(cimage);
        return cimage;
    }
    
    public Image editImage(Image cimage){
        em.merge(cimage);
        return cimage;
    }
    
    public Image removeImage(Image cimage){
        em.remove(em.merge(cimage));
        return cimage;
    }
    
    public Image findImage(Integer id){
        return em.find(Image.class, id);
    }
    
    public List<Image> getRecipeImages(Integer recipeId) {
        TypedQuery<Image> query = em.createNamedQuery("findImagesByRecipeId", Image.class).setParameter("recipeId", recipeId);
        List<Image> result = query.getResultList();
        return result;
    }
    
    public Image getProfileImage(String username) {
        TypedQuery <Image> query = em.createNamedQuery("findImageByUsername", Image.class).setParameter("imageOwner", username);
        Image result = (Image) query.getSingleResult();
        return result;
    }
    
    public Image getGroupImage(Integer groupId) {
        TypedQuery <Image> query = em.createNamedQuery("findImagesByGroupId", Image.class).setParameter("groupID", groupId);
        Image result = (Image) query.getSingleResult();
        return result;
    }
    
    //works fine and retreives the image if given the path
    public Image getImageByPath (String path) {
        TypedQuery <Image> query = em.createNamedQuery("findImageByPath", Image.class).setParameter("imagePath", path);
        Image result = (Image)query.getSingleResult();
        return result;
    }
}
