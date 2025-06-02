package ispw.project.project_ispw.connection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import ispw.project.project_ispw.exception.ExceptionAniListApi;
import ispw.project.project_ispw.model.AnimeModel;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AnimeAniList {

    private static final String ANILIST_API_URL = "https://graphql.anilist.co";
    private static final Gson gson = new Gson();

    private static final String JSON_KEY_ERRORS = "errors";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private AnimeAniList() {
        // This constructor is intentionally empty to prevent instantiation.
    }

    private static String executeGraphQLRequest(String query, JsonObject variables) throws ExceptionAniListApi {
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("query", query);
        if (variables != null) {
            jsonRequest.add("variables", variables);
        }

        MediaType jsonMediaType = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(gson.toJson(jsonRequest), jsonMediaType);

        Request request = new Request.Builder()
                .url(ANILIST_API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            JsonObject jsonResponse = parseResponseToJsonObject(responseBody);

            if (jsonResponse.has(JSON_KEY_ERRORS) && jsonResponse.getAsJsonArray(JSON_KEY_ERRORS).size() > 0) {
                JsonArray errors = jsonResponse.getAsJsonArray(JSON_KEY_ERRORS);
                throw new ExceptionAniListApi("AniList API returned GraphQL errors: " + errors.toString());
            }

            if (!response.isSuccessful()) {
                throw new ExceptionAniListApi("Unexpected HTTP code " + response.code() + ": " + responseBody,
                        response.code(), responseBody);
            }

            return responseBody;
        } catch (IOException e) {
            throw new ExceptionAniListApi("Network or I/O error during GraphQL request: " + e.getMessage(), e);
        } catch (ExceptionAniListApi e) {
            throw e;
        } catch (Exception e) {
            throw new ExceptionAniListApi("An unexpected error occurred during GraphQL request: " + e.getMessage(), e);
        }
    }

    public static AnimeModel getAnimeById(int animeId) throws ExceptionAniListApi {
        String query = """
                query ($id: Int) {
                  Media(id: $id, type: ANIME) {
                    id
                    title {
                      romaji
                      english
                      native
                    }
                    description
                    coverImage {
                      medium
                    }
                    episodes
                    duration
                    genres
                    countryOfOrigin
                    startDate {
                      year
                      month
                      day
                    }
                    endDate {
                      year
                      month
                      day
                    }
                    averageScore
                    meanScore
                    status
                    nextAiringEpisode {
                      episode
                      airingAt
                    }
                  }
                }
                """;

        JsonObject variables = new JsonObject();
        variables.addProperty("id", animeId);

        String jsonResponse = executeGraphQLRequest(query, variables);

        JsonObject rootJson = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject mediaJson = rootJson.getAsJsonObject("data").getAsJsonObject("Media");

        if (mediaJson == null) {
            throw new ExceptionAniListApi("No anime found with ID: " + animeId);
        }

        AnimeModel anime = gson.fromJson(mediaJson, AnimeModel.class);

        if (anime.getDescription() != null) {
            anime.setDescription(stripHtmlTags(anime.getDescription()));
        }

        return anime;
    }

    public static List<AnimeModel> searchAnime(String searchString) throws ExceptionAniListApi {
        String query = """
                query ($search: String) {
                  Page(perPage: 10) {
                    media(search: $search, type: ANIME) {
                      id
                      title {
                        romaji
                        english
                        native
                      }
                      coverImage {
                        medium
                      }
                      description
                      episodes
                      duration
                      genres
                      countryOfOrigin
                      startDate {
                        year
                        month
                        day
                      }
                      endDate {
                        year
                        month
                        day
                      }
                      averageScore
                      meanScore
                      status
                      nextAiringEpisode {
                        episode
                        airingAt
                      }
                    }
                  }
                }
                """;

        JsonObject variables = new JsonObject();
        variables.addProperty("search", searchString);

        String jsonResponse = executeGraphQLRequest(query, variables);

        JsonObject rootJson = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject pageJson = rootJson.getAsJsonObject("data").getAsJsonObject("Page");
        JsonArray mediaArrayJson = pageJson.getAsJsonArray("media");

        List<AnimeModel> animeList = new ArrayList<>();
        if (mediaArrayJson != null) {
            for (int i = 0; i < mediaArrayJson.size(); i++) {
                JsonObject animeJson = mediaArrayJson.get(i).getAsJsonObject();
                AnimeModel anime = gson.fromJson(animeJson, AnimeModel.class);

                if (anime.getDescription() != null) {
                    anime.setDescription(stripHtmlTags(anime.getDescription()));
                }
                animeList.add(anime);
            }
        }
        return animeList;
    }

    private static JsonObject parseResponseToJsonObject(String responseBody) throws ExceptionAniListApi {
        try {
            return JsonParser.parseString(responseBody).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new ExceptionAniListApi("Invalid JSON response from AniList API: " + responseBody, e);
        }
    }

    /**
     * For a sonarcloud error
     * Strips HTML tags from a string and cleans up whitespace.
     * Uses a possessive quantifier in the HTML tag regex to prevent ReDoS vulnerability.
     * Uses explicit grouping for whitespace regex to clarify precedence.
     * For more robust HTML parsing, consider using Jsoup library.
     *
     * @param html The input string potentially containing HTML tags.
     * @return The string with HTML tags removed and HTML entities decoded, and trimmed whitespace.
     */
    private static String stripHtmlTags(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }

        String stripped = html.replaceAll("<[^>]*+>", "");

        stripped = stripped.replace("&quot;", "\"");
        stripped = stripped.replace("&amp;", "&");
        stripped = stripped.replace("&lt;", "<");
        stripped = stripped.replace("&gt;", ">");
        stripped = stripped.replace("&#39;", "'");
        stripped = stripped.replace("&#x2F;", "/");
        stripped = stripped.replace("&#34;", "\"");
        stripped = stripped.replace("&#x27;", "'");
        stripped = stripped.replace("&#x2B;", "+");
        stripped = stripped.replace("&apos;", "'");
        stripped = stripped.replace("&nbsp;", " ");

        stripped = stripped.replaceAll("(?m)^[\\s&&[^\\n]]+", "");
        stripped = stripped.replaceAll("(?m)[\\s&&[^\\n]]+$", "");

        stripped = stripped.replaceAll("\\n\\n+", "\n");
        return stripped.trim();
    }
}