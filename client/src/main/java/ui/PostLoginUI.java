package ui;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Scanner;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import result.GameCreationResult;
import result.JoinGameResult;
import serverFacade.ServerFacade;
import serverFacade.*;
import chess.ChessBoard;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import websocket.WSClientEndpoint;


public class PostLoginUI {

    private Scanner scanner;
    private String username;

    private ServerFacade serverFacade;

    private String authToken;

    private ChessGame game;
    private WSClientEndpoint wsClient;


    // ANSI escape code colors for pretty output
    private final String ANSI_RESET = "\u001B[0m";
    private final String ANSI_CYAN = "\u001B[36m";
    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_YELLOW = "\u001B[33m";
    private final String ANSI_PURPLE = "\u001B[35m";
    private final String ANSI_BLUE = "\u001B[34m";

    public PostLoginUI(String authToken, String username) {
        this.game = new ChessGame();
        this.scanner = new Scanner(System.in);
        this.username = username;
        this.authToken = authToken;
        this.serverFacade = new ServerFacade(authToken, 8080);
        this.wsClient = null;
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
    }
    private void logout() {
        Result<Void> result = serverFacade.logout();
        if (result.isSuccess()) {
            System.out.println(ANSI_GREEN + "Logout successful." + ANSI_RESET);
            authToken = null; // Clear authToken
        } else {
            System.out.println(ANSI_RED + "Logout failed: " + result.getErrorMessage() + ANSI_RESET);
        }
    }

    private void createGame() {
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine();
        GameCreationResult result = serverFacade.createGame(gameName);
        if (result.success()) {
            System.out.println("Game created successfully.");
        } else {
            System.out.println("Failed to create game.");
        }
    }

    private void listGames() {
        Result<Collection<GameData>> result = serverFacade.listGames();
        if (result.isSuccess()) {
            System.out.println("Available games: " + result.getData());
        } else {
            System.out.println(result.getErrorMessage());
        }
    }

    private void joinGame() {
        System.out.print("Enter game ID to join: ");
        int gameId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Select which Color you would like to play as (BLACK/WHITE): ");
        String userColor = scanner.nextLine().toUpperCase();

        JoinGameResult joinResult = serverFacade.joinGame(gameId, userColor);
        if (joinResult.success()) {
            Result<ChessGame> gameStateResult = serverFacade.getGameState(gameId);
            if (gameStateResult.isSuccess()) {
                ChessGame game = gameStateResult.getData();
                System.out.println("game = " + game);
                System.out.println("Successfully joined game.");
                try {
                    URI uri = new URI("ws://localhost:8080/connect");
                    GameUI gameBoard = new GameUI(userColor, gameId, authToken, uri, game);
                    gameBoard.processUserInput();
                } catch (Exception e) {
                    System.out.println("WebSocket connection failed: " + e.getMessage());
                }
            } else {
                System.out.println("Failed to load game state: " + gameStateResult.getErrorMessage());
            }
        } else {
            System.out.println("Failed to join game: " + joinResult.message());
        }
    }


    private void joinAsObserver() {
        System.out.print("Enter game ID to join as an Observer: ");
        int gameId = scanner.nextInt();
        scanner.nextLine();

        JoinGameResult result = serverFacade.joinAsObserver(gameId);
        if (result.success()) {
            Result<ChessGame> gameStateResult = serverFacade.getGameState(gameId);
            if (gameStateResult.isSuccess()) {
                ChessGame game = gameStateResult.getData();
                System.out.println("game = " + game);
                System.out.println("Successfully joined game.");
                try {
                    URI uri = new URI("ws://localhost:8080/connect");
                    GameUI gameBoard = new GameUI(null, gameId, authToken, uri, game);
                    gameBoard.processUserInput();
                } catch (Exception e) {
                    System.out.println("WebSocket connection failed: " + e.getMessage());
                }
            } else {
                System.out.println("Failed to load game state: " + gameStateResult.getErrorMessage());
            }
        } else {
            System.out.println("Failed to join game as Observer: " + result.message());
        }
    }


}
