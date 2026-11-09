package com.vincenzoratti.flytrapiot;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ArrayAdapter<String> adaptador;
    private final ArrayList<String> trampas = new ArrayList<>();
    private final ArrayList<Integer> ids = new ArrayList<>();
    private TextView textoTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                });

        dbHelper = new DBHelper(this);
        textoTotal = findViewById(R.id.textoTotal);

        ListView listaTrampas = findViewById(R.id.listaTrampas);
        TextView textoVacio = findViewById(R.id.textoVacio);

        adaptador = new ArrayAdapter<>(
                this, R.layout.item_trampa, trampas);
        listaTrampas.setAdapter(adaptador);
        listaTrampas.setEmptyView(textoVacio);

        Button btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v ->
                startActivity(new Intent(this, FormularioActivity.class)));

        listaTrampas.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, FormularioActivity.class);
            intent.putExtra(FormularioActivity.EXTRA_ID, ids.get(position));
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTrampas();
    }

    private void cargarTrampas() {
        trampas.clear();
        ids.clear();

        try (Cursor cursor = dbHelper.getReadableDatabase().query(
                "trampas",
                new String[]{
                        "id",
                        "codigo_sector",
                        "tipo_trampa",
                        "nivel_captura",
                        "observaciones"
                },
                null, null, null, null, "id DESC")) {

            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));
                String sector = cursor.getString(
                        cursor.getColumnIndexOrThrow("codigo_sector"));
                String tipo = cursor.getString(
                        cursor.getColumnIndexOrThrow("tipo_trampa"));
                int capturas = cursor.getInt(
                        cursor.getColumnIndexOrThrow("nivel_captura"));
                String observaciones = cursor.getString(
                        cursor.getColumnIndexOrThrow("observaciones"));

                ids.add(id);

                String detalle = "Sector: " + sector
                        + "\nTipo: " + tipo
                        + "  |  Nivel de captura: " + capturas;

                if (observaciones != null
                        && !observaciones.trim().isEmpty()) {
                    detalle += "\nObservaciones: " + observaciones;
                }

                trampas.add(detalle);
            }
        }

        textoTotal.setText("Trampas registradas: " + trampas.size());
        adaptador.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}