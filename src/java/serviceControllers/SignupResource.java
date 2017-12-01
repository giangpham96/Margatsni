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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(
            @FormParam("uname") String uname,
            @FormParam("email") String email,
            @FormParam("password") String password) {

        if (hb.isEmailUsed(email)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"this email has already been used\"}")
                    .build();
        }

        if (hb.isUsernameUsed(uname)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"this username has already been taken\"}")
                    .build();
        }
        try {
            User user = hb.addUser(uname, email, password);
            if (user == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"message\":\"internal error occurs\"}")
                        .build();
            }

            String originalAuth = user.getUid() + "::" + System.currentTimeMillis() + 600000;
            String auth = SecureHelper.encrypt(originalAuth);

            return Response.status(Response.Status.OK)
                    .entity(new JSONObject()
                            .put("auth-token", auth)
                            .toString())
                    .build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"internal error occurs\"}")
                    .build();
        }
    }

}
