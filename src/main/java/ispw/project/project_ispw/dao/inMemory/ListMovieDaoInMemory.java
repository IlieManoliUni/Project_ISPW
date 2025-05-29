package ispw.project.project_ispw.dao.inMemory;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.dao.ListMovie;
import ispw.project.project_ispw.exception.ExceptionDao; // Assuming you have this custom exception

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMovieDaoInMemory implements ListMovie {

    // Maps list ID to a list of Movies
    private final Map<Integer, List<MovieBean>> movieByListId = new HashMap<>();

    @Override
    public void addMovieToList(ListBean list, MovieBean movie) throws ExceptionDao {
        if (list == null || movie == null) {
            throw new IllegalArgumentException("List and Movie cannot be null.");
        }

        int listId = list.getId();

        // Ensure the list exists for the given ID, creating it if necessary
        List<MovieBean> movieList = movieByListId.computeIfAbsent(listId, k -> new ArrayList<>());

        if (movieList.contains(movie)) {
            // Throw an ExceptionDao if the movie already exists in the list.
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

        // Check if the list itself exists and if the movie is successfully removed.
        if (movieList == null || !movieList.remove(movie)) {
            // Throw an ExceptionDao if the movie is not found in the list.
            throw new ExceptionDao("Movie with ID " + movie.getIdMovieTmdb() + " not found in list " + listId + ".");
        }

        // Optional: clean up empty lists to prevent memory leaks if lists are frequently emptied.
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
        // Retrieve the list, or an empty list if the listId is not found.
        List<MovieBean> movieList = movieByListId.getOrDefault(listId, Collections.emptyList());

        if (movieList.isEmpty() && !movieByListId.containsKey(listId)) {
            // If the list ID itself doesn't exist in our map, throw an exception.
            throw new ExceptionDao("No Movies found for list ID: " + listId);
        }
        // Return an unmodifiable list to prevent external modification of the internal state.
        return Collections.unmodifiableList(movieList);
    }

    @Override
    public void removeAllMoviesFromList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        int listId = list.getId();

        // Check if the list exists before attempting to remove it.
        // If remove returns null, it means the key was not present.
        if (movieByListId.remove(listId) == null) {
            throw new ExceptionDao("List with ID " + listId + " not found, so no movies could be removed.");
        }
        // If it was found and removed, no further action is needed as the list is now empty.
    }
}