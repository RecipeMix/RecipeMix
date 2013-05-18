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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Vendor holds the information for those third party groups who may want to use RecipeMix data.
 * @author Gustavo Rosas <grgustavorosas@gmail.com>
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Entity
@XmlRootElement
public class Vendor implements Serializable {
    
    // ======================================
    // =             Attributes             =
    // ======================================
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer apiKey;
    
    @Column
    private String company;

    // ======================================
    // =            Constructors            =
    // ======================================
    
    /**
     * Initializes a simple instance of Vendor
     */
    public Vendor() {
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================
    
    /**
     * Returns the api key
     * @return the integer api key of the vendor
     */
    public Integer getApiKey() {
        return apiKey;
    }

    /**
     * Sets the api key to the new key
     * @param apiKey new integer key
     */
    public void setApiKey(Integer apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Returns the company of the vendor
     * @return the string company of the vendor
     */
    public String getCompany() {
        return company;
    }
    
    /**
     * Sets the new company of the vendor
     * @param company the new company string
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * Returns hash for the vendor
     * @return integer hash for the vendor
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (apiKey != null ? apiKey.hashCode() : 0);
        return hash;
    }

    /**
     * Returns true if the api are equal
     * @param object
     * @return boolean 
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the apiKey fields are not set
        if (!(object instanceof Vendor)) {
            return false;
        }
        Vendor other = (Vendor) object;
        if ((this.apiKey == null && other.apiKey != null) || (this.apiKey != null && !this.apiKey.equals(other.apiKey))) {
            return false;
        }
        return true;
    }

    /**
     * Returns the vendor as a string
     * @return string vendor
     */
    @Override
    public String toString() {
        return "recipemix.models.Vendor[ id=" + apiKey + " ]";
    }
    
}
