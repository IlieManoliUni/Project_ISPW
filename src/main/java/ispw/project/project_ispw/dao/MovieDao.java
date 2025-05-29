package ispw.project.project_ispw.dao;

import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.List;

public interface MovieDao {

    MovieBean retrieveById(int id) throws ExceptionDao;

    void saveMovie(MovieBean movie) throws ExceptionDao;

    List<MovieBean> retrieveAllMovies() throws ExceptionDao;
}
