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
    ArrayList<String> arregloContactos;
    SQLiteConexion conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        conexion = new SQLiteConexion(this, Transacciones.NameDB, null, 1);
        listviewContactos = findViewById(R.id.listcontact);

        obtenerListaContactos();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, arregloContactos);
        listviewContactos.setAdapter(adapter);

        // Configurar el evento onClick para mostrar más detalles del contacto
        listviewContactos.setOnItemClickListener((parent, view, position, id) -> {
            Contacto contacto = listaContactos.get(position);
            Toast.makeText(this,
                    "Seleccionado: " + contacto.getNombres(),
                    Toast.LENGTH_SHORT).show();
            // Aquí puedes agregar la lógica para mostrar más detalles del contacto
            // por ejemplo, abrir una nueva actividad con los detalles completos
        });
    }

    private void obtenerListaContactos() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Contacto contacto;
        listaContactos = new ArrayList<>();

        // Cursor para recorrer los resultados
        Cursor cursor = db.rawQuery(Transacciones.SelectTableContacts, null);

        while(cursor.moveToNext()) {
            contacto = new Contacto();
            contacto.setId(cursor.getInt(0));
            contacto.setNombres(cursor.getString(1));
            contacto.setTelefono(cursor.getString(2));
            contacto.setPais(cursor.getString(3));
            contacto.setNota(cursor.getString(4));
            contacto.setFoto(cursor.getString(5));

            listaContactos.add(contacto);
        }

        cursor.close();

        // Llenar el ArrayList que se mostrará en el ListView
        arregloContactos = new ArrayList<>();
        for(Contacto c : listaContactos) {
            arregloContactos.add(c.getNombres() + " | " +
                    c.getTelefono() + " | " +
                    c.getPais());
        }
    }
}