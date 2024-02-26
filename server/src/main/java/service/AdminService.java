package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;

public class AdminService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public AdminService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void clearApplicationData() {
        userDAO.clear();
        authDAO.clearAll();
        // Clear other DAOs as needed
    }
}
