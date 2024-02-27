package handlers;

import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import service.UserService;
import result.*;
import request.*;
import exceptions.AuthenticationException;

public class UserHandler {
    private final UserService userService;
    private final Gson gson;

    public UserHandler(UserService userService) {
        this.userService = userService;
        this.gson = new Gson();
    }

    public Object handleLogin(Request req, Response res) {
        try {
            String requestBody = req.body();
            LoginRequest loginRequest = gson.fromJson(requestBody, LoginRequest.class);
            LoginResult loginResult = userService.login(loginRequest);

            res.status(200); // HTTP OK
            return gson.toJson(loginResult);
        } catch (AuthenticationException e) {
            res.status(401); // Unauthorized
            return gson.toJson(new ErrorResponse(e.getMessage()));
        }
    }
    public Object registerUser(Request req, Response res) {
        try {
            // Parse the request body to extract registration information
            String requestBody = req.body();
            RegisterRequest registerRequest = gson.fromJson(requestBody, RegisterRequest.class);

            // Attempt to register the user using the UserService
            RegisterResult registerResult = userService.register(registerRequest);

            // Set response status based on the outcome of the registration attempt
            if (registerResult.success()) {
                res.status(200); // HTTP OK
            } else if(registerResult.toString().contains("error: Username already exists")){
                res.status(403);
            } else{
                res.status(400); // Bad Request if registration fails
            }

            // Convert the registration result to JSON and return it
            return gson.toJson(registerResult);
        } catch (Exception e) {
            // Handle any exceptions, log errors as needed, and set an appropriate HTTP status code
            res.status(500); // Internal Server Error for unexpected exceptions
            return gson.toJson(new ErrorResponse(e.getMessage())); // Assuming ErrorResponse is defined elsewhere
        }
    }


    public Object logoutUser(Request req, Response res) {
        try {
            // Extract the auth token from the request, e.g., from a header
            String authToken = req.headers("Authorization");

            // Perform the logout action
            boolean success = userService.logout(authToken);

            if (success) {
                res.status(200); // OK
                return gson.toJson(new SimpleResponse(true, "User successfully logged out."));
            } else {
                res.status(401); // Bad Request or consider 401/403 if appropriate
                return gson.toJson(new SimpleResponse(false, "error: Failed to logout user."));
            }


        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return gson.toJson(new ErrorResponse(e.getMessage()));
        }
    }
}
