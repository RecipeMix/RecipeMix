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
package recipemix.models;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The ProfessionalInfo entity holds the additional information for a user who
 * is a professional and also confirmation status. The ProfessionalInfo entity
 * is associated with Users.
 *
 * @autor Steven Paz <steve.a.paz@gmail.com>
 * @author Alex Chavez <alex@alexchavez.net>
 */
@Entity
@Table(name = "PROFESSIONAL")
@XmlRootElement
public class ProfessionalInfo implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROFESSIONAL_ID")
    private Integer professionalId;
    
    @Column
    private String employer;
    
    @Column(name = "YEARS_OF_EXPERIENCE")
    private int yearsOfExperience;
    
    @Column(length = 2000)
    private String confirmationRequest;
    
    @Column
    private boolean confirmed;
    
    @OneToOne(mappedBy = "professionalInfo")
    private Users correspondingUser;

    // ======================================
    // =            Constructors            =
    // ======================================
    /**
     * Initializes a simple instance of ProfessionalInfo
     */
    public ProfessionalInfo() {
        this.employer = null;
        this.yearsOfExperience = 0;
        confirmed = false;
    }

    /**
     * Initializes an instance of ProfessionalInfo with employer and
     * yearsOfEsperience
     *
     * @param employer string name of employer
     * @param yearsOfExperience int number of years of experience
     */
    public ProfessionalInfo(String employer, int yearsOfExperience) {
        this.employer = employer;
        this.yearsOfExperience = yearsOfExperience;
        confirmed = false;
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================
    /**
     * Returns the id of the professional
     *
     * @return professionalId of professional
     */
    public Integer getProfessionalId() {
        return professionalId;
    }

    /**
     * Returns the name of the employer
     *
     * @return employer of professional
     */
    public String getEmployer() {
        return employer;
    }

    /**
     * Sets the employer name of professional to employer
     *
     * @param employer new string employer of professional
     */
    public void setEmployer(String employer) {
        this.employer = employer;
    }

    /**
     * Returns years of experience of the professional
     *
     * @return yearsOfExperience of professional
     */
    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    /**
     * Sets years of experience of professional to yearsOfEsperience
     *
     * @param yearsOfExperience new integer for yearsOfExperience of
     * professional
     */
    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    /**
     * Returns the User corresponding to this ProfessionalInfo
     *
     * @return the corresspondingUser
     */
    public Users getCorrespondingUser() {
        return correspondingUser;
    }

    /**
     * Sets this ProfessionalInfo entity's User
     *
     * @param correspondingUser
     */
    public void setCorrespondingUser(Users correspondingUser) {
        this.correspondingUser = correspondingUser;
    }

    /*
     * Returns true if the User has been confirmed as a professional
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Sets confirmation status
     *
     * @param confirmed
     */
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    /**
     * Returns the User's professional confirmation request
     * @return 
     */
    public String getConfirmationRequest() {
        return confirmationRequest;
    }

    /**
     * Sets the professional confirmation request
     * @param confirmationRequest 
     */
    public void setConfirmationRequest(String confirmationRequest) {
        this.confirmationRequest = confirmationRequest;
    }
}