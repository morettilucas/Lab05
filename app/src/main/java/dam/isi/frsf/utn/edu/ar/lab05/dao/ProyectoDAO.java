package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
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

    private static final String _SQL_TAREAS_CON_DESVIO = "SELECT *" +
            " FROM "+ProyectoDBMetadata.TABLA_TAREAS +
            " WHERE ? <= abs("+ ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS+" * 60 - "+ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS+") AND "+
            ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA+" = ?";

    private static final String _SQL_OBTENER_PRIORIDADES = "SELECT "+ProyectoDBMetadata.TablaPrioridadMetadata._ID+
            ", "+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD+
            " FROM "+ProyectoDBMetadata.TABLA_PRIORIDAD;

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
        /*
        Cursor cursorPry = db.rawQuery("SELECT "+ProyectoDBMetadata.TablaProyectoMetadata._ID+ " FROM "+ProyectoDBMetadata.TABLA_PROYECTO,null);
        Integer idPry= 0;
        if(cursorPry.moveToFirst()){
            idPry=cursorPry.getInt(0);
        }
        cursorPry.close();
        */
        Log.d("LAB05-MAIN","PROYECTO : _"+idProyecto.toString()+" - "+ _SQL_TAREAS_X_PROYECTO);

        return db.rawQuery(_SQL_TAREAS_X_PROYECTO,new String[]{idProyecto.toString()});
    }

    public long nuevaTarea(Tarea t){
        ContentValues nuevoRegistro = new ContentValues(6);
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA,t.getDescripcion());
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,t.getHorasEstimadas());
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,t.getMinutosTrabajados());
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD,t.getPrioridad().getId());
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE,1);
        nuevoRegistro.put(ProyectoDBMetadata.TablaTareasMetadata.PROYECTO,1); //TODO manejar id del proyecto

        open(true);
        long idNuevaTarea = db.insert(ProyectoDBMetadata.TABLA_TAREAS,null,nuevoRegistro);
        close();

        return idNuevaTarea;
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

        Tarea t = new Tarea();
        if (cursor.moveToFirst()) {
            t
                    .setId(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata._ID)))
                    .setDescripcion(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.TAREA)))
                    .setHorasEstimadas(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS)))
                    .setPrioridad(new Prioridad().setId(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD))).setPrioridad(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS))))
                    .setResponsable(getUsuarioById(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE))))
                    .setFinalizada(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA))==1)
                    .setMinutosTrabajados(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS)));
                    //.setProyecto();
            cursor.close();
        }
        return t;
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
        int cantFilasUpdated = db.update(ProyectoDBMetadata.TABLA_TAREAS,nuevoRegistro,ProyectoDBMetadata.TablaTareasMetadata._ID+"=?",new String[]{t.getId().toString()});
        close();

        return cantFilasUpdated;
    }

    public int borrarTarea(Integer idTarea){
        open(true);
        int cantFilasEliminadas = db.delete(ProyectoDBMetadata.TABLA_TAREAS,ProyectoDBMetadata.TablaTareasMetadata._ID+"=?", new String[]{idTarea.toString()});
        close();

        return cantFilasEliminadas;
    }

    public ArrayList<Prioridad> listarPrioridades(){

        Cursor cursor = db.rawQuery(_SQL_OBTENER_PRIORIDADES,null);

        ArrayList<Prioridad> prioridades = new ArrayList<>();
        while (cursor.moveToNext()){
            Prioridad p = new Prioridad()
                    .setId(cursor.getInt(0))
                    .setPrioridad(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD)));
            prioridades.add(p);
        }
        cursor.close();
        return prioridades;
    }

    public List<Usuario> listarUsuarios(){
        return null;
    }

    public void finalizar(Integer idTarea){
        //Establecemos los campos-valores a actualizar
        ContentValues valores = new ContentValues();
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,1);
        SQLiteDatabase mydb =dbHelper.getWritableDatabase();
        mydb.update(ProyectoDBMetadata.TABLA_TAREAS, valores, ProyectoDBMetadata.TablaTareasMetadata._ID+"=?", new String[]{idTarea.toString()});
    }

    public ArrayList<Tarea> listarDesviosPlanificacion(Boolean soloTerminadas,Integer tolerancia, Integer idProyecto) {
        // retorna una lista de todas las tareas que tardaron más (en exceso) o menos (por defecto) con una cierta tolerancia
        // que el tiempo planificado.
        // si la bandera soloTerminadas es true, se busca en las tareas terminadas, sino en todas.

        String terminadas = soloTerminadas? "1" : "0";

        Cursor cursor = db.rawQuery("SELECT *" +
                " FROM "+ProyectoDBMetadata.TABLA_TAREAS +
                " WHERE "+ ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA+" = "+ terminadas+ " AND "+
                tolerancia +" <= abs("+ ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS+" * 60 - "+ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS+")",null);

        ArrayList<Tarea> tareasConDesvio = new ArrayList<>();
        while (cursor.moveToNext()) {
            Tarea t = new Tarea()
                    .setDescripcion(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.TAREA)))
                    .setHorasEstimadas(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS)))
                    .setPrioridad(new Prioridad().setId(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD))))
                    .setResponsable(getUsuarioById(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE))))
                    .setFinalizada(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA))==1)
                    .setMinutosTrabajados(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS)));
                    //.setProyecto();

            tareasConDesvio.add(t);
        }
        cursor.close();
        return tareasConDesvio;
    }

    public void actualizarTiempoTrabajado(Integer idTarea, Integer diferencia) {
        SQLiteDatabase mydb =dbHelper.getReadableDatabase();
        Cursor cursor = mydb.rawQuery("SELECT "+ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS+
                " FROM "+ProyectoDBMetadata.TABLA_TAREAS+
                " WHERE "+ProyectoDBMetadata.TablaTareasMetadata._ID+"="+idTarea.toString(),null);

        cursor.moveToFirst();
        int minutosTrabajados = cursor.getInt(0);
        cursor.close();

        minutosTrabajados += diferencia;

        ContentValues valores = new ContentValues();
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,minutosTrabajados);

        mydb.update(ProyectoDBMetadata.TABLA_TAREAS,valores,ProyectoDBMetadata.TablaTareasMetadata._ID+"=?",new String[]{idTarea.toString()});
    }
}
