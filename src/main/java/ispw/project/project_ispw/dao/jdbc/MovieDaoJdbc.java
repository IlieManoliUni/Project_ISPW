package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.dao.queries.CrudMovie;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList; // Import ArrayList for an empty list
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovieDaoJdbc implements MovieDao {

    private static final Logger LOGGER = Logger.getLogger(MovieDaoJdbc.class.getName());

    @Override
    public MovieBean retrieveById(int id) throws ExceptionDao {
        Connection conn = null;
        MovieBean movie = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            movie = CrudMovie.getMovieById(conn, id);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after retrieveById for movie ID {0}: {1}", new Object[]{id, e.getMessage()});
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
            CrudMovie.addMovie(conn, movie);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after saveMovie for movie ''{0}'': {1}", new Object[]{movie.getTitle(), e.getMessage()});
                }
            }
        }
    }

    @Override
    public List<MovieBean> retrieveAllMovies() throws ExceptionDao {
        Connection conn = null;
        List<MovieBean> movies = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            movies = CrudMovie.getAllMovies(conn);

            if (movies == null || movies.isEmpty()) {
                return new ArrayList<>();
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after retrieveAllMovies: {0}", e.getMessage());
                }
            }
        }
        return movies;
    }
}