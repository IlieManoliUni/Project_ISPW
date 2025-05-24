package ispw.project.project_ispw.model; // This package is for API-specific models

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents a single TV series object as returned by The Movie Database (TMDb) API.
 * This class is designed to be deserialized directly from TMDb's JSON responses
 * using a library like Gson. It includes a comprehensive set of common fields
 * found in TMDb's 'tv series details' endpoint.
 */
public class TvSeriesModel {
    private int id;

    @SerializedName("name")
    private String name; // The primary name of the TV series

    @SerializedName("original_name")
    private String originalName; // The original name of the TV series

    @SerializedName("original_language")
    private String originalLanguage;

    private String overview; // A brief summary of the TV series

    @SerializedName("poster_path")
    private String posterPath; // Relative path to the TV series poster image

    @SerializedName("backdrop_path")
    private String backdropPath; // Relative path to the TV series backdrop image

    @SerializedName("first_air_date")
    private String firstAirDate; // Date of first air (e.g., "YYYY-MM-DD")

    @SerializedName("last_air_date")
    private String lastAirDate; // Date of last air (e.g., "YYYY-MM-DD")

    @SerializedName("vote_average")
    private double voteAverage; // Average rating

    @SerializedName("vote_count")
    private int voteCount; // Number of votes

    private double popularity;

    private String status; // e.g., "Returning Series", "Ended", "Canceled"

    private String tagline; // A short tagline for the TV series

    @SerializedName("number_of_seasons")
    private int numberOfSeasons;

    @SerializedName("number_of_episodes")
    private int numberOfEpisodes;

    @SerializedName("episode_run_time")
    private List<Integer> episodeRunTime; // List of episode runtimes (can be multiple if inconsistent)

    private List<String> origins; // Countries of origin

    @SerializedName("genres")
    private List<Genre> genres; // List of nested Genre objects

    @SerializedName("networks")
    private List<Network> networks; // List of networks that aired the series

    @SerializedName("production_companies")
    private List<ProductionCompany> productionCompanies; // List of production companies

    @SerializedName("spoken_languages")
    private List<SpokenLanguage> spokenLanguages; // List of nested SpokenLanguage objects

    @SerializedName("created_by")
    private List<Creator> createdBy; // List of creators

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOriginalName() {
        return originalName;
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

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
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

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public int getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public List<Integer> getEpisodeRunTime() {
        return episodeRunTime;
    }

    public List<String> getOrigins() {
        return origins;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public List<Network> getNetworks() {
        return networks;
    }

    public List<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }

    public List<SpokenLanguage> getSpokenLanguages() {
        return spokenLanguages;
    }

    public List<Creator> getCreatedBy() {
        return createdBy;
    }

    // --- Setters (Often not strictly necessary if deserialized directly, but useful for mapping/testing) ---
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
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

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
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

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public void setNumberOfEpisodes(int numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public void setEpisodeRunTime(List<Integer> episodeRunTime) {
        this.episodeRunTime = episodeRunTime;
    }

    public void setOrigins(List<String> origins) {
        this.origins = origins;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setNetworks(List<Network> networks) {
        this.networks = networks;
    }

    public void setProductionCompanies(List<ProductionCompany> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public void setSpokenLanguages(List<SpokenLanguage> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public void setCreatedBy(List<Creator> createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "TvSeries{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", originalName='" + originalName + '\'' +
                ", overview='" + (overview != null && overview.length() > 50 ? overview.substring(0, 50) + "..." : overview) + '\'' +
                ", firstAirDate='" + firstAirDate + '\'' +
                ", numberOfSeasons=" + numberOfSeasons +
                ", numberOfEpisodes=" + numberOfEpisodes +
                ", voteAverage=" + voteAverage +
                '}';
    }

    // --- Nested Classes for Complex JSON Objects ---

    /**
     * Represents a genre object as returned by TMDb API.
     * (Reusable from Movie class if package is structured accordingly, or duplicate here)
     */
    public static class Genre {
        private int id;
        private String name;

        public int getId() { return id; }
        public String getName() { return name; }
        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }

        @Override
        public String toString() { return "Genre{" + "id=" + id + ", name='" + name + '\'' + '}'; }
    }

    /**
     * Represents a network object as returned by TMDb API for TV series.
     */
    public static class Network {
        private int id;
        @SerializedName("logo_path")
        private String logoPath;
        private String name;
        @SerializedName("origin_country")
        private String originCountry;

        public int getId() { return id; }
        public String getLogoPath() { return logoPath; }
        public String getName() { return name; }
        public String getOriginCountry() { return originCountry; }
        public void setId(int id) { this.id = id; }
        public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
        public void setName(String name) { this.name = name; }
        public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

        @Override
        public String toString() { return "Network{" + "id=" + id + ", name='" + name + '\'' + '}'; }
    }

    /**
     * Represents a production company object as returned by TMDb API.
     * (Reusable from Movie class if package is structured accordingly, or duplicate here)
     */
    public static class ProductionCompany {
        private int id;
        @SerializedName("logo_path")
        private String logoPath;
        private String name;
        @SerializedName("origin_country")
        private String originCountry;

        public int getId() { return id; }
        public String getLogoPath() { return logoPath; }
        public String getName() { return name; }
        public String getOriginCountry() { return originCountry; }
        public void setId(int id) { this.id = id; }
        public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
        public void setName(String name) { this.name = name; }
        public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

        @Override
        public String toString() { return "ProductionCompany{" + "id=" + id + ", name='" + name + '\'' + '}'; }
    }

    /**
     * Represents a spoken language object as returned by TMDb API.
     * (Reusable from Movie class if package is structured accordingly, or duplicate here)
     */
    public static class SpokenLanguage {
        @SerializedName("english_name")
        private String englishName;
        @SerializedName("iso_639_1")
        private String iso6391; // e.g., "en", "es"
        private String name;

        public String getEnglishName() { return englishName; }
        public String getIso6391() { return iso6391; }
        public String getName() { return name; }
        public void setEnglishName(String englishName) { this.englishName = englishName; }
        public void setIso6391(String iso6391) { this.iso6391 = iso6391; }
        public void setName(String name) { this.name = name; }

        @Override
        public String toString() { return "SpokenLanguage{" + "iso6391='" + iso6391 + '\'' + ", name='" + name + '\'' + '}'; }
    }

    /**
     * Represents a creator (e.g., executive producer) object as returned by TMDb API.
     */
    public static class Creator {
        private int id;
        @SerializedName("credit_id")
        private String creditId;
        private String name;
        private int gender; // 1 for female, 2 for male, 0 for not specified
        @SerializedName("profile_path")
        private String profilePath;

        public int getId() { return id; }
        public String getCreditId() { return creditId; }
        public String getName() { return name; }
        public int getGender() { return gender; }
        public String getProfilePath() { return profilePath; }
        public void setId(int id) { this.id = id; }
        public void setCreditId(String creditId) { this.creditId = creditId; }
        public void setName(String name) { this.name = name; }
        public void setGender(int gender) { this.gender = gender; }
        public void setProfilePath(String profilePath) { this.profilePath = profilePath; }

        @Override
        public String toString() { return "Creator{" + "id=" + id + ", name='" + name + '\'' + '}'; }
    }
}