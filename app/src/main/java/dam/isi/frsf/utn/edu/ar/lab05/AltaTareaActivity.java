package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

public class AltaTareaActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private EditText etDescripcion;
    private EditText etHorasEstimadas;
    private TextView tvProgress;
    private SeekBar sbPrioridad;
    private Spinner responsable;
    private Button btnGuardar;
    private Button btnCancelar;

    private ProyectoDAO proyectoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_tarea);
        inicializarVista();
        sbPrioridad.setOnSeekBarChangeListener(this); //TODO inicializar tvProgress

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(guardarTarea()!=-1){
                    Toast.makeText(getApplicationContext(),"La tarea se a creado con éxito!",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Ocurrió un error, no hemos podido guardar la tarea",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        proyectoDAO = new ProyectoDAO(AltaTareaActivity.this);
        proyectoDAO.open();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(proyectoDAO!=null)
            proyectoDAO.close();
    }

    private long guardarTarea() {
        Tarea t = new Tarea()
                .setDescripcion(etDescripcion.getText().toString().trim())
                .setHorasEstimadas(Integer.valueOf(etHorasEstimadas.getText().toString()))
                .setFinalizada(false)
                .setMinutosTrabajados(0)
                .setPrioridad(new Prioridad(sbPrioridad.getProgress()+1,tvProgress.getText().toString().trim()));

        return proyectoDAO.nuevaTarea(t);
    }

    private void inicializarVista(){
        etDescripcion = (EditText) findViewById(R.id.et_descripcion);
        etHorasEstimadas = (EditText) findViewById(R.id.et_horas_estimadas);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        sbPrioridad = (SeekBar) findViewById(R.id.sb_prioridad);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (progress){
            case 0:
                tvProgress.setText("Baja");
                break;
            case 1:
                tvProgress.setText("Media");
                break;
            case 2:
                tvProgress.setText("Alta");
                break;
            case 3:
                tvProgress.setText("Urgente");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
