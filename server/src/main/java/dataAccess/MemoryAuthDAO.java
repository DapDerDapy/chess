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
    public boolean deleteAuth(String authToken) {
        if (authTokens.containsKey(authToken)){
            authTokens.remove(authToken);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isValidToken(String authToken) {
        return authTokens.containsKey(authToken);
    }

    public String getUsernameFromToken(String authToken){
        // Retrieve and return the username associated with the authToken
        // Returns null if the authToken does not exist in the map
        return authTokens.get(authToken);
    }

    public void clearAll(){
        authTokens.clear();
    }
}