package com.example.pm1e1410306;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pm1e1410306.Configuracion.SQLiteConexion;
import com.example.pm1e1410306.Configuracion.Transacciones;
import com.example.pm1e1410306.Models.Contacto;

import java.util.ArrayList;

public class ActivityLists extends AppCompatActivity {
    ListView listviewContactos;
    ArrayList<Contacto> listaContactos;
    SQLiteConexion conexion;
    ContactoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        conexion = new SQLiteConexion(this, Transacciones.NameDB, null, 1);
        listviewContactos = findViewById(R.id.listcontact);

        obtenerListaContactos();

        adapter = new ContactoAdapter(this, listaContactos);
        listviewContactos.setAdapter(adapter);
    }

    private void obtenerListaContactos() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        listaContactos = new ArrayList<>();

        Cursor cursor = db.rawQuery(Transacciones.SelectTableContacts, null);

        while(cursor.moveToNext()) {
            Contacto contacto = new Contacto();
            contacto.setId(cursor.getInt(0));
            contacto.setNombres(cursor.getString(1));
            contacto.setTelefono(cursor.getString(2));
            contacto.setPais(cursor.getString(3));
            contacto.setNota(cursor.getString(4));
            contacto.setFoto(cursor.getString(5));

            listaContactos.add(contacto);
        }

        cursor.close();
    }
}