<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="examples.*" %>
<jsp:useBean class="examples.DbHandler" id="dbh" scope="session"></jsp:useBean>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
        <title>List instruments</title>
    </head>
    <body>
    <center><h1>All available appointments</h1> </center>
        <table class="table table-hover">
        <% 
            GroupLesson [] lessons = (GroupLesson[]) application.getAttribute("lessons");
            for (GroupLesson lesson : lessons) {
        %>
        <form method="post" action="/L3/Controller">  
            <tr class="table-dark">
                <td>Date: <%= lesson.getDate()%></td>
                <td>Time: <%= lesson.getTime()%></td>
                <td>Type of class: <%= lesson.getLessonContent()%></td>
                <td> <button type="submit" name ="findAppointment" value="<%= lesson.getID()%>" class="btn btn-success"> Reserve </button> </td>
            </tr>
        </form>
        <%
            }
        %>
</table>
    </body>
</html>
