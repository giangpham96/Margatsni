/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servletControllers;

import dataAccessObjects.PostHelperBean;
import dataAccessObjects.SecureHelper;
import dataAccessObjects.UserHelperBean;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.User;

/**
 *
 * @author conme
 */
@WebServlet(name = "ProfilePic", urlPatterns = {"/api/profile/profilepic"})
@MultipartConfig(location = "/var/www/html/profile_pic")
public class ProfilePic extends HttpServlet {

    @EJB
    UserHelperBean hb;
    @EJB
    PostHelperBean pb;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String authToken = request.getHeader("auth-token");
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {

            if (authToken == null) {
                response.setStatus(400);
                out.println("{\"message\":\"bad request\"}");
                return;
            }

            String originalAuth = SecureHelper.decrypt(authToken);

            String[] authInfo = originalAuth.split("::");

//            long expired = Long.valueOf(SecureHelper.decrypt(authSession));
            long expired = Long.valueOf(authInfo[1]);
            if (expired < System.currentTimeMillis()) {
                response.setStatus(401);
                out.println("{\"message\":\"session expired\"}");
                return;
            }

//            long uid = Long.valueOf(SecureHelper.decrypt(authToken));
            long uid = Long.valueOf(authInfo[0]);

            User user = hb.getUserById(uid);

            if (user == null) {
                response.setStatus(401);
                out.println("{\"message\":\"user not found\"}");
                return;
            }

            String fileName = "profile_" + authInfo[0] + ".jpg";
            
            if (request.getPart("file")==null){
                response.setStatus(400);
                out.println("{\"message\":\"bad request\"}");
                return;
            }
            
            request.getPart("file").write(fileName);

            user.setProfilePic(fileName);

            user = hb.update(user);
            response.setStatus(200);

            out.println("{\"profile_pic\":\"http://10.114.32.118/profile_pic/" + user.getProfilePic() + "\"}");

        } catch (Exception ex) {
            response.setStatus(500);
        }
    }
}
