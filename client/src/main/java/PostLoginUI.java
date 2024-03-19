import java.util.Scanner;

public class PostLoginUI {

    private Scanner scanner;
    private final String authToken; // Assuming authToken is needed for some API calls

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
        // Implementation of Logout
    }

    private void createGame() {
        // Implementation of Create Game
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
