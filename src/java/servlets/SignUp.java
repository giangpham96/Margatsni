/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import controllers.AccountController;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.User;

/**
 *
 * @author conme
 */
@WebServlet(name = "SignUp", urlPatterns = {"/signup"})
public class SignUp extends HttpServlet {

    @EJB
    private AccountController account;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        /* TODO output your page here. You may use following sample code. */
//        JSONObject obj = new JSONObject();

        if (account.isUsernameUsed(username)) {
//                obj.put("Error", "This username has already been used.");
//                out.println(obj.toString());
            response.sendRedirect("index.jsp");
            return;
        }

        if (account.isEmailUsed(email)) {
//                obj.put("Error", "This email has already been used.");
//                out.println(obj.toString());
            response.sendRedirect("index.jsp");
            return;
        }

        User user = account.signUp(username, email, password);
        
        if (user == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("uid", user.getUid());
        response.sendRedirect("welcome.jsp");

//            obj.put("uid", user.getUid());
//            out.println(obj.toString());
    }

}
