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
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
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
@Path("comment")
public class CommentResource {

    @EJB
    CommentHelperBean cb;
    
    @EJB
    private UserHelperBean hb;
    @EJB
    private PostHelperBean pb;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String post(
            @HeaderParam("auth-token") String authToken,
            @FormParam("post") String authPost,
            @FormParam("content") String content) {
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
            
            long postId = Long.valueOf(SecureHelper.decrypt(authPost));
            
            Post post = pb.getPostById(postId);
            
            if(post == null)
                return "{\"message\":\"post not found\"}";
            
            if(post.getPermission()!=0)
                return "{\"message\":\"you don't have permission to comment\"}";
                
            Comment comment = cb.addComment(uid, postId, content);
            
            if (comment == null)
                return "{\"error\":\"internal error, cannot send comment\"}";
            
            post = pb.getPostById(postId);
            
            JSONObject jpost = new JSONObject();

                jpost.put("src", post.getSrc());
                jpost.put("postId", SecureHelper
                        .encrypt(String.valueOf(post.getPostId())));
                jpost.put("timestamp", post.getTimestamp());
                jpost.put("caption", post.getCaption());

                boolean ownedPost = false;
                if (post.getUid().getUid() == uid) {
                    ownedPost = true;
                }

                jpost.put("owned", ownedPost);

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

                    boolean ownedComment = false;
                    if (c.getUid().getUid() == uid) {
                        ownedComment = true;
                    }

                    jcom.put("owned", ownedComment);
                    jcomments.put(jcom);
                }

                jpost.put("comments", jcomments);

                JSONArray jlikes = new JSONArray();
                for (User u : post.getUserCollection()) {
                    JSONObject jlike = new JSONObject();
                    jlike.put("uid",
                            SecureHelper
                                    .encrypt(String.valueOf(u.getUid())));
                    jlike.put("uname", u.getUname());
                    jlike.put("profile_pic", u.getProfilePic());
                    jcomments.put(jlike);
                }
                jpost.put("likes", jlikes);
            
            
            return jpost.toString();
            
        } catch (Exception ex) {
            
            String err="";
            for(StackTraceElement e : ex.getStackTrace()){
                err += "\n" +e;
            }
            err+="\n"+ex.getCause();
            return "{\"error\":\"internal error, cannot send comment"+err+"\"}";
        }
    }
}
