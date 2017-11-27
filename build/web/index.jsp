<%-- 
    Document   : login
    Created on : Nov 26, 2017, 11:53:38 AM
    Author     : conme
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%  
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            
            response.setHeader("Pragma", "no-cache");
            
            response.setHeader("Expires", "0");
            if (request.getSession().getAttribute("uid") != null) {
                response.sendRedirect("welcome.jsp");
            }
        %>
        <h1>Sign Up</h1>
        <form action="signup" method="post">
            <input type="text" name="username" required placeholder="username"><br>
            <input type="email" name="email" required placeholder="email"><br>
            <input type="password" name="password" required placeholder="password"><br>
            <input type="submit" value="Sign Up">
        </form>
        <br>
        <form action="login" method="post">
            <input type="email" name="email" required placeholder="email"><br>
            <input type="password" name="password" required placeholder="password"><br>
            <input type="submit" value="Log In">
        </form>
    </body>
</html>
