package dam.isi.frsf.utn.edu.ar.lab05.modelo;

/**
 * Created by mdominguez on 06/10/16.
 */
public class Tarea {

    private String descripcion;

    private Integer id;
    private Integer horasEstimadas;
    private Integer minutosTrabajados;
    private Boolean finalizada;
    private Proyecto proyecto;
    private Prioridad prioridad;
    private Usuario responsable;

    public Tarea() {
    }

    public Tarea(Integer id, Integer horasEstimadas, Integer minutosTrabajados, Boolean finalizada, Proyecto proyecto, Prioridad prioridad, Usuario responsable) {
        this.id = id;
        this.horasEstimadas = horasEstimadas;
        this.minutosTrabajados = minutosTrabajados;
        this.finalizada = finalizada;
        this.proyecto = proyecto;
        this.prioridad = prioridad;
        this.responsable = responsable;
    }

    public Integer getId() {
        return id;
    }

    public Tarea setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getHorasEstimadas() {
        return horasEstimadas;
    }

    public Tarea setHorasEstimadas(Integer horasEstimadas) {
        this.horasEstimadas = horasEstimadas;
        return this;
    }

    public Integer getMinutosTrabajados() {
        return minutosTrabajados;
    }

    public Tarea setMinutosTrabajados(Integer minutosTrabajados) {
        this.minutosTrabajados = minutosTrabajados;
        return this;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Tarea setDescripcion(String descripcion) {
        this.descripcion = descripcion;
        return this;
    }
    public Boolean getFinalizada() {
        return finalizada;
    }

    public Tarea setFinalizada(Boolean finalizada) {
        this.finalizada = finalizada;
        return this;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public Tarea setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
        return this;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public Tarea setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
        return this;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public Tarea setResponsable(Usuario responsable) {
        this.responsable = responsable;
        return this;
    }
}
