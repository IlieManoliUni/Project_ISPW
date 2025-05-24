package ispw.project.project_ispw.dao.inMemory;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.dao.UserDao;
import ispw.project.project_ispw.exception.ExceptionDao; // Assuming you have this custom exception

import java.util.HashMap;
import java.util.Map;

public class UserDaoInMemory implements UserDao {
    private final Map<String, UserBean> users = new HashMap<>();

    @Override
    public UserBean retrieveByUsername(String username) throws ExceptionDao { // Changed to throw ExceptionDao
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        UserBean user = users.get(username);
        if (user == null) {
            // Throw ExceptionDao if the user is not found, consistent with other DAOs
            throw new ExceptionDao("No User Found with username: " + username);
        }
        return user;
    }

    @Override
    public void saveUser(UserBean user) throws ExceptionDao { // Changed to throw ExceptionDao
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        String username = user.getUsername();
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("User's username cannot be null or empty.");
        }

        if (users.containsKey(username)) {
            // Throw ExceptionDao if the username already exists
            throw new ExceptionDao("Username already exists: " + username);
        }
        users.put(username, user);
    }
}