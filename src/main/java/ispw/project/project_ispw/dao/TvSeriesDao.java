package ispw.project.project_ispw.dao;

import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.List;

public interface TvSeriesDao {

    // Method to retrieve a TV Series by ID
    TvSeriesBean retrieveById(int id) throws ExceptionDao;

    // Method to save a new TV Series
    boolean saveTvSeries(TvSeriesBean tvSeries) throws ExceptionDao;

    // Method to retrieve all TV Series
    List<TvSeriesBean> retrieveAllTvSeries() throws ExceptionDao;
}
