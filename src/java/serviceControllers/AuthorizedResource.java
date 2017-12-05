/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serviceControllers;

import dataAccessObjects.UserHelperBean;
import dataAccessObjects.SecureHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.Produces;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import models.User;
import org.json.JSONException;
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
    public Response post(@FormParam("email") String email,
            @FormParam("password") String password) {

        if (email == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"bad request\"}")
                    .build();
        }

        User user = hb.getUser(email, password);

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"wrong email or password\"}")
                    .build();
        }

        String originalAuth = user.getUid() + "::" + System.currentTimeMillis() + 600000;
        String auth;
        try {
            auth = SecureHelper.encrypt(originalAuth);
            return Response.status(Response.Status.OK)
                    .entity(new JSONObject()
                            .put("auth-token", auth)
                            .toString())
                    .build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"internal error occurs\"}")
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
            @HeaderParam("auth-token") String authToken) {
        if (authToken == null || authToken.equals("")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"user not found\"}")
                    .build();
        }

        String originalAuth;
        try {
            originalAuth = SecureHelper.decrypt(authToken);
        } catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"invalid token\"}")
                    .build();
        }

        String[] authInfo = originalAuth.split("::");

        long expired = Long.valueOf(authInfo[1]);
        if (expired < System.currentTimeMillis()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"session expired\"}")
                    .build();
        }

        long uid = Long.valueOf(authInfo[0]);

        if (!hb.isIdValid(uid)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"user not found\"}")
                    .build();
        }

        try {
            return Response.status(Response.Status.OK)
                    .entity(new JSONObject()
                            .put("auth-token", authToken)
                            .toString())
                    .build();
        } catch (JSONException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"internal error occurs\"}")
                    .build();
        }
    }
}
