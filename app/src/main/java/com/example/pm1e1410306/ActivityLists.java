package com.example.pm1e1410306;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
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
    Button btnCompartir, btnEliminar;
    private Contacto contactoSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        // Inicializar vistas
        listviewContactos = findViewById(R.id.listcontact);
        btnCompartir = findViewById(R.id.compartir);
        btnEliminar = findViewById(R.id.eliminar);

        conexion = new SQLiteConexion(this, Transacciones.NameDB, null, 1);
        obtenerListaContactos();

        adapter = new ContactoAdapter(this, listaContactos, contacto -> {
            // Callback para cuando se selecciona un contacto
            contactoSeleccionado = contacto;
            actualizarEstadoBotones(true);
        });

        listviewContactos.setAdapter(adapter);

        // Inicialmente deshabilitar botones
        actualizarEstadoBotones(false);

        // Configurar botón Compartir
        btnCompartir.setOnClickListener(v -> {
            if (contactoSeleccionado != null) {
                compartirContacto(contactoSeleccionado);
            } else {
                Toast.makeText(this, "Seleccione un contacto primero",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar botón Eliminar
        btnEliminar.setOnClickListener(v -> {
            if (contactoSeleccionado != null) {
                eliminarContacto(contactoSeleccionado);
            } else {
                Toast.makeText(this, "Seleccione un contacto primero",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarEstadoBotones(boolean enabled) {
        btnCompartir.setEnabled(enabled);
        btnEliminar.setEnabled(enabled);
    }

    private void compartirContacto(Contacto contacto) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareMessage = "Contacto: " + contacto.getNombres() +
                "\nTeléfono: " + contacto.getTelefono() +
                "\nPaís: " + contacto.getPais();

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Compartir contacto usando"));
    }

    private void eliminarContacto(Contacto contacto) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        String[] args = {String.valueOf(contacto.getId())};

        int deletedRows = db.delete(Transacciones.tabla_contactos,
                Transacciones.id + "=?", args);

        if (deletedRows > 0) {
            listaContactos.remove(contacto);
            adapter.notifyDataSetChanged();
            contactoSeleccionado = null;
            actualizarEstadoBotones(false);
            Toast.makeText(this, "Contacto eliminado exitosamente",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al eliminar el contacto",
                    Toast.LENGTH_SHORT).show();
        }
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