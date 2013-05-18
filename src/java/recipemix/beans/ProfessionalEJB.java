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

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import recipemix.models.ProfessionalInfo;

/**
 **Enterprise JavaBean belonging to professional entity
 * @author Jairo Lopez <jairo.lopez00@gmail.com>
 */
@Stateless
public class ProfessionalEJB {

   @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;
    
     public List<ProfessionalInfo> findProfessionals() {
        TypedQuery<ProfessionalInfo> query = em.createNamedQuery("findAllProfessionals", ProfessionalInfo.class);
        return query.getResultList();
    }
    public ProfessionalInfo createProfessional (ProfessionalInfo professional){
        em.persist(professional);
        return professional;
    }
    
    public ProfessionalInfo editProfessional(ProfessionalInfo professional) {
        em.merge(professional);
        return professional;
    }
    
    public void removeProfessional (ProfessionalInfo professional){
        em.remove(em.merge(professional));
    }
    
    public ProfessionalInfo findProfessional (Integer id){
        return em.find(ProfessionalInfo.class,id);
    }

}
