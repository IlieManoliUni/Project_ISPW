package ispw.project.project_ispw.dao;


import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.TvSeriesBean;

import java.util.List;

public interface ListTvSeries {

    // Add a TV Series to a List
    void addTvSeriesToList(ListBean list, TvSeriesBean tvSeries) throws Exception;

    // Remove a TV Series from a List
    void removeTvSeriesFromList(ListBean list, TvSeriesBean tvSeries) throws Exception;

    // Get all TV Series in a List
    List<TvSeriesBean> getAllTvSeriesInList(ListBean list) throws Exception;
}
