package sample.Client02;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;



import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


/*
 Integer values for the fields on the board:
 0 - empty (black)
 1 - player (red)
 2 - opponent (yellow)
 3 - 4 in a row (green)

                 column  column  column  column
                 list00  list01  list02  list03
row 0(index 0)    ( )     ( )      ( )     ( )
row 1(index 1)    ( )     ( )      ( )     ( )
row 2(index 2)    ( )     ( )      ( )     ( )
row 3(index 3)    ( )     ( )      ( )     ( )
 */

// To do:
// Multidimentionelt array is stedet for seperate
// send List istedet for int
// Grønne circler ved wongame
// Man kan selv vælge hvor mange brikker der skal være på brættet
// Man kan spille mod computeren
// Spillerne kan selv vælge farve

public class Main extends Application {
    private static Player player, player02;
    private static Board board;
    private static Text username01TopLine, username01BottomLine, username02TopLine, username02BottomLine, vs, isPlaying, userOnline;
    private static GridPane gridpaneBoard;
    private static Circle circle;
    private static Scene startscreenBoard, sceneBoard, connectToServer;
    private static boolean gameIsWon = false;
    private static int sceneWidth = 900;
    private static int sceneHeight = 600;

    private static Socket socket = null;
    //  private static int PORT = 8000;
    //  private static String HOST = "127.0.0.1";
    private static int PORT;
    private static String HOST;
    private static BufferedReader bufferedReader;
    private static PrintStream printWriter;
    private static String messageFromServer;
    private static Scanner input;
    private static PrintStream output;

    /* Creates one cell, consisting of a blue box with a circle in the middle
    *  The parameters it takes is the stackpane, that is going to be added to
    *  the overall grid of circles, the row and column, this particular cell is in */
    @SuppressWarnings("Duplicates")
    public static void createCell(StackPane stackpane, int row, int column) {
        Circle circle = new Circle();
        circle.centerXProperty().set(50);
        circle.centerYProperty().set(50);
        circle.setRadius(45);


        /* Check the integer value of the array corresponding with this cell, to check if the cell
        is empty, or occupied by user og opponent, and set color accordingly */
        if (checkOccupation(row, column)==0) {
            circle.setFill(Color.BLACK);
        }
        else if (checkOccupation(row, column)==1) {
            circle.setFill(Color.FIREBRICK);
        }
        else if (checkOccupation(row, column)==2) {
            circle.setFill(Color.GOLD);
        }
        else if (checkOccupation(row, column)==3) {
            circle.setFill(Color.GREEN);
        }

        Rectangle rect = new Rectangle(100, 100);

        // Blue box around the circle
        Shape cell = Path.subtract(rect, circle);
        cell.setFill(Color.DARKBLUE);

        // Add the circle and the blue around it as a border
        stackpane.getChildren().addAll(cell, circle);
    }


    /* Takes two integers as parameters, that represents the row and column, the method
    should check the value of (empty, occupied by player, occupied by opponant or wongame)*/
    @SuppressWarnings("Duplicates")
    public static int checkOccupation(int row, int column) {
        if (column == 0) {
            return board.getColumn00().get(row);
        }
        else if (column == 1) {
            return board.getColumn01().get(row);
        }
        else if (column == 2) {
            return board.getColumn02().get(row);
        }
        else if (column == 3) {
            return board.getColumn03().get(row);
        }

        return 0;
    }



    /* Create the gridpane that holds all the stacks. This method creates the whole board
    *  The parameter is the gridpane which is going to be added to the scene*/
    @SuppressWarnings("Duplicates")
    public static void createGrid(GridPane gridpane){
        for (int j = 0; j < 4; j++) { // For each row
            for (int i = 0; i < 4; i++){ // For each column
                StackPane stackpane = new StackPane();
                createCell(stackpane, j, i);
                GridPane.setConstraints(stackpane, i, j);
                gridpane.getChildren().add(stackpane);
            }
        }

    }



