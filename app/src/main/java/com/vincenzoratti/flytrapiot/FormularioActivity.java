package com.vincenzoratti.flytrapiot;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FormularioActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "extra_id";

    private DBHelper dbHelper;
    private EditText etCodigoSector;
    private EditText etTipoTrampa;
    private EditText etNivelCaptura;
    private EditText etObservaciones;
    private int idTrampa = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                });

        dbHelper = new DBHelper(this);

        TextView titulo = findViewById(R.id.txtTituloFormulario);
        etCodigoSector = findViewById(R.id.etCodigoSector);
        etTipoTrampa = findViewById(R.id.etTipoTrampa);
        etNivelCaptura = findViewById(R.id.etNivelCaptura);
        etObservaciones = findViewById(R.id.etObservaciones);

        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnEliminar = findViewById(R.id.btnEliminar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        idTrampa = getIntent().getIntExtra(EXTRA_ID, -1);

        if (idTrampa != -1) {
            titulo.setText("Editar trampa");
            btnGuardar.setText("Guardar cambios");
            btnEliminar.setVisibility(View.VISIBLE);
            cargarDatosGuardados();
        } else {
            titulo.setText("Registrar trampa");
            btnEliminar.setVisibility(View.GONE);
        }

        btnGuardar.setOnClickListener(v -> guardarTrampa());
        btnEliminar.setOnClickListener(v -> eliminarTrampa());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void cargarDatosGuardados() {
        try (Cursor cursor = dbHelper.getReadableDatabase().query(
                "trampas",
                new String[]{
                        "codigo_sector",
                        "tipo_trampa",
                        "nivel_captura",
                        "observaciones"
                },
                "id = ?",
                new String[]{String.valueOf(idTrampa)},
                null, null, null)) {

            if (!cursor.moveToFirst()) {
                Toast.makeText(this, "No se encontró la trampa",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            etCodigoSector.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow("codigo_sector")));
            etTipoTrampa.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow("tipo_trampa")));
            etNivelCaptura.setText(String.valueOf(cursor.getInt(
                    cursor.getColumnIndexOrThrow("nivel_captura"))));
            etObservaciones.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow("observaciones")));
        }
    }

    private void guardarTrampa() {
        String sector = etCodigoSector.getText().toString().trim();
        String tipo = etTipoTrampa.getText().toString().trim();
        String nivelTexto = etNivelCaptura.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();

        if (sector.isEmpty()) {
            etCodigoSector.setError("Ingresa el código del sector");
            etCodigoSector.requestFocus();
            return;
        }

        if (tipo.isEmpty()) {
            etTipoTrampa.setError("Ingresa el tipo de trampa");
            etTipoTrampa.requestFocus();
            return;
        }

        if (nivelTexto.isEmpty()) {
            etNivelCaptura.setError("Ingresa el nivel de captura");
            etNivelCaptura.requestFocus();
            return;
        }

        int nivel;
        try {
            nivel = Integer.parseInt(nivelTexto);
        } catch (NumberFormatException e) {
            etNivelCaptura.setError("Ingresa un número válido");
            etNivelCaptura.requestFocus();
            return;
        }

        ContentValues valores = new ContentValues();
        valores.put("codigo_sector", sector);
        valores.put("tipo_trampa", tipo);
        valores.put("nivel_captura", nivel);
        valores.put("observaciones", observaciones);

        if (idTrampa == -1) {
            long nuevoId = dbHelper.getWritableDatabase()
                    .insert("trampas", null, valores);

            if (nuevoId == -1) {
                Toast.makeText(this, "No se pudo registrar la trampa",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Trampa registrada",
                    Toast.LENGTH_SHORT).show();
        } else {
            int filas = dbHelper.getWritableDatabase().update(
                    "trampas",
                    valores,
                    "id = ?",
                    new String[]{String.valueOf(idTrampa)});

            if (filas == 0) {
                Toast.makeText(this, "No se pudieron guardar los cambios",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Cambios guardados",
                    Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void eliminarTrampa() {
        if (idTrampa == -1) {
            return;
        }

        int filas = dbHelper.getWritableDatabase().delete(
                "trampas",
                "id = ?",
                new String[]{String.valueOf(idTrampa)});

        if (filas > 0) {
            Toast.makeText(this, "Trampa eliminada",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "No se pudo eliminar la trampa",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
