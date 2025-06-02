package ispw.project.project_ispw.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TvSeriesModel {
    private int id;

    private String name;

    @SerializedName("original_name")
    private String originalName;

    @SerializedName("original_language")
    private String originalLanguage;

    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("first_air_date")
    private String firstAirDate;

    @SerializedName("last_air_date")
    private String lastAirDate;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("vote_count")
    private int voteCount;

    private double popularity;

    private String status;

    private String tagline;

    @SerializedName("number_of_seasons")
    private int numberOfSeasons;

    @SerializedName("number_of_episodes")
    private int numberOfEpisodes;

    @SerializedName("episode_run_time")
    private List<Integer> episodeRunTime;

    @SerializedName("origin_country")
    private List<String> origins;

    private List<Genre> genres;

    private List<Network> networks;

    @SerializedName("production_companies")
    private List<ProductionCompany> productionCompanies;

    @SerializedName("spoken_languages") // <<< ADDED
    private List<SpokenLanguage> spokenLanguages;

    @SerializedName("created_by")
    private List<Creator> createdBy;

    @SerializedName("in_production") // <<< ADDED
    private boolean inProduction;

    private static final String NAME_FIELD_PREFIX = ", name='";

    public TvSeriesModel() {
        //Empty constructor needed for Gson
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getOriginalName() { return originalName; }
    public String getOriginalLanguage() { return originalLanguage; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public String getBackdropPath() { return backdropPath; }
    public String getFirstAirDate() { return firstAirDate; }
    public String getLastAirDate() { return lastAirDate; }
    public double getVoteAverage() { return voteAverage; }
    public int getVoteCount() { return voteCount; }
    public double getPopularity() { return popularity; }
    public String getStatus() { return status; }
    public String getTagline() { return tagline; }
    public int getNumberOfSeasons() { return numberOfSeasons; }
    public int getNumberOfEpisodes() { return numberOfEpisodes; }
    public List<Integer> getEpisodeRunTime() { return episodeRunTime; }
    public List<String> getOrigins() { return origins; }
    public List<Genre> getGenres() { return genres; }
    public List<Network> getNetworks() { return networks; }
    public List<ProductionCompany> getProductionCompanies() { return productionCompanies; } // <<< UPDATED RETURN TYPE
    public List<SpokenLanguage> getSpokenLanguages() { return spokenLanguages; }
    public List<Creator> getCreatedBy() { return createdBy; } // <<< UPDATED RETURN TYPE
    public boolean getInProduction() { return inProduction; }


    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }
    public void setOverview(String overview) { this.overview = overview; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }
    public void setFirstAirDate(String firstAirDate) { this.firstAirDate = firstAirDate; }
    public void setLastAirDate(String lastAirDate) { this.lastAirDate = lastAirDate; }
    public void setVoteAverage(double voteAverage) { this.voteAverage = voteAverage; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
    public void setPopularity(double popularity) { this.popularity = popularity; }
    public void setStatus(String status) { this.status = status; }
    public void setTagline(String tagline) { this.tagline = tagline; }
    public void setNumberOfSeasons(int numberOfSeasons) { this.numberOfSeasons = numberOfSeasons; }
    public void setNumberOfEpisodes(int numberOfEpisodes) { this.numberOfEpisodes = numberOfEpisodes; }
    public void setEpisodeRunTime(List<Integer> episodeRunTime) { this.episodeRunTime = episodeRunTime; }
    public void setOrigins(List<String> origins) { this.origins = origins; }
    public void setGenres(List<Genre> genres) { this.genres = genres; }
    public void setNetworks(List<Network> networks) { this.networks = networks; }
    public void setProductionCompanies(List<ProductionCompany> productionCompanies) { this.productionCompanies = productionCompanies; } // <<< UPDATED PARAMETER TYPE
    public void setSpokenLanguages(List<SpokenLanguage> spokenLanguages) { this.spokenLanguages = spokenLanguages; }
    public void setCreatedBy(List<Creator> createdBy) { this.createdBy = createdBy; } // <<< UPDATED PARAMETER TYPE
    public void setInProduction(boolean inProduction) { this.inProduction = inProduction; }

    @Override
    public String toString() {
        return "TvSeries{" +
                "id=" + id +
                NAME_FIELD_PREFIX + name + '\'' +
                NAME_FIELD_PREFIX + originalName + '\'' +
                ", overview='" + (overview != null && overview.length() > 50 ? overview.substring(0, 50) + "..." : overview) + '\'' +
                ", firstAirDate='" + firstAirDate + '\'' +
                ", numberOfSeasons=" + numberOfSeasons +
                ", numberOfEpisodes=" + numberOfEpisodes +
                ", voteAverage=" + voteAverage +
                '}';
    }

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

    public static class Network {
        private int id;
        @SerializedName("logo_path")
        private String logoPath;
        private String name;
        @SerializedName("origin_country")
        private String originCountry;

        public Network() {
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
        public String toString() { return "Network{" + "id=" + id + NAME_FIELD_PREFIX + name + '\'' + '}'; }
    }

    public static class ProductionCompany {
        private int id;
        @SerializedName("logo_path")
        private String logoPath;
        private String name;
        @SerializedName("origin_country")
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

    public static class SpokenLanguage {
        @SerializedName("english_name")
        private String englishName;
        @SerializedName("iso_639_1")
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

    public static class Creator {
        private int id;
        @SerializedName("credit_id")
        private String creditId;
        private String name;
        private int gender;
        @SerializedName("profile_path")
        private String profilePath;

        public Creator() {
            //Default constructor
        }
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
        public String toString() { return "Creator{" + "id=" + id + NAME_FIELD_PREFIX + name + '\'' + '}'; }
    }
}