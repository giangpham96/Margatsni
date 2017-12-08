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
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    public Response post(@FormParam("post") String authPost,
            @HeaderParam("auth-token") String authToken) {
        if (authPost == null || authToken == null || authToken.equals("")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"bad request\"}")
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

        User user = hb.getUserById(uid);

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"user not found\"}")
                    .build();
        }

        long postId;
        try {
            postId = Long.valueOf(SecureHelper.decrypt(authPost));
        } catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"invalid post id\"}")
                    .build();
        }

        Post post = pb.getPostById(postId);

        if (post == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"post not found\"}")
                    .build();
        }

        post = hb.like(user, post);

        JSONObject json = new JSONObject();

        try {
//            json.put("src", post.getSrc());
//            json.put("postId", SecureHelper
//                    .encrypt(String.valueOf(post.getPostId())));
//            json.put("timestamp", post.getTimestamp());
//            json.put("caption", post.getCaption());

//            Collection<Comment> comments = post.getCommentCollection();

//            JSONArray jcomments = new JSONArray();
//            for (Comment c : comments) {
//                JSONObject jcom = new JSONObject();
//                jcom.put("uid",
//                        SecureHelper
//                                .encrypt(String.valueOf(c.getUid().getUid())));
//                jcom.put("uname", c.getUid().getUname());
//                if (c.getUid().getProfilePic() != null) {
//                    jcom.put("profile_pic", "http://10.114.32.118/profile_pic/" + c.getUid().getProfilePic());
//                }
//                jcom.put("content", c.getContent());
//                jcom.put("timestamp", c.getTimestamp());
//                jcom.put("comment_id", SecureHelper
//                        .encrypt(String.valueOf(c.getCommentId())));
//                jcomments.put(jcom);
//            }
            boolean liked = false;
            if (post.getUserCollection().contains(user)) {
                liked = true;
            }

            json.put("liked", liked);
//            json.put("comments", jcomments);

            json.put("likes", post.getUserCollection().size());

        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"internal error occurs\"}")
                    .build();
        }
        return Response.status(Response.Status.OK)
                .entity(json.toString())
                .build();
    }
}
