package ispw.project.project_ispw.bean;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class TvSeriesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idTvSeriesTmdb;
    private int episodeRuntime;
    private int numberOfEpisodes;
    private String name;
    private String overview;
    private String originalName;
    private String originalLanguage;
    private String firstAirDate;
    private String lastAirDate;
    private int numberOfSeasons;
    private boolean inProduction;
    private String status;
    private double voteAverage;
    private List<String> createdBy;
    private List<String> productionCompanies;
    private String posterPath;

    public TvSeriesBean() {
        // Default constructor
    }

    public TvSeriesBean(int episodeRuntime, int idTvSeriesTmdb, int numberOfEpisodes, String name) {
        this.episodeRuntime = episodeRuntime;
        this.idTvSeriesTmdb = idTvSeriesTmdb;
        this.numberOfEpisodes = numberOfEpisodes;
        this.name = name;
        // Initialize other fields to default values
        this.overview = null;
        this.originalName = null;
        this.originalLanguage = null;
        this.firstAirDate = null;
        this.lastAirDate = null;
        this.numberOfSeasons = 0;
        this.inProduction = false;
        this.status = null;
        this.voteAverage = 0.0;
        this.createdBy = Collections.emptyList();
        this.productionCompanies = Collections.emptyList();
        this.posterPath = null;
    }

    public int getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(int numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public int getEpisodeRuntime() {
        return episodeRuntime;
    }

    public void setEpisodeRuntime(int episodeRuntime) {
        this.episodeRuntime = episodeRuntime;
    }

    public int getIdTvSeriesTmdb() {
        return idTvSeriesTmdb;
    }

    public void setIdTvSeriesTmdb(int idTvSeriesTmdb) {
        this.idTvSeriesTmdb = idTvSeriesTmdb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public boolean isInProduction() {
        return inProduction;
    }

    public void setInProduction(boolean inProduction) {
        this.inProduction = inProduction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public List<String> getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(List<String> createdBy) {
        this.createdBy = createdBy;
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