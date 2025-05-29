package ispw.project.project_ispw.dao;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.List;

public interface ListMovie {
    void addMovieToList(ListBean list, MovieBean movie) throws ExceptionDao;

    void removeMovieFromList(ListBean list, MovieBean movie) throws ExceptionDao;

    List<MovieBean> getAllMoviesInList(ListBean list) throws ExceptionDao;

    void removeAllMoviesFromList(ListBean list) throws ExceptionDao;
}