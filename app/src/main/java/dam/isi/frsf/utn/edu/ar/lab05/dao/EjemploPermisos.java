package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

public class EjemploPermisos extends AppCompatActivity {
    private boolean flagPermisoPedido;
    private static final int PERMISSION_REQUEST_CONTACT =999;

    public void askForContactPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(EjemploPermisos.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(EjemploPermisos.this,
                        Manifest.permission.CALL_PHONE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EjemploPermisos.this);
                    builder.setTitle("Permisos Peligrosos!!!");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Puedo acceder a un permiso peligroso???");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            flagPermisoPedido=true;
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.GET_ACCOUNTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                } else {
                    flagPermisoPedido=true;
                    ActivityCompat.requestPermissions(EjemploPermisos.this,
                            new String[]
                                    {Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.GET_ACCOUNTS}
                            , PERMISSION_REQUEST_CONTACT);
                }

            }
        }
        if(!flagPermisoPedido) hacerAlgoQueRequeriaPermisosPeligrosos();
    }

    public void hacerAlgoQueRequeriaPermisosPeligrosos(){
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("ESCRIBIR_JSON","req code"+requestCode+ " "+ Arrays.toString(permissions)+" ** "+Arrays.toString(grantResults));
        switch (requestCode) {
            case EjemploPermisos.PERMISSION_REQUEST_CONTACT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hacerAlgoQueRequeriaPermisosPeligrosos();
                } else {
                    Toast.makeText(EjemploPermisos.this, "No permission for contacts", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

    }
}