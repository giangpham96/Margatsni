/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serviceControllers;

import dataAccessObjects.UserHelperBean;
import dataAccessObjects.SecureHelper;
import javax.ejb.EJB;
import javax.ws.rs.Produces;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import models.User;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author conme
 */
@Path("authorized")
public class AuthorizedResource {

    @EJB
    private UserHelperBean hb;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String post(@FormParam("email") String email,
            @FormParam("password") String password) {
        try {
            User user = hb.getUser(email, password);

            if (user == null) {
                return "{\"error\":\"wrong email or password\"}";
            }
            
            String originalAuth = user.getUid()+"::"+System.currentTimeMillis() + 600000;
            String auth = SecureHelper.encrypt(originalAuth);
            
//            String authSession = SecureHelper.encrypt(String.valueOf(System.currentTimeMillis() + 600000));
//            String authToken = SecureHelper.encrypt(String.valueOf(user.getUid()));

            return new JSONObject()
                    .put("auth-token", auth)
                    .toString();
        } catch (Exception ex) {
            return "{\"error\":\"internal error, cannot authorize user\"}";
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(
            @HeaderParam("auth-token") String authToken) {
        try {
            
            String originalAuth = SecureHelper.decrypt(authToken);
            
            String[] authInfo = originalAuth.split("::");
            
            long expired = Long.valueOf(authInfo[1]);
            if(expired < System.currentTimeMillis()) {
                return "{\"message\":\"session expired\"}";
            }
            
            long uid = Long.valueOf(authInfo[0]);
            
            if (!hb.isIdValid(uid)) {
                return "{\"message\":\"user not found\"}";
            }
            
            return new JSONObject()
                    .put("auth-token", authToken)
                    .toString();
        } catch (Exception ex) {
            return "{\"error\":\"internal error, log in again\"}";
        }
    }
}
