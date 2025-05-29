package ispw.project.project_ispw.dao.queries;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudUser {

    private CrudUser(){
        //Empty Constructor
    }

    private static final String INSERT_USER_SQL = "INSERT INTO user (username, password) VALUES (?, ?)";
    private static final String UPDATE_USER_SQL = "UPDATE user SET password=? WHERE username = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM user WHERE username = ?";
    private static final String SELECT_ALL_USERS_SQL = "SELECT username, password FROM user";
    private static final String SELECT_USER_BY_USERNAME_SQL = "SELECT username, password FROM user WHERE username = ?";

    public static int addUser(Connection conn, UserBean user) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_USER_SQL)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to add user '" + user.getUsername() + "': " + e.getMessage(), e);
        }
    }

    public static int updateUser(Connection conn, UserBean user) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_USER_SQL)) {
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getUsername());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to update user '" + user.getUsername() + "': " + e.getMessage(), e);
        }
    }

    public static int deleteUser(Connection conn, String username) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_USER_SQL)) {
            ps.setString(1, username);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to delete user '" + username + "': " + e.getMessage(), e);
        }
    }

    public static void printAllUsers(Connection conn) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_USERS_SQL)){
             ps.executeQuery();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to print all users: " + e.getMessage(), e);
        }
    }

    public static List<UserBean> getAllUsers(Connection conn) throws ExceptionDao {
        List<UserBean> userList = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_USERS_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                userList.add(mapResultSetToUserBean(rs));
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve all users: " + e.getMessage(), e);
        }
        return userList;
    }

    public static UserBean getUserByUsername(Connection conn, String username) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_USER_BY_USERNAME_SQL)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserBean(rs);
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve user by username '" + username + "': " + e.getMessage(), e);
        }
        return null;
    }

    private static UserBean mapResultSetToUserBean(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");

        return new UserBean(username, password);
    }
}