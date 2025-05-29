package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.UserDao;
import ispw.project.project_ispw.dao.queries.CrudUser;
import ispw.project.project_ispw.exception.ExceptionDao;

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
            user = CrudUser.getUserByUsername(conn, username);

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after retrieveByUsername for user ''{0}'': {1}", new Object[]{username, e.getMessage()});
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
            UserBean existingUser = CrudUser.getUserByUsername(conn, user.getUsername());

            if (existingUser != null) {
                throw new ExceptionDao("User already exists with username: " + user.getUsername());
            }

            CrudUser.addUser(conn, user);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after saveUser for user ''{0}'': {1}", new Object[]{user.getUsername(), e.getMessage()});
                }
            }
        }
    }
}