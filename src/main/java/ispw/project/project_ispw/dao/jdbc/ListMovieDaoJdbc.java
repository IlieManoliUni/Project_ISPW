package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.ListMovie;
import ispw.project.project_ispw.dao.queries.CrudMovie; // Import CrudMovie if needed for retrieveMovieById logic
import ispw.project.project_ispw.dao.queries.CrudListMovie;
import ispw.project.project_ispw.exception.ExceptionDao; // Import your custom DAO exception

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListMovieDaoJdbc implements ListMovie {

    private static final Logger LOGGER = Logger.getLogger(ListMovieDaoJdbc.class.getName());

    @Override
    public void addMovieToList(ListBean list, MovieBean movie) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudListMovie.addMovieToList now takes Connection directly
            CrudListMovie.addMovieToList(conn, list, movie);
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "Error adding movie ID " + movie.getIdMovieTmdb() + " to list ID " + list.getId(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after addMovieToList", e);
                }
            }
        }
    }

    @Override
    public void removeMovieFromList(ListBean list, MovieBean movie) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudListMovie.removeMovieFromList now takes Connection directly
            CrudListMovie.removeMovieFromList(conn, list, movie);
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "Error removing movie ID " + movie.getIdMovieTmdb() + " from list ID " + list.getId(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after removeMovieFromList", e);
                }
            }
        }
    }

    @Override
    public List<MovieBean> getAllMoviesInList(ListBean list) throws ExceptionDao {
        Connection conn = null;
        List<MovieBean> movies;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudListMovie.getMoviesFullDetailsByList now returns List<MovieBean> directly
            movies = CrudListMovie.getMoviesFullDetailsByList(conn, list);
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all movies for list ID " + list.getId(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after getAllMoviesInList", e);
                }
            }
        }
        return movies;
    }
}