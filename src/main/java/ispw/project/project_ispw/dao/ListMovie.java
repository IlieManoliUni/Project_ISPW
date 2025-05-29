package ispw.project.project_ispw.dao;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;

import java.util.List;

public interface ListMovie {
    void addMovieToList(ListBean list, MovieBean movie) throws Exception;
    void removeMovieFromList(ListBean list, MovieBean movie) throws Exception;
    List<MovieBean> getAllMoviesInList(ListBean list) throws Exception;
    // NEW METHOD
    void removeAllMoviesFromList(ListBean list) throws Exception; // Add this
}