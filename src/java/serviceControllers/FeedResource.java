/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serviceControllers;

import dataAccessObjects.PostHelperBean;
import dataAccessObjects.SecureHelper;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import models.Comment;
import models.Post;
import models.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author conme
 */
@Path("feed")
public class FeedResource {

    @EJB
    private PostHelperBean pb;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get() {
        List<Post> posts = pb.getAllPosts();
        try {
            JSONArray json = new JSONArray();
            for (Post post : posts) {
                JSONObject jpost = new JSONObject();

                jpost.put("src", post.getSrc());
                jpost.put("postId", SecureHelper
                        .encrypt(String.valueOf(post.getPostId())));
                jpost.put("timestamp", post.getTimestamp());
                jpost.put("caption", post.getCaption());

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
                json.put(jpost);
            }
            return json.toString();
        } catch (Exception ex) {
            return "{\"error\":\"internal error, cannot load feed\"}";
        }

    }
}
