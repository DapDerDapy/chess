package service;

import dataAccess.UserDAO;
import dataAccess.AuthDAO;
import exceptions.AuthenticationException;
import model.UserData;
import request.*;
import result.LoginResult;

public class UserService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest request) throws AuthenticationException {
        UserData user = userDAO.getUser(request.username());
        if (user == null || !user.password().equals(request.password())) {
            throw new AuthenticationException("Invalid username or password.");
        }
        String authToken = authDAO.createAuth(user.username());
        return new LoginResult(user.username(), authToken);
    }


    // Implement login and logout similarly...
}
