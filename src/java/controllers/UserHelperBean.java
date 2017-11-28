/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
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
}
