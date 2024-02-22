package dataAccess;
import model.UserData;

public interface UserDAO {
    void clear();
    void addUser(UserData user);
    boolean isEmpty();
    UserData getUser(String username);
}
