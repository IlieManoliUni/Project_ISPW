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
import com.google.gson.JsonSyntaxException; // Import for JSON parsing errors
import ispw.project.project_ispw.exception.ExceptionAniListApi;
import ispw.project.project_ispw.model.AnimeModel; // Assuming your Anime model is in this package

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AnimeAniList {

    private static final String ANILIST_API_URL = "https://graphql.anilist.co";
    private static final Gson gson = new Gson();

    // Create and reuse the OkHttpClient instance with basic timeouts
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(30, TimeUnit.SECONDS)   // Read timeout
            .build();

    // Private helper method to execute GraphQL requests and return the raw JSON string
    // This is still useful internally for error checking before deserialization
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

            // Always attempt to parse to JsonObject for error checking, even if not successful HTTP
            JsonObject jsonResponse;
            try {
                jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                // If the response is not valid JSON, it's an API issue or network problem
                throw new ExceptionAniListApi("Invalid JSON response from AniList API: " + responseBody, e);
            }


            // Check for GraphQL errors first (even if HTTP status is 200)
            if (jsonResponse.has("errors") && jsonResponse.getAsJsonArray("errors").size() > 0) {
                JsonArray errors = jsonResponse.getAsJsonArray("errors");
                throw new ExceptionAniListApi("AniList API returned GraphQL errors: " + errors.toString());
            }

            // Check for HTTP errors
            if (!response.isSuccessful()) {
                throw new ExceptionAniListApi("Unexpected HTTP code " + response.code() + ": " + responseBody,
                        response.code(), responseBody);
            }

            return responseBody; // Return the raw body for further processing
        } catch (Exception e) {
            // Catch IOExceptions or other runtime exceptions during the call
            throw new ExceptionAniListApi("Failed to execute GraphQL request: " + e.getMessage(), e);
        }
    }

    public static AnimeModel getAnimeById(int animeId) throws ExceptionAniListApi {
        String query = "query ($id: Int) {\n" +
                "  Media(id: $id, type: ANIME) {\n" +
                "    id\n" +
                "    title {\n" +
                "      romaji\n" +
                "      english\n" +
                "      native\n" +
                "    }\n" +
                "    description\n" +
                "    coverImage {\n" +
                "      medium\n" +
                "    }\n" +
                "    episodes\n" +
                "    duration\n" +
                "    genres\n" + // Genres is an array of strings
                "    countryOfOrigin\n" +
                "    startDate {\n" +
                "      year\n" +
                "      month\n" +
                "      day\n" +
                "    }\n" +
                "    endDate {\n" +
                "      year\n" +
                "      month\n" +
                "      day\n" +
                "    }\n" +
                "    averageScore\n" +
                "    meanScore\n" +
                "    status\n" +
                "    nextAiringEpisode {\n" +
                "      episode\n" +
                "      airingAt\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JsonObject variables = new JsonObject();
        variables.addProperty("id", animeId);

        String jsonResponse = executeGraphQLRequest(query, variables);

        // Parse the JSON response to extract the 'Media' object
        JsonObject rootJson = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject mediaJson = rootJson.getAsJsonObject("data").getAsJsonObject("Media");

        if (mediaJson == null) {
            throw new ExceptionAniListApi("No anime found with ID: " + animeId);
        }

        // Deserialize the 'Media' JSON object directly into the Anime model
        AnimeModel anime = gson.fromJson(mediaJson, AnimeModel.class);

        // AniList descriptions can contain HTML. Strip HTML tags if desired.
        // This is a common requirement, so adding it here for convenience.
        if (anime.getDescription() != null) {
            anime.setDescription(stripHtmlTags(anime.getDescription()));
        }

        return anime;
    }

    public static List<AnimeModel> searchAnime(String searchString) throws ExceptionAniListApi {
        String query = "query ($search: String) {\n" +
                "  Page(perPage: 10) {\n" +
                "    media(search: $search, type: ANIME) {\n" +
                "      id\n" +
                "      title {\n" +
                "        romaji\n" +
                "        english\n" +
                "        native\n" +
                "      }\n" +
                "      coverImage {\n" +
                "        medium\n" +
                "      }\n" +
                "      description\n" +
                "      episodes\n" +
                "      duration\n" +
                "      genres\n" +
                "      countryOfOrigin\n" +
                "      startDate {\n" +
                "        year\n" +
                "        month\n" +
                "        day\n" +
                "      }\n" +
                "      endDate {\n" +
                "        year\n" +
                "        month\n" +
                "        day\n" +
                "      }\n" +
                "      averageScore\n" +
                "      meanScore\n" +
                "      status\n" +
                "      nextAiringEpisode {\n" +
                "        episode\n" +
                "        airingAt\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JsonObject variables = new JsonObject();
        variables.addProperty("search", searchString);

        String jsonResponse = executeGraphQLRequest(query, variables);

        // Parse the JSON response to extract the 'media' array within 'Page'
        JsonObject rootJson = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject pageJson = rootJson.getAsJsonObject("data").getAsJsonObject("Page");
        JsonArray mediaArrayJson = pageJson.getAsJsonArray("media");

        List<AnimeModel> animeList = new ArrayList<>();
        if (mediaArrayJson != null) {
            for (int i = 0; i < mediaArrayJson.size(); i++) {
                JsonObject animeJson = mediaArrayJson.get(i).getAsJsonObject();
                AnimeModel anime = gson.fromJson(animeJson, AnimeModel.class);

                // Strip HTML tags from description if present
                if (anime.getDescription() != null) {
                    anime.setDescription(stripHtmlTags(anime.getDescription()));
                }
                animeList.add(anime);
            }
        }
        return animeList;
    }

    // Simple HTML tag stripping utility
    // For more robust HTML parsing, consider using Jsoup library
    private static String stripHtmlTags(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        // Remove common HTML tags like <p>, <br>, <i>, <b>, <u>, <a>
        String stripped = html.replaceAll("<[^>]*>", "");
        // Decode HTML entities like &quot; &amp; &lt; &gt; &#39; &#x2F;
        stripped = stripped.replace("&quot;", "\"");
        stripped = stripped.replace("&amp;", "&");
        stripped = stripped.replace("&lt;", "<");
        stripped = stripped.replace("&gt;", ">");
        stripped = stripped.replace("&#39;", "'");
        stripped = stripped.replace("&#x2F;", "/");
        stripped = stripped.replace("&#34;", "\""); // Common for double quotes
        stripped = stripped.replace("&#x27;", "'"); // Common for single quotes
        stripped = stripped.replace("&#x2B;", "+"); // Plus sign
        stripped = stripped.replace("&apos;", "'"); // Apostrophe (HTML5)
        stripped = stripped.replace("&nbsp;", " "); // Non-breaking space
        // Replace multiple newlines with a single one for cleaner output
        stripped = stripped.replaceAll("(?m)^[\\s&&[^\\n]]+|[\\s&&[^\\n]]+$", ""); // Trim whitespace lines
        stripped = stripped.replaceAll("\\n\\n+", "\n"); // Reduce multiple newlines
        return stripped.trim(); // Trim leading/trailing whitespace
    }
}