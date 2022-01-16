
/** 
    @author         Azer Hojlas and Porsev Aslan

    Date:           2021-11-16

    Description:    Program that facilitates communication between chat clients.

    Dependancies:   java.io  ---------- java.net ----------- java.util.ArrayList

    Compilation:    javac ChatServer.java

    Execution:      java ChatServer port

    Usage:          Replace port with any port number that you wish to use, but it must be identical to the client side. 
                    Press ctrl+c to disconnect
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ChatServer {

    ServerSocket serversocket;
    ArrayList<Socket> listeningList;
    InputThread inputThread;
    Socket listens;

    public ChatServer(int port) {

        try {
            this.serversocket = new ServerSocket(port);
            this.listeningList = new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Server cannot be established");
        }
    }
    // Thread for receiving chats and sharing them with all other clients
    private class InputThread implements Runnable {

        Socket recieveInput;
        String chat = "";

        public InputThread(Socket socket) {

            this.recieveInput = socket;
            this.chat = "";
        }

        /**
         * Iterates through the array list of sockets that are connected to the server. If the current iteration is not the recent input 
         * (in order to avoid duplicate writing) its content will be shared with all other threads/ClientSockets
         * @param username that associates with the chat to be shared
         * @param chat to be shared
         */
        public void shareWithAll(String username, String chat) {

            for (Socket socket : ChatServer.this.listeningList) {
                try {

                    if(socket.equals(this.recieveInput)) continue;

                    OutputStream outputStream = socket.getOutputStream();
                    OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                    BufferedWriter bw = new BufferedWriter(streamWriter);

                    String chatText= new String ("\n" + username + ": " + chat + "\n");

                    bw.write(chatText);
                    bw.flush();
                    outputStream.flush();

                } catch (IOException exception) {}
            }
        }
        /**
         * Initiates a BufferedReader that will read in chat messages given that these exist. This incoming chat message will then be shared 
         * with all other clients.
         */
        @Override
        public void run() {
            try {
                InputStreamReader input = new InputStreamReader(recieveInput.getInputStream());
                BufferedReader indata = new BufferedReader(input);
                String username = indata.readLine();
            
                while ((chat = indata.readLine()) != null) {

                    shareWithAll(username, chat);
                }
            } catch (IOException e) {
                System.out.println("A client lost connection to server");
            }
        }
    }
    /**
     * Listens gets assigned a new socket connection with which we create a new InputThread. The listens socket gets added to the existing
     * list of connections. ConnectionsList gets refreshed with in order for the inner private class to also contain the new Connection.
     * A new thread gets instantiated and started with the runnable InputThread. The start method will inturn invoke the run method
     * @throws IOException if the connection terminates
     */
    public void runChat() throws IOException {

        for(;;) {

            try {

                listens = serversocket.accept();
                inputThread = new InputThread(listens);
                listeningList.add(listens);
                Thread thread = new Thread(inputThread);
                thread.start();

            } catch (IOException e) {
                serversocket.close();
            }
        }
    }

    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);

        ChatServer chatServer = new ChatServer(port);

        try {
            chatServer.runChat();
        } catch (IOException e) {
            System.out.println("Server failed");
        }
    }
}
