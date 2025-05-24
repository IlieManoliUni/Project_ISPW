package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.dao.queries.CrudTvSeries;
import ispw.project.project_ispw.exception.ExceptionDao; // Import your custom DAO exception

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TvSeriesDaoJdbc implements TvSeriesDao {

    private static final Logger LOGGER = Logger.getLogger(TvSeriesDaoJdbc.class.getName());

    @Override
    public TvSeriesBean retrieveById(int id) throws ExceptionDao { // Changed return type to throw ExceptionDao
        Connection conn = null;
        TvSeriesBean tvSeries = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudTvSeries.getTvSeriesById now returns TvSeriesBean directly
            tvSeries = CrudTvSeries.getTvSeriesById(conn, id);

            if (tvSeries == null) {
                // If CrudTvSeries returns null, it means no record was found.
                throw new ExceptionDao("No TV Series Found with ID: " + id);
            }
        } catch (ExceptionDao e) { // Catch ExceptionDao directly
            LOGGER.log(Level.SEVERE, "Error retrieving TV Series by ID: " + id, e);
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after retrieveById", e);
                }
            }
        }
        return tvSeries;
    }

    @Override
    public boolean saveTvSeries(TvSeriesBean tvSeries) throws ExceptionDao { // Changed return type to throw ExceptionDao
        Connection conn = null;
        boolean success = false;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudTvSeries.addTvSeries now takes Connection directly
            int rowsAffected = CrudTvSeries.addTvSeries(conn, tvSeries);

            // If at least one row was affected, the insert was successful
            success = rowsAffected > 0;
        } catch (ExceptionDao e) { // Catch ExceptionDao directly
            LOGGER.log(Level.SEVERE, "Error saving TV Series: " + tvSeries.getName(), e);
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after saveTvSeries", e);
                }
            }
        }
        return success;
    }

    @Override
    public List<TvSeriesBean> retrieveAllTvSeries() throws ExceptionDao { // Changed return type to throw ExceptionDao
        Connection conn = null;
        List<TvSeriesBean> tvSeriesList;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudTvSeries.getAllTvSeries now returns List<TvSeriesBean> directly
            tvSeriesList = CrudTvSeries.getAllTvSeries(conn);
        } catch (ExceptionDao e) { // Catch ExceptionDao directly
            LOGGER.log(Level.SEVERE, "Error retrieving all TV Series", e);
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after retrieveAllTvSeries", e);
                }
            }
        }
        return tvSeriesList;
    }
}