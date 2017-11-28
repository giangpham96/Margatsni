/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import controllers.HelperBean;
import controllers.SecureHelper;
import javax.ejb.EJB;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import models.User;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author conme
 */
@Path("signup")
public class SignupResource {
    
    @EJB
    HelperBean hb;
    
    @POST
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
            User user = hb.signUp(uname, email, password);
            if (user == null)
                return "{\"error\":\"internal error, cannot create user\"}";
                
            String auth_session = SecureHelper.encrypt(String.valueOf(System.currentTimeMillis() + 600000));
            String auth_token = SecureHelper.encrypt(String.valueOf(user.getUid()));

            return new JSONObject()
                    .put("auth-session", auth_session)
                    .put("auth-token", auth_token)
                    .toString();
        } catch (Exception ex) {
            return "{\"error\":\""+ex+"\"}";
        }
    }

}
