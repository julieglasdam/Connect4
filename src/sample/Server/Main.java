package sample.Server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/*
* ERR - Some kind of error
* OK - User have joined server successfully, last word is the client number (can only be 1 or 2 for now)
* GET_USR - Get username of the other player
* MOVE - client makes a move
* WON - The client who sent the message won. Notify other player
*
* */

public class Main {
    private static ServerSocket serverSocket = null;
    private static Socket socket = null;
    private static int PORT = 8000;
    private static int clientNumber = 0;
    private static PrintStream printstream;
    private static Scanner input;
    public static ArrayList<ServerThread> threads = new ArrayList<>(); // Currently only 0 & 1


    // Create new socket, and wait for a client to connect
    public static void openConnection() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Connection open\n");
    }


    public static void handleClient() throws IOException {
        // Wait for client to connect, and accept them when they connect using the right port and IP
        socket = serverSocket.accept();

        if (clientNumber < 2) {  // Only 2 players can join for now. In the future new players will be paired with each others
            clientNumber++;
            System.out.println("Client " + clientNumber + " joined the server");

            // Add to list of clients
            threads.add(new ServerThread(socket, clientNumber));

            // Create new thread to handle client
            ServerThread serverThreadTCP = new ServerThread(socket, clientNumber);
            serverThreadTCP.start();

        }
        else {
            PrintStream printstream = new PrintStream(socket.getOutputStream(), true);
            printstream.println("ERR The server have too many players. Try again later");
        }


    }


    public static void main(String[] args) throws IOException {
        openConnection();
        do {
            handleClient();
        } while (true);
    }
}

