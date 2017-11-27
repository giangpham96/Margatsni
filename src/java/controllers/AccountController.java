/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

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
public class AccountController {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager em;

    public AccountController() {

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

    public User signUp(String username, String email, String password) {
        User user = new User();
        user.setUname(username);
        user.setEmail(email);
        user.setPassword(password);
        em.persist(user);
        return user;
    }
    
    public User logIn(String email, String password) {
        try {
            User user = (User) em.createNamedQuery("User.authorized")
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
            return user;
        } catch (NoResultException e) {
            return null;
        }
    }
}
