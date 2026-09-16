package cordero.pa2.org;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "soporte.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "solicitudes";

    private static final String COL_ID = "id";
    private static final String COL_SOLICITANTE = "solicitante";
    private static final String COL_PROBLEMA = "problema";
    private static final String COL_PRIORIDAD = "prioridad";
    private static final String COL_ESTADO = "estado";

    public DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SOLICITANTE + " TEXT NOT NULL, " +
                COL_PROBLEMA + " TEXT NOT NULL, " +
                COL_PRIORIDAD + " TEXT NOT NULL, " +
                COL_ESTADO + " TEXT NOT NULL)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertarSolicitud(String solicitante, String problema, String prioridad, String estado) {
        ContentValues values = new ContentValues();
        values.put(COL_SOLICITANTE, solicitante);
        values.put(COL_PROBLEMA, problema);
        values.put(COL_PRIORIDAD, prioridad);
        values.put(COL_ESTADO, estado);
        return getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public Cursor listarSolicitudes() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_ID + " DESC", null);
    }

    public int actualizarSolicitud(int id, String solicitante, String problema, String prioridad, String estado) {
        ContentValues values = new ContentValues();
        values.put(COL_SOLICITANTE, solicitante);
        values.put(COL_PROBLEMA, problema);
        values.put(COL_PRIORIDAD, prioridad);
        values.put(COL_ESTADO, estado);
        return getWritableDatabase().update(TABLE_NAME, values, COL_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public int eliminarSolicitud(int id) {
        return getWritableDatabase().delete(TABLE_NAME, COL_ID + "=?",
                new String[]{String.valueOf(id)});
    }
}
