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
import javax.ejb.EJB;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import models.Comment;
import models.Post;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author conme
 */
@Path("comment")
public class CommentResource {

    @EJB
    private CommentHelperBean cb;

    @EJB
    private UserHelperBean hb;

    @EJB
    private PostHelperBean pb;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(
            @HeaderParam("auth-token") String authToken,
            @FormParam("post") String authPost,
            @FormParam("content") String content) {
        try {
            String originalAuth = SecureHelper.decrypt(authToken);

            String[] authInfo = originalAuth.split("::");

            long expired = Long.valueOf(authInfo[1]);
            if (expired < System.currentTimeMillis()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"message\":\"session expired\"}")
                        .build();
            }

            long uid = Long.valueOf(authInfo[0]);

            if (!hb.isIdValid(uid)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"message\":\"user not found\"}")
                        .build();
            }

            long postId = Long.valueOf(SecureHelper.decrypt(authPost));

            Post post = pb.getPostById(postId);

            if (post == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\":\"post not found\"}")
                        .build();
            }

            if (post.getPermission() != 0) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"message\":\"you don't have permission to comment\"}")
                        .build();
            }

            Comment c = cb.addComment(uid, postId, content);

            if (c == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"internal error occurs\"}")
                        .build();
            }

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

            return Response.status(Response.Status.OK)
                        .entity(jcom.toString())
                        .build();

        } catch (Exception ex) {

//            String err="";
//            for(StackTraceElement e : ex.getStackTrace()){
//                err += "\n" +e;
//            }
//            err+="\n"+ex.getCause();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"internal error occurs\"}")
                        .build();
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response put(
            @HeaderParam("auth-token") String authToken,
            @FormParam("comment_id") String authComment,
            @FormParam("content") String content) {
        try {
            String originalAuth = SecureHelper.decrypt(authToken);

            String[] authInfo = originalAuth.split("::");

            long expired = Long.valueOf(authInfo[1]);
            if (expired < System.currentTimeMillis()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"message\":\"session expired\"}")
                        .build();
            }

            long uid = Long.valueOf(authInfo[0]);

            if (!hb.isIdValid(uid)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"message\":\"user not found\"}")
                        .build();
            }

            long commentId = Long.valueOf(SecureHelper.decrypt(authComment));

            Comment c = cb.getCommentById(commentId);

            if (c == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\":\"comment not found\"}")
                        .build();
            }

            c.setContent(content);

            cb.updateComment(c);

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
            return Response.status(Response.Status.OK)
                        .entity(jcom.toString())
                        .build();

        } catch (Exception ex) {

//            String err="";
//            for(StackTraceElement e : ex.getStackTrace()){
//                err += "\n" +e;
//            }
//            err+="\n"+ex.getCause();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"internal error occurs\"}")
                        .build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
            @HeaderParam("auth-token") String authToken,
            @FormParam("comment_id") String authComment) {
        try {
            String originalAuth = SecureHelper.decrypt(authToken);

            String[] authInfo = originalAuth.split("::");

            long expired = Long.valueOf(authInfo[1]);
            if (expired < System.currentTimeMillis()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"message\":\"session expired\"}")
                        .build();
            }

            long uid = Long.valueOf(authInfo[0]);

            if (!hb.isIdValid(uid)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"message\":\"user not found\"}")
                        .build();
            }

            long commentId = Long.valueOf(SecureHelper.decrypt(authComment));

            Comment c = cb.getCommentById(commentId);

            if (c == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\":\"comment not found\"}")
                        .build();
            }

            if (c.getUid().getUid() == uid) {
                cb.deleteComment(c);
            }

            JSONObject jcom = new JSONObject()
                    .put("message", "success");
            return Response.status(Response.Status.OK)
                        .entity(jcom.toString())
                        .build();

        } catch (Exception ex) {

//            String err = "";
//            for (StackTraceElement e : ex.getStackTrace()) {
//                err += "\n" + e;
//            }
//            err += "\n" + ex.getCause();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"internal error occurs\"}")
                        .build();
        }
    }
}
