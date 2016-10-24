package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

/**
 * Created by mdominguez on 06/10/16.
 */
public class ProyectoDAO {

    private static final String _SQL_TAREAS_X_PROYECTO = "SELECT "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata._ID+" as "+ProyectoDBMetadata.TablaTareasMetadata._ID+
            ", "+ProyectoDBMetadata.TablaTareasMetadata.TAREA +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD +
            ", "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+"."+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD +" as "+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS+
            ", "+ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE +
            ", "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+"."+ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO +" as "+ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO_ALIAS+
            " FROM "+ProyectoDBMetadata.TABLA_PROYECTO + " "+ProyectoDBMetadata.TABLA_PROYECTO_ALIAS+", "+
            ProyectoDBMetadata.TABLA_USUARIOS + " "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+", "+
            ProyectoDBMetadata.TABLA_PRIORIDAD + " "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+", "+
            ProyectoDBMetadata.TABLA_TAREAS + " "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+
            " WHERE "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PROYECTO+" = "+ProyectoDBMetadata.TABLA_PROYECTO_ALIAS+"."+ProyectoDBMetadata.TablaProyectoMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE+" = "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+"."+ProyectoDBMetadata.TablaUsuariosMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD+" = "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+"."+ProyectoDBMetadata.TablaPrioridadMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PROYECTO+" = ?";

    private ProyectoOpenHelper dbHelper;
    private SQLiteDatabase db;

    public ProyectoDAO(Context c){
        this.dbHelper = new ProyectoOpenHelper(c);
    }

    public void open(){
        this.open(false);
    }

    public void open(Boolean toWrite){
        if(toWrite) {
            db = dbHelper.getWritableDatabase();
        }
        else{
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close(){
        db = dbHelper.getReadableDatabase();
    }

    public Cursor listaTareas(Integer idProyecto){
        Cursor cursorPry = db.rawQuery("SELECT "+ProyectoDBMetadata.TablaProyectoMetadata._ID+ " FROM "+ProyectoDBMetadata.TABLA_PROYECTO,null);
        Integer idPry= 0;
        if(cursorPry.moveToFirst()){
            idPry=cursorPry.getInt(0);
        }
        cursorPry.close();
        Cursor cursor = null;
        Log.d("LAB05-MAIN","PROYECTO : _"+idPry.toString()+" - "+ _SQL_TAREAS_X_PROYECTO);
        cursor = db.rawQuery(_SQL_TAREAS_X_PROYECTO,new String[]{idPry.toString()});
        return cursor;
    }

    public long nuevaTarea(Tarea t){
        ContentValues nuevoRegistro = new ContentValues(6);
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA,t.getDescripcion());
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,t.getHorasEstimadas());
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,t.getMinutosTrabajados());
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD,t.getPrioridad().getId());
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE,1);
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.PROYECTO,1);

        open(true);
        long id = db.insert(ProyectoDBMetadata.TABLA_TAREAS,null,nuevoRegistro);
        close();

        return id;
    }

    public Tarea getTareaForEditById(Integer idTarea) {
        SQLiteDatabase mydb = dbHelper.getReadableDatabase();
        Cursor cursor = mydb.rawQuery("SELECT "
                + ProyectoDBMetadata.TablaTareasMetadata.TAREA + ", "
                + ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS + ", "
                + ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD + ", "
                + ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE +
                " FROM " + ProyectoDBMetadata.TABLA_TAREAS +
                " WHERE " + ProyectoDBMetadata.TablaTareasMetadata._ID + " = " + idTarea.toString(), null);

        if (cursor.moveToFirst()) {
            Tarea t = new Tarea()
                    .setDescripcion(cursor.getString(0))
                    .setHorasEstimadas(cursor.getInt(1))
                    .setPrioridad(new Prioridad().setId(cursor.getInt(2)))
                    .setResponsable(getUsuarioById(cursor.getInt(3)));
            return t;
        }
        return null;
    }

    private Usuario getUsuarioById(int idUsuario) {
        //TODO implementar
        return new Usuario(1,"Jose","jose@jose.com");
    }

    public int actualizarTarea(Tarea t){
        ContentValues nuevoRegistro = new ContentValues(5);
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA,t.getDescripcion());
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,t.getHorasEstimadas());
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD,t.getPrioridad().getId());
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE,1);
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.PROYECTO,1);

        open(true);
        int cont = db.update(ProyectoDBMetadata.TABLA_TAREAS,nuevoRegistro,ProyectoDBMetadata.TablaTareasMetadata._ID+"="+t.getId(),null);
        close();

        return cont;
    }

    public void borrarTarea(Tarea t){

    }

    public List<Prioridad> listarPrioridades(){
        return null;
    }

    public List<Usuario> listarUsuarios(){
        return null;
    }

    public void finalizar(Integer idTarea){
        //Establecemos los campos-valores a actualizar
        ContentValues valores = new ContentValues();
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,1);
        SQLiteDatabase mydb =dbHelper.getWritableDatabase();
        mydb.update(ProyectoDBMetadata.TABLA_TAREAS, valores, "_id=?", new String[]{idTarea.toString()});
    }

    public List<Tarea> listarDesviosPlanificacion(Boolean soloTerminadas,Integer desvioMaximoMinutos){
        // retorna una lista de todas las tareas que tardaron más (en exceso) o menos (por defecto)
        // que el tiempo planificado.
        // si la bandera soloTerminadas es true, se busca en las tareas terminadas, sino en todas.
        return null;
    }


    public void actualizarTiempoTrabajado(Integer idTarea, long diferenciaInMilis) {
        int diferencia = (int) diferenciaInMilis/5000; //5 segundos = 1 minuto

        SQLiteDatabase mydb =dbHelper.getReadableDatabase();
        Cursor cursor = mydb.rawQuery("SELECT "+ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS+
                " FROM "+ProyectoDBMetadata.TABLA_TAREAS+
                " WHERE "+ProyectoDBMetadata.TablaTareasMetadata._ID+" = "+idTarea.toString(),null);

        cursor.moveToFirst();
        int minutosTrabajados = cursor.getInt(0);
        minutosTrabajados += diferencia;

        ContentValues valores = new ContentValues();
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,minutosTrabajados);

        mydb.update(ProyectoDBMetadata.TABLA_TAREAS,valores,ProyectoDBMetadata.TablaTareasMetadata._ID+"="+idTarea,null);
    }
}
