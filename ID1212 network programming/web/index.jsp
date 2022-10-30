<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="examples.*" %>
<!DOCTYPE html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <title>Users</title>
    </head>
      <body>
       
        <h1>Login</h1>
        <form method="post" action="/L3/Controller">
            Email <input type="text" name="email"><br>
            Username <input type="text" name="username"><br>
            Password <input type="password" name="password"><br>               
            <input type="hidden" name="proceedToChooseQuiz" value="login">
            <input type="submit" value="login" class="btn btn-primary">
        </form>
        <form action="Register.jsp">
            <input type="submit" value="Register" />
        </form>
        
    </body>
</html>
