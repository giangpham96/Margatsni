/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serviceControllers;

import dataAccessObjects.UserHelperBean;
import dataAccessObjects.SecureHelper;
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
    UserHelperBean hb;
    
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
            User user = hb.addUser(uname, email, password);
            if (user == null)
                return "{\"error\":\"internal error, cannot create user\"}";
                
//            String auth_session = SecureHelper.encrypt(String.valueOf(System.currentTimeMillis() + 600000));
//            String auth_token = SecureHelper.encrypt(String.valueOf(user.getUid()));

            String originalAuth = user.getUid()+"::"+System.currentTimeMillis() + 600000;
            String auth = SecureHelper.encrypt(originalAuth);
            
            return new JSONObject()
                    .put("auth-token", auth)
                    .toString();
        } catch (Exception ex) {
            return "{\"error\":\"internal error, cannot create user\"}";
        }
    }

}