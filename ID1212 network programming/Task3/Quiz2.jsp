<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="examples.*" %>
<jsp:useBean class="examples.DbHandler" id="dbh" scope="session"></jsp:useBean>
<!DOCTYPE html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Users</title>
    </head>
    <body>
        
        <h1>Quiz</h1>     
        <%         
          Question[] questions = dbh.getQuestions();
          
          String [] options1 = questions[3].getOptions();
          String [] options2 = questions[4].getOptions();
          String [] options3 = questions[5].getOptions();
             
        %>
    <form action="/L3/Controller" method="post">
    
    <p><%= questions[3].getQuestion() %></p>
    <input type="checkbox" id="a" name="a"><%=options1[0]%></input>
    <input type="checkbox" id="b" name="b" ><%=options1[1]%></input>
    <input type="checkbox" id="c" name="c" ><%=options1[2]%></input>
    
    <p><%=questions[4].getQuestion()%></p>
    <input type="checkbox" id="d" name="d"><%=options2[0]%></input>
    <input type="checkbox" id="e" name="e" ><%=options2[1]%></input>
    <input type="checkbox" id="f" name="f" ><%=options2[2]%></input>
    
    <p><%=questions[5].getQuestion()%></p>
    <input type="checkbox" id="g" name="g"><%=options3[0]%></input>
    <input type="checkbox" id="h" name="h" ><%=options3[1]%></input>
    <input type="checkbox" id="i" name="i" ><%=options3[2]%></input>
    
    <input type="hidden" name="Quiz" value="Quiz2">
    
    <br/><br/><input type="submit" name = "proceed" value="answer"/>
    </form>
      
    </body>
</html>