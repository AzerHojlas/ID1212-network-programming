<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="examples.*" %>
<!DOCTYPE html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Users</title>
    </head>
    <body>
        <%
            Integer points = (Integer) application.getAttribute("points");  
        %>
        <div>You answered <%= points %> out of 3 points!</div>
    </body>
</html>