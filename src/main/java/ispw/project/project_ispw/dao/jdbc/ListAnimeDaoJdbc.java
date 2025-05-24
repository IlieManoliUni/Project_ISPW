package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.ListAnime;
import ispw.project.project_ispw.dao.queries.CrudAnime; // Import CrudAnime
import ispw.project.project_ispw.dao.queries.CrudListAnime;
import ispw.project.project_ispw.exception.ExceptionDao; // Import your custom DAO exception

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListAnimeDaoJdbc implements ListAnime {

    private static final Logger LOGGER = Logger.getLogger(ListAnimeDaoJdbc.class.getName());

    @Override
    public void addAnimeToList(ListBean list, AnimeBean anime) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudListAnime.addAnimeToList now takes Connection directly
            CrudListAnime.addAnimeToList(conn, list, anime);
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "Error adding anime ID " + anime.getIdAnimeTmdb() + " to list ID " + list.getId(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after addAnimeToList", e);
                }
            }
        }
    }

    @Override
    public void removeAnimeFromList(ListBean list, AnimeBean anime) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudListAnime.removeAnimeFromList now takes Connection directly
            CrudListAnime.removeAnimeFromList(conn, list, anime);
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "Error removing anime ID " + anime.getIdAnimeTmdb() + " from list ID " + list.getId(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after removeAnimeFromList", e);
                }
            }
        }
    }

    @Override
    public List<AnimeBean> getAllAnimesInList(ListBean list) throws ExceptionDao {
        Connection conn = null;
        List<AnimeBean> animes;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            // CrudListAnime.getAnimesFullDetailsByList now returns List<AnimeBean> directly
            animes = CrudListAnime.getAnimesFullDetailsByList(conn, list);
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all animes for list ID " + list.getId(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection after getAllAnimesInList", e);
                }
            }
        }
        return animes;
    }
}