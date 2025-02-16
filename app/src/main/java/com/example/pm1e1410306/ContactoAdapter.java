package com.example.pm1e1410306;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.example.pm1e1410306.Models.Contacto;
import java.util.List;

public class ContactoAdapter extends ArrayAdapter<Contacto> {
    private Context context;
    private List<Contacto> contactos;
    private ContactoSeleccionadoListener listener;
    private View selectedView;

    public interface ContactoSeleccionadoListener {
        void onContactoSeleccionado(Contacto contacto);
    }

    public ContactoAdapter(Context context, List<Contacto> contactos,
                           ContactoSeleccionadoListener listener) {
        super(context, 0, contactos);
        this.context = context;
        this.contactos = contactos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Crear una variable final para el listItemView
        final View listItemView;

        if (convertView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                    R.layout.contact_list_item, parent, false);
        } else {
            listItemView = convertView;
        }

        Contacto contactoActual = contactos.get(position);
        TextView txtNombreTelefono = listItemView.findViewById(R.id.txtNombreTelefono);
        String displayText = contactoActual.getNombres() + " | " + contactoActual.getTelefono();
        txtNombreTelefono.setText(displayText);

        // Ahora listItemView es efectivamente final y puede usarse en la lambda
        listItemView.setOnClickListener(v -> mostrarDialogoLlamada(contactoActual, listItemView));

        return listItemView;
    }

    private void mostrarDialogoLlamada(Contacto contacto, View itemView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Acción")
                .setMessage("¿Desea llamar a " + contacto.getNombres() + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    seleccionarItem(itemView, contacto);
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contacto.getTelefono()));
                    context.startActivity(intent);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    seleccionarItem(itemView, contacto);
                    dialog.dismiss();
                })
                .show();
    }

    private void seleccionarItem(View itemView, Contacto contacto) {
        // Desseleccionar vista anterior si existe
        if (selectedView != null) {
            selectedView.setBackgroundColor(
                    context.getResources().getColor(android.R.color.white));
        }

        // Seleccionar nueva vista
        selectedView = itemView;
        selectedView.setBackgroundColor(
                context.getResources().getColor(android.R.color.holo_blue_light));

        // Notificar al listener
        if (listener != null) {
            listener.onContactoSeleccionado(contacto);
        }
    }
}