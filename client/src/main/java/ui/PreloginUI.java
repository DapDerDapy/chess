package ui;

import java.util.Scanner;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import result.RegisterResult;
import serverFacade.ServerFacade;
import serverFacade.Result;

public class PreloginUI {

    private Scanner scanner;
    private boolean moveToPost = false;

    // ANSI escape code colors
    private final String ANSI_RESET = "\u001B[0m";
    private final String ANSI_YELLOW = "\u001B[33m";
    private final String ANSI_CYAN = "\u001B[36m";
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_MAGENTA = "\u001B[35m";
    private final String ANSI_RED = "\u001B[31m";

    private ServerFacade serverFacade = new ServerFacade(null, 0);

    public PreloginUI() {
        this.scanner = new Scanner(System.in);
    }

    public boolean moveToPost() {
        return moveToPost;
    }

    public String createAuth(HttpResponse<String> response){
        String responseBody = response.body();
        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
        System.out.println(jsonResponse.get("authToken").getAsString());
        return jsonResponse.get("authToken").getAsString();
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
                    quit();
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
        System.out.println(ANSI_GREEN + "Please log in:" + ANSI_RESET);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            Result<String> result = serverFacade.login(username, password);
            if (result.isSuccess()) {
                String authToken = result.getData();
                System.out.println(ANSI_GREEN + "Login successful. Logging you in..." + ANSI_RESET);
                PostLoginUI postLoginUI = new PostLoginUI(authToken, username);
                postLoginUI.processUserInput();
            } else {
                System.out.println(ANSI_RED + result.getErrorMessage() + ANSI_RESET);
            }
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Unexpected error during login: " + e.getMessage() + ANSI_RESET);
        }
    }



    private void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        RegisterResult result = serverFacade.register(username, password, email);
        if (result.success()) {
            String authToken = result.authToken();
            System.out.println("Registration successful. Transitioning to Postlogin UI...");
            PostLoginUI postLoginUI = new PostLoginUI(authToken, username);
            postLoginUI.processUserInput();
        } else {
            System.out.println("Registration failed: " + result.message());
        }
    }



    private void quit(){
        System.out.println(ANSI_GREEN + "Goodbye!" + ANSI_RESET);
        System.exit(0);
    }

}