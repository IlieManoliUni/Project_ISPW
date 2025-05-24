package ispw.project.project_ispw.dao.queries;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionDao; // Using your custom exception

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudList {

    private static final String INSERT_LIST_SQL = "INSERT INTO list (idList, name, username) VALUES (?, ?, ?)";
    private static final String UPDATE_LIST_SQL = "UPDATE list SET name=?, username=? WHERE idList = ?";
    private static final String DELETE_LIST_SQL = "DELETE FROM list WHERE idList = ?";
    private static final String SELECT_ALL_LISTS_SQL = "SELECT idList, name, username FROM list";
    private static final String SELECT_LIST_BY_ID_SQL = "SELECT idList, name, username FROM list WHERE idList = ?";
    private static final String SELECT_LISTS_BY_USERNAME_SQL = "SELECT idList, name, username FROM list WHERE username = ?";


    public static int addList(Connection conn, ListBean list, UserBean user) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_LIST_SQL)) {
            ps.setInt(1, list.getId());
            ps.setString(2, list.getName());
            ps.setString(3, user.getUsername());
            System.out.println("Executing INSERT: " + ps.toString()); // For debugging, remove in production
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to add list: " + e.getMessage(), e);
        }
    }

    public static int updateList(Connection conn, ListBean list, UserBean user) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_LIST_SQL)) {
            ps.setString(1, list.getName());
            ps.setString(2, user.getUsername());
            ps.setInt(3, list.getId());
            System.out.println("Executing UPDATE: " + ps.toString()); // For debugging, remove in production
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to update list with ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }

    public static int deleteList(Connection conn, int listId) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_LIST_SQL)) {
            ps.setInt(1, listId);
            System.out.println("Executing DELETE: " + ps.toString()); // For debugging, remove in production
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to delete list with ID " + listId + ": " + e.getMessage(), e);
        }
    }

    // This method is primarily for debugging/logging, often not used in production logic directly
    public static void printAllLists(Connection conn) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_LISTS_SQL);
             ResultSet res = ps.executeQuery()) {
            while (res.next()) {
                System.out.printf("List ID: %d, Name: %s, Username: %s\n",
                        res.getInt("idList"), res.getString("name"), res.getString("username"));
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to print all lists: " + e.getMessage(), e);
        }
    }

    public static List<ListBean> getAllLists(Connection conn) throws ExceptionDao {
        List<ListBean> lists = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_LISTS_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lists.add(mapResultSetToListBean(rs));
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve all lists: " + e.getMessage(), e);
        }
        return lists;
    }

    public static ListBean getListById(Connection conn, int id) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_LIST_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToListBean(rs);
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve list by ID " + id + ": " + e.getMessage(), e);
        }
        return null; // Return null if no list is found
    }

    public static List<ListBean> getListsByUsername(Connection conn, String username) throws ExceptionDao {
        List<ListBean> userLists = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_LISTS_BY_USERNAME_SQL)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userLists.add(mapResultSetToListBean(rs));
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve lists for username " + username + ": " + e.getMessage(), e);
        }
        return userLists;
    }

    private static ListBean mapResultSetToListBean(ResultSet rs) throws SQLException {
        int idList = rs.getInt("idList");
        String name = rs.getString("name");
        String username = rs.getString("username"); // Assuming ListBean has a way to store the associated username

        // IMPORTANT: Ensure your ListBean has a constructor or setters that match these fields.
        // For example: return new ListBean(idList, name, username);
        // If ListBean doesn't inherently store username, you might need to adjust or create a specific DTO.
        // For simplicity, assuming a constructor like ListBean(int id, String name, String username)
        return new ListBean(idList, name, username);
    }
}