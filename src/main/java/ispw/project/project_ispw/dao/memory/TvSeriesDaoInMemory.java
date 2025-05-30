package ispw.project.project_ispw.dao.memory;

import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TvSeriesDaoInMemory implements TvSeriesDao {

    private final Map<Integer, TvSeriesBean> tvSeriesMap = new HashMap<>();

    @Override
    public TvSeriesBean retrieveById(int id) throws ExceptionDao {
        return tvSeriesMap.get(id);
    }

    @Override
    public boolean saveTvSeries(TvSeriesBean tvSeries) throws ExceptionDao {
        if (tvSeries == null) {
            throw new IllegalArgumentException("TV Series cannot be null.");
        }
        int id = tvSeries.getIdTvSeriesTmdb();
        if (tvSeriesMap.containsKey(id)) {
            throw new ExceptionDao("TV Series with ID " + id + " already exists.");
        }
        tvSeriesMap.put(id, tvSeries);
        return true;
    }

    @Override
    public List<TvSeriesBean> retrieveAllTvSeries() throws ExceptionDao {
        if (tvSeriesMap.isEmpty()) {
            return new ArrayList<>();
        }
        return Collections.unmodifiableList(new ArrayList<>(tvSeriesMap.values()));
    }
}