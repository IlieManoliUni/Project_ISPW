package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.UserDao;
import ispw.project.project_ispw.dao.queries.CrudUser;
import ispw.project.project_ispw.exception.ExceptionDao; // Import your custom DAO exception

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDaoJdbc implements UserDao {

    private static final Logger LOGGER = Logger.getLogger(UserDaoJdbc.class.getName());

    @Override
    public UserBean retrieveByUsername(String username) throws ExceptionDao {
        Connection conn = null;
        UserBean user = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudUser.getUserByUsername now returns UserBean directly
            user = CrudUser.getUserByUsername(conn, username);

            if (user == null) {
                // If CrudUser returns null, it means no record was found.
                throw new ExceptionDao("No User Found matching username: " + username);
            }
        } catch (ExceptionDao e) { // Catch ExceptionDao directly
            LOGGER.log(Level.SEVERE, "Error retrieving user by username: " + username, e);
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after retrieveByUsername", e);
                }
            }
        }
        return user;
    }

    @Override
    public void saveUser(UserBean user) throws ExceptionDao {
        Connection conn = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // First, check if the user already exists using the new CrudUser.getUserByUsername
            UserBean existingUser = CrudUser.getUserByUsername(conn, user.getUsername());

            if (existingUser != null) {
                throw new ExceptionDao("User already exists with username: " + user.getUsername());
            }

            // If not, add the new user
            CrudUser.addUser(conn, user);
        } catch (ExceptionDao e) { // Catch ExceptionDao directly
            LOGGER.log(Level.SEVERE, "Error saving user '" + user.getUsername() + "': " + e.getMessage(), e);
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after saveUser", e);
                }
            }
        }
    }
}