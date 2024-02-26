package dataAccess;

public interface AuthDAO {
    String createAuth(String username);
    String getAuth(String authToken);
    void deleteAuth(String authToken);
    boolean isValidToken(String authToken);

    void clearAll();
}