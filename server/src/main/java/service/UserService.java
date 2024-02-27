package service;

import dataAccess.UserDAO;
import dataAccess.AuthDAO;
import exceptions.AuthenticationException;
import model.UserData;
import result.LoginResult;
import request.LoginRequest;
import request.RegisterRequest;
import result.RegisterResult;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) {
        // Check if user already exists
        if (userDAO.getUser(request.username()) != null) {
            return new RegisterResult(false, "error: Username already exists.", null, null);
        }

        // Validate password
        if (request.password() == null || request.password().isEmpty()) {
            // Note: Adjust the message and details based on your error handling strategy
            return new RegisterResult(false, "error: Password is required.", null, null);
        }

        // Create new user
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.addUser(newUser);

        // Generate authToken for the new user
        String authToken = authDAO.createAuth(request.username());

        return new RegisterResult(true, "User registered successfully.", authToken, request.username());
    }

    public LoginResult login(LoginRequest request) throws AuthenticationException {
        UserData user = userDAO.getUser(request.username());
        if (user == null || !user.password().equals(request.password())) {
            throw new AuthenticationException("error: Invalid username or password.");
        }
        String authToken = authDAO.createAuth(user.username());
        return new LoginResult(user.username(), authToken);
    }

    public boolean logout(String authToken) {
        // Delete the auth token
        return authDAO.deleteAuth(authToken);
    }

}
