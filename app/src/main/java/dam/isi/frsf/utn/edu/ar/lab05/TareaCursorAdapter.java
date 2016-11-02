package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

public class TareaCursorAdapter extends CursorAdapter{
    private LayoutInflater inflador;
    private ProyectoDAO myDao;
    private Context contexto;
    private HashMap<Integer, Long> marcasDeTiempos;

    public TareaCursorAdapter(Context contexto, Cursor c, ProyectoDAO dao) {
        super(contexto, c, false);
        myDao = dao;
        this.contexto = contexto;
        marcasDeTiempos = new HashMap<>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        inflador = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflador.inflate(R.layout.fila_tarea, viewGroup, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        //obtener la posicion de la fila actual y asignarla a los botones y checkboxes
        int pos = cursor.getPosition();

        // Referencias UI.
        TextView nombre = (TextView) view.findViewById(R.id.tareaTitulo);
        TextView tiempoAsignado = (TextView) view.findViewById(R.id.tareaMinutosAsignados);
        final TextView tiempoTrabajado = (TextView) view.findViewById(R.id.tareaMinutosTrabajados);
        ImageView prioridad = (ImageView) view.findViewById(R.id.tareaPrioridad);
        final TextView responsable = (TextView) view.findViewById(R.id.tareaResponsable);
        CheckBox finalizada = (CheckBox) view.findViewById(R.id.tareaFinalizada);

        final ToggleButton btnEstado = (ToggleButton) view.findViewById(R.id.tareaBtnTrabajando);
        final ImageButton btnEditar = (ImageButton) view.findViewById(R.id.tareaBtnEditarDatos);
        final ImageButton btnFinalizar = (ImageButton) view.findViewById(R.id.tareaBtnFinalizada);
        final ImageButton btnBorrar = (ImageButton) view.findViewById(R.id.tareaBtnBorrar);

        nombre.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.TAREA)));
        Integer horasAsignadas = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS));
        tiempoAsignado.setText(horasAsignadas * 60 + " minutos");

        final Integer minutosTrabajados = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS));
        tiempoTrabajado.setText(minutosTrabajados + " minutos");

        String p = cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS));
        switch (p){
            case "Baja":
                prioridad.setImageResource(R.drawable.prioridad_baja);
                break;
            case "Media":
                prioridad.setImageResource(R.drawable.prioridad_media);
                break;
            case "Alta":
                prioridad.setImageResource(R.drawable.prioridad_alta);
                break;
            case "Urgente":
                prioridad.setImageResource(R.drawable.prioridad_urgente);
                break;
        }

        responsable.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO_ALIAS)));

        finalizada.setChecked(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA)) == 1);
        finalizada.setTag(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata._ID)));

        Integer idTarea = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata._ID));
        btnEstado.setTag(idTarea);
        btnEstado.setOnCheckedChangeListener(null);
        if(marcasDeTiempos.get(idTarea)!=null){
            btnEstado.setChecked(marcasDeTiempos.get(idTarea)!=0);
        }
        else{
            btnEstado.setChecked(false);
        }

        btnEstado.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final Integer idTarea = (Integer) buttonView.getTag();

                if(puedeComenzarATrabajar(idTarea)) {
                    if (isChecked) {
                        marcasDeTiempos.put(idTarea, System.currentTimeMillis());
                    } else {
                        final Integer diferencia = (int) (long) ((System.currentTimeMillis() - marcasDeTiempos.get(idTarea)) / 5000);
                        marcasDeTiempos.put(idTarea,null);

                        Thread backGroundUpdate = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                myDao.actualizarTiempoTrabajado(idTarea, diferencia);
                                handlerRefresh.sendEmptyMessage(1);
                            }
                        });
                        backGroundUpdate.start();
                    }
                }
                else{
                    btnEstado.setChecked(false);
                    Toast.makeText(context,"La tarea se encuentra finalizada",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnEditar.setTag(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata._ID)));
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer idTarea = (Integer) view.getTag();
                Intent intEditarAct = new Intent(contexto, AltaTareaActivity.class);
                intEditarAct.putExtra("ID_TAREA", idTarea);
                contexto.startActivity(intEditarAct);
            }
        });

        btnFinalizar.setTag(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata._ID)));
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Integer idTarea = (Integer) view.getTag();

                String msjError = validarFinalizarTarea(idTarea,marcasDeTiempos.get(idTarea)!=null);
                if(msjError==null) {
                    Thread backGroundUpdate = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("LAB05-MAIN", "finalizar tarea : --- " + idTarea);
                            myDao.finalizar(idTarea);
                            handlerRefresh.sendEmptyMessage(1);
                        }
                    });
                    backGroundUpdate.start();
                }
                else{
                    Toast.makeText(context,msjError,Toast.LENGTH_LONG).show();
                }
            }
        });

        btnBorrar.setTag(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata._ID)));
        btnBorrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Integer idTarea = (Integer) v.getTag();

                if(marcasDeTiempos.get(idTarea)==null) {
                    int result = myDao.borrarTarea(idTarea);
                    if (result == 1) {
                        Toast.makeText(context, "Tarea eliminada con éxito!", Toast.LENGTH_LONG).show();
                        handlerRefresh.sendEmptyMessage(1);
                    } else {
                        Toast.makeText(context, "Ha ocurrido un error, no hemos podido eliminar la tarea", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(context,"No se puede borrar esta tarea, se está trabajando en ella",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean puedeComenzarATrabajar(Integer idTarea) {
        Tarea t = myDao.getTareaForEditById(idTarea);
        Log.v("INFO_ID",idTarea+"");
        return !t.getFinalizada();
    }

    private String validarFinalizarTarea(Integer idTarea, Boolean trabajando) {
        String msj = null;
        Tarea t = myDao.getTareaForEditById(idTarea);
        if(t.getFinalizada()){
            msj = "La tarea ya ha sido finalizada";
        }
        if(trabajando){
            msj = "No se puede finalizar, se está trabajando en la tarea";
        }
        return msj;
    }

    private Handler handlerRefresh = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            TareaCursorAdapter.this.changeCursor(myDao.listaTareas(1));
            Log.v("Handler: ","empty msg");
        }
    };
}