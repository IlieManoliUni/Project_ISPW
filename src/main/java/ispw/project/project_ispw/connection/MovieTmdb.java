package ispw.project.project_ispw.connection;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import ispw.project.project_ispw.exception.ExceptionTmdbApi;
import ispw.project.project_ispw.model.MovieDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class MovieTmdb {

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String SEARCH_MOVIE_URL = "https://api.themoviedb.org/3/search/movie";
    private static final Gson gson = new Gson();

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private static String apiKey;

    static {
        try (InputStream input = MovieTmdb.class.getClassLoader().getResourceAsStream("tmdbapi.properties")) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
                apiKey = prop.getProperty("TMDB_API_KEY");
                if (apiKey == null || apiKey.trim().isEmpty()) {
                    throw new ExceptionTmdbApi("TMDB_API_KEY not found or is empty in tmdbapi.properties!");
                }
            } else {
                throw new ExceptionTmdbApi("tmdbapi.properties file not found in resources! Please ensure it's in the classpath.");
            }
        } catch (IOException e) {
            throw new ExceptionTmdbApi("Failed to load TMDB API key from properties file due to an I/O error.", e);
        }
    }

    private MovieTmdb() {
        // This constructor is intentionally empty to prevent instantiation.
    }

    private static String executeHttpRequest(String url) throws ExceptionTmdbApi {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                throw new ExceptionTmdbApi(
                        "HTTP error: " + response.code() + ", Body: " + responseBody
                );
            }
            return responseBody;
        } catch (IOException e) {
            throw new ExceptionTmdbApi("Network error: " + e.getMessage(), e);
        }
    }

    public static MovieDto getMovieById(int movieId) throws ExceptionTmdbApi {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + movieId).newBuilder();
        urlBuilder.addQueryParameter("api_key", apiKey);
        String url = urlBuilder.build().toString();

        String jsonResponse = executeHttpRequest(url);

        try {
            return gson.fromJson(jsonResponse, MovieDto.class);
        } catch (JsonSyntaxException e) {
            throw new ExceptionTmdbApi("Failed to parse JSON for movie ID " + movieId + ": " + e.getMessage(), e);
        }
    }

    public static List<MovieDto> searchMovies(String query) throws ExceptionTmdbApi {
        return searchMovies(query, 1);
    }

    public static List<MovieDto> searchMovies(String query, int page) throws ExceptionTmdbApi {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SEARCH_MOVIE_URL).newBuilder();
        urlBuilder.addQueryParameter("api_key", apiKey);
        urlBuilder.addQueryParameter("query", query);
        urlBuilder.addQueryParameter("page", String.valueOf(page));
        String url = urlBuilder.build().toString();

        String jsonResponse = executeHttpRequest(url);

        try {
            JsonObject rootJson = gson.fromJson(jsonResponse, JsonObject.class);
            JsonArray resultsArray = rootJson.getAsJsonArray("results");

            List<MovieDto> movies = new ArrayList<>();
            if (resultsArray != null) {
                for (int i = 0; i < resultsArray.size(); i++) {
                    MovieDto movie = gson.fromJson(resultsArray.get(i), MovieDto.class);
                    movies.add(movie);
                }
            }
            return movies;
        } catch (JsonSyntaxException e) {
            throw new ExceptionTmdbApi("Failed to parse JSON for movie search query '" + query + "': " + e.getMessage(), e);
        }
    }
}