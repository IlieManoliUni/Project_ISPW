package ispw.project.project_ispw.dao;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.List;

public interface ListAnime {
    void addAnimeToList(ListBean list, AnimeBean anime) throws ExceptionDao;

    void removeAnimeFromList(ListBean list, AnimeBean anime) throws ExceptionDao;

    List<AnimeBean> getAllAnimeInList(ListBean list) throws ExceptionDao;

    void removeAllAnimesFromList(ListBean list) throws ExceptionDao;
}