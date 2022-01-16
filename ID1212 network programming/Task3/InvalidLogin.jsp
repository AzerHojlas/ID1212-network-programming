<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="examples.*" %>
<jsp:useBean class="examples.DbHandler" id="dbh" scope="session"></jsp:useBean>
<!DOCTYPE html>
<html>
    <head>
        <title>Login</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href=
              "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css%22%3E
        <script src="https://code.jquery.com/jquery-3.6.0.slim.js%22%3E
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.min.js" integrity="sha384-QJHtvGhmr9XOIpI6YVutG+2QOK9T+ZnN4kzFN1RtK3zEFEIsxhlmWl5/YESvpZ13" crossorigin="anonymous"></script>
    </head>
    <body>
        <div>Invalid username and or password, do you wish to register a new user? If yes, click register below</div>
        <form action="Register.jsp">
            <input type="submit" value="Register" />
        </form>
        <script type="text/javascript"> alert("Wrong username and or password, please return to main page"); </script>
    </body>
</html>
