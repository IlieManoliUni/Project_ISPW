package ispw.project.project_ispw.dao;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.List;

public interface ListTvSeries {
    void addTvSeriesToList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao;

    void removeTvSeriesFromList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao;

    List<TvSeriesBean> getAllTvSeriesInList(ListBean list) throws ExceptionDao;

    void removeAllTvSeriesFromList(ListBean list) throws ExceptionDao;
}