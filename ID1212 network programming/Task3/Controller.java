package examples;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Controller extends HttpServlet {
 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                // Creating the session object has been moved to getpost (compared to the lecture)
                RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
                rd.forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(true);
      
        ServletContext application = request.getServletContext();
        DbHandler dbh = (DbHandler)application.getAttribute("dbh");
        if(dbh==null)
            dbh = new DbHandler();
    
        if("login".equals(request.getParameter("proceedToChooseQuiz"))){
            try {    

                User[] users = users = dbh.getUsers();

                boolean match = false;
                Integer userID = 0;

                String password = getMD5(request.getParameter("password"));
                String username = request.getParameter("username");

                for(User user: users) {
                    if(user.getPassword().equals(password) & user.getUsername().equals(username)){

                        match = true;
                        userID = user.getUserID();
                        application.setAttribute("userID", userID);
                        break;
                    }  
                }
                if(match){
                    session.setAttribute("dbh", dbh);
                    RequestDispatcher rd = request.getRequestDispatcher("/chooseQuiz.jsp");
                    rd.forward(request, response);
                } else {

                    RequestDispatcher rd = request.getRequestDispatcher("/InvalidLogin.jsp");
                    rd.forward(request, response);
                }
            } 
            catch (Exception ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }

        }   
      
        if("answer".equals(request.getParameter("proceed"))){
            
            try {
                Question [] questions = questions = dbh.getQuestions();   
                
                String a = request.getParameter("a");
                String b = request.getParameter("b");
                String c = request.getParameter("c");
                String d = request.getParameter("d");
                String e = request.getParameter("e");
                String f = request.getParameter("f");
                String g = request.getParameter("g");
                String h = request.getParameter("h");
                String i = request.getParameter("i");

                String quiz = request.getParameter("Quiz");

                Integer quizID = (Integer) dbh.getQuizID(quiz);
                int[] id = dbh.getQuestionID(quizID);
                Integer userID = (Integer) application.getAttribute("userID");
                
                Integer point1, point2, point3;

                point1 = binary(a, b, c);
                point2 = binary(d, e, f);
                point3 = binary(g, h, i);

                if(questions[id[0]].getCorrect() == point1) point1 = 1;
                    else point1 = 0;

                if(questions[id[1]].getCorrect() == point2) point2 = 1;
                    else point2 = 0;

                if(questions[id[2]].getCorrect() == point3) point3 = 1;
                    else point3 = 0;

                Integer points = point1 + point2 + point3;

                application.setAttribute("points", points);

                dbh.sqlInjectScore(userID, quizID, points);

                RequestDispatcher rt = request.getRequestDispatcher("/finish.jsp");
                rt.forward(request, response);
                //out.println(id[0] + ", " + id[1] + ", " + id[2]);
            }
            catch (Exception ex) {
                    out.println("there was an error");
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if("register".equals(request.getParameter("proceedToLogin"))){
            try {
                dbh.sqlInjectUser(request.getParameter("registerusername"), request.getParameter("registerpassword"));
            } catch (Exception ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            RequestDispatcher rt = request.getRequestDispatcher("/index.jsp");
            rt.forward(request, response);
        }
    }
    
    public static Integer binary(String a, String b, String c) {
        
        Integer point = null;
        
        if(a == null && b == null && c == null) point = 0;
        if(a == null && b == null && "on".equals(c)) point = 1;
        if(a == null && "on".equals(b) && c == null) point = 2;
        if(a == null && "on".equals(b) && "on".equals(c)) point = 3;
        if("on".equals(a) && b == null && c == null) point = 4;
        if("on".equals(a) && b == null && "on".equals(c)) point = 5;
        if("on".equals(a) && "on".equals(b) && c == null) point = 6;
        if("on".equals(a) && "on".equals(b) && "on".equals(c)) point = 7;
    
        return point;
    }
    
    public static String getMD5(String input)
    {
        try {
  
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
  
            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());
  
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
  
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } 
  
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
