/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccessObjects;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import models.Post;
import models.User;

/**
 *
 * @author conme
 */
@Stateless
public class UserHelperBean {

    @PersistenceContext
    private EntityManager em;

    public UserHelperBean() {

    }

    public boolean isEmailUsed(String email) {
        try {
            User user = (User) em.createNamedQuery("User.findByEmail")
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }

    public boolean isUsernameUsed(String username) {
        try {
            User user = (User) em.createNamedQuery("User.findByUname")
                    .setParameter("uname", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }

    public boolean isIdValid(long uid) {
        try {
            User user = (User) em.createNamedQuery("User.findByUid")
                    .setParameter("uid", uid)
                    .getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }

    public User addUser(String uname, String email, String password) {
        try {
            User user = new User();
            user.setUname(uname);
            user.setEmail(email);
            password = SecureHelper.encrypt(password);
            user.setPassword(password);
            user.setIsAdmin(false);
            em.persist(user);
            return user;
        } catch (Exception ex) {
            return null;
        }
    }

    public User getUser(String email, String password) {
        try {
            return (User) em.createNamedQuery("User.authorized")
                    .setParameter("email", email)
                    .setParameter("password", SecureHelper.encrypt(password))
                    .getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }
    
    public User getUserById(long uid) {
        try {
            return (User) em.createNamedQuery("User.findByUid")
                    .setParameter("uid", uid)
                    .getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    public Post like(User user, Post post) {
        
        if (user.getPostCollection().contains(post)) {
            user.getPostCollection().remove(post);
            post.getUserCollection().remove(user);
        } else {
            user.getPostCollection().add(post);
            post.getUserCollection().add(user);
        }
        em.merge(user);
        Post merged = em.merge(post);
        return merged;
    }

    public User update(User user) {
        User u = em.merge(user);
        return u;
    }
}
