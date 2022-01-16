/**
 * @authors Porsev Aslan & Azer Hojlas 2021-12-09
 * Web server for guessing game
 * Notice: Remember to clear the cookies for localhost each time you test the server
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class server {

    static ArrayList<game> startedGames = new ArrayList<>();
    static int cookieID = 0;
    static int port = 1234;

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            try {
                System.out.println("Waiting for client ...");

                Socket client = serverSocket.accept();
                InputStream inputStream = client.getInputStream();
                OutputStream outputStream = client.getOutputStream();
                BufferedReader requestReader = new BufferedReader(new InputStreamReader(inputStream));

                System.out.println("Client connected!");
                getRequest(requestReader, outputStream, client);
                client.close();
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks the request from client and finds the necessary information to pass forward to the next function.
     * Ignores /favicon.ico requests.
     * @param bufferedReader
     * @param outputStream
     * @param client
     * @throws IOException
     */
    public static void getRequest(BufferedReader bufferedReader, OutputStream outputStream, Socket client) throws IOException {
        int cookie = -1;
        int guessValue = -1;
        int newGame = -1;
        String request = bufferedReader.readLine();

        if (request != null) {
            StringTokenizer tokens = new StringTokenizer(request, " ?");
            if (tokens.nextToken().equals("GET")) {
                if (tokens.nextToken().equals("/favicon.ico")) {
                    client.close();
                    outputStream.close();
                    bufferedReader.close();
                    return;
                } else if (request.equals("GET /new? HTTP/1.1")) {
                    newGame = 1;
                }
                while (tokens.hasMoreTokens()) {
                    String word = tokens.nextToken();
                    if (word.contains("guess")) {
                        String[] guess = word.split("=");
                        guessValue = Integer.parseInt(guess[1].trim());
                    }
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(request + "\n");

        while ((request = bufferedReader.readLine()) != null && request.length() > 0) {
            if (request.contains("gameID")) {
                String[] words = request.split(" ");
                for (String word : words) {
                    if (word.contains("gameID")) {
                        String[] gameID = word.split("=");
                        cookie = Integer.parseInt(gameID[1].trim());
                    }
                }
            }
            stringBuilder.append(request + "\n");
        }

        if (cookie == -1) {
            System.out.println("Starting new instance of a game");
            addStartedGame();
            sendServerResponse(outputStream, cookie, guessValue, newGame);
            cookieID++;
        } else {
            if (newGame == 1) {
                startedGames.get(cookie).restart();
            }
            sendServerResponse(outputStream, cookie, guessValue, newGame);
        }
        System.out.println(stringBuilder);
        System.out.println("Request processed.");
    }

    /**
     * Writes the servers HTTP response to the clients outputstream.
     * @param outputStream
     * @param cookie
     * @param guess
     * @param newGame
     * @throws IOException
     */
    public static void sendServerResponse(OutputStream outputStream, int cookie, int guess, int newGame) throws IOException {
        byte[] response = serverResponse(cookie, guess, newGame);
        outputStream.write(response);
    }

    /**
     * Generates correct HTTP response depending on the values of the parameters.
     * @param cookie
     * @param guess
     * @param newGame
     * @return The response in bytes as a byte-array.
     */
    public static byte[] serverResponse(int cookie, int guess, int newGame) {
        StringBuilder response = new StringBuilder();
        String html = pageBuilder(cookie, guess, newGame);
        response.append("HTTP/1.1 200 OK\r\n");
        response.append("Content-Type: text/html\r\n");
        response.append("Cache-Control: no-cache, no-store, must-revalidate\r\n");
        response.append("Pragma: no-cache\r\n");
        response.append("Expires: 0\r\n");
        if (cookie == -1) {
            cookie = cookieID;
            response.append("Set-Cookie: gameID= " + cookie);
        }
        response.append("\r\n");
        response.append(html);
        return response.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Builds the html page depending on the parameter values.
     * @param cookie
     * @param guess
     * @param newGame
     * @return The chosen html page as a string.
     */
    public static String pageBuilder(int cookie, int guess, int newGame) {
        String html = "";
        if (cookie == -1 || newGame == 1) {
            html = basePage();
            return html;
        } else {

            String getGuessCorrectness = startedGames.get(cookie).guess(guess);

            if (getGuessCorrectness.equals("Correct")) {
                html = correctPage(cookie);
            } else if (getGuessCorrectness.equals("Higher")) {
                html = higherPage();
            } else if (getGuessCorrectness.equals("Lower")) {
                html = lowerPage();
            }

            return html;
        }
    }

    /**
     * @return base page as a string.
     */
    public static String basePage() {
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>\n");
        content.append("<html>\n");
        content.append("<body>\n");
        content.append("\n");
        content.append("<h1>Number guessing game</h1>\n");
        content.append("\n");
        content.append("<p><b>Welcome to the number guessing game!</b> \n I'm thinking of a number between 1 and 100, can you guess which it is?");
        content.append("</p>");
        content.append("<form method=\"get\"> \n");
        content.append("    <label for=\"guess\">What's your guess?:</label>\n");
        content.append("    <input type=\"text\" id=\"guess\" name=\"guess\"><br><br>\n");
        content.append("    <button type=\"submit\">Submit</button>\n");
        content.append("</form>\n" + "\n" + "</body>\n" + "</html>\n");
        return content.toString();
    }

    /**
     * @return guess higher page as a string.
     */
    public static String higherPage() {
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>\n");
        content.append("<html>\n");
        content.append("<body>\n");
        content.append("\n");
        content.append("<h1>Number guessing game</h1>\n");
        content.append("\n");
        content.append("<p><b>Guess higher!</b> \n I'm thinking of a number between 1 and 100, can you guess which it is?");
        content.append("</p>");
        content.append("<form method=\"get\"> \n");
        content.append("    <label for=\"guess\">What's your guess?:</label>\n");
        content.append("    <input type=\"text\" id=\"guess\" name=\"guess\"><br><br>\n");
        content.append("    <button type=\"submit\">Submit</button>\n");
        content.append("</form>\n" + "\n" + "</body>\n" + "</html>\n");
        return content.toString();
    }

    /**
     * @return guess lower page as a string.
     */
    public static String lowerPage() {
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>\n");
        content.append("<html>\n");
        content.append("<body>\n");
        content.append("\n");
        content.append("<h1>Number guessing game</h1>\n");
        content.append("\n");
        content.append("<p><b>Guess lower!</b> \n I'm thinking of a number between 1 and 100, can you guess which it is?");
        content.append("</p>");
        content.append("<form method=\"get\"> \n");
        content.append("    <label for=\"guess\">What's your guess?:</label>\n");
        content.append("    <input type=\"text\" id=\"guess\" name=\"guess\"><br><br>\n");
        content.append("    <button type=\"submit\">Submit</button>\n");
        content.append("</form>\n" + "\n" + "</body>\n" + "</html>\n");
        return content.toString();
    }

    /**
     * @param cookie
     * @return guessed correctly page with number of attempts and a new game button that restarts the game for corresponding cookie.
     */
    public static String correctPage(int cookie) {
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>\n");
        content.append("<html>\n");
        content.append("<body>\n");
        content.append("\n");
        content.append("<h1>Number guessing game</h1>\n");
        content.append("\n");
        content.append("<p><b>You've guessed correctly with " + startedGames.get(cookie).getAttempts() + " attempts! The correct answer was: " + startedGames.get(cookie).getRandomNumber() + "</b> \n Start new game?");
        content.append("</p>");
        content.append("<form action=\"/new\" method=\"get\"> \n");
        content.append("    <button type=\"submit\" id=\"new\">New game</button>\n");
        content.append("</form>\n" + "\n" + "</body>\n" + "</html>\n");
        return content.toString();
    }

    /**
     * Adds a new game to the arraylist of started games.
     */
    public static void addStartedGame() {
        startedGames.add(new game());
    }
}