package service;

import dataAccess.UserDAO;
import dataAccess.AuthDAO;
import exceptions.AuthenticationException;
import model.UserData;
import result.LoginResult;
import request.LoginRequest;
import request.RegisterRequest;
import result.RegisterResult;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) {
        // Check if user already exists
        if (userDAO.getUser(request.username()) != null) {
            return new RegisterResult(false, "error: Username already exists.", null, null);
        }
        if (userDAO.getUser(request.username()) != null) {
            return new RegisterResult(false, "error: Username already exists.", null, null);
        } else if (request.password() == null || request.password().isEmpty()) {
            return new RegisterResult(false, "error: Password cannot be empty.", null, null);
        }

        // Hash the password before storing it
        String hashedPassword = encoder.encode(request.password());

        // Create new user with the hashed password
        UserData newUser = new UserData(request.username(), hashedPassword, request.email());
        userDAO.addUser(newUser);

        // Generate authToken for the new user
        String authToken = authDAO.createAuth(request.username());

        return new RegisterResult(true, "User registered successfully.", authToken, request.username());
    }

    public LoginResult login(LoginRequest request) throws AuthenticationException {
        UserData user = userDAO.getUser(request.username());
        if (user == null) {
            throw new AuthenticationException("error: Invalid username or password.");
        }

        // Verify the password with the hashed password stored in the database
        if (!encoder.matches(request.password(), user.password())) {
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
