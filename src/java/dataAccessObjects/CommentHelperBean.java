/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccessObjects;

import java.math.BigInteger;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import models.Comment;
import models.Post;
import models.User;

/**
 *
 * @author conme
 */
@Stateless
@LocalBean
public class CommentHelperBean {

    @PersistenceContext
    private EntityManager em;

    public Comment addComment(long uid, long postId, String content) {
        try {
            
            em.getEntityManagerFactory().getCache().evictAll();
            User user = (User) em.createNamedQuery("User.findByUid")
                    .setParameter("uid", uid)
                    .getSingleResult();

            Post post = (Post) em.createNamedQuery("Post.findByPostId")
                    .setParameter("postId", postId)
                    .getSingleResult();

            Comment comment = new Comment();
            comment.setUid(user);
            comment.setPostId(post);
            comment.setContent(content);
            comment.setTimestamp(System.currentTimeMillis());

            em.persist(comment);
            return comment;
        } catch (Exception ex) {
            return null;
        }

    }

    public Comment updateComment(Comment c) {
        em.getEntityManagerFactory().getCache().evictAll();
        em.merge(c);
        return c;
    }

    public Comment getCommentById(long id) {
        try {
        em.getEntityManagerFactory().getCache().evictAll();
        return (Comment) em.createNamedQuery("Comment.findByCommentId")
                    .setParameter("commentId", id)
                    .getSingleResult();
        } catch (Exception ex) {
            return null;
        }

    }

    public void deleteComment(Comment c) {
        em.getEntityManagerFactory().getCache().evictAll();
        em.remove(em.merge(c));
    }
}
