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
 * The NewsPost entity holds information for a news entry.
 * The NewsPost is associated with Users who post the news.
 * @author Gustavo Rosas <grgustavorosas@gmail.com>
 */
@Entity
@Table(name = "NEWS_POST")
@NamedQueries({
    @NamedQuery(name = NewsPost.FIND_ALL_NEWS_POSTS, query = "SELECT n FROM NewsPost n")
})
@XmlRootElement
public class NewsPost implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================

    private static final long serialVersionUID = 1L;
    public static final String FIND_ALL_NEWS_POSTS = "findAllNewsPosts";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer newsId;

    @Column (name = "NEWS_TITLE", length = 50)
    private String newsTitle;

    @Column (name = "NEWS_BODY", length = 2500)
    private String newsBody;
    //needed
    @ManyToOne
    @JoinColumn (name = "USER_NAME")
    private Users poster;
    
    @Column (name= "POST_DATE")
    private long postDate; 

    // ======================================
    // =            Constructors            =
    // ======================================

    /**
     * default constructor
     */
    public NewsPost() {
    }

    /**
     * Constructor with all attributes as parameters
     * @param newsId
     * @param newsTitle
     * @param newsBody
     */
    public NewsPost(Integer newsId, String newsTitle, String newsBody) {
        this.newsId = newsId;
        this.newsTitle = newsTitle;
        this.newsBody = newsBody;
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================

    /**
     * returns current newsID
     * @return int newsId
     */
    public Integer getNewsId() {
        return newsId;
    }

    /**
     * returns current newsTitle
     * @return String newsTitle
     */
   public String getNewsTitle() {
        return newsTitle;
    }

   /**
    * returns current newsBody
    * @return String newsBody
    */
    public String getNewsBody() {
        return newsBody;
    }

    /**
     * The newsId of this entity will be changed to the parameter newsId
     * @param newsId new newsId
     */
    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }

    /**
     * The newsTitle of this entity will be changed to the parameter newsTitle
     * @param newsTitle new news title
     */
    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    /**
     * The newsBody of this entity will be changed to the parameter newsBody
     * @param newsBody new news body
     */
    public void setNewsBody(String newsBody) {
        this.newsBody = newsBody;
    }

    /**
     * Returns the poster of the news
     * @return poster of NewsPost
     */
    public Users getPoster() {
        return poster;
    }

    /**
     * Sets the poster of the news to poster
     * @param poster new User of NewsPost
     */
    public void setPoster(Users poster) {
        this.poster = poster;
    }

    /**
     * Returns the date when the news was created
     * @return long integer postDate
     */
    public long getPostDate() {
        return postDate;
    }

    /**
     * sets the postDate with the date when the news was created
     * @param postDate long integer postDate
     */
    public void setPostDate(long postDate) {
        this.postDate = postDate;
    }
}
