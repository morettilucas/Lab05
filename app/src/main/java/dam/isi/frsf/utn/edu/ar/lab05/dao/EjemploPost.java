package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EjemploPost {

    private final String IP_SERVER = "10.0.2.2";
    private final String PORT_SERVER = "4000";

    // la operacion puede ser POST - PUT - DELETE
    private void enviarOperacionToAPI(String operacion){
        HttpURLConnection urlConnection=null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy mm:ss SSS");
        try {
            JSONObject nuevoObjeto= new JSONObject();
            nuevoObjeto.put("descripcion", "creado desde android " + sdf.format(new Date()));
            nuevoObjeto.put("autor", "martin");

            String str= nuevoObjeto.toString();
            byte[] data=str.getBytes("UTF-8");
            Log.d("EjemploPost","str---> "+str);

            URL url = new URL("http://"+IP_SERVER+":"+PORT_SERVER+"/comments/");

            // VER AQUI https://developer.android.com/reference/java/net/HttpURLConnection.html
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod(operacion);
            urlConnection.setFixedLengthStreamingMode(data.length);
            urlConnection.setRequestProperty("Content-Type","application/json");
            DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream());

            printout.write(data);
            printout.flush ();
            printout.close ();
            Log.d("TEST-ARR","FIN!!! "+urlConnection.getResponseMessage());

        }catch (JSONException e2) {
            Log.e("TEST-ARR",e2.getMessage(),e2);
            e2.printStackTrace();
        }  catch (IOException e1) {
            Log.e("TEST-ARR",e1.getMessage(),e1);
            e1.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }
    }
}