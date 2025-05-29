package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.dao.queries.CrudMovie;
import ispw.project.project_ispw.exception.ExceptionDao; // Import your custom DAO exception

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MovieDaoJdbc implements MovieDao {

    @Override
    public MovieBean retrieveById(int id) throws ExceptionDao {
        Connection conn = null;
        MovieBean movie = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudMovie.getMovieById now returns MovieBean directly
            movie = CrudMovie.getMovieById(conn, id);

            if (movie == null) {
                // If CrudMovie returns null, it means no record was found.
                throw new ExceptionDao("No Movie Found with ID: " + id);
            }
        } catch (ExceptionDao e) {
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after retrieveById
                }
            }
        }
        return movie;
    }

    @Override
    public void saveMovie(MovieBean movie) throws ExceptionDao {
        Connection conn = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudMovie.addMovie now takes Connection directly
            CrudMovie.addMovie(conn, movie);
        } catch (ExceptionDao e) {
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after saveMovie
                }
            }
        }
    }

    @Override
    public List<MovieBean> retrieveAllMovies() throws ExceptionDao {
        Connection conn = null;
        List<MovieBean> movies;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudMovie.getAllMovies now returns List<MovieBean> directly
            movies = CrudMovie.getAllMovies(conn);
        } catch (ExceptionDao e) {
            throw e; // Re-throw the ExceptionDao
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after retrieveAllMovies
                }
            }
        }
        return movies;
    }
}