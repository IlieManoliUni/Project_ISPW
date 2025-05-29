package ispw.project.project_ispw.dao;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.TvSeriesBean;

import java.util.List;

public interface ListTvSeries {
    void addTvSeriesToList(ListBean list, TvSeriesBean tvSeries) throws Exception;
    void removeTvSeriesFromList(ListBean list, TvSeriesBean tvSeries) throws Exception;
    List<TvSeriesBean> getAllTvSeriesInList(ListBean list) throws Exception;
    // NEW METHOD
    void removeAllTvSeriesFromList(ListBean list) throws Exception; // Add this line
}