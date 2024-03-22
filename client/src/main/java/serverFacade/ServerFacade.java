package serverFacade;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ServerFacade {
    private final String serverBaseUri = "http://localhost:8080"; // Change this as necessary
    private final String authToken;

    private final HttpClient httpClient;
    private static final String BASE_URL = "http://localhost:8080";
    private final Gson gson = new Gson();

    public ServerFacade(String authToken) {
        //this.serverBaseUri = serverBaseUri;
        this.authToken = authToken;
        this.httpClient = HttpClient.newHttpClient();
    }

    public String createAuth(HttpResponse<String> response){
        String responseBody = response.body();
        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
        //System.out.println(jsonResponse.get("authToken").getAsString());
        return jsonResponse.get("authToken").getAsString();
    }

    public Result register(String username, String password, String email) {
        String requestBody = gson.toJson(new UserRegisterRequest(username, password, email));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverBaseUri + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String authToken = createAuth(response);
                return Result.success(authToken);
            } else {
                return Result.failure("Registration failed: " + response.body());
            }
        } catch (Exception e) {
            return Result.failure("Error during registration: " + e.getMessage());
        }
    }

    public Result login(String username, String password) {
        String requestBody = gson.toJson(new UserLoginRequest(username, password));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverBaseUri + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String authToken = createAuth(response);
                return Result.success(authToken);
            } else {
                return Result.failure("Login failed: " + response.body());
            }
        } catch (Exception e) {
            return Result.failure("Error during login: " + e.getMessage());
        }
    }

    public Result<Void> logout() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/session"))
                    .header("Authorization", authToken)
                    .DELETE()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return Result.success();
            } else {
                return Result.failure("Logout failed: " + response.body());
            }
        } catch (Exception e) {
            return Result.failure("Error during logout: " + e.getMessage());
        }
    }

    public Result<String> createGame(String gameName) {
        String requestBody = gson.toJson(Map.of("gameName", gameName));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverBaseUri + "/game"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authToken)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // Assuming the server response includes the game ID or some success message
                return Result.success(response.body());
            } else {
                return Result.failure("Failed to create game: " + response.body());
            }
        } catch (Exception e) {
            return Result.failure("Error when trying to create game: " + e.getMessage());
        }
    }


    public String listGames() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/game"))
                    .header("Authorization", authToken)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (Exception e) {
            System.err.println("Error during listing games: " + e.getMessage());
        }
        return "Failed to fetch games.";
    }

    public boolean joinGame(String gameId) {
        // This method might need adjustments based on how you want to specify the joining logic
        String jsonPayload = String.format("{\"gameID\":%s}", gameId);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/game/join")) // Update this endpoint as needed
                    .header("Authorization", authToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.err.println("Error during joining game: " + e.getMessage());
            return false;
        }
    }

    public boolean joinAsObserver(String gameId) {
        // Adjust the endpoint and the request as necessary
        String jsonPayload = String.format("{\"gameID\":%s}", gameId);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/game/observe")) // Ensure this matches your actual join game as observer endpoint
                    .header("Authorization", authToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.err.println("Error during joining game as observer: " + e.getMessage());
            return false;
        }
    }


    private HttpResponse<String> sendRequest(String path, String method, String jsonBody, String authToken) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(new URI(serverBaseUri + path));

        if (method.equalsIgnoreCase("POST")) {
            requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonBody));
        } else if (method.equalsIgnoreCase("DELETE")) {
            requestBuilder.DELETE();
        } // Add other methods as necessary

        requestBuilder.header("Content-Type", "application/json");
        if (authToken != null && !authToken.isEmpty()) {
            requestBuilder.header("Authorization", authToken);
        }

        HttpRequest request = requestBuilder.build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String handleResponse(HttpResponse<String> response, String key) throws Exception {
        if (response.statusCode() == 200) {
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
            return jsonResponse.get(key).getAsString();
        } else {
            throw new Exception("Server responded with status code: " + response.statusCode() + " and message: " + response.body());
        }
    }

    // Add other methods (createGame, listGames, etc.) here using similar patterns.

    // Helper classes for requests
    private static class UserRegisterRequest {
        String username, password, email;

        public UserRegisterRequest(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
        }
    }

    private static class UserLoginRequest {
        String username, password;

        public UserLoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }


}
