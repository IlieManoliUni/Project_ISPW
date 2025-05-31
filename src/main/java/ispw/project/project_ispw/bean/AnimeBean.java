package ispw.project.project_ispw.bean;

import java.io.Serializable;

public class AnimeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idAnimeTmdb;
    private String title;
    private Integer episodes;
    private Integer duration;


    public AnimeBean() {
        // Default constructor
    }

    public AnimeBean(int idAnimeTmdb, Integer duration, Integer episodes, String title) {
        this.idAnimeTmdb = idAnimeTmdb;
        this.duration = duration;
        this.episodes = episodes;
        this.title = title;
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

}