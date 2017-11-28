/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import controllers.HelperBean;
import controllers.SecureHelper;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import models.User;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author conme
 */
@Path("signup")
public class SignupResource {

    @PersistenceContext
    private EntityManager em;
    
    @EJB
    HelperBean hb;
    
    @POST
    @Path("/signup")
    public String post(
            @FormParam("uname") String uname,
            @FormParam("email") String email,
            @FormParam("password") String password) {

        if (hb.isEmailUsed(email)) {
            return "{\"error\":\"this email has already been used\"}";
        }

        if (hb.isUsernameUsed(uname)) {
            return "{\"error\":\"this username has already been taken\"}";
        }
        try {
            User user = new User();
            user.setUname(uname);
            user.setEmail(email);
            password = SecureHelper.encrypt(password);
            user.setPassword(password);
            em.persist(user);
            String auth_session = SecureHelper.encrypt(String.valueOf(System.currentTimeMillis() + 60000));
            String auth_token = SecureHelper.encrypt(String.valueOf(user.getUid()));

            return new JSONObject()
                    .put("auth-session", auth_session)
                    .put("auth-token", auth_token)
                    .toString();
        } catch (Exception ex) {
            return "{\"error\":\"internal error, cannot create user\"}";
        }
    }

}
