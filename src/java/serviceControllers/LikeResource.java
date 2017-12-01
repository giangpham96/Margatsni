/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serviceControllers;

import dataAccessObjects.CommentHelperBean;
import dataAccessObjects.PostHelperBean;
import dataAccessObjects.SecureHelper;
import dataAccessObjects.UserHelperBean;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import models.Comment;
import models.Post;
import models.User;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author conme
 */
@Path("like")
public class LikeResource {

    @EJB
    private CommentHelperBean cb;

    @EJB
    private UserHelperBean hb;

    @EJB
    private PostHelperBean pb;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String post(@FormParam("post") String authPost,
            @HeaderParam("auth-token") String authToken) {

        try {
            String originalAuth = SecureHelper.decrypt(authToken);

            String[] authInfo = originalAuth.split("::");

            long expired = Long.valueOf(authInfo[1]);
            if (expired < System.currentTimeMillis()) {
                return "{\"message\":\"session expired\"}";
            }

            long uid = Long.valueOf(authInfo[0]);

            User user = hb.getUserById(uid);

            if (user == null) {
                return "{\"message\":\"user not found\"}";
            }

            long postId = Long.valueOf(SecureHelper.decrypt(authPost));

            Post post = pb.getPostById(postId);

            if (post == null) {
                return "{\"message\":\"post not found\"}";
            }

            post = hb.like(user, post);

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
                if(c.getUid().getProfilePic()!=null)
                    jcom.put("profile_pic", "http://10.114.32.118/profile_pic/"+c.getUid().getProfilePic());
                jcom.put("content", c.getContent());
                jcom.put("timestamp", c.getTimestamp());
                jcom.put("comment_id", SecureHelper
                        .encrypt(String.valueOf(c.getCommentId())));
                jcomments.put(jcom);
            }

            json.put("comments", jcomments);

//            JSONArray jlikes = new JSONArray();
//            for (User u : post.getUserCollection()) {
//                JSONObject jlike = new JSONObject();
//                jlike.put("uid",
//                        SecureHelper
//                                .encrypt(String.valueOf(u.getUid())));
//                jlike.put("uname", u.getUname());
//                jlike.put("profile_pic", u.getProfilePic());
//                jcomments.put(jlike);
//            }
            json.put("likes", post.getUserCollection().size());

            return json.toString();
        } catch (Exception ex) {
            return "{\"error\":\"internal error, cannot like post\"}";
        }
    }
}
