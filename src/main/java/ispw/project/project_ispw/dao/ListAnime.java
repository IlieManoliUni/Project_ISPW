package ispw.project.project_ispw.dao;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;

import java.util.List;

public interface ListAnime {

    // Add an Anime to a List
    void addAnimeToList(ListBean list, AnimeBean anime) throws Exception;

    // Remove an Anime from a List
    void removeAnimeFromList(ListBean list, AnimeBean anime) throws Exception;

    // Get all Anime in a List
    List<AnimeBean> getAllAnimesInList(ListBean list) throws Exception;
}
