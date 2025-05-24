package ispw.project.project_ispw.dao;

import ispw.project.project_ispw.bean.AnimeBean;

import java.util.List;

public interface AnimeDao {

    // Method to retrieve an Anime by its ID
    AnimeBean retrieveById(int id) throws Exception;

    // Method to save a new Anime
    void saveAnime(AnimeBean anime) throws Exception;

    // Method to retrieve all Anime
    List<AnimeBean> retrieveAllAnime() throws Exception;
}

