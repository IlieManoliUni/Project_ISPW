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
import ispw.project.project_ispw.model.TvSeriesDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TvSeriesTmdb {

    private static final Logger logger = Logger.getLogger(TvSeriesTmdb.class.getName());

    private static final String BASE_URL = "https://api.themoviedb.org/3/tv/";
    private static final String SEARCH_TV_URL = "https://api.themoviedb.org/3/search/tv";
    private static final Gson gson = new Gson();

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private static String apiKey;

    static {
        try (InputStream input = TvSeriesTmdb.class.getClassLoader().getResourceAsStream("tmdbapi.properties")) {
            if (input == null) {
                throw new ExceptionTmdbApi("Unable to find tmdbapi.properties file on the classpath.");
            }
            Properties properties = new Properties();
            properties.load(input);
            apiKey = properties.getProperty("TMDB_API_KEY");
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new ExceptionTmdbApi("TMDB_API_KEY is not defined or is empty in tmdbapi.properties.");
            }
        } catch (IOException e) {
            throw new ExceptionTmdbApi("Failed to load TMDb API key properties file due to an I/O error during application startup.", e);
        } catch (ExceptionTmdbApi e) {
            throw e;
        }
    }

    private TvSeriesTmdb() {
        // Private constructor to prevent instantiation
    }

    private static String executeHttpRequest(String url) throws ExceptionTmdbApi {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                throw new ExceptionTmdbApi(
                        "TMDb API request failed: HTTP " + response.code() + " - " + responseBody);
            }
            return responseBody;

        } catch (IOException e) {
            throw new ExceptionTmdbApi("Network or I/O error during TMDb API call: " + e.getMessage(), e);
        }
    }

    public static TvSeriesDto getTvSeriesById(int tvSeriesId) throws ExceptionTmdbApi {
        String url = BASE_URL + tvSeriesId + "?api_key=" + apiKey;
        String jsonResponse = executeHttpRequest(url);

        try {
            TvSeriesDto tvSeries = gson.fromJson(jsonResponse, TvSeriesDto.class);
            if (tvSeries == null) {
                throw new ExceptionTmdbApi("Failed to deserialize TV Series details for ID " + tvSeriesId);
            }
            return tvSeries;
        } catch (JsonSyntaxException e) {
            throw new ExceptionTmdbApi("Failed to parse JSON for TV series ID " + tvSeriesId + ": " + e.getMessage(), e);
        }
    }

    public static List<TvSeriesDto> searchTvSeries(String query) throws ExceptionTmdbApi {
        return searchTvSeries(query, 1);
    }

    public static List<TvSeriesDto> searchTvSeries(String query, int page) throws ExceptionTmdbApi {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SEARCH_TV_URL).newBuilder();
        urlBuilder.addQueryParameter("api_key", apiKey);
        urlBuilder.addQueryParameter("query", query);
        urlBuilder.addQueryParameter("page", String.valueOf(page));
        String url = urlBuilder.build().toString();

        String jsonResponse = executeHttpRequest(url);

        try {
            JsonObject rootJson = gson.fromJson(jsonResponse, JsonObject.class);
            JsonArray resultsArray = rootJson.getAsJsonArray("results");

            List<TvSeriesDto> tvSeriesList = new ArrayList<>();
            if (resultsArray != null) {
                for (int i = 0; i < resultsArray.size(); i++) {
                    TvSeriesDto tvSeries = parseSingleTvSeriesModel(resultsArray.get(i));
                    if (tvSeries != null) {
                        tvSeriesList.add(tvSeries);
                    }
                }
            }
            return tvSeriesList;
        } catch (JsonSyntaxException e) {
            throw new ExceptionTmdbApi("Failed to parse JSON for TV series search query '" + query + "': " + e.getMessage(), e);
        }
    }
    private static TvSeriesDto parseSingleTvSeriesModel(com.google.gson.JsonElement jsonElement) {
        try {
            return gson.fromJson(jsonElement, TvSeriesDto.class);
        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING,
                    "Failed to parse TV series element due to JSON syntax error. Element: {0}. Error: {1}",
                    new Object[]{jsonElement, e.getMessage()});
            return null;
        }
    }
}