    /* Create the text at the bottom of the screen that tells the user who he is playing against
    *  and who's turn it is to play */
    @SuppressWarnings("Duplicates")
    public static void createText(HBox hbox, HBox hbox2) {
        // Set the text at the bottom of the screen
        username01TopLine = new Text(player.getName());
        username01BottomLine = new Text(player.getName());

        username02TopLine = new Text(player02.getName());
        username02BottomLine = new Text(player02.getName());
        vs = new Text(" is playing against ");

        if (player.getIsTurn()) {
            isPlaying = new Text(player.getName() + " is up");
        }
        else if (!player.getIsTurn()) {
            isPlaying = new Text(player02.getName() + " is up");
        }
        else {
            isPlaying = new Text("Error");
        }


        isPlaying.setFill(Color.WHITE);
        vs.setFill(Color.WHITE);

        username01TopLine.setStyle("-fx-font: 20 arial;");
        username02TopLine.setStyle("-fx-font: 20 arial;");
        vs.setStyle("-fx-font: 20 arial;");


        if (player.getPlayerNumber() == 1) {
            username01TopLine.setFill(Color.FIREBRICK);
            username01BottomLine.setFill(Color.FIREBRICK);
            username02TopLine.setFill(Color.GOLD);
            username02BottomLine.setFill(Color.GOLD);
        }

        else {
            username02TopLine.setFill(Color.FIREBRICK);
            username01TopLine.setFill(Color.GOLD);
            username02BottomLine.setFill(Color.FIREBRICK);
            username01BottomLine.setFill(Color.GOLD);
        }

        hbox.getChildren().addAll(username01TopLine, vs, username02TopLine);
        hbox.setPadding(new Insets(10, 0, 0, 0));

     /*   hbox2.getChildren().addAll(isPlaying); // temp
        hbox2.setPadding(new Insets(10, 0, 0, 150));*/

    }

    /* Takes the gridpane object that make up the board as parameter. Create buttons
    * over the columns to add pieces */
    @SuppressWarnings("Duplicates")
    public static void rowOfButtons(GridPane gridpane) {
        for (int i = 0; i < 4; i++){ // For each column
            StackPane stackpane = new StackPane();
            int k = createButton(stackpane, i);
            GridPane.setConstraints(stackpane, i, 0);
            gridpane.getChildren().add(stackpane);
            System.out.println("return createbutton: " + k);
        }


    }

    /* Creates one cell, consisting of a black box(transparent) with a circle in the middle
 *  The parameter it takes is the stackpane, that is going to be added to
 *  the overall button row grid, and an int that represents the column of the button
  *  that was clicked. */
    @SuppressWarnings("Duplicates")
    public static int createButton(StackPane stackpane, int column) {
        circle = new Circle();
        circle.setRadius(30);
        circle.setFill(Color.WHITE);

        Rectangle rect = new Rectangle(100, 100);

        // Black box around the circle
        Shape cell = Path.subtract(rect, circle);
        cell.setFill(Color.BLACK);


        // Add the circle and the blue around it as a border
        stackpane.getChildren().addAll(cell, circle);




        // Set action for the circle
        circle.setOnMouseClicked(event -> {

            // Users shouldn't be able to click buttons after the game has ended, or if it's not his turn
            if (!gameIsWon && player.getIsTurn()) {
                System.out.println("Button number: " + column);
                // Check if column is already full, before trying to add a piece
                if (!board.columnIsFull(column)) {
                    // Insert a new value in the array corresponding with the right column as the first empty spot

                    if (column == 0) {
                        player.setIsTurn(false);
                        sendMessageToServer("MOVE " + column + " " + board.firstEmptySpot(board.getColumn00()));
                        board.getColumn00().set(board.firstEmptySpot(board.getColumn00()), 1);
                        // createGrid(gridpaneBoard);
                        // Check if the user has 4 in a row
                        if (fourInARow(column)) {
                            endGame(player.getName());
                        }


                    }
                    else if (column == 1) {
                        player.setIsTurn(false);
                        sendMessageToServer("MOVE " + column + " " + board.firstEmptySpot(board.getColumn01()));
                        board.getColumn01().set(board.firstEmptySpot(board.getColumn01()), 1);
                        // createGrid(gridpaneBoard);
                        if (fourInARow(column)) {
                            endGame(player.getName());
                        }


                    }
                    else if (column == 2) {
                        player.setIsTurn(false);
                        sendMessageToServer("MOVE " + column + " " + board.firstEmptySpot(board.getColumn02()));
                        board.getColumn02().set(board.firstEmptySpot(board.getColumn02()), 1);
                        //   createGrid(gridpaneBoard);
                        if (fourInARow(column)) {
                            endGame(player.getName());
                        }


                    }
                    else if (column == 3) {
                        player.setIsTurn(false);
                        sendMessageToServer("MOVE " + column + " " + board.firstEmptySpot(board.getColumn03()));
                        board.getColumn03().set(board.firstEmptySpot(board.getColumn03()), 1);
                        //    createGrid(gridpaneBoard);
                        if (fourInARow(column)) {
                            endGame(player.getName());
                        }


                    }

                }
            }


        });



        return 0;

    }

