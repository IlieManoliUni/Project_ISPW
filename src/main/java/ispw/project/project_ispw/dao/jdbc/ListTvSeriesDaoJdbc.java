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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListTvSeriesDaoJdbc implements ListTvSeries {

    private static final Logger LOGGER = Logger.getLogger(ListTvSeriesDaoJdbc.class.getName());

    @Override
    public void addTvSeriesToList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudListTvSeries.addTvSeriesToList now takes Connection directly
            CrudListTvSeries.addTvSeriesToList(conn, list, tvSeries);
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "Error adding TV Series ID " + tvSeries.getIdTvSeriesTmdb() + " to list ID " + list.getId(), e);
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after addTvSeriesToList", e);
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
            LOGGER.log(Level.SEVERE, "Error removing TV Series ID " + tvSeries.getIdTvSeriesTmdb() + " from list ID " + list.getId(), e);
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after removeTvSeriesFromList", e);
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
            LOGGER.log(Level.SEVERE, "Error retrieving all TV Series for list ID " + list.getId(), e);
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after getAllTvSeriesInList", e);
                }
            }
        }
        return tvSeriesList;
    }
}