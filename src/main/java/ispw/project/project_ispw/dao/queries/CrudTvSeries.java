package ispw.project.project_ispw.dao.queries;

import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudTvSeries {

    private CrudTvSeries(){
        //Empty Constructor
    }

    private static final String INSERT_TVSERIES_SQL = "INSERT INTO tvseries (idTvSeriesTmdb, numberOfEpisodes, episodeRuntime, name) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_TVSERIES_SQL = "UPDATE tvseries SET name=?, episodeRuntime=?, numberOfEpisodes=? WHERE idTvSeriesTmdb = ?";
    private static final String DELETE_TVSERIES_SQL = "DELETE FROM tvseries WHERE idTvSeriesTmdb = ?";
    private static final String SELECT_ALL_TVSERIES_SQL = "SELECT idTvSeriesTmdb, numberOfEpisodes, episodeRuntime, name FROM tvseries";
    private static final String SELECT_TVSERIES_BY_ID_SQL = "SELECT idTvSeriesTmdb, numberOfEpisodes, episodeRuntime, name FROM tvseries WHERE idTvSeriesTmdb = ?";

    public static int addTvSeries(Connection conn, TvSeriesBean tvSeries) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_TVSERIES_SQL)) {
            ps.setInt(1, tvSeries.getIdTvSeriesTmdb());
            ps.setInt(2, tvSeries.getNumberOfEpisodes());
            ps.setInt(3, tvSeries.getEpisodeRuntime());
            ps.setString(4, tvSeries.getName());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to add TV Series: " + e.getMessage(), e);
        }
    }

    public static int updateTvSeries(Connection conn, TvSeriesBean tvSeries) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_TVSERIES_SQL)) {
            ps.setString(1, tvSeries.getName());
            ps.setInt(2, tvSeries.getEpisodeRuntime());
            ps.setInt(3, tvSeries.getNumberOfEpisodes());
            ps.setInt(4, tvSeries.getIdTvSeriesTmdb());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to update TV Series with ID " + tvSeries.getIdTvSeriesTmdb() + ": " + e.getMessage(), e);
        }
    }

    public static int deleteTvSeries(Connection conn, int tvSeriesId) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_TVSERIES_SQL)) {
            ps.setInt(1, tvSeriesId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to delete TV Series with ID " + tvSeriesId + ": " + e.getMessage(), e);
        }
    }

    public static void printAllTvSeries(Connection conn) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_TVSERIES_SQL)){
             ps.executeQuery();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to print all TV Series: " + e.getMessage(), e);
        }
    }

    public static List<TvSeriesBean> getAllTvSeries(Connection conn) throws ExceptionDao {
        List<TvSeriesBean> tvSeriesList = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_TVSERIES_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tvSeriesList.add(mapResultSetToTvSeriesBean(rs));
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve all TV Series: " + e.getMessage(), e);
        }
        return tvSeriesList;
    }

    public static TvSeriesBean getTvSeriesById(Connection conn, int id) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_TVSERIES_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTvSeriesBean(rs);
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve TV Series by ID " + id + ": " + e.getMessage(), e);
        }
        return null;
    }

    private static TvSeriesBean mapResultSetToTvSeriesBean(ResultSet rs) throws SQLException {
        int idTvSeriesTmdb = rs.getInt("idTvSeriesTmdb");
        int numberOfEpisodes = rs.getInt("numberOfEpisodes");
        int episodeRuntime = rs.getInt("episodeRuntime");
        String name = rs.getString("name");

        return new TvSeriesBean(episodeRuntime, idTvSeriesTmdb, numberOfEpisodes, name);
    }
}