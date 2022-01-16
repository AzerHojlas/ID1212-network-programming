/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examples;

import jakarta.servlet.http.HttpServlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;



public class DbHandler extends HttpServlet{
    User[] users;
    int usersize;
    int questionsize;
    int pointsSize;
    Question[] questions;
    String[] previousResults;
    
    public User[] getUsers() throws Exception {
        
        Statement stmt = createStatement();
        ResultSet user = stmt.executeQuery("SELECT * from users");

        user.last();
        this.usersize=user.getRow();
        user.beforeFirst();

        this.users = new User[usersize];

        while(user.next()){
            
            int row = user.getRow();

            String username = user.getString("username");
            String password = user.getString("password");
            int userID = user.getInt("id");

            users[row-1]=new User();

            users[row-1].setUserID(userID);
            users[row-1].setUsername(username); 
            users[row-1].setPassword(password);

        }

        //con.close();
        user.close();

        //return users;
        return users;
    }
    
    public int getQuizID (String subject) throws Exception {
    
        Statement stmt = createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * from quizzes");
        
        while (rs.next()) {
            if(rs.getString("subject").equals(subject))
                break;
        }
        return rs.getInt("id");
    }
    
    public int[] getQuestionID (int quizID) throws Exception {
    
        Statement stmt = createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * from selector");
        
        int [] id = new int[3];
        
        while (rs.next()) {
            if(rs.getInt("quiz_id") == quizID)
                break;
        }
        id[0] = rs.getInt("question_id") - 1;
        rs.next();
        id[1] = rs.getInt("question_id") - 1;
        rs.next();
        id[2] = rs.getInt("question_id") - 1;
 
        return id;
    }
    
    public Question[] getQuestions() throws Exception {
       
        Statement stmt = createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * from questions");

        rs.last();
        this.questionsize=rs.getRow();
        rs.beforeFirst();

        this.questions = new Question[this.questionsize];


        while(rs.next()){

            int row = rs.getRow();

            String question = rs.getString("text");

            String options = rs.getString("options");

            String[] dividedOptions = options.split("/");

            String correctAlternatives = rs.getString("answer");

            int correct = 0;

            if (correctAlternatives.equals("0/0/0")) correct  = 0;
            if (correctAlternatives.equals("0/0/1")) correct  = 1;
            if (correctAlternatives.equals("0/1/0")) correct  = 2;
            if (correctAlternatives.equals("0/1/1")) correct  = 3;
            if (correctAlternatives.equals("1/0/0")) correct  = 4;
            if (correctAlternatives.equals("1/0/1")) correct  = 5;
            if (correctAlternatives.equals("1/1/0")) correct  = 6;
            if (correctAlternatives.equals("1/1/1")) correct  = 7;

            questions[row-1]=new Question(question, dividedOptions, correct);

        }

        //con.close();
        rs.close();

        return questions;
    }
    
    public void sqlInjectScore(Integer userID, Integer quizID, Integer score) throws Exception {
    
        
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "root", "12345678");

        String insertSQL = "INSERT INTO results VALUES (default, ?, ?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(insertSQL);
        
        preparedStatement.setString(1, userID.toString());
        preparedStatement.setString(2, quizID.toString());
        preparedStatement.setString(3, score.toString());
        preparedStatement.executeUpdate();   
    }
    public void sqlInjectUser(String username, String password) throws Exception {
        
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "root", "12345678");
        
        String insertSQL = "INSERT INTO users VALUES (default, ?, md5(?))";
        PreparedStatement preparedStatement = con.prepareStatement(insertSQL);
        
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.executeUpdate();   
    }
    
    
    public Statement createStatement() throws Exception {
        
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "root", "12345678");
     
        Statement stmt = con.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY
        );
        
        return stmt;
    }
    
    public String[] getPreviousPoints(Integer userID) throws Exception {
        
        Statement stmt = createStatement();
        ResultSet result = stmt.executeQuery("SELECT * from results");
        
        result.last();
        this.pointsSize=result.getRow();
        result.beforeFirst();
        
        this.previousResults = new String[this.pointsSize];

        while(result.next()){
            
            if (result.getInt("user_id") != userID) continue;
            
            int row = result.getRow();

            previousResults[row-1] = result.getString("score");

        }
        
        result.close();
        
        return previousResults;
    }
}

