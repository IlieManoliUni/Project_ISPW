package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.dao.queries.CrudAnime;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnimeDaoJdbc implements AnimeDao {

    private static final Logger LOGGER = Logger.getLogger(AnimeDaoJdbc.class.getName()); // Declare the logger

    @Override
    public AnimeBean retrieveById(int id) throws ExceptionDao {
        Connection conn = null;
        AnimeBean anime = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            anime = CrudAnime.getAnimeById(conn, id);

            if (anime == null) {
                throw new ExceptionDao("No Anime Found with ID: " + id);
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after retrieveById: {0}", e.getMessage());
                }
            }
        }
        return anime;
    }

    @Override
    public void saveAnime(AnimeBean anime) throws ExceptionDao {
        Connection conn = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            CrudAnime.addAnime(conn, anime);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after saveAnime: {0}", e.getMessage());
                }
            }
        }
    }

    @Override
    public List<AnimeBean> retrieveAllAnime() throws ExceptionDao {
        Connection conn = null;
        List<AnimeBean> animes = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            animes = CrudAnime.getAllAnimes(conn);

            if (animes == null || animes.isEmpty()) {
                throw new ExceptionDao("No Anime Found in the database.");
            }

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after retrieveAllAnime: {0}", e.getMessage());
                }
            }
        }
        return animes;
    }
}