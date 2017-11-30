/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servletControllers;

import dataAccessObjects.PostHelperBean;
import dataAccessObjects.UserHelperBean;
import dataAccessObjects.SecureHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import models.Comment;
import models.User;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author conme
 */
@WebServlet(name = "Post", urlPatterns = {"/api/post/new"})
@MultipartConfig(location = "/var/www/html/margatsni")
public class Post extends HttpServlet {

    @EJB
    UserHelperBean hb;
    @EJB
    PostHelperBean pb;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        String authSession = request.getHeader("auth-session");
        String authToken = request.getHeader("auth-token");

        String caption = getValue(request.getPart("caption"));

        short permission = 0;

        boolean isSharedPost = false;

        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {

            if (authToken == null) {
                out.println("{\"message\":\"must logged in first\"}");
                return;
            }

            String originalAuth = SecureHelper.decrypt(authToken);

            String[] authInfo = originalAuth.split("::");

//            long expired = Long.valueOf(SecureHelper.decrypt(authSession));
            long expired = Long.valueOf(authInfo[1]);
            if (expired < System.currentTimeMillis()) {
                out.println("{\"message\":\"session expired\"}");
                return;
            }

//            long uid = Long.valueOf(SecureHelper.decrypt(authToken));
            long uid = Long.valueOf(authInfo[0]);
            
            User user = hb.getUserById(uid);
            if (user == null) {
                out.println("{\"message\":\"user not found\"}");
                return;
            }

            String fileName = uid + "_" + System.currentTimeMillis() + request
                    .getPart("file").getSubmittedFileName();

            request.getPart("file").write(fileName);

            models.Post post = pb.addPost(uid,
                    "http://10.114.32.118/margatsni/" + fileName,
                    caption, permission, isSharedPost, 0L);

            if (post == null) {
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
            for (Comment c : comments) {
                JSONObject jcom = new JSONObject();
                jcom.put("uid",
                        SecureHelper
                                .encrypt(String.valueOf(c.getUid().getUid())));
                jcom.put("uname", c.getUid().getUname());
                jcom.put("profile_pic", c.getUid().getProfilePic());
                jcom.put("content", c.getContent());
                jcom.put("timestamp", c.getTimestamp());
                jcom.put("comment_id", SecureHelper
                        .encrypt(String.valueOf(c.getCommentId())));
                jcomments.put(jcom);
            }

            json.put("comments", jcomments);

            json.put("likes", post.getUserCollection().size());
            boolean liked = false;

            if (post.getUserCollection().contains(user)) {
                liked = true;
            }

            json.put("liked", liked);

            boolean canLike = true, canComment = true;

            if (uid == -1) {
                canLike = false;
                canComment = false;
            }
            json.put("can_like", canLike);
            json.put("can_comment", canComment);
            out.println(json.toString());
        } catch (Exception ex) {

        }
    }

    private String getValue(Part part) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream(), "UTF-8"));
        StringBuilder value = new StringBuilder();
        char[] buffer = new char[1024];
        for (int length = 0; (length = reader.read(buffer)) > 0;) {
            value.append(buffer, 0, length);
        }
        return value.toString();
    }

}
