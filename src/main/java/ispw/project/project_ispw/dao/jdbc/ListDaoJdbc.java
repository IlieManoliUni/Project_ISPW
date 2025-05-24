package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.dao.queries.CrudList;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListDaoJdbc implements ListDao {

    private static final Logger LOGGER = Logger.getLogger(ListDaoJdbc.class.getName());

    @Override
    public ListBean retrieveById(int id) throws ExceptionDao {
        Connection conn = null;
        ListBean list = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudList.getListById now returns ListBean directly
            list = CrudList.getListById(conn, id);

            if (list == null) {
                // If CrudList returns null, it means no record was found.
                throw new ExceptionDao("No List Found with ID: " + id);
            }
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "Error retrieving list by ID: " + id, e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after retrieveById", e);
                }
            }
        }
        return list;
    }

    @Override
    public void saveList(ListBean list, UserBean user) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudList.addList now takes Connection
            CrudList.addList(conn, list, user);
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "Error saving list: " + list.getName(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after saveList", e);
                }
            }
        }
    }

    @Override
    public void deleteList(ListBean list) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudList.deleteList now takes Connection and list ID directly
            CrudList.deleteList(conn, list.getId());
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "Error deleting list ID " + list.getId(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after deleteList", e);
                }
            }
        }
    }

    @Override
    public List<ListBean> retrieveAllListsOfUsername(String username) throws ExceptionDao {
        Connection conn = null;
        List<ListBean> lists;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudList.getListsByUsername now returns List<ListBean> directly
            lists = CrudList.getListsByUsername(conn, username);
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all lists for username: " + username, e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after retrieveAllListsOfUsername", e);
                }
            }
        }
        return lists;
    }
}