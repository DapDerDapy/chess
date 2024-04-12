package server;

import dataAccess.*;
import handlers.AdminHandler;
import handlers.GameHandler;
import handlers.UserHandler;
import server.websocket.WSHandler;
import server.websocket.WSServer;
import service.AdminService;
import service.GameService;
import service.UserService;
import spark.Spark;

public class Server {

    public WSHandler wsHandler;

    public Server() {
        this.wsHandler = new WSHandler();
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/connect", WSHandler.class);

        AuthDAO authDAO = null;
        GameDAO gameDAO = null;
        UserDAO userDAO = null;

        try {
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Register your endpoints and handle exceptions here.
        UserService userService = new UserService(userDAO, authDAO);
        UserHandler userHandler = new UserHandler(userService);

        AdminService adminService = new AdminService(userDAO, authDAO, gameDAO);
        AdminHandler AdminHandler = new AdminHandler(adminService);

        GameService gameService = new GameService(gameDAO, authDAO, userDAO);
        GameHandler gameHandler = new GameHandler(gameService, userService, adminService);

        // Register endpoints
        Spark.post("/session", userHandler::handleLogin);
        Spark.post("/user", userHandler::registerUser);
        Spark.delete("/db", AdminHandler::clearApplicationData);
        Spark.delete("/session", userHandler::logoutUser);
        Spark.post("/game", gameHandler::handleGameCreation);
        Spark.get("/game", gameHandler::listGames);
        Spark.put("/game", gameHandler::joinGameHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
