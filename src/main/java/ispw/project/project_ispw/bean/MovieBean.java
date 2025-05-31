package ispw.project.project_ispw.bean;

import java.io.Serializable;

public class MovieBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idMovieTmdb;
    private String title;
    private int runtime;

    public MovieBean() {
        // Default constructor
    }

    public MovieBean(int idMovieTmdb, int runtime, String title) {
        this.idMovieTmdb = idMovieTmdb;
        this.runtime = runtime;
        this.title = title;
    }

    public int getIdMovieTmdb() {
        return idMovieTmdb;
    }

    public void setIdMovieTmdb(int idMovieTmdb) {
        this.idMovieTmdb = idMovieTmdb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

}