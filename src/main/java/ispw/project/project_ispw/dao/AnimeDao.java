package ispw.project.project_ispw.dao;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.List;

public interface AnimeDao {

    AnimeBean retrieveById(int id) throws ExceptionDao;

    void saveAnime(AnimeBean anime) throws ExceptionDao;

    List<AnimeBean> retrieveAllAnime() throws ExceptionDao;
}

