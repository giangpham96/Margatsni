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

            em.getEntityManagerFactory().getCache().evictAll();
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
        em.getEntityManagerFactory().getCache().evictAll();
        return em.createNamedQuery("Post.findAll")
                .setFirstResult(page * 24)
                .setMaxResults(24)
                .getResultList();
    }

    public List<Post> getTopPostsInPage(int page) {

        em.getEntityManagerFactory().getCache().evictAll();
        return (List<Post>) em.createNativeQuery("select m_post.post_id, m_post.timestamp, m_post.src, m_post.caption, m_post.uid, COUNT(m_like.post_id) as likes FROM m_post left JOIN m_like ON m_post.post_id = m_like.post_id GROUP BY m_post.post_id ORDER BY likes DESC LIMIT 24 OFFSET " + page * 24,
                Post.class)
                .getResultList();
    }

    public List<Post> getPostsByUidInPage(long uid, int page) {

        em.getEntityManagerFactory().getCache().evictAll();
        User user = (User) em.createNamedQuery("User.findByUid")
                .setParameter("uid", uid)
                .getSingleResult();
        return em.createNamedQuery("Post.findAllByUid")
                .setParameter("uid", user)
                .setFirstResult(page * 24)
                .setMaxResults(24)
                .getResultList();
    }

    public Post getPostById(long postId) {
        try {
            em.getEntityManagerFactory().getCache().evictAll();
            return (Post) em.createNamedQuery("Post.findByPostId")
                    .setParameter("postId", postId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Post update(Post p) {

        em.getEntityManagerFactory().getCache().evictAll();
        em.merge(p);
        return p;
    }

    public void delete(Post post) {

        em.getEntityManagerFactory().getCache().evictAll();
        Post p = em.merge(post);
        em.remove(p);
        for (User user : p.getUserCollection()) {
            user.getPostCollection().remove(p);
            em.merge(user);
        }
    }
}
