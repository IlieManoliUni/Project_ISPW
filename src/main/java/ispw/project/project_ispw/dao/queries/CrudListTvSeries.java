package ispw.project.project_ispw.dao.queries;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudListTvSeries {

    private CrudListTvSeries(){
        //Empty Constructor
    }

    private static final String INSERT_LIST_TVSERIES_SQL = "INSERT INTO list_tvseries (idList, idTvSeriesTmdb) VALUES (?, ?)";
    private static final String DELETE_LIST_TVSERIES_SQL = "DELETE FROM list_tvseries WHERE idList = ? AND idTvSeriesTmdb = ?";
    private static final String SELECT_TVSERIES_IDS_IN_LIST_SQL = "SELECT idTvSeriesTmdb FROM list_tvseries WHERE idList = ?";
    private static final String SELECT_FULL_DETAILS_TVSERIES_IN_LIST_SQL =
            "SELECT ts.idTvSeriesTmdb, ts.name, ts.episodeRuntime, ts.numberOfEpisodes " +
                    "FROM list_tvseries lts " +
                    "JOIN tvseries ts ON lts.idTvSeriesTmdb = ts.idTvSeriesTmdb " +
                    "WHERE lts.idList = ?";
    private static final String DELETE_ALL_TVSERIES_FROM_LIST_SQL = "DELETE FROM list_tvseries WHERE idList = ?";


    public static int addTvSeriesToList(Connection conn, ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_LIST_TVSERIES_SQL)) {
            ps.setInt(1, list.getId());
            ps.setInt(2, tvSeries.getIdTvSeriesTmdb());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to add TV Series ID " + tvSeries.getIdTvSeriesTmdb() + " to list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }

    public static int removeTvSeriesFromList(Connection conn, ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_LIST_TVSERIES_SQL)) {
            ps.setInt(1, list.getId());
            ps.setInt(2, tvSeries.getIdTvSeriesTmdb());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to remove TV Series ID " + tvSeries.getIdTvSeriesTmdb() + " from list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }

    public static void printAllTvSeriesInList(Connection conn, ListBean list) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_TVSERIES_IDS_IN_LIST_SQL)) {
            ps.setInt(1, list.getId());
            ps.executeQuery();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to print all TV Series in list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }

    public static List<Integer> getTvSeriesIdsByList(Connection conn, ListBean list) throws ExceptionDao {
        List<Integer> tvSeriesIds = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_TVSERIES_IDS_IN_LIST_SQL)) {
            ps.setInt(1, list.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tvSeriesIds.add(rs.getInt("idTvSeriesTmdb"));
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve TV Series IDs for list ID " + list.getId() + ": " + e.getMessage(), e);
        }
        return tvSeriesIds;
    }

    public static List<TvSeriesBean> getTvSeriesFullDetailsByList(Connection conn, ListBean list) throws ExceptionDao {
        List<TvSeriesBean> tvSeriesDetails = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_FULL_DETAILS_TVSERIES_IN_LIST_SQL)) {
            ps.setInt(1, list.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tvSeriesDetails.add(new TvSeriesBean(
                            rs.getInt("episodeRuntime"),
                            rs.getInt("idTvSeriesTmdb"),
                            rs.getInt("numberOfEpisodes"),
                            rs.getString("name")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve full TV Series details for list ID " + list.getId() + ": " + e.getMessage(), e);
        }
        return tvSeriesDetails;
    }

    public static int removeAllTvSeriesFromList(Connection conn, ListBean list) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_ALL_TVSERIES_FROM_LIST_SQL)) {
            ps.setInt(1, list.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to remove all TV series from list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }
}