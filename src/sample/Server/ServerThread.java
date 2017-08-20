package sample.Server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by julieglasdam on 10/07/2017.
 */
public class ServerThread extends Thread{
    private Socket socket;
    private PrintStream printstream;
    private Scanner input;
    private String message;
    private int number; // Player 1 or 2
    private Date date;
    private String username = "";

    /* Constructor takes a socket as parameter, so the thread knows which client
     the thread belongs to */
    public ServerThread(Socket socket, int number) throws IOException {
        this.socket = socket;
        this.number = number;

        // Set up input stream and output stream, to connect with client
        input = new Scanner(socket.getInputStream());
        printstream = new PrintStream(socket.getOutputStream(), true);
    }

    /* Client sends message when making a move. Messages does not include request to
    * join the server, because this thread will only be created after client
    * is accepted by server */
    public void receiveMessageFromClient() {
        System.out.println("recieveMessageFromClient " + number);
        // Receive message from client and print to console
        message = input.nextLine();
        System.out.println("CLIENT " + number + "> " + message);


        // Update other client
        if (message.startsWith("MOVE")) {
            sendMessageToClient(message);
        }

    }

    // Server thread updates the other server thread, to let it know it needs to update
    // Needs to updated in the future if more clients than two, can be accepted
    public void sendMessageToClient(String message){
        System.out.println("sendMessageToClient " + number);
        // Send message with move to the other client
        synchronized (this) {
            // Client 1 sending message to client 2
            if (number == 1) {
                Main.threads.get(1).updateFromThread(message);
            }
            // Client 2 sending message to client 1
            else if (number == 2) {
                Main.threads.get(0).updateFromThread(message);
            }
            else {
                throw new IndexOutOfBoundsException();
            }
        }
    }


    // The other thread calls this method to let it now the other client have updated their board
    public void updateFromThread(String message) {
        System.out.println("updateFromThread " + number);
        printstream.println(message);
    }

    // Call this if the thread needs to get input before sending
    public void updateFromThread() {
        String message = input.nextLine();
        printstream.println(message);
    }


    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public void run() {
        System.out.println("run " + number);
        // Send message to client to let them know they have joined successfully
        printstream.println("OK " + number); // 1 & 2


        do {
            receiveMessageFromClient();

        } while (!message.equals("CLOSE"));

        // Close connection
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
