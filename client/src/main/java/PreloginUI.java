import java.util.Scanner;
import service.UserService;
import request.*;
import result.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dataAccess.AuthDAO;

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
        System.out.println(ANSI_GREEN + "Please log in:" + ANSI_RESET);

        // Prompt for username
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        // Prompt for password
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            // Create the JSON body for the login request
            String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/session")) // Adjust the URI to your login endpoint
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // If the login was successful, server should respond with the authToken
                // Extract authToken and other necessary details here if needed
                System.out.println(ANSI_GREEN + "Login successful. Transitioning to Postlogin UI..." + ANSI_RESET);
                // Here you would transition to Postlogin UI and pass along any needed information such as authToken

                String authToken = createAuth(response);
                PostLoginUI postLoginUI = new PostLoginUI(authToken);
                postLoginUI.processUserInput();

            } else {
                // If login failed, the server response might include the reason which you can display to the user
                System.out.println(ANSI_RED + "Login failed: " + response.body() + ANSI_RESET);
            }
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Error during login: " + e.getMessage() + ANSI_RESET);
        }
    }




    private void register() {
        try {
            System.out.print(ANSI_MAGENTA + "Enter username: " + ANSI_RESET);
            String username = scanner.nextLine();
            System.out.print(ANSI_MAGENTA + "Enter email: " + ANSI_RESET);
            String email = scanner.nextLine();
            System.out.print(ANSI_MAGENTA + "Enter password: " + ANSI_RESET);
            String password = scanner.nextLine();

            String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\"}",
                    username, password, email);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/user")) // Adjust the port number accordingly
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Registration successful. Transitioning to Postlogin UI...");
                // Transition to Postlogin UI here
                String authToken = createAuth(response);
                PostLoginUI postLoginUI = new PostLoginUI(authToken);
                postLoginUI.processUserInput();
            } else {
                System.out.println("Registration failed: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

}