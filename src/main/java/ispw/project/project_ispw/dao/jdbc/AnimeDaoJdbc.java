package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.dao.queries.CrudAnime;
import ispw.project.project_ispw.exception.ExceptionDao; // Import your custom DAO exception

import java.sql.Connection;
import java.sql.SQLException; // Still needed for conn.close() in finally block
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnimeDaoJdbc implements AnimeDao {

    private static final Logger LOGGER = Logger.getLogger(AnimeDaoJdbc.class.getName());

    @Override
    public AnimeBean retrieveById(int id) throws ExceptionDao {
        Connection conn = null;
        AnimeBean anime = null;

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudAnime.getAnimeById now returns AnimeBean directly
            anime = CrudAnime.getAnimeById(conn, id);

            if (anime == null) {
                // If CrudAnime returns null, it means no record was found.
                throw new ExceptionDao("No Anime Found with ID: " + id);
            }
        } catch (ExceptionDao e) { // Catch ExceptionDao directly
            LOGGER.log(Level.SEVERE, "Error retrieving anime by ID: " + id, e);
            throw e; // Re-throw the ExceptionDao caught from CrudAnime or SingletonDatabase
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { // SQLException can still happen during close()
                    LOGGER.log(Level.SEVERE, "Error closing connection after retrieveById", e);
                    // For close() errors, often just logging is sufficient as the main operation's success/failure is already determined.
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
            // CrudAnime.addAnime now takes Connection
            CrudAnime.addAnime(conn, anime);
        } catch (ExceptionDao e) { // Catch ExceptionDao directly
            LOGGER.log(Level.SEVERE, "Error saving anime: " + anime.getIdAnimeTmdb(), e);
            throw e; // Re-throw the ExceptionDao caught from CrudAnime or SingletonDatabase
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after saveAnime", e);
                }
            }
        }
    }

    @Override
    public List<AnimeBean> retrieveAllAnime() throws ExceptionDao {
        Connection conn = null;
        List<AnimeBean> animes = null; // Initialize to null or empty list

        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudAnime.getAllAnimes now returns List<AnimeBean> directly
            animes = CrudAnime.getAllAnimes(conn);
        } catch (ExceptionDao e) { // Catch ExceptionDao directly
            LOGGER.log(Level.SEVERE, "Error retrieving all animes", e);
            throw e; // Re-throw the ExceptionDao caught from CrudAnime or SingletonDatabase
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after retrieveAllAnime", e);
                }
            }
        }
        return animes;
    }
}