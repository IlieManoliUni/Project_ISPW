package ispw.project.project_ispw.model; // This package is for API-specific models

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents a single movie object as returned by The Movie Database (TMDb) API.
 * This class is designed to be deserialized directly from TMDb's JSON responses
 * using a library like Gson. It includes a comprehensive set of common fields
 * found in TMDb's 'movie details' endpoint.
 */
public class MovieModel {
    private int id;
    private String title;

    @SerializedName("original_title")
    private String originalTitle; // Often the title in its original language

    @SerializedName("original_language")
    private String originalLanguage;

    private String overview; // A brief summary of the movie

    @SerializedName("poster_path")
    private String posterPath; // Relative path to the movie poster image

    @SerializedName("backdrop_path")
    private String backdropPath; // Relative path to the movie backdrop image

    @SerializedName("release_date")
    private String releaseDate; // Date of release (e.g., "YYYY-MM-DD")

    @SerializedName("vote_average")
    private double voteAverage; // Average rating

    @SerializedName("vote_count")
    private int voteCount; // Number of votes

    private int runtime; // Duration of the movie in minutes

    private double popularity;

    private String status; // e.g., "Released", "Rumored", "In Production"

    private String tagline; // A short tagline for the movie

    @SerializedName("imdb_id")
    private String imdbId; // IMDb ID for the movie

    // Added budget and revenue fields
    private long budget;
    private long revenue;

    private List<Genre> genres; // List of nested Genre objects

    @SerializedName("production_companies")
    private List<ProductionCompany> productionCompanies; // List of nested ProductionCompany objects

    @SerializedName("production_countries")
    private List<ProductionCountry> productionCountries; // List of nested ProductionCountry objects

    @SerializedName("spoken_languages")
    private List<SpokenLanguage> spokenLanguages; // List of nested SpokenLanguage objects

    // --- Constant for common string literal in toString methods ---
    private static final String NAME_FIELD_PREFIX = ", name='";

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public int getRuntime() {
        return runtime;
    }

    public double getPopularity() {
        return popularity;
    }

    public String getStatus() {
        return status;
    }

    public String getTagline() {
        return tagline;
    }

    public String getImdbId() {
        return imdbId;
    }

    // Added getters for budget and revenue
    public long getBudget() {
        return budget;
    }

    public long getRevenue() {
        return revenue;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public List<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }

    public List<ProductionCountry> getProductionCountries() {
        return productionCountries;
    }

    public List<SpokenLanguage> getSpokenLanguages() {
        return spokenLanguages;
    }

    // --- Setters (Often not strictly necessary if deserialized directly, but useful for mapping/testing) ---
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    // Added setters for budget and revenue
    public void setBudget(long budget) {
        this.budget = budget;
    }

    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setProductionCompanies(List<ProductionCompany> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public void setProductionCountries(List<ProductionCountry> productionCountries) {
        this.productionCountries = productionCountries;
    }

    public void setSpokenLanguages(List<SpokenLanguage> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    // An empty constructor is typically needed for deserialization by libraries like Gson.
    public MovieModel() {
        // This empty constructor is required by Gson for deserialization.
        // It allows Gson to create an instance of MovieModel before populating its fields from JSON.
    }

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
                ", budget=" + budget + // Added to toString
                ", revenue=" + revenue + // Added to toString
                '}';
    }

    // --- Nested Classes for Complex JSON Objects ---

    /**
     * Represents a genre object as returned by TMDb API.
     */
    public static class Genre {
        private int id;
        private String name;

        // An empty constructor is typically needed for deserialization by libraries like Gson.
        public Genre() {
            // This empty constructor is required by Gson for deserialization.
            // It allows Gson to create an instance of Genre before populating its fields.
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }

        @Override
        public String toString() { return "Genre{" + "id=" + id + NAME_FIELD_PREFIX + name + '\'' + '}'; }
    }

    /**
     * Represents a production company object as returned by TMDb API.
     */
    public static class ProductionCompany {
        private int id;
        @SerializedName("logo_path")
        private String logoPath;
        private String name;
        @SerializedName("origin_country")
        private String originCountry;

        // An empty constructor is typically needed for deserialization by libraries like Gson.
        public ProductionCompany() {
            // This empty constructor is required by Gson for deserialization.
            // It allows Gson to create an instance of ProductionCompany before populating its fields.
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

    /**
     * Represents a production country object as returned by TMDb API.
     */
    public static class ProductionCountry {
        @SerializedName("iso_3166_1")
        private String iso31661; // e.g., "US", "GB"
        private String name;

        // An empty constructor is typically needed for deserialization by libraries like Gson.
        public ProductionCountry() {
            // This empty constructor is required by Gson for deserialization.
            // It allows Gson to create an instance of ProductionCountry before populating its fields.
        }

        public String getIso31661() { return iso31661; }
        public String getName() { return name; }
        public void setIso31661(String iso31661) { this.iso31661 = iso31661; }
        public void setName(String name) { this.name = name; }

        @Override
        public String toString() { return "ProductionCountry{" + "iso31661='" + iso31661 + '\'' + NAME_FIELD_PREFIX + name + '\'' + '}'; }
    }

    /**
     * Represents a spoken language object as returned by TMDb API.
     */
    public static class SpokenLanguage {
        @SerializedName("english_name")
        private String englishName;
        @SerializedName("iso_639_1")
        private String iso6391; // e.g., "en", "es"
        private String name;

        // An empty constructor is typically needed for deserialization by libraries like Gson.
        public SpokenLanguage() {
            // This empty constructor is required by Gson for deserialization.
            // It allows Gson to create an instance of SpokenLanguage before populating its fields.
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