package com.example.pm1e1410306;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pm1e1410306.Configuracion.SQLiteConexion;
import com.example.pm1e1410306.Configuracion.Transacciones;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.example.pm1e1410306.Utils.ValidacionesRegex;

public class ActivityPrincipal extends AppCompatActivity {

    private static final String TAG = "ActivityPrincipal";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;

    // Variables para las vistas
    private ImageView vistaFoto;
    private String currentPhotoPath;
    private EditText nombres, telefono, nota;
    private Spinner spinnerPais;
    private Button btnGuardar, btnLista;
    private ImageButton btnCapturarFoto;

    private boolean esActualizacion = false;
    private int contactoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);
        Log.d(TAG, "onCreate iniciado");

        // Inicializar
        initializeApp();

        // Verificar si es una actualizacion
        Intent intent = getIntent();
        if (intent != null) {
            esActualizacion = intent.getBooleanExtra("esActualizacion", false);
            Log.d(TAG, "esActualizacion: " + esActualizacion);

            if (esActualizacion) {
                contactoId = intent.getIntExtra("id", -1);
                Log.d(TAG, "contactoId: " + contactoId);

                if (contactoId != -1) {
                    cargarDatosContacto(intent);
                    btnGuardar.setText("Actualizar");
                } else {
                    Log.e(TAG, "ID de contacto inválido");
                    Toast.makeText(this, "Error al cargar el contacto",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }

        // Verificar permisos
        if (!checkPermissions()) {
            requestPermissions();
        }
    }

    private void cargarDatosContacto(Intent intent) {
        try {
            String nombreStr = intent.getStringExtra("nombres");
            String telefonoStr = intent.getStringExtra("telefono");
            String notaStr = intent.getStringExtra("nota");
            String paisStr = intent.getStringExtra("pais");
            String fotoStr = intent.getStringExtra("foto");

            Log.d(TAG, "Cargando datos: " + nombreStr + ", " + telefonoStr +
                    ", " + paisStr + ", " + notaStr + ", " + fotoStr);

            if (nombreStr != null) nombres.setText(nombreStr);
            if (telefonoStr != null) telefono.setText(telefonoStr);
            if (notaStr != null) nota.setText(notaStr);

            // Seleccionar país
            if (paisStr != null) {
                ArrayAdapter<CharSequence> adapter =
                        (ArrayAdapter<CharSequence>) spinnerPais.getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.getItem(i).toString().equals(paisStr)) {
                        spinnerPais.setSelection(i);
                        break;
                    }
                }
            }

            // Cargar foto
            if (fotoStr != null && !fotoStr.isEmpty()) {
                currentPhotoPath = fotoStr;
                File imgFile = new File(fotoStr);
                if (imgFile.exists()) {
                    vistaFoto.setImageURI(Uri.fromFile(imgFile));
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar datos: " + e.getMessage());
            Toast.makeText(this, "Error al cargar los datos del contacto",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPermissions() {
        boolean cameraPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean writePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean readPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return cameraPermission && writePermission && readPermission;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permisos concedidos");
                initializeApp();
            } else {
                Log.e(TAG, "Permisos denegados");
                Toast.makeText(this,
                        "Se requieren permisos para el funcionamiento de la app",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void initializeApp() {
        try {
            // Inicializar vistas
            nombres = findViewById(R.id.nombres);
            telefono = findViewById(R.id.telefono);
            nota = findViewById(R.id.nota);
            spinnerPais = findViewById(R.id.pais);
            btnGuardar = findViewById(R.id.btnGuardar);
            btnLista = findViewById(R.id.btnLista);
            btnCapturarFoto = findViewById(R.id.capturarFoto);
            vistaFoto = findViewById(R.id.vistaFoto);

            // slector de paises
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.paises_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPais.setAdapter(adapter);

            // Aplicar filtros al campo de nombres
            nombres.setFilters(new InputFilter[]{
                    ValidacionesRegex.nombreFilter,
                    new InputFilter.LengthFilter(50) // limitar a 50 caracteres
            });

            // Aplicar filtros al campo de teléfono
            telefono.setFilters(new InputFilter[]{
                    ValidacionesRegex.telefonoFilter,
                    new InputFilter.LengthFilter(15) // máximo según estándar internacional
            });

            // Agregar validación en tiempo real para el teléfono
            spinnerPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Validar el teléfono actual cuando cambia el país
                    String telefonoActual = telefono.getText().toString();
                    if (!telefonoActual.isEmpty()) {
                        validarTelefono(telefonoActual);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            telefono.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    validarTelefono(s.toString());
                }
            });

            btnGuardar.setOnClickListener(v -> agregarContacto());
            btnLista.setOnClickListener(v -> {
                Intent intent = new Intent(ActivityPrincipal.this, ActivityLists.class);
                startActivity(intent);
            });
            btnCapturarFoto.setOnClickListener(v -> showPictureDialog());

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top,
                        systemBars.right, systemBars.bottom);
                return insets;
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error al inicializar la aplicación",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void validarTelefono(String numero) {
        String paisSeleccionado = spinnerPais.getSelectedItem().toString();
        if (!ValidacionesRegex.validarTelefonoPorPais(numero, paisSeleccionado)) {
            telefono.setError(ValidacionesRegex.getMensajeErrorTelefono(paisSeleccionado));
        } else {
            telefono.setError(null);
        }
    }



    private void agregarContacto() {
        if (!validarCampos()) {
            return;
        }

        try {
            SQLiteConexion conexion = new SQLiteConexion(this,
                    Transacciones.NameDB, null, 1);
            SQLiteDatabase db = conexion.getWritableDatabase();

            ContentValues valores = new ContentValues();
            valores.put(Transacciones.nombres, nombres.getText().toString().trim());
            valores.put(Transacciones.telefono, telefono.getText().toString().trim());
            valores.put(Transacciones.pais, spinnerPais.getSelectedItem().toString());
            valores.put(Transacciones.nota, nota.getText().toString().trim());
            valores.put(Transacciones.foto, currentPhotoPath);

            if (esActualizacion) {
                String[] args = {String.valueOf(contactoId)};
                int resultado = db.update(Transacciones.tabla_contactos,
                        valores, Transacciones.id + "=?", args);

                if (resultado > 0) {
                    Toast.makeText(this, "Contacto actualizado exitosamente",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error al actualizar el contacto",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                // Insertar nuevo contacto
                Long resultado = db.insert(Transacciones.tabla_contactos,
                        Transacciones.id, valores);
                if (resultado > 0) {
                    Toast.makeText(this, "Contacto guardado exitosamente",
                            Toast.LENGTH_LONG).show();
                    limpiarFormulario();
                }
            }

            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
            Toast.makeText(this, "Error al procesar el contacto",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean validarCampos() {
        if (nombres.getText().toString().trim().isEmpty()) {
            nombres.setError("El nombre es requerido");
            return false;
        }

        String telefonoText = telefono.getText().toString().trim();
        if (telefonoText.isEmpty()) {
            telefono.setError("El teléfono es requerido");
            return false;
        }

        String paisSeleccionado = spinnerPais.getSelectedItem().toString();
        if (!ValidacionesRegex.validarTelefonoPorPais(telefonoText, paisSeleccionado)) {
            telefono.setError(ValidacionesRegex.getMensajeErrorTelefono(paisSeleccionado));
            return false;
        }

        if (nota.getText().toString().trim().isEmpty()) {
            nota.setError("La nota es requerida");
            return false;
        }

        if (currentPhotoPath == null) {
            Toast.makeText(this, "Debe tomar o seleccionar una foto",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        nombres.setText("");
        telefono.setText("");
        nota.setText("");
        spinnerPais.setSelection(0);
        vistaFoto.setImageResource(android.R.drawable.ic_menu_camera);
        currentPhotoPath = null;
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Seleccione una opción");
        String[] pictureDialogItems = {
                "Seleccionar foto de la galería",
                "Tomar usando la cámara"
        };
        pictureDialog.setItems(pictureDialogItems, (dialog, which) -> {
            switch (which) {
                case 0:
                    fotoGaleria();
                    break;
                case 1:
                    fotoCamara();
                    break;
            }
        });
        pictureDialog.show();
    }

    private void fotoGaleria() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
    }

    private void fotoCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error al crear archivo de imagen: " + ex.getMessage());
                Toast.makeText(this, "Error al crear archivo de imagen",
                        Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.pm1e1410306.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                procesarFotoCamara();
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                procesarFotoGaleria(data);
            }
        } else {
            Toast.makeText(this, "No se seleccionó ninguna imagen",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void procesarFotoCamara() {
        File file = new File(currentPhotoPath);
        if (file.exists()) {
            vistaFoto.setImageURI(Uri.fromFile(file));
        }
    }

    private void procesarFotoGaleria(Intent data) {
        Uri selectedImageUri = data.getData();
        if (selectedImageUri != null) {
            try {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = getExternalFilesDir(null);
                File imageFile = File.createTempFile(
                        imageFileName,
                        ".jpg",
                        storageDir
                );

                try (InputStream inputStream = getContentResolver()
                        .openInputStream(selectedImageUri);
                     FileOutputStream outputStream = new FileOutputStream(imageFile)) {

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }

                currentPhotoPath = imageFile.getAbsolutePath();
                vistaFoto.setImageURI(Uri.fromFile(imageFile));

            } catch (IOException e) {
                Log.e(TAG, "Error al procesar imagen de galería: " + e.getMessage());
                Toast.makeText(this, "Error al procesar la imagen",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}