package ispw.project.project_ispw.bean;

import java.io.Serializable;
import java.util.Objects;

public class TvSeriesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idTvSeriesTmdb;
    private int episodeRuntime;
    private int numberOfEpisodes;
    private String name;

    public TvSeriesBean() {
        // Default constructor
    }

    public TvSeriesBean(int episodeRuntime, int idTvSeriesTmdb, int numberOfEpisodes, String name) {
        this.episodeRuntime = episodeRuntime;
        this.idTvSeriesTmdb = idTvSeriesTmdb;
        this.numberOfEpisodes = numberOfEpisodes;
        this.name = name;
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

    // For the InMemory Operations
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TvSeriesBean that = (TvSeriesBean) o;
        return idTvSeriesTmdb == that.idTvSeriesTmdb;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTvSeriesTmdb);
    }

    @Override
    public String toString() {
        return "TvSeriesBean{" +
                "idTvSeriesTmdb=" + idTvSeriesTmdb +
                ", episodeRuntime=" + episodeRuntime +
                ", numberOfEpisodes=" + numberOfEpisodes +
                ", name='" + name + '\'' +
                '}';
    }
}