import java.util.Scanner;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import dataAccess.DatabaseManager;
import java.net.http.HttpResponse.BodyHandlers;

public class PostLoginUI {

    private Scanner scanner;
    private String username;

    private String authToken;

    // ANSI escape code colors for pretty output
    private final String ANSI_RESET = "\u001B[0m";
    private final String ANSI_CYAN = "\u001B[36m";
    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_YELLOW = "\u001B[33m";
    private final String ANSI_PURPLE = "\u001B[35m";
    private final String ANSI_BLUE = "\u001B[34m";

    public PostLoginUI(String authToken, String username) {
        this.scanner = new Scanner(System.in);
        this.username = username;
        this.authToken = authToken;
        // Assuming authToken is needed for some API calls
    }

    public void displayMenu() {
        System.out.println(ANSI_YELLOW + "Welcome back to 240 Chess! You are logged in." + ANSI_RESET);
        System.out.println( "1. " + ANSI_CYAN + "Help" + ANSI_RESET);
        System.out.println("2. " + ANSI_RED + "Logout" + ANSI_RESET);
        System.out.println("3. " + ANSI_YELLOW + "Create Game" + ANSI_RESET);
        System.out.println("4. " + ANSI_BLUE + "List Games" + ANSI_RESET);
        System.out.println("5. " + ANSI_PURPLE +  "Join Game" + ANSI_RESET);
        System.out.println("6. " + ANSI_GREEN + "Join as Observer" + ANSI_RESET);
        System.out.print("Please enter your choice: ");
    }

    public void processUserInput() {
        boolean keepRunning = true;

        while (keepRunning) {
            displayMenu();
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    displayHelp();
                    break;
                case "2":
                    logout();
                    keepRunning = false; // Assuming you want to exit PostLoginUI after logout
                    break;
                case "3":
                    createGame();
                    break;
                case "4":
                    listGames();
                    break;
                case "5":
                    joinGame();
                    break;
                case "6":
                    joinAsObserver();
                    break;
                default:
                    System.out.println(ANSI_RED + "Invalid input. Please try again." + ANSI_RESET);
                    break;
            }
        }
    }

    private void displayHelp() {
        System.out.println(ANSI_CYAN + "Help:" + ANSI_RESET);
        System.out.println("- Type '1' to see this help message.");
        System.out.println("- Type '2' to logout and return to the PreLoginUI.");
        System.out.println("- Type '3' to create a new chess game.");
        System.out.println("- Type '4' to list all current games.");
        System.out.println("- Type '5' to join a game.");
        System.out.println("- Type '6' to join a game as an observer.");
        //System.out.println(this.authToken);
    }

    private void logout() {
        System.out.println(ANSI_GREEN + "Logging out..." + ANSI_RESET);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/session")) // Adjust the URI to your logout endpoint
                    .header("Authorization", authToken) // Include the authToken in the request header
                    .DELETE() // Use the DELETE method
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println(ANSI_GREEN + "Logout successful." + ANSI_RESET);
                // Transition back to PreloginUI
                authToken = null; // Clear authToken
                PreloginUI preloginUI = new PreloginUI();
                preloginUI.processUserInput();

            } else {
                // If logout failed, the server response might include the reason which you can display to the user
                System.out.println(ANSI_RED + "Logout failed: " + response.body() + ANSI_RESET);
            }
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Error during logout: " + e.getMessage() + ANSI_RESET);
        }
    }


    private void createGame() {
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine();

        // Prepare JSON payload for creating a game with just a name
        String jsonPayload = String.format("{\"gameName\":\"%s\"}", gameName);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/game")) // Adjust this URI accordingly
                    .header("Authorization", authToken) // Include authToken in the request
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Game created successfully. Game ID: " + response.body());
                ChessBoard board = new ChessBoard();
                board.resetBoard();
                GameUI gameUI = new GameUI(board);
                gameUI.displayBoards();
            } else {
                System.out.println("Failed to create game: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Error during game creation: " + e.getMessage());
        }
    }





    private void listGames() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/game"))
                    .header("Authorization", this.authToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Available games: " + response.body());
            } else {
                System.out.println("Failed to list games: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Error during listing games: " + e.getMessage());
        }
    }

    private void joinGame() {
        System.out.print("Enter game ID to join: ");
        String gameId = scanner.nextLine();
        System.out.print("Enter color (BLACK/WHITE): ");
        String color = scanner.nextLine();

        String jsonPayload;
        if ("BLACK".equalsIgnoreCase(color)) {
            jsonPayload = String.format("{\"gameID\":%s, \"black_username\":\"%s\"}", gameId, username);
        } else {
            jsonPayload = String.format("{\"gameID\":%s, \"white_username\":\"%s\"}", gameId, username);
        }


        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/game")) // Ensure this matches your actual join game endpoint
                    .header("Authorization", this.authToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Joined game successfully.");
                // output default starting position!
                ChessBoard board = new ChessBoard();
                board.resetBoard();
                GameUI gameUI = new GameUI(board);
                gameUI.displayBoards();
            } else {
                System.out.println("Failed to join game: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Error during joining game: " + e.getMessage());
        }
    }

    private void joinAsObserver() {
        System.out.print("Enter game ID to join as an Observer: ");
        String gameId = scanner.nextLine();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/game"))
                    .header("Authorization", authToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString("{\"gameID\":" + gameId + ",\"color\":\"" + "\"}"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Joined game successfully.");
                // output default starting position!
                ChessBoard board = new ChessBoard();
                board.resetBoard();
                GameUI gameUI = new GameUI(board);
                gameUI.displayBoards();
            } else {
                System.out.println("Failed to join game: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Error during joining game: " + e.getMessage());
        }
    }

}
