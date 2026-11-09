package com.vincenzoratti.flytrapiot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "flytrap_db";
    private static final int VERSION_BD = 1;
    private static final String TABLA = "trampas";

    public DBHelper(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE trampas (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "codigo_sector TEXT NOT NULL, " +
                        "tipo_trampa TEXT NOT NULL, " +
                        "nivel_captura INTEGER NOT NULL, " +
                        "observaciones TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        throw new IllegalStateException(
                "Falta definir la migración de la base de datos"
        );
    }

    public long insertarTrampa(String codigoSector, String tipoTrampa,
                               int nivelCaptura, String observaciones) {
        ContentValues valores = new ContentValues();
        valores.put("codigo_sector", codigoSector);
        valores.put("tipo_trampa", tipoTrampa);
        valores.put("nivel_captura", nivelCaptura);
        valores.put("observaciones", observaciones);

        return getWritableDatabase().insert(TABLA, null, valores);
    }

    public Cursor obtenerTrampas() {
        return getReadableDatabase().query(
                TABLA, null, null, null, null, null, "id DESC"
        );
    }

    public Cursor obtenerTrampaPorId(long id) {
        return getReadableDatabase().query(
                TABLA, null, "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
    }

    public int actualizarTrampa(long id, String codigoSector,
                                String tipoTrampa, int nivelCaptura,
                                String observaciones) {
        ContentValues valores = new ContentValues();
        valores.put("codigo_sector", codigoSector);
        valores.put("tipo_trampa", tipoTrampa);
        valores.put("nivel_captura", nivelCaptura);
        valores.put("observaciones", observaciones);

        return getWritableDatabase().update(
                TABLA, valores, "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int eliminarTrampa(long id) {
        return getWritableDatabase().delete(
                TABLA, "id = ?",
                new String[]{String.valueOf(id)}
        );
    }
}