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

public class ListMovieDaoJdbc implements ListMovie {

    @Override
    public void addMovieToList(ListBean list, MovieBean movie) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            CrudListMovie.addMovieToList(conn, list, movie);
        } catch (ExceptionDao e) {
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after addMovieToList
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
        } catch (ExceptionDao e) {
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after removeMovieFromList
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
        } catch (ExceptionDao e) {
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after getAllMoviesInList
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
            // Assuming you have a corresponding method in CrudListMovie to handle this
            CrudListMovie.removeAllMoviesFromList(conn, list);
        } catch (ExceptionDao e) {
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after removeAllMoviesFromList
                }
            }
        }
    }
}