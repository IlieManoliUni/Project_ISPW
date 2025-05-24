package ispw.project.project_ispw.dao.queries;

import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.exception.ExceptionDao; // Using your custom exception

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudMovie {

    private static final String INSERT_MOVIE_SQL = "INSERT INTO movie (idMovieTmdb, runtime, name) VALUES (?, ?, ?)";
    private static final String UPDATE_MOVIE_SQL = "UPDATE movie SET name=?, runtime=? WHERE idMovieTmdb = ?";
    private static final String DELETE_MOVIE_SQL = "DELETE FROM movie WHERE idMovieTmdb = ?";
    private static final String SELECT_ALL_MOVIES_SQL = "SELECT idMovieTmdb, runtime, name FROM movie";
    private static final String SELECT_MOVIE_BY_ID_SQL = "SELECT idMovieTmdb, runtime, name FROM movie WHERE idMovieTmdb = ?";

    public static int addMovie(Connection conn, MovieBean movie) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_MOVIE_SQL)) {
            ps.setInt(1, movie.getIdMovieTmdb());
            ps.setInt(2, movie.getRuntime());
            ps.setString(3, movie.getTitle());
            System.out.println("Executing INSERT: " + ps.toString()); // For debugging, remove in production
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to add movie: " + e.getMessage(), e);
        }
    }

    public static int updateMovie(Connection conn, MovieBean movie) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_MOVIE_SQL)) {
            ps.setString(1, movie.getTitle());
            ps.setInt(2, movie.getRuntime());
            ps.setInt(3, movie.getIdMovieTmdb());
            System.out.println("Executing UPDATE: " + ps.toString()); // For debugging, remove in production
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to update movie with ID " + movie.getIdMovieTmdb() + ": " + e.getMessage(), e);
        }
    }

    public static int deleteMovie(Connection conn, int movieId) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_MOVIE_SQL)) {
            ps.setInt(1, movieId);
            System.out.println("Executing DELETE: " + ps.toString()); // For debugging, remove in production
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to delete movie with ID " + movieId + ": " + e.getMessage(), e);
        }
    }

    public static void printAllMovies(Connection conn) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_MOVIES_SQL);
             ResultSet res = ps.executeQuery()) {
            while (res.next()) {
                System.out.printf("Movie ID: %d, Name: %s, Runtime: %d minutes\n",
                        res.getInt("idMovieTmdb"), res.getString("name"), res.getInt("runtime"));
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to print all movies: " + e.getMessage(), e);
        }
    }

    public static List<MovieBean> getAllMovies(Connection conn) throws ExceptionDao {
        List<MovieBean> movieList = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_MOVIES_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                movieList.add(mapResultSetToMovieBean(rs));
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve all movies: " + e.getMessage(), e);
        }
        return movieList;
    }

    public static MovieBean getMovieById(Connection conn, int id) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_MOVIE_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMovieBean(rs);
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve movie by ID " + id + ": " + e.getMessage(), e);
        }
        return null; // Return null if no movie is found
    }

    private static MovieBean mapResultSetToMovieBean(ResultSet rs) throws SQLException {
        int idMovieTmdb = rs.getInt("idMovieTmdb");
        int runtime = rs.getInt("runtime");
        String name = rs.getString("name");

        // Using your MovieBean constructor: public MovieBean(int id, int runtime, String title)
        return new MovieBean(idMovieTmdb, runtime, name);
    }
}