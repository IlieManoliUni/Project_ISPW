package ispw.project.project_ispw.model;

import java.time.LocalDate; // For handling dates more robustly

public class AnimeModel {
    private int id;
    private Title title; // Nested object for various title forms
    private String description;
    private CoverImage coverImage; // Nested object for cover image URL
    private Integer episodes; // Use Integer to allow for null if not yet determined
    private Integer duration; // In minutes per episode, Integer to allow for null
    private String countryOfOrigin;
    private FuzzyDate startDate; // Nested object for start date
    private FuzzyDate endDate;   // Nested object for end date
    private Integer averageScore; // Use Integer to allow for null
    private Integer meanScore;    // Use Integer to allow for null
    private String status; // e.g., RELEASING, FINISHED, NOT_YET_RELEASED, CANCELLED, HIATUS
    private AiringSchedule nextAiringEpisode; // Information about the next episode to air
    private String[] genres; // Array or List of genres

    // --- Nested Classes ---

    public static class Title {
        private String romaji;
        private String english;
        private String nativeTitle; // Corresponds to 'native' in your query

        public Title() {
            // This empty constructor is required by Gson for deserialization.
            // It allows Gson to create an instance of Title before populating its fields.
        }

        // Getters and Setters
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
                    ", nativeTitle='" + nativeTitle + '\'' +
                    '}';
        }
    }

    public static class CoverImage {
        private String medium; // Only 'medium' is requested in your query

        public CoverImage() {
            // This empty constructor is required by Gson for deserialization.
        }

        // Getter and Setter
        public String getMedium() { return medium; }
        public void setMedium(String medium) { this.medium = medium; }

        @Override
        public String toString() {
            return "CoverImage{" +
                    "medium='" + medium + '\'' +
                    '}';
        }
    }

    public static class FuzzyDate {
        private Integer year; // Integer to allow for null
        private Integer month;
        private Integer day;

        public FuzzyDate() {
            // This empty constructor is required by Gson for deserialization.
        }

        // Getters and Setters
        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
        public Integer getMonth() { return month; }
        public void setMonth(Integer month) { this.month = month; }
        public Integer getDay() { return day; }
        public void setDay(Integer day) { this.day = day; }

        // Helper method to convert to LocalDate if all parts are present
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

    public static class AiringSchedule {
        private Integer episode; // Integer to allow for null
        private Long airingAt; // Unix timestamp, use Long to allow for null

        public AiringSchedule() {
            // This empty constructor is required by Gson for deserialization.
        }

        // Getters and Setters
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

    // --- Main Anime Class Getters and Setters ---

    public AnimeModel() {
        // This empty constructor is required by Gson for deserialization.
        // It allows Gson to create an instance of AnimeModel before populating its fields from JSON.
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

    public String[] getGenres() { return genres; }
    public void setGenres(String[] genres) { this.genres = genres; }

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
                ", genres=" + java.util.Arrays.toString(genres) +
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