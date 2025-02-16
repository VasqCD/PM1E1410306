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

    public ContactoAdapter(Context context, List<Contacto> contactos) {
        super(context, 0, contactos);
        this.context = context;
        this.contactos = contactos;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                    R.layout.contact_list_item, parent, false);
        }

        Contacto contactoActual = contactos.get(position);

        TextView txtNombreTelefono = listItemView.findViewById(R.id.txtNombreTelefono);

        // Configurar el texto principal
        String displayText = contactoActual.getNombres() + " | " + contactoActual.getTelefono();
        txtNombreTelefono.setText(displayText);

        // Configurar el click en el item
        listItemView.setOnClickListener(v -> mostrarDialogoLlamada(contactoActual));

        return listItemView;
    }

    private void mostrarDialogoLlamada(Contacto contacto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Acción")
                .setMessage("¿Desea llamar a " + contacto.getNombres() + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contacto.getTelefono()));
                    context.startActivity(intent);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}