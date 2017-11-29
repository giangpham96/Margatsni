/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccessObjects;

import java.math.BigInteger;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import models.Post;
import models.User;
import org.json.JSONObject;

/**
 *
 * @author conme
 */
@Stateless
public class PostHelperBean {

    @PersistenceContext
    private EntityManager em;

    public Post addPost(long uid, String src, String caption,
            short permission, boolean isSharedPost, long sharedPostId) {
        try {
            Post post = new Post();

            User user = (User) em.createNamedQuery("User.findByUid")
                    .setParameter("uid", uid)
                    .getSingleResult();
            post.setUid(user);
            post.setCaption(caption);
            post.setSrc(src);
            post.setPermission(permission);
            post.setIsSharedPost(isSharedPost);
            post.setTimestamp(BigInteger.valueOf(System.currentTimeMillis()));
            if (isSharedPost) {
                Post sharedPost = (Post) em.createNamedQuery("Post.findByPostId")
                        .setParameter("postId", sharedPostId)
                        .getSingleResult();
                post.setSharedpostId(sharedPost);
            }

            em.persist(post);
            return post;
        } catch (Exception ex) {
            return null;
        }

    }

    public List<Post> getPostsInPage(int page) {
        return em.createNamedQuery("Post.findAll")
                .setFirstResult(page * 20)
                .setMaxResults(20)
                .getResultList();
    }

    public List<Post> getPostsByUidInPage(long uid, int page) {
        User user = (User) em.createNamedQuery("User.findByUid")
                .setParameter("uid", uid)
                .getSingleResult();
        return em.createNamedQuery("Post.findAllByUid")
                .setParameter("uid", user)
                .setFirstResult(page * 20)
                .setMaxResults(20)
                .getResultList();
    }

    public Post getPostById(long postId) {
        try {
            return (Post) em.createNamedQuery("Post.findByPostId")
                    .setParameter("postId", postId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
