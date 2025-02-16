package com.example.pm1e1410306;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ActivityCall extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private String phoneNumber;
    private TextView txtNumeroTelefono;
    private FloatingActionButton fabLlamar;
    private Button btnAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_call);

        // Inicializar vistas
        txtNumeroTelefono = findViewById(R.id.txtNumeroTelefono);
        fabLlamar = findViewById(R.id.fabLlamar);
        btnAtras = findViewById(R.id.btnAtras);

        // Obtener número de teléfono del intent
        phoneNumber = getIntent().getStringExtra("telefono");
        String nombre = getIntent().getStringExtra("nombre");
        txtNumeroTelefono.setText("+" + phoneNumber);

        // Configurar listeners
        fabLlamar.setOnClickListener(v -> mostrarDialogoConfirmacion(nombre));
        btnAtras.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void mostrarDialogoConfirmacion(String nombre) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar llamada")
                .setMessage("¿Desea llamar a " + (nombre != null ? nombre : phoneNumber) + "?")
                .setPositiveButton("📞 Llamar ahora", (dialog, which) -> {
                    if (checkCallPermission()) {
                        makePhoneCall();
                    } else {
                        requestCallPermission();
                    }
                })
                .setNegativeButton("✖ Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private boolean checkCallPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCallPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CALL_PHONE},
                PERMISSION_REQUEST_CODE);
    }

    private void makePhoneCall() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        } catch (SecurityException e) {
            Toast.makeText(this, "Error al realizar la llamada",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permiso denegado para realizar llamadas",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}