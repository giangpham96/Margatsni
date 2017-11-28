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
public class HelperBean {

    @PersistenceContext
    private EntityManager em;

    public HelperBean() {

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
}
