package ispw.project.project_ispw.dao.memory;

import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieDaoInMemory implements MovieDao {

    private final Map<Integer, MovieBean> movieMap = new HashMap<>();

    @Override
    public MovieBean retrieveById(int id) throws ExceptionDao {
        return movieMap.get(id);
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
            return new ArrayList<>();
        }
        return Collections.unmodifiableList(new ArrayList<>(movieMap.values()));
    }
}