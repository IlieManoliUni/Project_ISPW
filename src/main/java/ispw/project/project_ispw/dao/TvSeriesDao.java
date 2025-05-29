package ispw.project.project_ispw.dao;

import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.List;

public interface TvSeriesDao {

    TvSeriesBean retrieveById(int id) throws ExceptionDao;

    boolean saveTvSeries(TvSeriesBean tvSeries) throws ExceptionDao;

    List<TvSeriesBean> retrieveAllTvSeries() throws ExceptionDao;
}
