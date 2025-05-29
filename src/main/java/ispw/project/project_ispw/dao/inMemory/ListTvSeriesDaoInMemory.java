package ispw.project.project_ispw.dao.inMemory;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.dao.ListTvSeries;
import ispw.project.project_ispw.exception.ExceptionDao; // Assuming you have this custom exception

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListTvSeriesDaoInMemory implements ListTvSeries {

    // Maps list ID to a list of TV Series
    private final Map<Integer, List<TvSeriesBean>> tvSeriesByListId = new HashMap<>();

    @Override
    public void addTvSeriesToList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        if (list == null || tvSeries == null) {
            throw new IllegalArgumentException("List and TV Series cannot be null.");
        }

        int listId = list.getId();

        // Ensure the list exists for the given ID, creating it if necessary
        List<TvSeriesBean> tvSeriesList = tvSeriesByListId.computeIfAbsent(listId, k -> new ArrayList<>());

        if (tvSeriesList.contains(tvSeries)) {
            // Throw an ExceptionDao if the TV Series already exists in the list.
            throw new ExceptionDao("TV Series with ID " + tvSeries.getIdTvSeriesTmdb() + " already exists in list " + listId + ".");
        }

        tvSeriesList.add(tvSeries);
    }

    @Override
    public void removeTvSeriesFromList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        if (list == null || tvSeries == null) {
            throw new IllegalArgumentException("List and TV Series cannot be null.");
        }

        int listId = list.getId();

        List<TvSeriesBean> tvSeriesList = tvSeriesByListId.get(listId);

        // Check if the list itself exists and if the TV Series is successfully removed.
        if (tvSeriesList == null || !tvSeriesList.remove(tvSeries)) {
            // Throw an ExceptionDao if the TV Series is not found in the list.
            throw new ExceptionDao("TV Series with ID " + tvSeries.getIdTvSeriesTmdb() + " not found in list " + listId + ".");
        }

        // Optional: clean up empty lists to prevent memory leaks if lists are frequently emptied.
        if (tvSeriesList.isEmpty()) {
            tvSeriesByListId.remove(listId);
        }
    }

    @Override
    public List<TvSeriesBean> getAllTvSeriesInList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        int listId = list.getId();
        // Retrieve the list, or an empty list if the listId is not found.
        List<TvSeriesBean> tvSeriesList = tvSeriesByListId.getOrDefault(listId, Collections.emptyList());

        if (tvSeriesList.isEmpty() && !tvSeriesByListId.containsKey(listId)) {
            // If the list ID itself doesn't exist in our map, throw an exception.
            throw new ExceptionDao("No TV Series found for list ID: " + listId);
        }
        // Return an unmodifiable list to prevent external modification of the internal state.
        return Collections.unmodifiableList(tvSeriesList);
    }

    @Override
    public void removeAllTvSeriesFromList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        int listId = list.getId();

        // Attempt to remove the entry for the given list ID.
        // If remove returns null, it means the key (listId) was not present in the map.
        if (tvSeriesByListId.remove(listId) == null) {
            throw new ExceptionDao("List with ID " + listId + " not found, so no TV Series could be removed.");
        }
        // If the entry was found and removed, the list is now conceptually empty.
    }
}