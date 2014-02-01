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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import recipemix.models.Recipe;
import recipemix.models.Tag;

/**
 **Enterprise JavaBean belonging to tag entity
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
@Stateless
public class TagEJB {

    @PersistenceContext(unitName = "RecipeMixPU")
    private EntityManager em;
    private static final String DELIMITER = ",";
    /**
     * Below member expression got from StackOverflow here :
     * http://stackoverflow.com/questions/4731055/whitespace-matching-regex-java
     *
     * Apparently Java doesn't play nice with just \s
     */
    private static final String whitespace_chars = "["
            + "" // dummy empty string for homogeneity
            + "\\u0009" // CHARACTER TABULATION
            + "\\u000A" // LINE FEED (LF)
            + "\\u000B" // LINE TABULATION
            + "\\u000C" // FORM FEED (FF)
            + "\\u000D" // CARRIAGE RETURN (CR)
            + "\\u0020" // SPACE
            + "\\u0085" // NEXT LINE (NEL) 
            + "\\u00A0" // NO-BREAK SPACE
            + "\\u1680" // OGHAM SPACE MARK
            + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
            + "\\u2000" // EN QUAD 
            + "\\u2001" // EM QUAD 
            + "\\u2002" // EN SPACE
            + "\\u2003" // EM SPACE
            + "\\u2004" // THREE-PER-EM SPACE
            + "\\u2005" // FOUR-PER-EM SPACE
            + "\\u2006" // SIX-PER-EM SPACE
            + "\\u2007" // FIGURE SPACE
            + "\\u2008" // PUNCTUATION SPACE
            + "\\u2009" // THIN SPACE
            + "\\u200A" // HAIR SPACE
            + "\\u2028" // LINE SEPARATOR
            + "\\u2029" // PARAGRAPH SEPARATOR
            + "\\u202F" // NARROW NO-BREAK SPACE
            + "\\u205F" // MEDIUM MATHEMATICAL SPACE
            + "\\u3000" // IDEOGRAPHIC SPACE
            + "]";

    public List<Tag> splitTags(String input) {

        //Throw out whitespace and split into a list of strings
        String withoutWhitespace = input.replaceAll(whitespace_chars, "");
        List<String> strings = Arrays.asList(withoutWhitespace.split(DELIMITER));
        List<Tag> results = new ArrayList<Tag>();
        ListIterator i = strings.listIterator();

        while (i.hasNext()) {
            String s = (String) i.next();
            if (!s.equals("")) {
                Tag t = findTag(s);

                if (t == null) {
                    t = new Tag();
                    //truncate if too long
                    if (s.length() > 50) {
                        s = s.substring(0, 49);
                    }
                    t.setTagName(s.toLowerCase());
                }
                results.add(t);
            }
        }
        return results;
    }

    public Integer getWeight(Tag t) {
        Integer weight = 0;

        //count recipes with the tag
        Query q =
                em.createQuery(
                "SELECT COUNT(r) FROM Recipe r "
                + "INNER JOIN r.tags t "
                + "WHERE t.tagName = :tag",
                Recipe.class);
        q.setParameter("tag", t.getTagName());
        try {
            weight += ((Long) q.getSingleResult()).intValue();
        } catch (NoResultException nre) {
            //Do nothing
        }

        //Count groups with the tag

        q = em.createQuery(
                "SELECT COUNT(g) FROM Groups g "
                + "INNER JOIN g.tags t "
                + "WHERE t.tagName = :tag",
                Recipe.class);
        q.setParameter("tag", t.getTagName());
        try {
            weight += ((Long) q.getSingleResult()).intValue();
        } catch (NoResultException nre) {
            //Do nothing
        }

        return weight;
    }

    /**
     * returns the list of all the tags in the db
     * @return 
     */
    public List<Tag> findAllTags() {
        TypedQuery<Tag> query = em.createNamedQuery("findAllCategoryTags", Tag.class);
        return query.getResultList();
    }

    public Tag findTag(String tag) {
        TypedQuery<Tag> query = em.createQuery(
                "SELECT t FROM Tag t "
                + "WHERE t.tagName = :tag", Tag.class);
        query.setParameter("tag", tag);
        Tag t;
        try {
            t = query.getSingleResult();
        } catch (NoResultException nre) {
            t = null;
        }

        return t;
    }

    /**
     * persists the tag entity to the db
     * @param cTag
     * @return 
     */
    public Tag createTag(Tag cTag) {
        em.persist(cTag);
        em.flush();
        return cTag;
    }

    /**
     * edits the tag to the the tag given
     * @param cTag
     * @return 
     */
    public Tag editTag(Tag cTag) {
        em.merge(cTag);
        em.flush();
        return cTag;
    }

    /**
     * removes the given tag from the db
     * @param cTag 
     */
    public void removeTag(Tag cTag) {
        em.remove(em.merge(cTag));
        em.flush();
    }

    /**
     * finds a tab by its id
     * @param id
     * @return 
     */
    public Tag findTag(Integer id) {
        return em.find(Tag.class, id);
    }
}
