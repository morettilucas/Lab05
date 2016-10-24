package dam.isi.frsf.utn.edu.ar.lab05.modelo;

/**
 * Created by mdominguez on 06/10/16.
 */
public class Prioridad {

    private Integer id;
    private String prioridad;

    public Prioridad(){

    }

    public Prioridad(Integer id, String prioridad) {
        this.id = id;
        this.prioridad = prioridad;
    }

    public Integer getId() {
        return id;
    }

    public Prioridad setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public Prioridad setPrioridad(String prioridad) {
        this.prioridad = prioridad;
        return this;
    }
}
