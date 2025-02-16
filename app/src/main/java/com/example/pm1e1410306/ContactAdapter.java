package com.example.pm1e1410306;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pm1e1410306.Models.Contacto;
import com.example.pm1e1410306.R;

import java.io.File;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contacto> {
    private Context context;
    private List<Contacto> contactos;

    public ContactAdapter(@NonNull Context context, List<Contacto> contactos) {
        super(context, 0, contactos);
        this.context = context;
        this.contactos = contactos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        }

        Contacto contacto = contactos.get(position);

        ImageView imgContacto = view.findViewById(R.id.imgContacto);
        TextView txtNombre = view.findViewById(R.id.txtNombre);
        TextView txtTelefono = view.findViewById(R.id.txtTelefono);
        TextView txtPais = view.findViewById(R.id.txtPais);

        // Configurar la imagen del contacto
        if (contacto.getFoto() != null && !contacto.getFoto().isEmpty()) {
            File imgFile = new File(contacto.getFoto());
            if (imgFile.exists()) {
                imgContacto.setImageURI(Uri.fromFile(imgFile));
            }
        }

        // Configurar los textos
        txtNombre.setText(contacto.getNombres());
        txtTelefono.setText(contacto.getTelefono());
        txtPais.setText(contacto.getPais());

        return view;
    }
}