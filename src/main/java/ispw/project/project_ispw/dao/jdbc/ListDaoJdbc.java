package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.dao.queries.CrudList;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
            list = CrudList.getListById(conn, id);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after retrieveById for list ID {0}: {1}", new Object[]{id, e.getMessage()});
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
            CrudList.addList(conn, list, user);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after saveList for list ''{0}'': {1}", new Object[]{list.getName(), e.getMessage()});
                }
            }
        }
    }

    @Override
    public void deleteList(ListBean list) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            CrudList.deleteList(conn, list.getId());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after deleteList for list ID {0}: {1}", new Object[]{list.getId(), e.getMessage()});
                }
            }
        }
    }

    @Override
    public List<ListBean> retrieveAllListsOfUsername(String username) throws ExceptionDao {
        Connection conn = null;
        List<ListBean> lists = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            lists = CrudList.getListsByUsername(conn, username);

            if (lists == null || lists.isEmpty()) {
                return new ArrayList<>();
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after retrieveAllListsOfUsername for user ''{0}'': {1}", new Object[]{username, e.getMessage()});
                }
            }
        }
        return lists;
    }
}