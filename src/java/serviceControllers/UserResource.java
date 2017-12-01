/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serviceControllers;

import dataAccessObjects.SecureHelper;
import dataAccessObjects.UserHelperBean;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import models.User;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author conme
 */
@Path("user")
public class UserResource {

    @EJB
    private UserHelperBean hb;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
            @HeaderParam("auth-token") String authToken,
            @QueryParam("search") String search
    ) {

        List<User> users = hb.searchUser(search);

        try {
            long uid = -1;
            User user = new User(-1L);
            if (authToken != null) {
                String originalAuth = SecureHelper.decrypt(authToken);

                String[] authInfo = originalAuth.split("::");

                long expired = Long.valueOf(authInfo[1]);
                if (expired < System.currentTimeMillis()) {
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"message\":\"session expired\"}")
                            .build();
                }
                uid = Long.valueOf(authInfo[0]);

                user = hb.getUserById(uid);

                if (user == null) {
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"message\":\"user not found\"}")
                            .build();
                }
            }

            JSONArray json = new JSONArray();

            for (User u : users) {
                if (u.getUid() == uid) {
                    continue;
                }
                JSONObject juser = new JSONObject();
                juser.put("uid", SecureHelper.encrypt(String.valueOf(u.getUid())));
                juser.put("uname", u.getUname());
                if (u.getProfilePic()!=null)
                    juser.put("profile_pic", "http://10.114.32.118/profile_pic/"+u.getProfilePic());
                json.put(juser);
            }
            
            return Response.status(Response.Status.OK)
                            .entity(json.toString())
                            .build();

        } catch (Exception ex) {
            return Response.status(Response.Status.OK)
                            .entity("[]")
                            .build();
        }
    }
}
