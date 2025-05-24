package ispw.project.project_ispw.dao.inMemory;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.dao.AnimeDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimeDaoInMemory implements AnimeDao {

    private final Map<Integer, AnimeBean> animeMap = new HashMap<>();

    @Override
    public AnimeBean retrieveById(int id) throws Exception {
        // In this in-memory implementation, we just return null if not found.
        // If this were a real database, you might throw an exception if the ID is invalid
        // or if there's a connection issue. For in-memory, null is a reasonable default.
        return animeMap.get(id);
    }

    @Override
    public void saveAnime(AnimeBean anime) throws Exception {
        if (anime == null) {
            throw new IllegalArgumentException("Anime cannot be null.");
        }
        int id = anime.getIdAnimeTmdb();
        if (animeMap.containsKey(id)) {
            // As per the interface, we throw an Exception if the anime already exists.
            throw new Exception("Anime with ID " + id + " already exists.");
        }
        animeMap.put(id, anime);
    }

    @Override
    public List<AnimeBean> retrieveAllAnime() throws Exception {
        // For an in-memory DAO, retrieving all elements won't typically throw an exception
        // unless there's some unexpected internal error (e.g., out of memory),
        // which is generally handled at a higher level.
        return new ArrayList<>(animeMap.values());
    }
}