/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import controllers.PostHelperBean;
import controllers.UserHelperBean;
import controllers.SecureHelper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.Comment;
import models.User;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author conme
 */
@WebServlet(name = "Post", urlPatterns = {"/api/post"})
@MultipartConfig(location = "/var/www/html/margatsni")
public class Post extends HttpServlet {

    @EJB
    UserHelperBean hb;
    @EJB
    PostHelperBean pb;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String authSession = request.getHeader("auth-session");
        String authToken = request.getHeader("auth-token");
        
        String caption = request.getParameter("caption");
        
        short permission = 0;
        
        boolean isSharedPost = false;
        
        response.setContentType("application/json");
        try(PrintWriter out = response.getWriter()) {

            long expired = Long.valueOf(SecureHelper.decrypt(authSession));
            if (expired < System.currentTimeMillis()) {
                out.println("{\"message\":\"session expired\"}");
                return;
            }
            
            
            long uid = Long.valueOf(SecureHelper.decrypt(authToken));

            if (!hb.isIdValid(uid)) {
                out.println("{\"message\":\"user not found\"}");
                return;
            }
            
            String fileName = uid+"_"+System.currentTimeMillis()+request
                    .getPart("file").getSubmittedFileName();
            
            request.getPart("file").write(fileName);
            
            
            models.Post post =  pb.addPost(uid, 
                    "http://10.114.32.118/margatsni/"+fileName
                    , caption, permission, isSharedPost, 0L);
            
            if(post == null) {
                out.println("{\"message\":\"internal error, cannot write post\"}");
                return;
            }
            
            JSONObject json = new JSONObject();
            
            json.put("src", post.getSrc());
            json.put("postId", SecureHelper
                                .encrypt(String.valueOf(post.getPostId())));
            json.put("timestamp", post.getTimestamp());
            json.put("caption", post.getCaption());
            
            Collection<Comment> comments = post.getCommentCollection();
            
            JSONArray jcomments = new JSONArray();
            for(Comment c : comments) {
                JSONObject jcom = new JSONObject();
                jcom.put("uid", 
                        SecureHelper
                                .encrypt(String.valueOf(c.getUid().getUid())));
                jcom.put("uname", c.getUid().getUname());
                jcom.put("profile_pic", c.getUid().getProfilePic());
                jcom.put("content", c.getContent());
                jcom.put("timestamp", c.getTimestamp());
                jcomments.put(jcom);
            }
            
            json.put("comments", jcomments);
            
            
            JSONArray jlikes = new JSONArray();
            for(User u : post.getUserCollection()) {
                JSONObject jlike = new JSONObject();
                jlike.put("uid", 
                        SecureHelper
                                .encrypt(String.valueOf(u.getUid())));
                jlike.put("uname", u.getUname());
                jlike.put("profile_pic", u.getProfilePic());
                jcomments.put(jlike);
            }
            json.put("likes", jlikes);
            
            out.println(json.toString());
        } catch (Exception ex) {
            
        }
    }
    
}
