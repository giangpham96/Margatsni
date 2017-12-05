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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
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
@Path("profile")
public class ProfileResource {

    @EJB
    private PostHelperBean pb;

    @EJB
    private UserHelperBean hb;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{uid}")
    public Response get(@HeaderParam("auth-token") String authToken,
            @PathParam("uid") String authUid,
            @QueryParam("page") int page) {
        if (authUid == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"bad request\"}")
                    .build();
        }

        long userUid;
        try {
            userUid = Long.valueOf(SecureHelper.decrypt(authUid));
        } catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"invalid user id\"}")
                    .build();
        }

        long uid = -1;
        if (authToken != null) {
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
            uid = Long.valueOf(authInfo[0]);

            if (!hb.isIdValid(uid)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"message\":\"user not found\"}")
                        .build();
            }

            if (uid == userUid) {
                return Response.status(Response.Status.SEE_OTHER)
                        .entity("{\"message\":\"redirect to /profile/me\"}")
                        .build();
            }
        }

        User user = hb.getUserById(userUid);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"message\":\"cannot find this profile\"}").build();
        }

        JSONObject json = new JSONObject();
        try {
            json.put("uid", authUid);
            json.put("uname", user.getUname());
            json.put("fav_quote", user.getFavQuote());
            if (user.getProfilePic() != null) {
                json.put("profile_pic", "http://10.114.32.118/profile_pic/" + user.getProfilePic());
            }

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
                    if (c.getUid().getProfilePic() != null) {
                        jcom.put("profile_pic", "http://10.114.32.118/profile_pic/" + c.getUid().getProfilePic());
                    }
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

                jpost.put("likes", post.getUserCollection().size());
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
                jpost.put("can_like", canLike);
                jpost.put("can_comment", canComment);
                jsonArray.put(jpost);
            }

            JSONObject postJson = new JSONObject();
            postJson.put("page", page);
            postJson.put("posts", jsonArray);
            json.put("post", postJson);
        } catch (Exception ex) {
//            String err="";
//            for(StackTraceElement e : ex.getStackTrace()){
//                err += "\n" +e;
//            }
//            err+="\n"+ex.getCause();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"internal error occurs\"}")
                    .build();
        }
        return Response.status(Response.Status.OK).entity(json.toString()).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("me")
    public Response getMe(@HeaderParam("auth-token") String authToken,
            @QueryParam("page") int page) {

        if (authToken == null || authToken.equals("")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"bad request\"}")
                    .build();
        }
        long uid = -1;
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
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\":\"session expired\"}").build();
        }
        uid = Long.valueOf(authInfo[0]);

        User user = hb.getUserById(uid);

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\":\"user not found\"}").build();
        }

        JSONObject json = new JSONObject();

        try {
            json.put("uid", SecureHelper.encrypt(String.valueOf(uid)));
            json.put("uname", user.getUname());
            json.put("fav_quote", user.getFavQuote());
            if (user.getProfilePic() != null) {
                json.put("profile_pic", "http://10.114.32.118/profile_pic/" + user.getProfilePic());
            }

            JSONArray jsonArray = new JSONArray();

            List<Post> posts = pb.getPostsByUidInPage(uid, page);
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
                    if (c.getUid().getProfilePic() != null) {
                        jcom.put("profile_pic", "http://10.114.32.118/profile_pic/" + c.getUid().getProfilePic());
                    }
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

                jpost.put("likes", post.getUserCollection().size());
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
                jpost.put("can_like", canLike);
                jpost.put("can_comment", canComment);
                jsonArray.put(jpost);
            }

            JSONObject postJson = new JSONObject();
            postJson.put("page", page);
            postJson.put("posts", jsonArray);
            json.put("post", postJson);
        } catch (Exception ex) {
//            String err="";
//            for(StackTraceElement e : ex.getStackTrace()){
//                err += "\n" +e;
//            }
//            err+="\n"+ex.getCause();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"internal error occurs\"}")
                    .build();
        }
        return Response.status(Response.Status.OK)
                .entity(json.toString())
                .build();

    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response put(@HeaderParam("auth-token") String authToken,
            @FormParam("fav_quote") String favQuote) {

        long uid = -1;
        if (authToken != null) {
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
            uid = Long.valueOf(authInfo[0]);

            if (!hb.isIdValid(uid)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"message\":\"user not found\"}")
                        .build();
            }

        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"bad request\"}")
                    .build();
        }

        User u = hb.getUserById(uid);
        u.setFavQuote(favQuote);

        u = hb.update(u);

        return Response.status(Response.Status.OK)
                .entity("{\"fav_quote\":\"" + favQuote + "\"}")
                .build();
    }
}
