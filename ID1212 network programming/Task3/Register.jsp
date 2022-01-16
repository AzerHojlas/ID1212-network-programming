<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="examples.*" %>
<!DOCTYPE html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Users</title>
        <script type = "text/javascript">  
            function myfunction() {   
            alert("New user has been created");  
         }  
        </script>  
    </head>
      <body>
       
        <h1>Register</h1>
        <form method="post" action="/L3/Controller">
            Username <input type="text" name="registerusername"><br>
            Password <input type="password" name="registerpassword"><br>               
            <input type="hidden" name="proceedToLogin" value="register">
            <input type = "submit" onclick = "myfunction()" value = "register">
        </form>
    </body>
</html>
