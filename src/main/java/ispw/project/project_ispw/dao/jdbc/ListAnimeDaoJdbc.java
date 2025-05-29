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

public class ListAnimeDaoJdbc implements ListAnime {

    @Override
    public void addAnimeToList(ListBean list, AnimeBean anime) throws ExceptionDao {
        Connection conn = null;
        try {
            conn = SingletonDatabase.getInstance().getConnection();
            CrudListAnime.addAnimeToList(conn, list, anime);
        } catch (ExceptionDao e) {
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after addAnimeToList
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
        } catch (ExceptionDao e) {
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after removeAnimeFromList
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
        } catch (ExceptionDao e) {
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after getAllAnimeInList
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
            // You'll need to implement this method in your CrudListAnime class.
            // This method should delete all anime entries associated with the given list ID in the database.
            CrudListAnime.removeAllAnimesFromList(conn, list);
        } catch (ExceptionDao e) {
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Error closing connection after removeAllAnimesFromList
                }
            }
        }
    }
}