package ispw.project.project_ispw.dao.queries;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudListMovie {

    private CrudListMovie(){
        //Empty Constructor
    }
    private static final String IDMOVIETMDB = "idMovieTmdb";

    private static final String INSERT_LIST_MOVIE_SQL = "INSERT INTO list_movie (idList, idMovieTmdb) VALUES (?, ?)";
    private static final String DELETE_LIST_MOVIE_SQL = "DELETE FROM list_movie WHERE idList = ? AND idMovieTmdb = ?";
    private static final String SELECT_MOVIE_IDS_IN_LIST_SQL = "SELECT idMovieTmdb FROM list_movie WHERE idList = ?";
    private static final String SELECT_FULL_DETAILS_MOVIES_IN_LIST_SQL =
            "SELECT m.idMovieTmdb, m.runtime, m.name " +
                    "FROM list_movie lm " +
                    "JOIN movie m ON lm.idMovieTmdb = m.idMovieTmdb " +
                    "WHERE lm.idList = ?";
    private static final String DELETE_ALL_MOVIES_FROM_LIST_SQL = "DELETE FROM list_movie WHERE idList = ?";


    public static int addMovieToList(Connection conn, ListBean list, MovieBean movie) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_LIST_MOVIE_SQL)) {
            ps.setInt(1, list.getId());
            ps.setInt(2, movie.getIdMovieTmdb());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to add movie ID " + movie.getIdMovieTmdb() + " to list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }

    public static int removeMovieFromList(Connection conn, ListBean list, MovieBean movie) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_LIST_MOVIE_SQL)) {
            ps.setInt(1, list.getId());
            ps.setInt(2, movie.getIdMovieTmdb());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to remove movie ID " + movie.getIdMovieTmdb() + " from list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }

    public static void printAllMoviesInList(Connection conn, ListBean list) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_MOVIE_IDS_IN_LIST_SQL)) {
            ps.setInt(1, list.getId());
            ps.executeQuery();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to print all movies in list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }

    public static List<Integer> getMovieIdsByList(Connection conn, ListBean list) throws ExceptionDao {
        List<Integer> movieIds = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_MOVIE_IDS_IN_LIST_SQL)) {
            ps.setInt(1, list.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    movieIds.add(rs.getInt(IDMOVIETMDB));
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve movie IDs for list ID " + list.getId() + ": " + e.getMessage(), e);
        }
        return movieIds;
    }

    public static List<MovieBean> getMoviesFullDetailsByList(Connection conn, ListBean list) throws ExceptionDao {
        List<MovieBean> movieDetails = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_FULL_DETAILS_MOVIES_IN_LIST_SQL)) {
            ps.setInt(1, list.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    movieDetails.add(new MovieBean(
                            rs.getInt(IDMOVIETMDB),
                            rs.getInt("runtime"),
                            rs.getString("name") // Corrected to "name"
                    ));
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve full movie details for list ID " + list.getId() + ": " + e.getMessage(), e);
        }
        return movieDetails;
    }

    public static int removeAllMoviesFromList(Connection conn, ListBean list) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_ALL_MOVIES_FROM_LIST_SQL)) {
            ps.setInt(1, list.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to remove all movies from list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }
}