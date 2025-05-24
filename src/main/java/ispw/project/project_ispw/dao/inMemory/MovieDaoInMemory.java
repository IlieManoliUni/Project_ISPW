package ispw.project.project_ispw.dao.inMemory;

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
    public MovieBean retrieveById(int id) throws ExceptionDao { // Changed to throw ExceptionDao
        MovieBean movie = movieMap.get(id);
        if (movie == null) {
            // Throw ExceptionDao if the movie is not found, consistent with other DAOs
            throw new ExceptionDao("No Movie Found with ID: " + id);
        }
        return movie;
    }

    @Override
    public void saveMovie(MovieBean movie) throws ExceptionDao { // Changed to throw ExceptionDao
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null.");
        }
        int id = movie.getIdMovieTmdb();
        if (movieMap.containsKey(id)) {
            // Throw ExceptionDao if the movie already exists
            throw new ExceptionDao("Movie with ID " + id + " already exists.");
        }
        movieMap.put(id, movie);
    }

    @Override
    public List<MovieBean> retrieveAllMovies() throws ExceptionDao { // Changed to throw ExceptionDao
        if (movieMap.isEmpty()) {
            // Throw ExceptionDao if no movies are found, consistent with other DAOs
            throw new ExceptionDao("No Movies Found in memory.");
        }
        // Return an unmodifiable list to prevent external modification of the internal state.
        return Collections.unmodifiableList(new ArrayList<>(movieMap.values()));
    }
}