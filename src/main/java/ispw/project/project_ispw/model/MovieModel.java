package ispw.project.project_ispw.model;

import com.google.gson.annotations.SerializedName; // <<< ADD THIS IMPORT
import java.util.List;

public class MovieModel {
    private int id;
    private String title;

    @SerializedName("original_title") // <<< ADDED
    private String originalTitle;

    @SerializedName("original_language") // <<< ADDED
    private String originalLanguage;

    private String overview;

    @SerializedName("poster_path") // <<< ADDED
    private String posterPath;

    @SerializedName("backdrop_path") // <<< ADDED
    private String backdropPath;

    @SerializedName("release_date") // <<< ADDED
    private String releaseDate;

    @SerializedName("vote_average") // <<< ADDED
    private double voteAverage;

    @SerializedName("vote_count") // <<< ADDED
    private int voteCount;

    private int runtime; // TMDB uses "runtime" directly

    private double popularity; // TMDB uses "popularity" directly

    private String status; // TMDB uses "status" directly

    private String tagline; // TMDB uses "tagline" directly

    @SerializedName("imdb_id") // <<< ADDED
    private String imdbId;

    private long budget; // TMDB uses "budget" directly
    private long revenue; // TMDB uses "revenue" directly

    // <<< CHANGED TYPE FROM List<String> to List<Genre>
    private List<Genre> genres;

    // <<< CHANGED TYPE FROM List<String> to List<ProductionCompany> AND ADDED @SerializedName
    @SerializedName("production_companies")
    private List<ProductionCompany> productionCompanies;

    @SerializedName("production_countries") // <<< ADDED
    private List<ProductionCountry> productionCountries;

    @SerializedName("spoken_languages") // <<< ADDED
    private List<SpokenLanguage> spokenLanguages;

    private static final String NAME_FIELD_PREFIX = ", name='";

    public MovieModel() {
        // Empty constructor needed for Gson
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOriginalTitle() { return originalTitle; }
    public String getOriginalLanguage() { return originalLanguage; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public String getBackdropPath() { return backdropPath; }
    public String getReleaseDate() { return releaseDate; }
    public double getVoteAverage() { return voteAverage; }
    public int getVoteCount() { return voteCount; }
    public int getRuntime() { return runtime; }
    public double getPopularity() { return popularity; }
    public String getStatus() { return status; }
    public String getTagline() { return tagline; }
    public String getImdbId() { return imdbId; }
    public long getBudget() { return budget; }
    public long getRevenue() { return revenue; }
    public List<Genre> getGenres() { return genres; } // <<< UPDATED RETURN TYPE
    public List<ProductionCompany> getProductionCompanies() { return productionCompanies; } // <<< UPDATED RETURN TYPE
    public List<ProductionCountry> getProductionCountries() { return productionCountries; }
    public List<SpokenLanguage> getSpokenLanguages() { return spokenLanguages; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }
    public void setOverview(String overview) { this.overview = overview; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public void setVoteAverage(double voteAverage) { this.voteAverage = voteAverage; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
    public void setRuntime(int runtime) { this.runtime = runtime; }
    public void setPopularity(double popularity) { this.popularity = popularity; }
    public void setStatus(String status) { this.status = status; }
    public void setTagline(String tagline) { this.tagline = tagline; }
    public void setImdbId(String imdbId) { this.imdbId = imdbId; }
    public void setBudget(long budget) { this.budget = budget; }
    public void setRevenue(long revenue) { this.revenue = revenue; }
    public void setGenres(List<Genre> genres) { this.genres = genres; } // <<< UPDATED PARAMETER TYPE
    public void setProductionCompanies(List<ProductionCompany> productionCompanies) { this.productionCompanies = productionCompanies; } // <<< UPDATED PARAMETER TYPE
    public void setProductionCountries(List<ProductionCountry> productionCountries) { this.productionCountries = productionCountries; }
    public void setSpokenLanguages(List<SpokenLanguage> spokenLanguages) { this.spokenLanguages = spokenLanguages; }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", overview='" + (overview != null && overview.length() > 50 ? overview.substring(0, 50) + "..." : overview) + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", runtime=" + runtime +
                ", voteAverage=" + voteAverage +
                ", budget=" + budget +
                ", revenue=" + revenue +
                ", genres=" + genres + // Including for better toString debugging
                ", productionCompanies=" + productionCompanies + // Including for better toString debugging
                '}';
    }

    // --- Nested Classes (Must be public static for Gson to work easily) ---

    public static class Genre {
        private int id;
        private String name;

        public Genre() {
            //Default constructor
        }
        public int getId() { return id; }
        public String getName() { return name; }
        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        @Override
        public String toString() { return "Genre{" + "id=" + id + NAME_FIELD_PREFIX + name + '\'' + '}'; }
    }

    public static class ProductionCompany {
        private int id;
        @SerializedName("logo_path") // <<< ADDED
        private String logoPath;
        private String name;
        @SerializedName("origin_country") // <<< ADDED
        private String originCountry;

        public ProductionCompany() {
            //Default constructor
        }
        public int getId() { return id; }
        public String getLogoPath() { return logoPath; }
        public String getName() { return name; }
        public String getOriginCountry() { return originCountry; }
        public void setId(int id) { this.id = id; }
        public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
        public void setName(String name) { this.name = name; }
        public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }
        @Override
        public String toString() { return "ProductionCompany{" + "id=" + id + NAME_FIELD_PREFIX + name + '\'' + '}'; }
    }

    public static class ProductionCountry {
        @SerializedName("iso_3166_1") // <<< ADDED
        private String iso31661;
        private String name;

        public ProductionCountry() {
            //Default constructor
        }
        public String getIso31661() { return iso31661; }
        public String getName() { return name; }
        public void setIso31661(String iso31661) { this.iso31661 = iso31661; }
        public void setName(String name) { this.name = name; }
        @Override
        public String toString() { return "ProductionCountry{" + "iso31661='" + iso31661 + '\'' + NAME_FIELD_PREFIX + name + '\'' + '}'; }
    }

    public static class SpokenLanguage {
        @SerializedName("english_name") // <<< ADDED
        private String englishName;
        @SerializedName("iso_639_1") // <<< ADDED
        private String iso6391;
        private String name;

        public SpokenLanguage() {
            //Default constructor
        }
        public String getEnglishName() { return englishName; }
        public String getIso6391() { return iso6391; }
        public String getName() { return name; }
        public void setEnglishName(String englishName) { this.englishName = englishName; }
        public void setIso6391(String iso6391) { this.iso6391 = iso6391; }
        public void setName(String name) { this.name = name; }
        @Override
        public String toString() { return "SpokenLanguage{" + "iso6391='" + iso6391 + '\'' + NAME_FIELD_PREFIX + name + '\'' + '}'; }
    }
}