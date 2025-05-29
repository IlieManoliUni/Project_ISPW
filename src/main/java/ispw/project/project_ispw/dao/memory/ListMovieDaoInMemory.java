package ispw.project.project_ispw.dao.memory;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.dao.ListMovie;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMovieDaoInMemory implements ListMovie {

    private final Map<Integer, List<MovieBean>> movieByListId = new HashMap<>();

    @Override
    public void addMovieToList(ListBean list, MovieBean movie) throws ExceptionDao {
        if (list == null || movie == null) {
            throw new IllegalArgumentException("List and Movie cannot be null.");
        }

        int listId = list.getId();

        List<MovieBean> movieList = movieByListId.computeIfAbsent(listId, k -> new ArrayList<>());

        if (movieList.contains(movie)) {
            throw new ExceptionDao("Movie with ID " + movie.getIdMovieTmdb() + " already exists in list " + listId + ".");
        }

        movieList.add(movie);
    }

    @Override
    public void removeMovieFromList(ListBean list, MovieBean movie) throws ExceptionDao {
        if (list == null || movie == null) {
            throw new IllegalArgumentException("List and Movie cannot be null.");
        }

        int listId = list.getId();

        List<MovieBean> movieList = movieByListId.get(listId);

        if (movieList == null || !movieList.remove(movie)) {
            throw new ExceptionDao("Movie with ID " + movie.getIdMovieTmdb() + " not found in list " + listId + ".");
        }

        if (movieList.isEmpty()) {
            movieByListId.remove(listId);
        }
    }

    @Override
    public List<MovieBean> getAllMoviesInList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        int listId = list.getId();

        List<MovieBean> movieList = movieByListId.getOrDefault(listId, Collections.emptyList());

        return Collections.unmodifiableList(movieList);
    }

    @Override
    public void removeAllMoviesFromList(ListBean list) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        int listId = list.getId();
        movieByListId.remove(listId);

    }
}