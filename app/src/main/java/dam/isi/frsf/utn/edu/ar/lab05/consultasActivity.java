/*
 * Copyright (c) 2016.
 */

package dam.isi.frsf.utn.edu.ar.lab05;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

public class ConsultasActivity extends AppCompatActivity implements View.OnClickListener {

    private ProyectoDAO proyectoDAO;
    private Integer idProyectoActual;

    private EditText etDesvioEnMinutos;
    private CheckBox chkBoxFinalizada;
    private Button btnBuscar;
    private TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultas);
        idProyectoActual = getIntent().getIntExtra("id_proyecto",1);
        setParametros();
        chkBoxFinalizada.setChecked(true);
        btnBuscar.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        proyectoDAO = new ProyectoDAO(ConsultasActivity.this);
        proyectoDAO.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(proyectoDAO!=null) proyectoDAO.close();
    }

    private void setParametros(){
        etDesvioEnMinutos = (EditText) findViewById(R.id.et_desvio_en_minutos);
        chkBoxFinalizada = (CheckBox) findViewById(R.id.chkbox_finalizada);
        btnBuscar = (Button) findViewById(R.id.btn_buscar);
        tvResultado = (TextView) findViewById(R.id.tv_resultado);
    }

    @Override
    public void onClick(View v) {
        proyectoDAO = new ProyectoDAO(ConsultasActivity.this);
        proyectoDAO.open();

        if(validar()) {
            ArrayList<Tarea> tareasConDesvio = proyectoDAO.listarDesviosPlanificacion(chkBoxFinalizada.isChecked(), Integer.valueOf(etDesvioEnMinutos.getText().toString()), idProyectoActual);

            tvResultado.setText("");
            for (Tarea t : tareasConDesvio) {
                tvResultado.append(t.getDescripcion() + "\n");
            }
            Toast.makeText(this, tareasConDesvio.size() + " resultados.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Ingrese un desvío", Toast.LENGTH_LONG).show();
        }
    }

    private Boolean validar(){
        return etDesvioEnMinutos.getText().length()!=0;
    }
}
