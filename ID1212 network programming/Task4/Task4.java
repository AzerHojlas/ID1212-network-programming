/** 
    @author         Azer Hojlas and Porsev Aslan

    Date:           2022-01-03

    Description:    Program that sends emails using SMTP and retrieves Emails using IMAP.

    Dependencies:   All the ones listed below

    Compilation:    javac Task4.java

    Execution:      java Task4

    Usage:          The instructions are pretty self-explanatory when you start the program. One thing to pay attention to
                    is that you initially write your username only (as in "hojlas", not "hojlas@kth.se") and later the 
                    recipient of the mail as the entire mail (as in "hojlas@kth.se").

    Issues:         Due to the rules of the kth servers, spamming mails will result in the program not working for a short
                    while. Furthermore, there appear to exist some unwritten rules as what, how and when one can send mails.
                    We have not been able to ascertain what said rules are specifically, therefore we recommend that the
                    client sends one e-mail and then waits two minutes or more before sending the next.
 */

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.Scanner;
import java.util.Base64;


class Task4 {
    public static final String IMAP_HOST = "webmail.kth.se";
    public static final int IMAP_PORT = 993;
    public static final String SMTP_HOST = "smtp.kth.se";
    public static final int SMTP_PORT = 587;

    public static void main(String[] args) {

      // Take in username as input from terminal
      Scanner sc = new Scanner(System.in);
      System.out.print("Enter your username (only username, e-mail not required): ");
      String username = sc.next();

      // Take in password as hidden input from terminal
      Console console = System.console(); 
      System.out.print("Enter your password: "); 
      char[] password1 = console.readPassword();
      String password = new String(password1);
      
      // Client gets the option to either send or retrieve a mail
      System.out.println("Would you like to retrieve the latest recieved email or send an email?");
      System.out.print("Type 1 to retrieve and 2 to send: ");
      String option = sc.next();
      System.out.println();

      // proceed to either of options
      if("1".equals(option)){
        sc.close();
          iMAP(username, password);
      }
      else if("2".equals(option)) {
          System.out.print("KTH-mail of the recipient: ");
          String recipient = sc.next();
          System.out.print("Contents of mail: ");
          String contents = sc.next();
          sMTP(username, password, recipient, contents);
      }
    }

    private static void iMAP(String username, String password) {

        System.out.println();
        
        // Establish encrypted connection
        SSLSocketFactory socketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);
        SSLSocket socket = null;

        try {

            socket = (SSLSocket)socketFactory.createSocket(IMAP_HOST,  IMAP_PORT);
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            InputStreamReader isr = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);

            // Contact server and login
            write(pw, "a001 LOGIN " + username + " " + password + "\r\n");
            write(pw, "a002 select inbox\r\n");
            
            // Find amount of mails in inbox
            String[] properAmount = iterate(br, "EXISTS").split(" ");
            int latestEmail = Integer.parseInt(properAmount[1]);
            
            // Retrieve latest email
            write(pw, "a003 fetch " + latestEmail + " full\r\n");
            write(pw, "a004 fetch " + latestEmail + " body[text]\r\n");

            // Skip unnecessary responses from server
            iterate(br, "a002 OK [READ-WRITE] SELECT completed.");

            // Logout
            write(pw, "a006 logout\r\n");

            // Print mailInfo
            String mailInfo = br.readLine();
            while (mailInfo != null ) {
                System.out.println(mailInfo);

                mailInfo = br.readLine();
            }
            br.close();
            pw.close();

        } catch(Exception exception) {
            System.out.println("An error occured while retrieving mail");
        }
    }

    private static void sMTP(String username, String password, String recipient, String contents) {
        
        Socket socket = null;
    
        try {
          // Establish connection to server
          socket = new Socket(SMTP_HOST, SMTP_PORT);
          PrintWriter pw = new PrintWriter(socket.getOutputStream());
          InputStreamReader isr = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
          BufferedReader br = new BufferedReader(isr);

          write(pw, "EHLO " + SMTP_HOST);
          Thread.sleep(1000);
 
          // We believe this command constitutes the encrypted connection
          write(pw, "STARTTLS");
          Thread.sleep(1000);

          // Iterate through the responses recieved from the server. Used mostly for debugging, can be disabled
          String line = br.readLine();
          while (line != null) {
            System.out.println(line);
            if(line.contains("220 2.0.0 Ready to start TLS")) {
              break;
            }
            line = br.readLine();
          }
          
          SSLSocketFactory socketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
    
          SSLSocket socketSSL = (SSLSocket)socketFactory.createSocket(socket, socket.getInetAddress().getHostAddress(), socket.getPort(), true);
          pw = new PrintWriter(socketSSL.getOutputStream(), true);
          isr = new InputStreamReader(socketSSL.getInputStream(), StandardCharsets.UTF_8);
          br = new BufferedReader(isr);

          // Initiate communication and login
          write(pw, "EHLO " + SMTP_HOST);
          Thread.sleep(1000);
          write(pw, "AUTH LOGIN");
          Thread.sleep(1000);
          write(pw, Base64.getEncoder().encodeToString(username.getBytes()));
          Thread.sleep(1000);
          write(pw, Base64.getEncoder().encodeToString(password.getBytes()));
          Thread.sleep(1000);
          write(pw, "MAIL FROM:<" + username + "@kth.se>");
          Thread.sleep(1000);
          write(pw, "RCPT TO:<" + recipient + ">");
          Thread.sleep(1000);
          write(pw, "DATA");
    
          // Sleep is necessary here, otherwise bugs occur
          Thread.sleep(1000);

          // Send contents of mail and finish with a ".", which is required to close connection
          // Contents are sent both as text and as the subject
          write(pw, "Subject: " + contents);
          Thread.sleep(1000);
          write(pw, contents);
          Thread.sleep(1000);
          write(pw, ".");

          // Iterate through the responses recieved from the server. Used mostly for debugging, can be disabled
          String text = br.readLine();
          while (text != null) {
            System.out.println(text);
            if(text.contains("250 2.0.0 Ok: queued as")) {
              System.out.println("\n Email has been sent");
              break;
            }
            text = br.readLine();
          }
          br.close();
          pw.close();

        } catch(Exception exception) {
          System.out.println("An error occured while sending mail");
        }
    }

    // This method is used to minimize code reusage when writing to servers
    public static void write (PrintWriter pw, String command) throws IOException{
      
      pw.println(command);
      pw.flush();
      // System.out.println("command");
    }

    // This method is used to minimize code reusage and skip responses from servers if the print commands are disabled
    public static String iterate (BufferedReader br, String message) throws IOException{

        String text = br.readLine();

        while(!text.contains(message)){
          System.out.println(text);
          text = br.readLine();
        }
        System.out.println(text);
        return text;
    }
}

