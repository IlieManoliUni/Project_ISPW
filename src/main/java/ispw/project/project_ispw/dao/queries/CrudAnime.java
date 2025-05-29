package ispw.project.project_ispw.dao.queries;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudAnime {

    private CrudAnime(){
        //Empty CrudAnime
    }

    private static final String INSERT_ANIME_SQL = "INSERT INTO anime (idAniList, duration, episodes, name) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_ANIME_SQL = "UPDATE anime SET name=?, episodes=?, duration=? WHERE idAniList = ?";
    private static final String DELETE_ANIME_SQL = "DELETE FROM anime WHERE idAniList = ?";
    private static final String SELECT_ALL_ANIMES_SQL = "SELECT idAniList, duration, episodes, name FROM anime";
    private static final String SELECT_ANIME_BY_ID_SQL = "SELECT idAniList, duration, episodes, name FROM anime WHERE idAniList = ?";

    public static int addAnime(Connection conn, AnimeBean anime) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_ANIME_SQL)) {
            ps.setInt(1, anime.getIdAnimeTmdb());
            ps.setInt(2, anime.getDuration());
            ps.setInt(3, anime.getEpisodes());
            ps.setString(4, anime.getTitle());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to add anime: " + e.getMessage(), e);
        }
    }

    public static int updateAnime(Connection conn, AnimeBean anime) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_ANIME_SQL)) {
            ps.setString(1, anime.getTitle());
            ps.setInt(2, anime.getEpisodes());
            ps.setInt(3, anime.getDuration());
            ps.setInt(4, anime.getIdAnimeTmdb());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to update anime with ID " + anime.getIdAnimeTmdb() + ": " + e.getMessage(), e);
        }
    }

    public static int deleteAnime(Connection conn, int animeId) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_ANIME_SQL)) {
            ps.setInt(1, animeId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to delete anime with ID " + animeId + ": " + e.getMessage(), e);
        }
    }

    public static List<AnimeBean> getAllAnimes(Connection conn) throws ExceptionDao {
        List<AnimeBean> animeList = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_ANIMES_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                animeList.add(mapResultSetToAnimeBean(rs));
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve all animes: " + e.getMessage(), e);
        }
        return animeList;
    }

    public static AnimeBean getAnimeById(Connection conn, int id) throws ExceptionDao {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ANIME_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAnimeBean(rs);
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDao("Failed to retrieve anime by ID " + id + ": " + e.getMessage(), e);
        }
        return null;
    }

    private static AnimeBean mapResultSetToAnimeBean(ResultSet rs) throws SQLException {
        int idAniList = rs.getInt("idAniList");
        int duration = rs.getInt("duration");
        int episodes = rs.getInt("episodes");
        String name = rs.getString("name");

        return new AnimeBean(idAniList, duration, episodes, name);
    }
}