package ispw.project.project_ispw.dao.inMemory;

import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.exception.ExceptionDao; // Assuming you have this custom exception

import java.util.ArrayList;
import java.util.Collections; // For unmodifiable list
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TvSeriesDaoInMemory implements TvSeriesDao {

    private final Map<Integer, TvSeriesBean> tvSeriesMap = new HashMap<>();

    @Override
    public TvSeriesBean retrieveById(int id) throws ExceptionDao { // Changed to throw ExceptionDao
        TvSeriesBean tvSeries = tvSeriesMap.get(id);
        if (tvSeries == null) {
            // Throw ExceptionDao if the TV series is not found, consistent with other DAOs
            throw new ExceptionDao("No TV Series Found with ID: " + id);
        }
        return tvSeries;
    }

    @Override
    public boolean saveTvSeries(TvSeriesBean tvSeries) throws ExceptionDao { // Changed to throw ExceptionDao
        if (tvSeries == null) {
            throw new IllegalArgumentException("TV Series cannot be null.");
        }
        int id = tvSeries.getIdTvSeriesTmdb();
        if (tvSeriesMap.containsKey(id)) {
            // Throw ExceptionDao if the TV series already exists
            throw new ExceptionDao("TV Series with ID " + id + " already exists.");
        }
        tvSeriesMap.put(id, tvSeries);
        return true;
    }

    @Override
    public List<TvSeriesBean> retrieveAllTvSeries() throws ExceptionDao { // Changed to throw ExceptionDao
        if (tvSeriesMap.isEmpty()) {
            // Throw ExceptionDao if no TV series are found, consistent with other DAOs
            throw new ExceptionDao("No TV Series Found in memory.");
        }
        // Return an unmodifiable list to prevent external modification of the internal state.
        return Collections.unmodifiableList(new ArrayList<>(tvSeriesMap.values()));
    }
}