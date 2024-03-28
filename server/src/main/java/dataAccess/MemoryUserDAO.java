package dataAccess;

import model.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class MemoryUserDAO implements UserDAO {

    private final List<UserData> userInfo = new ArrayList<>();
    @Override
    public void clear(){
        //clear memory!
        // it should clear the username, password, and email from the model package
        userInfo.clear();
    }

    // Method to add a UserData record to the list
    public void addUser(UserData user) {
        userInfo.add(user);
    }

    public UserData getUser(String username){
        // Stream through the users list, filter by username, and find the first match
        Optional<UserData> match = userInfo.stream()
                .filter(user -> user.username().equals(username))
                .findFirst();
        // Return the match or null if not found
        return match.orElse(null);
    }

    @Override
    public boolean isEmpty(){
        return userInfo.isEmpty();
    }



}
