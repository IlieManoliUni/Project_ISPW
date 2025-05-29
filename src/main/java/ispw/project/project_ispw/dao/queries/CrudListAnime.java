package ispw.project.project_ispw.dao.queries;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudListAnime {


    private CrudListAnime(){
        //Empty Constructor
    }

    private static final String IDANILIST = "idAniList";

    private static final String INSERT_LIST_ANIME_SQL = "INSERT INTO list_anime (idList, idAniList) VALUES (?, ?)";
    private static final String DELETE_LIST_ANIME_SQL = "DELETE FROM list_anime WHERE idList = ? AND idAniList = ?";
    private static final String SELECT_ANIMES_IN_LIST_SQL = "SELECT idAniList FROM list_anime WHERE idList = ?";
    private static final String SELECT_FULL_DETAILS_ANIMES_IN_LIST_SQL =
            "SELECT a.idAniList, a.duration, a.episodes, a.name " +
                    "FROM list_anime la " +
                    "JOIN anime a ON la.idAniList = a.idAniList " +
                    "WHERE la.idList = ?";
    private static final String DELETE_ALL_ANIME_FROM_LIST_SQL = "DELETE FROM list_anime WHERE idList = ?";

    public static int addAnimeToList(Connection conn, ListBean list, AnimeBean anime) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_LIST_ANIME_SQL)) {
            ps.setInt(1, list.getId());
            ps.setInt(2, anime.getIdAnimeTmdb());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to add anime ID " + anime.getIdAnimeTmdb() + " to list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }

    public static int removeAnimeFromList(Connection conn, ListBean list, AnimeBean anime) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_LIST_ANIME_SQL)) {
            ps.setInt(1, list.getId());
            ps.setInt(2, anime.getIdAnimeTmdb());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to remove anime ID " + anime.getIdAnimeTmdb() + " from list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }

    public static void printAllAnimesInList(Connection conn, ListBean list) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ANIMES_IN_LIST_SQL)) {
            ps.setInt(1, list.getId());
            ps.executeQuery();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to print all anime in list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }

    public static List<Integer> getAnimeIdsByList(Connection conn, ListBean list) throws ExceptionDao {
        List<Integer> animeIds = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ANIMES_IN_LIST_SQL)) {
            ps.setInt(1, list.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    animeIds.add(rs.getInt(IDANILIST));
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve anime IDs for list ID " + list.getId() + ": " + e.getMessage(), e);
        }
        return animeIds;
    }

    public static List<AnimeBean> getAnimesFullDetailsByList(Connection conn, ListBean list) throws ExceptionDao {
        List<AnimeBean> animeDetails = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_FULL_DETAILS_ANIMES_IN_LIST_SQL)) {
            ps.setInt(1, list.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    animeDetails.add(new AnimeBean(
                            rs.getInt(IDANILIST),
                            rs.getInt("duration"),
                            rs.getInt("episodes"),
                            rs.getString("name")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve full anime details for list ID " + list.getId() + ": " + e.getMessage(), e);
        }
        return animeDetails;
    }

    public static int removeAllAnimesFromList(Connection conn, ListBean list) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_ALL_ANIME_FROM_LIST_SQL)) {
            ps.setInt(1, list.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to remove all anime from list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }
}