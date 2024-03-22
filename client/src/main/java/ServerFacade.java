
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ServerFacade {
    private final String serverBaseUri = "http://localhost:8080"; // Change this as necessary
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public ServerFacade() {
    }

    public String register(String username, String password, String email) throws Exception {
        String path = "/user";
        String jsonRequest = gson.toJson(new UserRegisterRequest(username, password, email));

        HttpResponse<String> response = sendRequest(path, "POST", jsonRequest, null);
        return handleResponse(response, "authToken");
    }

    public String login(String username, String password) throws Exception {
        String path = "/session";
        String jsonRequest = gson.toJson(new UserLoginRequest(username, password));

        HttpResponse<String> response = sendRequest(path, "POST", jsonRequest, null);
        return handleResponse(response, "authToken");
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
