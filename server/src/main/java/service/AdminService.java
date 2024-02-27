package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import dataAccess.GameDAO;

public class AdminService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public AdminService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clearApplicationData() {
        userDAO.clear();
        authDAO.clearAll();
        gameDAO.clearAll();
    }

    public boolean checkAuth(String authToken){
        return authDAO.isValidToken(authToken);
    }

    public String getUsernameByToken(String authToken) {
        // Placeholder for actual implementation
        return null;
    }
}
