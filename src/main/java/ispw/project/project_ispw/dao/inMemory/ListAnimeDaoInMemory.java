package ispw.project.project_ispw.dao.inMemory;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.dao.ListAnime;
import ispw.project.project_ispw.exception.ExceptionDao; // Assuming you have this custom exception

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAnimeDaoInMemory implements ListAnime {

    // Maps list ID to a list of Anime
    private final Map<Integer, List<AnimeBean>> animeByListId = new HashMap<>();

    @Override
    public void addAnimeToList(ListBean list, AnimeBean anime) throws ExceptionDao {
        if (list == null || anime == null) {
            throw new IllegalArgumentException("List and Anime cannot be null.");
        }

        int listId = list.getId();

        // Ensure the list exists for the given ID, creating it if necessary
        // Using computeIfAbsent to ensure the list is created if it doesn't exist
        List<AnimeBean> animeList = animeByListId.computeIfAbsent(listId, k -> new ArrayList<>());

        if (animeList.contains(anime)) {
            // Throw an ExceptionDao if the anime already exists in the list.
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

        // Check if the list itself exists and if the anime is successfully removed.
        if (animeList == null || !animeList.remove(anime)) {
            // Throw an ExceptionDao if the anime is not found in the list.
            throw new ExceptionDao("Anime with ID " + anime.getIdAnimeTmdb() + " not found in list " + listId + ".");
        }

        // Optional: clean up empty lists to prevent memory leaks if lists are frequently emptied.
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
        // Retrieve the list, or an empty list if the listId is not found.
        List<AnimeBean> animeList = animeByListId.getOrDefault(listId, Collections.emptyList());

        if (animeList.isEmpty() && !animeByListId.containsKey(listId)) {
            // If the list ID itself doesn't exist in our map, it means no such list was ever created,
            // or it was created and then emptied and removed.
            // Throw an ExceptionDao as per the pattern in other DAOs if nothing is found.
            throw new ExceptionDao("No Animes found for list ID: " + listId);
        }
        // Return an unmodifiable list to prevent external modification of the internal state.
        return Collections.unmodifiableList(animeList);
    }

    @Override
    public void removeAllAnimesFromList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        int listId = list.getId();

        // Check if the list exists before attempting to remove it.
        // If remove returns null, it means the key was not present.
        if (animeByListId.remove(listId) == null) {
            throw new ExceptionDao("List with ID " + listId + " not found, so no animes could be removed.");
        }
        // If it was found and removed, no further action is needed as the list is now empty.
    }
}