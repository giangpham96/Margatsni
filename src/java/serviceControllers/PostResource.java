/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serviceControllers;

import dataAccessObjects.PostHelperBean;
import dataAccessObjects.SecureHelper;
import dataAccessObjects.UserHelperBean;
import java.util.Collection;
import javax.ejb.EJB;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import models.Comment;
import models.User;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author conme
 */
@Path("post")
public class PostResource {

    @EJB
    UserHelperBean hb;
    @EJB
    PostHelperBean pb;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public String put(@FormParam("post") String authPost,
            @FormParam("caption") String caption,
            @HeaderParam("auth-token") String authToken) {
        try {
            String originalAuth = SecureHelper.decrypt(authToken);

            String[] authInfo = originalAuth.split("::");

//            long expired = Long.valueOf(SecureHelper.decrypt(authSession));
            long expired = Long.valueOf(authInfo[1]);
            if (expired < System.currentTimeMillis()) {
                return "{\"message\":\"session expired\"}";
            }

//            long uid = Long.valueOf(SecureHelper.decrypt(authToken));
            long uid = Long.valueOf(authInfo[0]);
            
            User user = hb.getUserById(uid);
            
            if (user==null) {
                return "{\"message\":\"user not found\"}";
            }

            long postid = Long.valueOf(SecureHelper.decrypt(authPost));

            models.Post post = pb.getPostById(postid);

            if (post == null) {
                return "{\"message\":\"post not found\"}";
            }

            if (post.getUid().getUid() != uid) {
                return "{\"message\":\"permission denied\"}";
            }

            post.setCaption(caption);
            post = pb.update(post);

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
            return json.toString();
        } catch (Exception ex) {
            return "{\"error\":\"internal error, cannot update post\"}";
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String delete(@FormParam("post") String authPost,
            @HeaderParam("auth-token") String authToken) {
        try {
            String originalAuth = SecureHelper.decrypt(authToken);

            String[] authInfo = originalAuth.split("::");

//            long expired = Long.valueOf(SecureHelper.decrypt(authSession));
            long expired = Long.valueOf(authInfo[1]);
            if (expired < System.currentTimeMillis()) {
                return "{\"message\":\"session expired\"}";
            }

//            long uid = Long.valueOf(SecureHelper.decrypt(authToken));
            long uid = Long.valueOf(authInfo[0]);

            if (!hb.isIdValid(uid)) {
                return "{\"message\":\"user not found\"}";
            }

            long postid = Long.valueOf(SecureHelper.decrypt(authPost));

            models.Post post = pb.getPostById(postid);

            if (post == null) {
                return "{\"message\":\"post not found\"}";
            }

            if (post.getUid().getUid() != uid) {
                return "{\"message\":\"permission denied\"}";
            }

            pb.delete(post);

            return "{\"message\":\"success\"}";
        } catch (Exception ex) {
            return "{\"error\":\"internal error, cannot update post\"}";
        }
    }
}
