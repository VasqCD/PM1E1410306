package com.example.pm1e1410306;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    Button btnCompartir, btnEliminar, btnVerImagen, btnActualizar;
    private Contacto contactoSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        listviewContactos = findViewById(R.id.listcontact);
        btnCompartir = findViewById(R.id.compartir);
        btnEliminar = findViewById(R.id.eliminar);
        btnVerImagen = findViewById(R.id.verImagen);
        btnActualizar = findViewById(R.id.actualizar);

        conexion = new SQLiteConexion(this, Transacciones.NameDB, null, 1);
        obtenerListaContactos();

        adapter = new ContactoAdapter(this, listaContactos, contacto -> {
            contactoSeleccionado = contacto;
            actualizarEstadoBotones(true);
        });

        listviewContactos.setAdapter(adapter);

        actualizarEstadoBotones(false);
        configurarBotones();
    }

    private void configurarBotones() {
        btnCompartir.setOnClickListener(v -> {
            if (contactoSeleccionado != null) {
                compartirContacto(contactoSeleccionado);
            } else {
                Toast.makeText(this, "Seleccione un contacto primero",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnVerImagen.setOnClickListener(v -> {
            if (contactoSeleccionado != null && contactoSeleccionado.getFoto() != null) {
                mostrarImagen(contactoSeleccionado.getFoto());
            } else {
                Toast.makeText(this, "No hay imagen disponible",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // boton eliminar
        btnEliminar.setOnClickListener(v -> {
            if (contactoSeleccionado != null) {
                confirmarEliminarContacto(contactoSeleccionado);
            } else {
                Toast.makeText(this, "Seleccione un contacto primero",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // actualizar
        btnActualizar.setOnClickListener(v -> {
            if (contactoSeleccionado != null) {
                abrirActualizacionContacto(contactoSeleccionado);
            } else {
                Toast.makeText(this, "Seleccione un contacto primero",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarEstadoBotones(boolean enabled) {
        btnCompartir.setEnabled(enabled);
        btnEliminar.setEnabled(enabled);
        btnVerImagen.setEnabled(enabled);
        btnActualizar.setEnabled(enabled);
    }

    private void compartirContacto(Contacto contacto) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareMessage = "Contacto: " + contacto.getNombres() +
                "\nTeléfono: " + contacto.getTelefono() +
                "\nPaís: " + contacto.getPais() +
                "\nNota: " + contacto.getNota();

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Compartir contacto usando"));
    }

    private void mostrarImagen(String rutaImagen) {
        if (rutaImagen != null && !rutaImagen.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ImageView imageView = new ImageView(this);
            imageView.setImageURI(Uri.parse(rutaImagen));

            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            builder.setView(imageView)
                    .setTitle("Foto del Contacto")
                    .setPositiveButton("Cerrar", null)
                    .show();
        } else {
            Toast.makeText(this, "No hay imagen disponible para este contacto",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarEliminarContacto(Contacto contacto) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro que desea eliminar el contacto " +
                        contacto.getNombres() + "?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarContacto(contacto))
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminarContacto(Contacto contacto) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        String[] args = {String.valueOf(contacto.getId())};

        int deletedRows = db.delete(Transacciones.tabla_contactos,
                Transacciones.id + "=?", args);

        if (deletedRows > 0) {
            // Recargar la lista desde la base de datos
            obtenerListaContactos();
            adapter.clear();
            adapter.addAll(listaContactos);
            adapter.notifyDataSetChanged();
            contactoSeleccionado = null;
            actualizarEstadoBotones(false);
            Toast.makeText(this, "Contacto eliminado exitosamente",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al eliminar el contacto",
                    Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private void abrirActualizacionContacto(Contacto contacto) {
        Intent intent = new Intent(this, ActivityPrincipal.class);
        intent.putExtra("id", contacto.getId());
        intent.putExtra("nombres", contacto.getNombres());
        intent.putExtra("telefono", contacto.getTelefono());
        intent.putExtra("pais", contacto.getPais());
        intent.putExtra("nota", contacto.getNota());
        intent.putExtra("foto", contacto.getFoto());
        intent.putExtra("esActualizacion", true);
        startActivity(intent);
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
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerListaContactos();
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(listaContactos);
            adapter.notifyDataSetChanged();

            if (contactoSeleccionado != null) {
                for (Contacto contacto : listaContactos) {
                    if (contacto.getId() == contactoSeleccionado.getId()) {
                        contactoSeleccionado = contacto;
                        actualizarEstadoBotones(true);
                        break;
                    }
                }
            }
        }
    }
}