    // Checks all methods in Board class, to see if if any of them returns true
    // Needs to be changed later
    @SuppressWarnings("Duplicates")
    public static boolean fourInARow(int column) {
        if (board.columnContainsOnlyOneColor(column)) {
            return true;
        }

        if (column == 0) {
            // Find the first empty spot and plus one, because one piece will be added
            // This should be changed later
            if (board.rowContainsOnlyOneColor(board.firstEmptySpot(board.getColumn00())+1)){
                return true;
            }
        }
        else if (column == 1) {
            if (board.rowContainsOnlyOneColor(board.firstEmptySpot(board.getColumn01())+1)){
                return true;
            }
        }
        if (column == 2) {
            if (board.rowContainsOnlyOneColor(board.firstEmptySpot(board.getColumn02())+1)){
                return true;
            }
        }
        if (column == 3) {
            if (board.rowContainsOnlyOneColor(board.firstEmptySpot(board.getColumn03())+1)){
                return true;
            }
        }


        else if (board.diagonalOnlyContainsOneColorLeftToRight()) {
            return true;
        }

        else if (board.diagonalOnlyContainsOneColorRightToLeft()) {
            return true;
        }
        return false;

    }



    /* Initialize method when game is won to change the GUI
    * The parameter is the username of the person who's won */
    @SuppressWarnings("Duplicates")
    public static void endGame(String username) {
        // Color 4 pieces green


        // Remove previous text initialized in createText
        username02TopLine.setText("");
        vs.setText("");
        username01BottomLine.setText("");
        username02BottomLine.setText("");
        isPlaying.setText("");

        // Let the user know who's wom
        username01TopLine.setText(username + " won the game!");
        username01TopLine.setFill(Color.GREEN);

        // Set boolean, to disable buttons
        gameIsWon = true;

    }

    @SuppressWarnings("Duplicates")
    public static void sceneConnectToServer(Stage primaryStage){
        // IP address
        TextField textfieldIP = new TextField("127.0.0.1");
        Text textIP = new Text("Choose IP address (default 127.0.0.1)");

        // Port number
        TextField textfieldPORT = new TextField("8000");
        Text textPORT = new Text("Choose port (default 8000)");

        // Connect to server
        Button submit = new Button("Connect");

        // Info text below below button
        Text text = new Text("");

        submit.setOnMouseClicked(event -> {
            HOST = textfieldIP.getText();
            PORT = Integer.parseInt(textfieldPORT.getText());
            if (!connectToSocket()) {
                text.setText("Could not connect to server");
                text.setFill(Color.FIREBRICK);
            }
            else {
                startscreenBoard(primaryStage);
            }

        });



        VBox vbox = new VBox();
        vbox.getChildren().addAll(textfieldIP, textIP, textfieldPORT, textPORT, submit, text);
        vbox.setPadding(new Insets(50,300,0,250));
        vbox.setSpacing(10);

        // Create the start screen scene. This is where the user types username
        connectToServer = new Scene(vbox, sceneWidth, sceneHeight);
        connectToServer.setFill(Color.GRAY);
    }

