<%-- 
    Document   : welcome
    Created on : Nov 27, 2017, 12:47:24 AM
    Author     : conme
--%>

<%@page session="true" contentType="text/html" pageEncoding="UTF-8" %>
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
            
            if (request.getSession().getAttribute("uid") == null) {
                response.sendRedirect("index.jsp");
            }
            Long uid = (Long) request.getSession().getAttribute("uid");
        %>
        <h1><%= uid %></h1>
        <br>
        <form action="logout">
            <input type="submit" value="Log out"/>
        </form>
    </body>
</html>
