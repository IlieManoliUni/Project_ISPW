package ispw.project.project_ispw.bean;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class AnimeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idAnimeTmdb;
    private String title;
    private String description;
    private String coverImageUrl;
    private Integer episodes;
    private Integer duration;
    private String countryOfOrigin;
    private String startDate;
    private String endDate;
    private Integer averageScore;
    private Integer meanScore;
    private String status;
    private String nextAiringEpisodeDetails;
    private List<String> genres;


    public AnimeBean() {
        // Default constructor
    }

    public AnimeBean(int idAnimeTmdb, Integer duration, Integer episodes, String title) {
        this.idAnimeTmdb = idAnimeTmdb;
        this.duration = duration;
        this.episodes = episodes;
        this.title = title;
        // Initialize other fields to default values
        this.description = null;
        this.coverImageUrl = null;
        this.countryOfOrigin = null;
        this.startDate = null;
        this.endDate = null;
        this.averageScore = null;
        this.meanScore = null;
        this.status = null;
        this.nextAiringEpisodeDetails = null;
        this.genres = Collections.emptyList();
    }

    public int getIdAnimeTmdb() {
        return idAnimeTmdb;
    }

    public void setIdAnimeTmdb(int idAnimeTmdb) {
        this.idAnimeTmdb = idAnimeTmdb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Integer getEpisodes() {
        return episodes;
    }

    public void setEpisodes(Integer episodes) {
        this.episodes = episodes;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Integer averageScore) {
        this.averageScore = averageScore;
    }

    public Integer getMeanScore() {
        return meanScore;
    }

    public void setMeanScore(Integer meanScore) {
        this.meanScore = meanScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNextAiringEpisodeDetails() {
        return nextAiringEpisodeDetails;
    }

    public void setNextAiringEpisodeDetails(String nextAiringEpisodeDetails) {
        this.nextAiringEpisodeDetails = nextAiringEpisodeDetails;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}