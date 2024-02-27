package server;

import handlers.*;
import service.*;
import dataAccess.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Instantiate concrete DAO implementations
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        // Register your endpoints and handle exceptions here.
        UserService userService = new UserService(userDAO, authDAO);
        UserHandler userHandler = new UserHandler(userService);
        AdminService adminService = new AdminService(userDAO, authDAO, gameDAO);
        AdminHandler AdminHandler = new AdminHandler(adminService);

        // Register endpoints
        Spark.post("/session", userHandler::handleLogin);
        Spark.post("/user", userHandler::registerUser);
        Spark.delete("/db", AdminHandler::clearApplicationData);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
