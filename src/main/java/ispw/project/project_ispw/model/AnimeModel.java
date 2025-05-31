package ispw.project.project_ispw.model;

import java.time.LocalDate;
import java.util.List;

public class AnimeModel {
    private int id;
    private Title title;
    private String description;
    private CoverImage coverImage;
    private Integer episodes;
    private Integer duration;
    private String countryOfOrigin;
    private FuzzyDate startDate;
    private FuzzyDate endDate;
    private Integer averageScore;
    private Integer meanScore;
    private String status;
    private AiringSchedule nextAiringEpisode;
    private List<String> genres;

    public static class Title {
        private String romaji;
        private String english;
        private String nativeTitle;

        public Title() {
            // Empty constructor
        }

        public String getRomaji() { return romaji; }
        public void setRomaji(String romaji) { this.romaji = romaji; }
        public String getEnglish() { return english; }
        public void setEnglish(String english) { this.english = english; }
        public String getNativeTitle() { return nativeTitle; } // Getter for native title
        public void setNativeTitle(String nativeTitle) { this.nativeTitle = nativeTitle; } // Setter for native title

        @Override
        public String toString() {
            return "Title{" +
                    "romaji='" + romaji + '\'' +
                    ", english='" + english + '\'' +
                    '}';
        }
    }

    public static class FuzzyDate {
        private Integer year;
        private Integer month;
        private Integer day;

        public FuzzyDate() {
            // Empty constructor
        }

        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
        public Integer getMonth() { return month; }
        public void setMonth(Integer month) { this.month = month; }
        public Integer getDay() { return day; }
        public void setDay(Integer day) { this.day = day; }

        public LocalDate toLocalDate() {
            if (year != null && month != null && day != null) {
                return LocalDate.of(year, month, day);
            }
            return null;
        }

        @Override
        public String toString() {
            return "FuzzyDate{" +
                    "year=" + year +
                    ", month=" + month +
                    ", day=" + day +
                    '}';
        }
    }

    public static class CoverImage {
        private String medium; // Only need 'medium' if that's all you're querying

        public CoverImage() {
            // Empty constructor
        }

        public String getMedium() { return medium; }
        public void setMedium(String medium) { this.medium = medium; }

        @Override
        public String toString() {
            return "CoverImage{" +
                    "medium='" + medium + '\'' +
                    '}';
        }
    }

    public static class AiringSchedule {
        private Integer episode;
        private Long airingAt;

        public AiringSchedule() {
            // Empty constructor
        }

        public Integer getEpisode() { return episode; }
        public void setEpisode(Integer episode) { this.episode = episode; }
        public Long getAiringAt() { return airingAt; }
        public void setAiringAt(Long airingAt) { this.airingAt = airingAt; }

        @Override
        public String toString() {
            return "AiringSchedule{" +
                    "episode=" + episode +
                    ", airingAt=" + airingAt +
                    '}';
        }
    }


    public AnimeModel() {
        // Empty constructor
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Title getTitle() { return title; }
    public void setTitle(Title title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CoverImage getCoverImage() { return coverImage; }
    public void setCoverImage(CoverImage coverImage) { this.coverImage = coverImage; }

    public Integer getEpisodes() { return episodes; }
    public void setEpisodes(Integer episodes) { this.episodes = episodes; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public String getCountryOfOrigin() { return countryOfOrigin; }
    public void setCountryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; }

    public FuzzyDate getStartDate() { return startDate; }
    public void setStartDate(FuzzyDate startDate) { this.startDate = startDate; }

    public FuzzyDate getEndDate() { return endDate; }
    public void setEndDate(FuzzyDate endDate) { this.endDate = endDate; }

    public Integer getAverageScore() { return averageScore; }
    public void setAverageScore(Integer averageScore) { this.averageScore = averageScore; }

    public Integer getMeanScore() { return meanScore; }
    public void setMeanScore(Integer meanScore) { this.meanScore = meanScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public AiringSchedule getNextAiringEpisode() { return nextAiringEpisode; }
    public void setNextAiringEpisode(AiringSchedule nextAiringEpisode) { this.nextAiringEpisode = nextAiringEpisode; }

    @Override
    public String toString() {
        return "Anime{" +
                "id=" + id +
                ", title=" + title +
                ", description='" + description + '\'' +
                ", coverImage=" + coverImage +
                ", episodes=" + episodes +
                ", duration=" + duration +
                ", genres=" + genres +
                ", countryOfOrigin='" + countryOfOrigin + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", averageScore=" + averageScore +
                ", meanScore=" + meanScore +
                ", status='" + status + '\'' +
                ", nextAiringEpisode=" + nextAiringEpisode +
                '}';
    }
}