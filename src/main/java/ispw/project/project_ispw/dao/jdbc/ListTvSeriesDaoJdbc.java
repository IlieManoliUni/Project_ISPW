package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.ListTvSeries;
import ispw.project.project_ispw.dao.queries.CrudTvSeries; // Import CrudTvSeries if needed for retrieveTvSeriesById logic
import ispw.project.project_ispw.dao.queries.CrudListTvSeries;
import ispw.project.project_ispw.exception.ExceptionDao; // Import your custom DAO exception

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ListTvSeriesDaoJdbc implements ListTvSeries {

    @Override
    public void addTvSeriesToList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudListTvSeries.addTvSeriesToList now takes Connection directly
            CrudListTvSeries.addTvSeriesToList(conn, list, tvSeries);
        } catch (ExceptionDao e) {
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after addTvSeriesToList
                }
            }
        }
    }

    @Override
    public void removeTvSeriesFromList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudListTvSeries.removeTvSeriesFromList now takes Connection directly
            CrudListTvSeries.removeTvSeriesFromList(conn, list, tvSeries);
        } catch (ExceptionDao e) {
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after removeTvSeriesFromList
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
            // CrudListTvSeries.getTvSeriesFullDetailsByList now returns List<TvSeriesBean> directly
            tvSeriesList = CrudListTvSeries.getTvSeriesFullDetailsByList(conn, list);
        } catch (ExceptionDao e) {
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after getAllTvSeriesInList
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
            // You'll need to implement this method in your CrudListTvSeries class as well.
            // It should delete all entries in the 'list_tvseries' table for the given list ID.
            CrudListTvSeries.removeAllTvSeriesFromList(conn, list);
        } catch (ExceptionDao e) {
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after removeAllTvSeriesFromList
                }
            }
        }
    }
}