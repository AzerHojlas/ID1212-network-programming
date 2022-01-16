
/** 
    @author         Azer Hojlas and Porsev Aslan

    Date:           2021-11-16

    Description:    Program that initiates threads for chat clients, which communicates with other threads throught the ChatServer

    Dependancies:   java.io  ---------- java.net.socket ----------- java.util.scanner

    Compilation:    javac ChatClient.java

    Execution:      java ChatClient port

    Usage:          Replace port with any port number that you wish to use, but it must be identical to the server side
                    Press ctrl+c to disconnect
 */

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    int port;
    Socket output;

    public ChatClient (int port) {

        this.port = port;
    }
    // Thread for writing to the server
    private class OutputThread implements Runnable {
        
        String user;
        String chat;
        Socket output;

        public OutputThread(Socket socket, String username) {

            this.output = socket;
            this.user = username;
            this.chat = "";
        }

         /**
         * Initiates a BufferedReader that will write out chat messages given that these exist. 
         * These chat messages will be shared with the server and then with all other clients.
         */
        @Override
        public void run(){

            try {
                PrintStream outPrint = new PrintStream(output.getOutputStream());
                InputStreamReader input = new InputStreamReader(System.in);
                BufferedReader reader = new BufferedReader(input);

                outPrint.println(user);

                while ((this.chat = reader.readLine()) != null) {
                    outPrint.println(this.chat);
                }

                // Used instead of close in concurrent programming
                output.shutdownOutput();
            } catch (Exception e) {
                System.out.println("Connection failed");
            }
        }
    }

    /**
     * Receives a chat from the Server socket and proceeds to print it out. If the incomming chat is null, i.e server has been disconnected
     * then it will say so and proceed to exit the program
     * @param output
     */
    public void readSharedChat(Socket output) {
        try {
            InputStreamReader input = new InputStreamReader(output.getInputStream());
            BufferedReader reader = new BufferedReader(input);

            for(;;) {

                if (reader.readLine() == null) {
                    System.out.println("Server disconnected"); 
                    System.exit(1);
                }

                System.out.println(reader.readLine());
            }
        } catch (Exception e) {
            System.out.println("Server disconnected");
        }
    }

     /**
     * Output initiates with a new socket connection that connects to the server. 
     * A new thread gets instantiated and started with the runnable OutputThread. The start method will in turn invoke the run method from
     * which we writeto the server, and any incoming chat will be read and printed.
     * @throws IOException if the connection terminates
     */
    public void runChat() throws IOException {

        try {

            output = new Socket("localhost", port);
            
            System.out.print("Welcome to the chat room. Please enter your username: ");

            Scanner sc = new Scanner(System.in);

            String username = sc.next();

            System.out.println("Welcome " + username + ", connecting to server ...");

            OutputThread outputThread = new OutputThread(output, username);

            Thread thread = new Thread(outputThread);

            thread.start();

            readSharedChat(output);

            sc.close();
        } catch (IOException e) {
            System.out.printf("No server with port: %d found", port);
        }
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("No port inserted");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        ChatClient chatClient = new ChatClient(port);

        try {
            chatClient.runChat();
        } catch (IOException e) {
            System.out.println("Connection failed");
        }
    }
}
