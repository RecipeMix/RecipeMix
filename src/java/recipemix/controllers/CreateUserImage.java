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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import recipemix.beans.ImageEJB;
import recipemix.beans.UsersEJB;
import recipemix.models.Image;
import recipemix.models.Users;

/**
 *
 * @author Gustavo Rosas <grgustavorosas@gmail.com>
 */
@Named
@RequestScoped
public class CreateUserImage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("CreateImage");
    
    @EJB
    private ImageEJB imageEJB;
    @EJB
    private UsersEJB userEJB;
    @Inject
    private UserIdentity ui;
    @Inject
    private RequestParameterManager rpm;
    
    private Image newImage;
    
    private String description;
    
    private String imageCaption;
    
    private String imagePath;

    
    CreateUserImage() {
        newImage = new Image();
    }
    
    /**
     * creates the folder and uploads the image file to the local files
     * @param event
     * @return 
     */
    public void doCreateUserImage(FileUploadEvent event){
        //get the user identity
        Users owner = ui.getUser();
        Image i = owner.getImage();
        String userName = owner.getUserName();
        String destination = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        //create the directory to transfer the file to if it is non existent yet
        File file = new File(destination+File.separator+"uploads"+File.separator+"profile"+File.separator+userName);
        String abspath = file.getAbsolutePath()+ File.separator;
        if(!file.exists()){
            if(file.mkdirs());
        }
        this.imageCaption = "This is "+userName+"'s profile picture.";
        //create a new name for the Image
        String imageNameType;
        if(event.getFile().getContentType().equalsIgnoreCase("image/jpeg")){
            imageNameType = userName+".jpeg";
        }else if(event.getFile().getContentType().equalsIgnoreCase("image/gif")){
            imageNameType = userName+".gif";
        }else {
            imageNameType = userName+".png";
        }
        this.description = imageNameType;
        //image path for jsf to recognize
        this.imagePath = "/uploads/profile/"+userName+"/"+imageNameType;
        // make changes to the newImage and persist it       
        try {
            //call the function to copy the image to the destination bit by bit.         
            copyFile(abspath, imageNameType , event.getFile().getInputstream());
            //set the path of the Image
            i.setImagePath(this.imagePath);
            i.setCaption(this.imageCaption);
            i.setImageOwner(owner);
            i.setImageName(imageNameType);
            i.setDescription(this.description);
            i = imageEJB.editImage(i);
            owner.setImage(i);
            userEJB.editUser(owner);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Success!", "Your image was uploaded successfully."));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * uploads a file to the local files
     * @param destination
     * @param fileName
     * @param in 
     */
    public void copyFile(String destination, String fileName, InputStream in) {
        try {
            // write the inputStream to a FileOutputStream
            OutputStream out = new FileOutputStream(new File(destination + fileName));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();
            System.out.println("New file created!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * sets the caption and description of the image
     */
    public void setImageFields(){
        Users owner = ui.getUser();
        newImage = owner.getImage();
        newImage.setCaption(this.imageCaption);
        newImage.setDescription(this.description);
        owner.setImage(this.newImage);
        userEJB.editUser(owner);
        newImage = imageEJB.editImage(this.newImage);
    }

    public String getCaption() {
        return imageCaption;
    }

    public void setCaption(String imageCaption) {
        this.imageCaption = imageCaption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
