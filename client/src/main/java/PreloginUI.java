import java.util.Scanner;

public class PreloginUI {

    private Scanner scanner;

    // ANSI escape code colors
    private final String ANSI_RESET = "\u001B[0m";
    private final String ANSI_YELLOW = "\u001B[33m";
    private final String ANSI_CYAN = "\u001B[36m";
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_MAGENTA = "\u001B[35m";
    private final String ANSI_RED = "\u001B[31m";

    public PreloginUI() {
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println(ANSI_YELLOW + "Welcome to 240 Chess!" + ANSI_RESET);
        System.out.println( "1." + ANSI_CYAN + " Help" + ANSI_RESET);
        System.out.println( "2." + ANSI_GREEN + " Login" + ANSI_RESET);
        System.out.println( "3." + ANSI_MAGENTA + " Register" + ANSI_RESET);
        System.out.println( "4." + ANSI_RED + " Quit" + ANSI_RESET);
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
                    login();
                    break;
                case "3":
                    register();
                    break;
                case "4":
                    keepRunning = false;
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
        System.out.println("- Type '2' to login.");
        System.out.println("- Type '3' to register a new account.");
        System.out.println("- Type '4' to quit the application.");
    }

    private void login() {
        System.out.println(ANSI_GREEN + "Login (this functionality is not implemented yet)" + ANSI_RESET);
        // Implementation for login goes here
    }

    private void register() {
        System.out.println(ANSI_GREEN + "Register (this functionality is not implemented yet)" + ANSI_RESET);
        // Implementation for register goes here
    }
}
