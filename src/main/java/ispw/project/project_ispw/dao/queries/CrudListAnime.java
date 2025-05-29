package ispw.project.project_ispw.dao.queries;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.exception.ExceptionDao; // Using your custom exception

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudListAnime {

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
            ps.setInt(2, anime.getIdAnimeTmdb()); // Corrected: Use getIdAnimeTmdb()
            System.out.println("Executing INSERT: " + ps.toString()); // For debugging, remove in production
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to add anime ID " + anime.getIdAnimeTmdb() + " to list ID " + list.getId() + ": " + e.getMessage(), e); // Corrected: Use getIdAnimeTmdb() in message
        }
    }

    public static int removeAnimeFromList(Connection conn, ListBean list, AnimeBean anime) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_LIST_ANIME_SQL)) {
            ps.setInt(1, list.getId());
            ps.setInt(2, anime.getIdAnimeTmdb()); // Corrected: Use getIdAnimeTmdb()
            System.out.println("Executing DELETE: " + ps.toString()); // For debugging, remove in production
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to remove anime ID " + anime.getIdAnimeTmdb() + " from list ID " + list.getId() + ": " + e.getMessage(), e); // Corrected: Use getIdAnimeTmdb() in message
        }
    }

    // This method is primarily for debugging/logging, often not used in production logic directly
    public static void printAllAnimesInList(Connection conn, ListBean list) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ANIMES_IN_LIST_SQL)) {
            ps.setInt(1, list.getId());
            System.out.println("Executing SELECT: " + ps.toString()); // For debugging, remove in production
            try (ResultSet res = ps.executeQuery()) {
                while (res.next()) {
                    System.out.printf("List ID: %d, Anime ID: %d\n",
                            list.getId(), res.getInt("idAniList")); // list.getId() is constant for this query
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to print all anime in list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }

    // Returns a list of Anime IDs (integers) present in a given list
    public static List<Integer> getAnimeIdsByList(Connection conn, ListBean list) throws ExceptionDao {
        List<Integer> animeIds = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ANIMES_IN_LIST_SQL)) {
            ps.setInt(1, list.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    animeIds.add(rs.getInt("idAniList"));
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve anime IDs for list ID " + list.getId() + ": " + e.getMessage(), e);
        }
        return animeIds;
    }

    // Returns a list of AnimeBean objects with full details for anime in a given list
    public static List<AnimeBean> getAnimesFullDetailsByList(Connection conn, ListBean list) throws ExceptionDao {
        List<AnimeBean> animeDetails = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_FULL_DETAILS_ANIMES_IN_LIST_SQL)) {
            ps.setInt(1, list.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Assuming AnimeBean constructor: public AnimeBean(int idAnimeTmdb, int duration, int episodes, String title)
                    // 'name' column from DB maps to 'title' in AnimeBean
                    animeDetails.add(new AnimeBean(
                            rs.getInt("idAniList"),
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

    // NEW METHOD to remove all anime for a given list ID
    public static int removeAllAnimesFromList(Connection conn, ListBean list) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_ALL_ANIME_FROM_LIST_SQL)) {
            ps.setInt(1, list.getId());
            System.out.println("Executing DELETE ALL ANIME FROM LIST: " + ps.toString());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to remove all anime from list ID " + list.getId() + ": " + e.getMessage(), e);
        }
    }
}