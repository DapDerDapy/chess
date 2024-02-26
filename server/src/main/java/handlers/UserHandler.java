package handlers;

import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import service.UserService;
import result.LoginResult;
import request.LoginRequest;
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

    // Additional handler methods for register, logout, etc.
}
