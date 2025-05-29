package ispw.project.project_ispw.dao;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;

import java.util.List;

public interface ListAnime {
    void addAnimeToList(ListBean list, AnimeBean anime) throws Exception;
    void removeAnimeFromList(ListBean list, AnimeBean anime) throws Exception;
    List<AnimeBean> getAllAnimeInList(ListBean list) throws Exception;
    // NEW METHOD
    void removeAllAnimesFromList(ListBean list) throws Exception; // Add this line
}