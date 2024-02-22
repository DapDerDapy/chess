package dataAccess;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, String> authTokens = new HashMap<>();

    @Override
    public String createAuth(String username) {
        String authToken = UUID.randomUUID().toString(); // Generate a unique authToken
        authTokens.put(authToken, username); // Associate the authToken with the username
        return authToken; // Return the generated authToken
    }

    @Override
    public String getAuth(String authToken) {
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        authTokens.remove(authToken);
    }
}