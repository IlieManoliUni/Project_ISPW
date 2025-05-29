package ispw.project.project_ispw.dao.memory;

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

    private final Map<Integer, List<TvSeriesBean>> tvSeriesByListId = new HashMap<>();

    @Override
    public void addTvSeriesToList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        if (list == null || tvSeries == null) {
            throw new IllegalArgumentException("List and TV Series cannot be null.");
        }

        int listId = list.getId();

        List<TvSeriesBean> tvSeriesList = tvSeriesByListId.computeIfAbsent(listId, k -> new ArrayList<>());

        if (tvSeriesList.contains(tvSeries)) {
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

        if (tvSeriesList == null || !tvSeriesList.remove(tvSeries)) {
            throw new ExceptionDao("TV Series with ID " + tvSeries.getIdTvSeriesTmdb() + " not found in list " + listId + ".");
        }

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
        List<TvSeriesBean> tvSeriesList = tvSeriesByListId.getOrDefault(listId, Collections.emptyList());

        if (tvSeriesList.isEmpty() && !tvSeriesByListId.containsKey(listId)) {
            throw new ExceptionDao("No TV Series found for list ID: " + listId);
        }
        return Collections.unmodifiableList(tvSeriesList);
    }

    @Override
    public void removeAllTvSeriesFromList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        int listId = list.getId();

        if (tvSeriesByListId.remove(listId) == null) {
            throw new ExceptionDao("List with ID " + listId + " not found, so no TV Series could be removed.");
        }
    }
}