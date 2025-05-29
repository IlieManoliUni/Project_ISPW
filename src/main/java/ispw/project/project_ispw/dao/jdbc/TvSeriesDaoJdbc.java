package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.dao.queries.CrudTvSeries;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TvSeriesDaoJdbc implements TvSeriesDao {

    private static final Logger LOGGER = Logger.getLogger(TvSeriesDaoJdbc.class.getName());

    @Override
    public TvSeriesBean retrieveById(int id) throws ExceptionDao {
        Connection conn = null;
        TvSeriesBean tvSeries = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            tvSeries = CrudTvSeries.getTvSeriesById(conn, id);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after retrieveById for TV Series ID {0}: {1}", new Object[]{id, e.getMessage()});
                }
            }
        }
        return tvSeries;
    }

    @Override
    public boolean saveTvSeries(TvSeriesBean tvSeries) throws ExceptionDao {
        Connection conn = null;
        boolean success = false;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            int rowsAffected = CrudTvSeries.addTvSeries(conn, tvSeries);
            success = rowsAffected > 0;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after saveTvSeries for TV Series ''{0}'': {1}", new Object[]{tvSeries.getName(), e.getMessage()});
                }
            }
        }
        return success;
    }

    @Override
    public List<TvSeriesBean> retrieveAllTvSeries() throws ExceptionDao {
        Connection conn = null;
        List<TvSeriesBean> tvSeriesList = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            tvSeriesList = CrudTvSeries.getAllTvSeries(conn);
            if (tvSeriesList == null || tvSeriesList.isEmpty()) {
                return new ArrayList<>();
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after retrieveAllTvSeries: {0}", e.getMessage());
                }
            }
        }
        return tvSeriesList;
    }
}