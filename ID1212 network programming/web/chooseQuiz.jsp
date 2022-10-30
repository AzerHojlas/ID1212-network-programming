<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:useBean class="examples.DbHandler" id="dbh" scope="session"></jsp:useBean>
<!DOCTYPE html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Users</title>
    </head>
    <body>
     
        
         <h1>Previous results and quiz choices</h1>
         
        <table><tr><th>Previous results: Most recent are displayed at the bottom</th></tr>
        <%
            // pre defined variables are request, response, out, session, application
            String[] previousPoints = dbh.getPreviousPoints((Integer) application.getAttribute("userID"));
            for(String points : previousPoints){
                if(points == null) continue;
        %>
    <tr>
        <td><%= points %> out of 3 points</td>
    </tr>

        <%
            }
        %>
        </table>
        
        <h1>Choose quiz</h1>
        
        <form action="Astronomy.jsp">
            <input type="submit" value="AstronomyQuiz" />
        </form>
        
        <form action="Quiz2.jsp">
            <input type="submit" value="Quiz2" />
        </form>
    </body>
</html>