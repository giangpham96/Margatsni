/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author conme
 */
@Entity
@Table(name = "m_post")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Post.findAll", query = "SELECT p FROM Post p ORDER BY p.timestamp DESC")
    , @NamedQuery(name = "Post.findAllByUid", query = "SELECT p FROM Post p WHERE p.uid = :uid ORDER BY p.timestamp DESC")
    , @NamedQuery(name = "Post.findByPostId", query = "SELECT p FROM Post p WHERE p.postId = :postId")
    , @NamedQuery(name = "Post.findByTimestamp", query = "SELECT p FROM Post p WHERE p.timestamp = :timestamp")
    , @NamedQuery(name = "Post.findBySrc", query = "SELECT p FROM Post p WHERE p.src = :src")
    , @NamedQuery(name = "Post.findByCaption", query = "SELECT p FROM Post p WHERE p.caption = :caption")
    , @NamedQuery(name = "Post.findByPermission", query = "SELECT p FROM Post p WHERE p.permission = :permission")
    , @NamedQuery(name = "Post.findByIsSharedPost", query = "SELECT p FROM Post p WHERE p.isSharedPost = :isSharedPost")})
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "post_id")
    private Long postId;
    @Column(name = "timestamp")
    private BigInteger timestamp;
    @Basic(optional = false)
    @Column(name = "src", length=8000)
    private String src;
    @Column(name = "caption",length=8000)
    private String caption;
    @Column(name = "permission")
    private Short permission;
    @Column(name = "isSharedPost")
    private Boolean isSharedPost;
    @ManyToMany(mappedBy = "postCollection")
    private Collection<User> userCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "postId")
    private Collection<Comment> commentCollection;
    @OneToMany(mappedBy = "sharedpostId")
    private Collection<Post> postCollection;
    @JoinColumn(name = "sharedpost_id", referencedColumnName = "post_id")
    @ManyToOne
    private Post sharedpostId;
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    @ManyToOne
    private User uid;

    public Post() {
    }

    public Post(Long postId) {
        this.postId = postId;
    }

    public Post(Long postId, String src) {
        this.postId = postId;
        this.src = src;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public BigInteger getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(BigInteger timestamp) {
        this.timestamp = timestamp;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Short getPermission() {
        return permission;
    }

    public void setPermission(Short permission) {
        this.permission = permission;
    }

    public Boolean getIsSharedPost() {
        return isSharedPost;
    }

    public void setIsSharedPost(Boolean isSharedPost) {
        this.isSharedPost = isSharedPost;
    }

    @XmlTransient
    public Collection<User> getUserCollection() {
        return userCollection;
    }

    public void setUserCollection(Collection<User> userCollection) {
        this.userCollection = userCollection;
    }

    @XmlTransient
    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    @XmlTransient
    public Collection<Post> getPostCollection() {
        return postCollection;
    }

    public void setPostCollection(Collection<Post> postCollection) {
        this.postCollection = postCollection;
    }

    public Post getSharedpostId() {
        return sharedpostId;
    }

    public void setSharedpostId(Post sharedpostId) {
        this.sharedpostId = sharedpostId;
    }

    public User getUid() {
        return uid;
    }

    public void setUid(User uid) {
        this.uid = uid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (postId != null ? postId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Post)) {
            return false;
        }
        Post other = (Post) object;
        if ((this.postId == null && other.postId != null) || (this.postId != null && !this.postId.equals(other.postId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "models.Post[ postId=" + postId + " ]";
    }
    
}
