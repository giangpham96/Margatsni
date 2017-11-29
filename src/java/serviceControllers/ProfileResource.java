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
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
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
@Path("profile")
public class ProfileResource {

    @EJB
    private PostHelperBean pb;

    @EJB
    private UserHelperBean hb;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@HeaderParam("auth-token") String authToken,
            @QueryParam("user") String authUid,
            @QueryParam("page") int page) {
        try {
            
            long userUid = Long.valueOf(SecureHelper.decrypt(authUid));
            
            long uid = -1;
            if (authToken != null) {
                String originalAuth = SecureHelper.decrypt(authToken);

                String[] authInfo = originalAuth.split("::");

                long expired = Long.valueOf(authInfo[1]);
                if (expired < System.currentTimeMillis()) {
                    return "{\"message\":\"session expired\"}";
                }
                uid = Long.valueOf(authInfo[0]);

                if (!hb.isIdValid(uid)) {
                    return "{\"message\":\"user not found\"}";
                }
                
                if (uid == userUid) {
                    return "{\"redirect\":\"profile/me\"}";
                }
            }
            
            User user = hb.getUserById(userUid);
            
            if(user == null)
                return "{\"message\":\"cannot find profile\"}";
            
            JSONObject json = new JSONObject();
            
            json.put("uid", authUid);
            json.put("uname", user.getUname());
            json.put("fav_quote", user.getFavQuote());
            json.put("profile_pic", user.getProfilePic());
            
            JSONArray jsonArray = new JSONArray();
            
            List<Post> posts = pb.getPostsByUidInPage(userUid, page);
            for (Post post : posts) {
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
                jsonArray.put(jpost);
            }
            
            JSONObject postJson = new JSONObject();
            postJson.put("page", page);
            postJson.put("posts", jsonArray);
            json.put("post", postJson);
            return json.toString();
        } catch (Exception ex) {
//            String err="";
//            for(StackTraceElement e : ex.getStackTrace()){
//                err += "\n" +e;
//            }
//            err+="\n"+ex.getCause();
            return "{\"error\":\"internal error, cannot load this profile\"}";
        }

    }
}
