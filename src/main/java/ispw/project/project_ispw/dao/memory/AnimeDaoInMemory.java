package ispw.project.project_ispw.dao.memory;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.exception.CsvDaoException;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimeDaoInMemory implements AnimeDao {

    private final Map<Integer, AnimeBean> animeMap = new HashMap<>();

    @Override
    public AnimeBean retrieveById(int id) throws ExceptionDao {
        return animeMap.get(id);
    }

    @Override
    public void saveAnime(AnimeBean anime) throws ExceptionDao {
        if (anime == null) {
            throw new IllegalArgumentException("Anime cannot be null.");
        }
        int id = anime.getIdAnimeTmdb();
        if (animeMap.containsKey(id)) {
            throw new CsvDaoException("Anime with ID " + id + " already exists.");
        }
        animeMap.put(id, anime);
    }

    @Override
    public List<AnimeBean> retrieveAllAnime() throws ExceptionDao {
        return new ArrayList<>(animeMap.values());
    }
}