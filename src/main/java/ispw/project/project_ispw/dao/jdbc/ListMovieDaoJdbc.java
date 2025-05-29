package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.ListMovie;
import ispw.project.project_ispw.dao.queries.CrudListMovie;
import ispw.project.project_ispw.exception.ExceptionDao;

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
            CrudListMovie.addMovieToList(conn, list, movie);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after addMovieToList: {0}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void removeMovieFromList(ListBean list, MovieBean movie) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            CrudListMovie.removeMovieFromList(conn, list, movie);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after removeMovieFromList: {0}", e.getMessage());
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
            movies = CrudListMovie.getMoviesFullDetailsByList(conn, list);

            if (movies == null || movies.isEmpty()) {
                throw new ExceptionDao("No Movies Found in list with ID: " + list.getId());
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after getAllMoviesInList: {0}", e.getMessage());
                }
            }
        }
        return movies;
    }

    @Override
    public void removeAllMoviesFromList(ListBean list) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            CrudListMovie.removeAllMoviesFromList(conn, list);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after removeAllMoviesFromList: {0}", e.getMessage());
                }
            }
        }
    }
}