package ispw.project.project_ispw.dao.memory;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.dao.ListAnime;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAnimeDaoInMemory implements ListAnime {

    private final Map<Integer, List<AnimeBean>> animeByListId = new HashMap<>();

    @Override
    public void addAnimeToList(ListBean list, AnimeBean anime) throws ExceptionDao {
        if (list == null || anime == null) {
            throw new IllegalArgumentException("List and Anime cannot be null.");
        }

        int listId = list.getId();

        List<AnimeBean> animeList = animeByListId.computeIfAbsent(listId, k -> new ArrayList<>());

        if (animeList.contains(anime)) {
            throw new ExceptionDao("Anime with ID " + anime.getIdAnimeTmdb() + " already exists in list " + listId + ".");
        }

        animeList.add(anime);
    }

    @Override
    public void removeAnimeFromList(ListBean list, AnimeBean anime) throws ExceptionDao {
        if (list == null || anime == null) {
            throw new IllegalArgumentException("List and Anime cannot be null.");
        }

        int listId = list.getId();

        List<AnimeBean> animeList = animeByListId.get(listId);

        if (animeList == null || !animeList.remove(anime)) {
            throw new ExceptionDao("Anime with ID " + anime.getIdAnimeTmdb() + " not found in list " + listId + ".");
        }

        if (animeList.isEmpty()) {
            animeByListId.remove(listId);
        }
    }

    @Override
    public List<AnimeBean> getAllAnimeInList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        int listId = list.getId();
        List<AnimeBean> animeList = animeByListId.getOrDefault(listId, Collections.emptyList());

        return Collections.unmodifiableList(animeList);
    }

    @Override
    public void removeAllAnimesFromList(ListBean list) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }
        int listId = list.getId();
        animeByListId.remove(listId);
    }
}