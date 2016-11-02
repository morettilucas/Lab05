/*
 * Copyright (c) 2016.
 */

package dam.isi.frsf.utn.edu.ar.lab05.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class EjemploContactos extends AppCompatActivity {

    public void nuevoContacto(){
        String accountType =null;
        String accountName =null;
        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, accountType);
        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, accountName);
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Walter White");
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Email.DATA, "heissmberg@pollohermanos.com");
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }

    public void buscarContactos(String nombreBuscado){
        JSONArray arr = new JSONArray();
        final StringBuilder resultado = new StringBuilder();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        // consulta ejemplo buscando por nombre visualizado en los contactos agregados
        Cursor c =this.getContentResolver().query(uri, null, ContactsContract.Contacts.DISPLAY_NAME+" LIKE '"+nombreBuscado+"%'", null, sortOrder);
        int count = c.getColumnCount();
        int fila = 0;
        String[] columnas= new String[count];
        try {
            while(c.moveToNext()) {
                JSONObject unContacto = new JSONObject();
                for(int i = 0; (i < count );  i++) {
                    if(fila== 0)columnas[i]=c.getColumnName(i);
                    unContacto.put(columnas[i],c.getString(i));
                }
                Log.d("TEST-ARR",unContacto.toString());
                arr.put(fila,unContacto);
                fila++;
                Log.d("TEST-ARR","fila : "+fila);

                // elegir columnas de ejemplo
                resultado.append(unContacto.get("name_raw_contact_id")+" - "+unContacto.get("display_name")+System.getProperty("line.separator"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("TEST-ARR",arr.toString());
    }
}