package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.ListTvSeries;
import ispw.project.project_ispw.dao.queries.CrudListTvSeries;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList; // Added import for ArrayList
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListTvSeriesDaoJdbc implements ListTvSeries {

    private static final Logger LOGGER = Logger.getLogger(ListTvSeriesDaoJdbc.class.getName());

    @Override
    public void addTvSeriesToList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            CrudListTvSeries.addTvSeriesToList(conn, list, tvSeries);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after addTvSeriesToList: {0}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void removeTvSeriesFromList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            CrudListTvSeries.removeTvSeriesFromList(conn, list, tvSeries);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after removeTvSeriesFromList: {0}", e.getMessage());
                }
            }
        }
    }

    @Override
    public List<TvSeriesBean> getAllTvSeriesInList(ListBean list) throws ExceptionDao {
        Connection conn = null;
        List<TvSeriesBean> tvSeriesList;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            tvSeriesList = CrudListTvSeries.getTvSeriesFullDetailsByList(conn, list);

            if (tvSeriesList == null) {
                return new ArrayList<>();
            }

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after getAllTvSeriesInList: {0}", e.getMessage());
                }
            }
        }
        return tvSeriesList;
    }

    @Override
    public void removeAllTvSeriesFromList(ListBean list) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            CrudListTvSeries.removeAllTvSeriesFromList(conn, list);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after removeAllTvSeriesFromList: {0}", e.getMessage());
                }
            }
        }
    }
}