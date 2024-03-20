import java.util.Scanner;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import chess.ChessGame;
import java.net.http.HttpResponse.BodyHandlers;

public class PostLoginUI {

    private Scanner scanner;

    private String authToken;

    // ANSI escape code colors for pretty output
    private final String ANSI_RESET = "\u001B[0m";
    private final String ANSI_CYAN = "\u001B[36m";
    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_YELLOW = "\u001B[33m";
    private final String ANSI_PURPLE = "\u001B[35m";
    private final String ANSI_BLUE = "\u001B[34m";

    public PostLoginUI(String authToken) {
        this.scanner = new Scanner(System.in);
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
    }

    private void logout() {
        System.out.println(ANSI_GREEN + "Logging out..." + ANSI_RESET);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/session")) // Adjust the URI to your logout endpoint
                    .header("Authorization", this.authToken) // Include the authToken in the request header
                    .DELETE() // Use the DELETE method
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println(ANSI_GREEN + "Logout successful." + ANSI_RESET);
                // Transition back to PreloginUI
                this.authToken = null; // Clear authToken
            } else {
                // If logout failed, the server response might include the reason which you can display to the user
                System.out.println(ANSI_RED + "Logout failed: " + response.body() + ANSI_RESET);
            }
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Error during logout: " + e.getMessage() + ANSI_RESET);
        }
    }


    private void createGame() {
        // Implementation of Create Game
        // Example of creating a new game - adjust according to your application's logic
        ChessGame game = new ChessGame(); // Assuming you have a ChessGame class
        GameUI gameUI = new GameUI(game);
        gameUI.displayBoards(); // Or any initial method to start the game interaction
    }

    private void listGames() {
        // Implementation of List Games
    }

    private void joinGame() {
        // Implementation of Join Game
    }

    private void joinAsObserver() {
        // Implementation of Join as Observer
    }

    // Additional methods as needed for functionality (e.g., API calls)
}
