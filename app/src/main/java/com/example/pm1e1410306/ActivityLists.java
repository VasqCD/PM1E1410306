package com.example.pm1e1410306;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.pm1e1410306.Configuracion.SQLiteConexion;
import com.example.pm1e1410306.Configuracion.Transacciones;
import com.example.pm1e1410306.Models.Contacto;

import java.util.ArrayList;

public class ActivityLists extends AppCompatActivity {
    ListView listviewContactos;
    ArrayList<Contacto> listaContactos;
    ArrayList<Contacto> listaContactosFiltrada;
    SQLiteConexion conexion;
    ContactoAdapter adapter;
    Button btnCompartir, btnEliminar, btnVerImagen, btnActualizar;
    private Contacto contactoSeleccionado = null;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        inicializarComponentes();
        cargarDatos();
        configurarBotones();
    }

    private void inicializarComponentes() {

        searchView = findViewById(R.id.searchView);
        listviewContactos = findViewById(R.id.listcontact);
        btnCompartir = findViewById(R.id.compartir);
        btnEliminar = findViewById(R.id.eliminar);
        btnVerImagen = findViewById(R.id.verImagen);
        btnActualizar = findViewById(R.id.actualizar);

        conexion = new SQLiteConexion(this, Transacciones.NameDB, null, 1);
        listaContactos = new ArrayList<>();
        listaContactosFiltrada = new ArrayList<>();

        configurarSearchView();
    }

    private void cargarDatos() {
        obtenerListaContactos();

        if (listaContactos.isEmpty()) {
            Toast.makeText(this, "No hay contactos guardados", Toast.LENGTH_SHORT).show();
        }

        adapter = new ContactoAdapter(this, listaContactosFiltrada, contacto -> {
            contactoSeleccionado = contacto;
            actualizarEstadoBotones(true);
        });

        listviewContactos.setAdapter(adapter);
        listaContactosFiltrada.addAll(listaContactos);
        adapter.notifyDataSetChanged();
        actualizarEstadoBotones(false);
    }

    private void configurarSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarContactos(newText);
                return true;
            }
        });
    }

    private void filtrarContactos(String texto) {
        listaContactosFiltrada.clear();

        if (texto.isEmpty()) {
            listaContactosFiltrada.addAll(listaContactos);
        } else {
            String busqueda = texto.toLowerCase().trim();
            for (Contacto contacto : listaContactos) {
                if (contacto.getNombres().toLowerCase().contains(busqueda) ||
                        contacto.getTelefono().toLowerCase().contains(busqueda) ||
                        contacto.getPais().toLowerCase().contains(busqueda)) {
                    listaContactosFiltrada.add(contacto);
                }
            }
        }

        adapter.notifyDataSetChanged();
        contactoSeleccionado = null;
        actualizarEstadoBotones(false);
    }

    private void obtenerListaContactos() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        listaContactos.clear();

        try {
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
            Log.d("DEBUG", "Contactos obtenidos: " + listaContactos.size());
        } catch (Exception e) {
            Log.e("ERROR", "Error al obtener contactos: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    private void configurarBotones() {
        btnCompartir.setOnClickListener(v -> {
            if (contactoSeleccionado != null) {
                compartirContacto(contactoSeleccionado);
            } else {
                mostrarMensaje("Seleccione un contacto primero");
            }
        });

        btnVerImagen.setOnClickListener(v -> {
            if (contactoSeleccionado != null && contactoSeleccionado.getFoto() != null) {
                mostrarImagen(contactoSeleccionado.getFoto());
            } else {
                mostrarMensaje("No hay imagen disponible");
            }
        });

        btnEliminar.setOnClickListener(v -> {
            if (contactoSeleccionado != null) {
                confirmarEliminarContacto(contactoSeleccionado);
            } else {
                mostrarMensaje("Seleccione un contacto primero");
            }
        });

        btnActualizar.setOnClickListener(v -> {
            if (contactoSeleccionado != null) {
                abrirActualizacionContacto(contactoSeleccionado);
            } else {
                mostrarMensaje("Seleccione un contacto primero");
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
        String shareMessage = String.format("Contacto: %s\nTeléfono: %s\nPaís: %s\nNota: %s",
                contacto.getNombres(),
                contacto.getTelefono(),
                contacto.getPais(),
                contacto.getNota());

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
            mostrarMensaje("No hay imagen disponible para este contacto");
        }
    }

    private void confirmarEliminarContacto(Contacto contacto) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro que desea eliminar el contacto " + contacto.getNombres() + "?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarContacto(contacto))
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminarContacto(Contacto contacto) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        String[] args = {String.valueOf(contacto.getId())};

        try {
            int deletedRows = db.delete(Transacciones.tabla_contactos,
                    Transacciones.id + "=?", args);

            if (deletedRows > 0) {
                obtenerListaContactos();
                listaContactosFiltrada.clear();
                listaContactosFiltrada.addAll(listaContactos);
                adapter.notifyDataSetChanged();
                contactoSeleccionado = null;
                actualizarEstadoBotones(false);
                mostrarMensaje("Contacto eliminado exitosamente");
            } else {
                mostrarMensaje("Error al eliminar el contacto");
            }
        } finally {
            db.close();
        }
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

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerListaContactos();
        if (adapter != null) {
            listaContactosFiltrada.clear();
            listaContactosFiltrada.addAll(listaContactos);
            adapter.notifyDataSetChanged();

            if (contactoSeleccionado != null) {
                boolean encontrado = false;
                for (Contacto contacto : listaContactosFiltrada) {
                    if (contacto.getId() == contactoSeleccionado.getId()) {
                        contactoSeleccionado = contacto;
                        actualizarEstadoBotones(true);
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    contactoSeleccionado = null;
                    actualizarEstadoBotones(false);
                }
            }
            searchView.setQuery("", false);
            searchView.clearFocus();
        }
    }
}