package ispw.project.project_ispw.dao;


import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;

import java.util.List;

public interface ListMovie {

    // Add a Movie to a List
    void addMovieToList(ListBean list, MovieBean movie) throws Exception;

    // Remove a Movie from a List
    void removeMovieFromList(ListBean list, MovieBean movie) throws Exception;

    // Get all Movies in a List
    List<MovieBean> getAllMoviesInList(ListBean list) throws Exception;
}
