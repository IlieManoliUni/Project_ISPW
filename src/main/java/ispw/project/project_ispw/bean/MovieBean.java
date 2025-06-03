package ispw.project.project_ispw.bean;

import java.io.Serializable;
import java.util.Objects;

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

    // For the InMemory Operations
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieBean movieBean = (MovieBean) o;
        return idMovieTmdb == movieBean.idMovieTmdb; // Equality based solely on ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMovieTmdb); // Hash code based solely on ID
    }

    @Override
    public String toString() {
        return "MovieBean{" +
                "idMovieTmdb=" + idMovieTmdb +
                ", title='" + title + '\'' +
                ", runtime=" + runtime +
                '}';
    }
}