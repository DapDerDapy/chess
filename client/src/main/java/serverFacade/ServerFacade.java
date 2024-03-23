package serverFacade;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.GameData;
import request.GameCreationRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.GameCreationResult;
import result.JoinGameResult;
import result.RegisterResult;
import wrappers.GamesWrapper;


public class ServerFacade {

    private final String authToken;

    private final String serverBaseUri;

    private final HttpClient httpClient;
    private final Gson gson = new Gson();

    public ServerFacade(String authToken, int port) {
        //this.serverBaseUri = serverBaseUri;
        this.authToken = authToken;
        this.httpClient = HttpClient.newHttpClient();
        this.serverBaseUri = "http://localhost:" + port;
    }

    public String createAuth(HttpResponse<String> response){
        String responseBody = response.body();
        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
        //System.out.println(jsonResponse.get("authToken").getAsString());
        return jsonResponse.get("authToken").getAsString();
    }

    public RegisterResult register(String username, String password, String email) {
        String requestBody = gson.toJson(new RegisterRequest(username, password, email));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverBaseUri + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            Gson gson = new Gson();
            return gson.fromJson(response.body(), RegisterResult.class);
        } catch (Exception e) {
            return new RegisterResult(false, "failed", null, null);
        }
    }

    public Result login(String username, String password) {
        String requestBody = gson.toJson(new LoginRequest(username, password, authToken));
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
                    .uri(new URI(serverBaseUri + "/session"))
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


    public GameCreationResult createGame(String gameName) {
        String requestBody = gson.toJson(new GameCreationRequest(gameName, null, null));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverBaseUri + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            Gson gson = new Gson();
            return gson.fromJson(response.body(), GameCreationResult.class);
        } catch (Exception e) {
            return new GameCreationResult(false, "Failed!!", 0);
        }
    }


    public Result<Collection<GameData>> listGames() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(serverBaseUri + "/game"))
                    .header("Authorization", authToken)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Gson gson = new Gson();
                GamesWrapper gamesWrapper = gson.fromJson(response.body(), GamesWrapper.class);
                return Result.success(gamesWrapper.getGames());
            } else {
                // Since failure doesn't need to return data, use Void for the generic type.
                return Result.failure("Failed to list games: " + response.body());
            }
        } catch (Exception e) {
            // Similarly, use Void for the generic type on failure.
            return Result.failure("Error during listing games: " + e.getMessage());
        }
    }



    public JoinGameResult joinGame(int gameId, String userColor) {
        String requestBody = gson.toJson(new JoinGameRequest(gameId, userColor));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverBaseUri + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            Gson gson = new Gson();
            return gson.fromJson(response.body(), JoinGameResult.class);
        } catch (Exception e) {
            return new JoinGameResult(false, "join failed!!!");
        }
    }


    public JoinGameResult joinAsObserver(int gameId) {
        // Adjust the endpoint and the request as necessary
        String requestBody = gson.toJson(new JoinGameRequest(gameId, null));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverBaseUri + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            Gson gson = new Gson();
            return gson.fromJson(response.body(), JoinGameResult.class);
        } catch (Exception e) {
            return new JoinGameResult(false, "join failed!!!");
        }
    }


}
