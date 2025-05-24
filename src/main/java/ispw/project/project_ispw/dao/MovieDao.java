package ispw.project.project_ispw.dao;

import ispw.project.project_ispw.bean.MovieBean;

import java.util.List;

public interface MovieDao {

    // Method to retrieve a Movie by its ID
    MovieBean retrieveById(int id) throws Exception;

    // Method to save a Movie
    void saveMovie(MovieBean movie) throws Exception;

    // Method to retrieve all Movies
    List<MovieBean> retrieveAllMovies() throws Exception;
}
