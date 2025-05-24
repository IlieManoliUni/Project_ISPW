package ispw.project.project_ispw.bean;

import java.io.Serializable;
import java.util.Collections; // For Collections.emptyList()
import java.util.List;

public class MovieBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idMovieTmdb; // Unique ID for the movie (e.g., from TMDB)
    private String title;
    private String overview;
    private String originalTitle;
    private String originalLanguage;
    private String releaseDate;
    private int runtime; // in minutes
    private List<String> genres; // List of genre names
    private double voteAverage;
    private long budget;
    private long revenue;
    private List<String> productionCompanies; // List of production company names
    private String posterPath; // Path to the movie poster image

    public MovieBean() {
        // Default constructor
    }

    /**
     * Full constructor for MovieBean.
     */
    public MovieBean(int idMovieTmdb, String title, String overview,
                     String originalTitle, String originalLanguage, String releaseDate,
                     int runtime, List<String> genres, double voteAverage,
                     long budget, long revenue, List<String> productionCompanies,
                     String posterPath) {
        this.idMovieTmdb = idMovieTmdb;
        this.title = title;
        this.overview = overview;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
        this.genres = genres;
        this.voteAverage = voteAverage;
        this.budget = budget;
        this.revenue = revenue;
        this.productionCompanies = productionCompanies;
        this.posterPath = posterPath;
    }

    /**
     * New constructor for MovieBean with common essential fields (ID, Runtime, Title).
     * This constructor is likely intended for use cases where only these basic details are available,
     * such as when constructing a MovieBean from a simplified database record or CSV entry.
     * All other fields are initialized to default values.
     *
     * @param idMovieTmdb The unique ID of the movie (e.g., from TMDB).
     * @param runtime The runtime of the movie in minutes.
     * @param title The main title of the movie.
     */
    public MovieBean(int idMovieTmdb, int runtime, String title) {
        this.idMovieTmdb = idMovieTmdb;
        this.runtime = runtime;
        this.title = title;
        // Initialize other fields to default values
        this.overview = null;
        this.originalTitle = null;
        this.originalLanguage = null;
        this.releaseDate = null;
        this.genres = Collections.emptyList(); // Use an empty, immutable list
        this.voteAverage = 0.0;
        this.budget = 0;
        this.revenue = 0;
        this.productionCompanies = Collections.emptyList(); // Use an empty, immutable list
        this.posterPath = null;
    }


    // Getters and Setters for all fields

    public int getIdMovieTmdb() {
        return idMovieTmdb;
    }

    public void setIdMovieTmdb(int idMovieTmdb) {
        this.idMovieTmdb = idMovieTmdb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }

    public long getRevenue() {
        return revenue;
    }

    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }

    public List<String> getProductionCompanies() {
        return productionCompanies;
    }

    public void setProductionCompanies(List<String> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}