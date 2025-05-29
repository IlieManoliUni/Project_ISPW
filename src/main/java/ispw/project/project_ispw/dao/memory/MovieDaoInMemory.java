package ispw.project.project_ispw.dao.memory;

import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.exception.ExceptionDao; // Assuming you have this custom exception

import java.util.ArrayList;
import java.util.Collections; // For unmodifiable list
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieDaoInMemory implements MovieDao {

    private final Map<Integer, MovieBean> movieMap = new HashMap<>();

    @Override
    public MovieBean retrieveById(int id) throws ExceptionDao {
        MovieBean movie = movieMap.get(id);
        if (movie == null) {
            throw new ExceptionDao("No Movie Found with ID: " + id);
        }
        return movie;
    }

    @Override
    public void saveMovie(MovieBean movie) throws ExceptionDao {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null.");
        }
        int id = movie.getIdMovieTmdb();
        if (movieMap.containsKey(id)) {
            throw new ExceptionDao("Movie with ID " + id + " already exists.");
        }
        movieMap.put(id, movie);
    }

    @Override
    public List<MovieBean> retrieveAllMovies() throws ExceptionDao {
        if (movieMap.isEmpty()) {
            throw new ExceptionDao("No Movies Found in memory.");
        }
        return Collections.unmodifiableList(new ArrayList<>(movieMap.values()));
    }
}