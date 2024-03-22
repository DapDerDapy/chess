package ui;
import java.util.Scanner;
import serverFacade.ServerFacade;
import serverFacade.*;

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

    private ServerFacade serverFacade;

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
        this.serverFacade = new ServerFacade(authToken);
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
        Result<Void> result = serverFacade.logout();
        if (result.isSuccess()) {
            System.out.println(ANSI_GREEN + "Logout successful." + ANSI_RESET);
            authToken = null; // Clear authToken
            // Optionally, switch back to PreloginUI
        } else {
            System.out.println(ANSI_RED + "Logout failed: " + result.getErrorMessage() + ANSI_RESET);
        }
    }

    private void createGame() {
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine();
        Result<String> result = serverFacade.createGame(gameName);
        if (result.isSuccess()) {
            System.out.println("Game created successfully. Game ID: " + result.getData());
        } else {
            System.out.println(result.getErrorMessage());
        }
    }


    private void listGames() {
        String gamesList = serverFacade.listGames();
        System.out.println("Available games: " + gamesList);
    }

    private void joinGame() {
        System.out.print("Enter game ID to join: ");
        String gameId = scanner.nextLine();
        boolean success = serverFacade.joinGame(gameId);
        if (success) {
            System.out.println("Joined game successfully.");
            // Optionally display the chessboard
        } else {
            System.out.println("Failed to join game.");
        }
    }

    private void joinAsObserver() {
        System.out.print("Enter game ID to join as an Observer: ");
        String gameId = scanner.nextLine();
        boolean success = serverFacade.joinAsObserver(gameId);
        if (success) {
            System.out.println("Joined game as observer successfully.");
            // Optionally display the chessboard
        } else {
            System.out.println("Failed to join game as observer.");
        }
    }

}
