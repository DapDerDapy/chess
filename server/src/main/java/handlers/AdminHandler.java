package handlers;

import com.google.gson.Gson;
import service.AdminService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class AdminHandler {
    private static AdminService adminService;
    private static Gson gson;

    public AdminHandler(AdminService adminService) {
        AdminHandler.adminService = adminService;
        gson = new Gson();
    }

    public Object clearApplicationData(Request req, Response res) {
        try {
            // Perform the action to clear application data
            adminService.clearApplicationData();

            // Set response status and return a success message
            res.status(200); // HTTP OK
            return gson.toJson(Map.of("message", "Application data cleared successfully"));
        } catch (Exception e) {
            // Handle any exceptions, set an appropriate HTTP status code, and return an error message
            res.status(500); // Internal Server Error
            return gson.toJson(Map.of("error", "An error occurred while clearing application data"));
        }
    }
}
