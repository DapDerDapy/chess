package dataAccess;

public interface AuthDAO {
    String createAuth(String username);
    String getAuth(String authToken);
    boolean deleteAuth(String authToken);
    boolean isValidToken(String authToken);

    String getUsernameFromToken(String authToken);
    void clearAll();
}