    @SuppressWarnings("Duplicates")
    public static void startscreenBoard(Stage primaryStage) {

        // Select username
        TextField userinput = new TextField();
        Text header = new Text("Select a username to start playing");
        Button submit = new Button("Select");

        // Info text below button
        userOnline = new Text("");
        userOnline.setFill(Color.DARKBLUE);

        /* Tell server user logs on, and get a response regarding the other players online.
         * Calling this method will set the player objects number, so that when the submit button
          * is set on action, the event handler can determine what to do depending on
          * the players number */
        try {
            userLogsIn();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Choose username
        submit.setOnAction(event -> {
            player.setName(userinput.getText());
            userOnline.setFill(Color.GREEN);
            userOnline.setText("Your username is: " + player.getName());
            userinput.clear();



            if (player.getPlayerNumber() == 1) {
                sceneBoard(primaryStage);
                waitForSecondUser(); // Create state for the client to wait for new user
            }
            else if (player.getPlayerNumber() == 2) {
                // sendMessageToServer("GET_USR"); // request the other players username from server. after that, start game
                sceneBoard(primaryStage);
            }
            else {
                System.out.println("Error. Invalid player number in method startscreenBoard");
            }

        });

        VBox vbox = new VBox();
        vbox.getChildren().addAll(header, userinput, submit, userOnline);
        vbox.setPadding(new Insets(50,300,0,200));
        vbox.setSpacing(10);

        // Create the start screen scene. This is where the user types username
        startscreenBoard = new Scene(vbox, 900, 600);
        startscreenBoard.setFill(Color.GRAY);
        primaryStage.setScene(startscreenBoard);
    }



    @SuppressWarnings("Duplicates")
    public static void sceneBoard(Stage primaryStage) {

        // Create the text for the game
        HBox hbox = new HBox();
        HBox hbox2 = new HBox();
        createText(hbox, hbox2);


        // Create the grid that holds the spots for the circles
        gridpaneBoard = new GridPane();
        GridPane gridpaneButtons = new GridPane();

        // Add stackpanes to the gridpane board
        createGrid(gridpaneBoard);

        // Add stackpanes to the gridpane button row
        rowOfButtons(gridpaneButtons);

        // Create a vbox that contains the text and the grid pane. This is added to the scene
        VBox vbox = new VBox();
        vbox.getChildren().addAll(gridpaneButtons, gridpaneBoard, hbox, hbox2);

        // Set the coordinates for the vbox
        vbox.setLayoutX(250);
        vbox.setLayoutY(10);

        // Create the board scene. This is where the actual game is displayed
        sceneBoard = new Scene(vbox, sceneWidth, sceneHeight);
        sceneBoard.setFill(Color.BLACK);

        primaryStage.setScene(sceneBoard);


    }




    // Creates a thread that initialize a socket and connect to the server
    public static boolean connectToSocket() { // Not sure if it needs Runnable
        try {
            socket = new Socket(HOST, PORT);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("Connected to server");
        return true;
    }

    /* Open streams when client tries to joins the server. Wait for a response from server, to se
    * if the client can connect or if the server is busy/can't connect */
    @SuppressWarnings("Duplicates")
    public static void userLogsIn() throws IOException {
        // Create streams, that will be will be open unitl client closed program
        input = new Scanner(socket.getInputStream());
        output = new PrintStream(socket.getOutputStream(), true);

        // Get message from server, to se if server is available and what number the player has
        String message = recieveMessageFromServer();


        // Get message from server telling if client was successfully connected
        if (message.startsWith("OK")) {
            // Player number is 1, and player is up when game starts
            if (message.endsWith("1")) {
                player.setPlayerNumber(1);
                player.setIsTurn(true);

                player02.setPlayerNumber(2);
                player02.setIsTurn(false);

                userOnline.setText("No other user is currently connected. After choosing a username, wait for another user to log on");

            }
            // Player number is 2, and player is not up when the game starts
            else if (message.endsWith("2")) {
                player.setPlayerNumber(2);
                player.setIsTurn(false);

                player02.setPlayerNumber(1);
                player02.setIsTurn(true);
                userOnline.setText("Another user is currently online. Choose username to play");
            }
        }
        else if (message.startsWith("ERR")) {
            System.out.println("Server too busy");
            userOnline.setFill(Color.FIREBRICK);
            userOnline.setText("Too many players are currently on this server. Try again later");
        }

    }

    public static void waitForSecondUser() {}

    @SuppressWarnings("Duplicates")
    public static void opponantMakesMove() {

        System.out.println("Opponant makes move");
        // Wait for response from the server
        String message = recieveMessageFromServer();
        Scanner s = new Scanner(message);



        // Check message from server
        if (message.startsWith("MOVE")) {
            System.out.println(message);
            s.next(); // the OK
            int column = s.nextInt();
            int row = s.nextInt();

            // Update the board
            if (column == 0) {
                board.getColumn00().set(row, 2);
            }
            else if (column == 1) {
                board.getColumn01().set(row, 2);
            }
            else if (column == 2) {
                board.getColumn02().set(row, 2);
            }
            else if (column == 3) {
                board.getColumn03().set(row, 2);
            }



        }
        else if (message.startsWith("WIN")) {
            // Other user wins
        }


        // Update who's turn it is
        System.out.println("Finished updating");
        player.setIsTurn(true);
        player02.setIsTurn(false);

        // Update the GUI
        createGrid(gridpaneBoard); // Update names

    }


    /* Receive messages from the server and return as a String. The methods that call
    * this method are responsible for reading the protocol messages (eg. GET_USR, MOVE, ERR)
    * and handle the message accordingly. This method only returns the data */
    public static String recieveMessageFromServer() {

        try {
            input = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        messageFromServer = input.nextLine();

        System.out.println("receiveMessageFromServer");
        return messageFromServer;
    }


    /* Send message to the server. When calling this method, always use proper protocol
    *  when setting the parameter, or the server will ignore the message */
    @SuppressWarnings("Duplicates")
    public static void sendMessageToServer(String message) {
        System.out.println("sendMessageToServer");
        try {
            printWriter = new PrintStream(socket.getOutputStream(), true);
            System.out.println("Printing from sendmessagetoserver");
            printWriter.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    @SuppressWarnings("Duplicates")
    public void start(Stage primaryStage) throws Exception{
        // Try to connect to server
        // connectToSocket();

        // Initialize the board and players
        board = new Board();
        player = new Player();
        player02 = new Player();

        // Temp until login works
        player02.setIsTurn(true);
        player02.setName("TheYee");
        System.out.println("Players turn");

        player.setIsTurn(false);
        player.setName("ohHaiMark_93");

        // Create the scenes for the application
        sceneConnectToServer(primaryStage);
        // startscreenBoard(); // udkommenter når den ikke er i brug
        // sceneBoard();



        // Create stream and get acceptance/deny message from server, and create username
        // userLogsIn(); // Udkommenter for at teste spil

        // Temp until the message is read in login
        // This is the OK message after user successfully logs in
        //



        // Set the scene and display it
        primaryStage.setScene(connectToServer);
        primaryStage.setTitle("Connect4");


        // The flow of the game
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                primaryStage.show();
                if (primaryStage.getScene().equals(sceneBoard)) {
                    while (!player.getIsTurn()) {
                        opponantMakesMove();
                    }
                }
            }
        }.start();

    }


    public static void main(String[] args) {launch(args);}
}
