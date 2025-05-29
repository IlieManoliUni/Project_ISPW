package ispw.project.project_ispw.dao.jdbc;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.ListAnime;
import ispw.project.project_ispw.dao.queries.CrudListAnime;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
            CrudListAnime.addAnimeToList(conn, list, anime);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after addAnimeToList: {0}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void removeAnimeFromList(ListBean list, AnimeBean anime) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            CrudListAnime.removeAnimeFromList(conn, list, anime);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after removeAnimeFromList: {0}", e.getMessage());
                }
            }
        }
    }

    @Override
    public List<AnimeBean> getAllAnimeInList(ListBean list) throws ExceptionDao {
        Connection conn = null;
        List<AnimeBean> animes;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            animes = CrudListAnime.getAnimesFullDetailsByList(conn, list);
            if (animes == null) {
                return new ArrayList<>();
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after getAllAnimeInList: {0}", e.getMessage());
                }
            }
        }
        return animes;
    }

    @Override
    public void removeAllAnimesFromList(ListBean list) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            CrudListAnime.removeAllAnimesFromList(conn, list);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection after removeAllAnimesFromList: {0}", e.getMessage());
                }
            }
        }
    }
}