package cordero.pa2.org;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private DatabaseHelper db;
    private EditText etId, etSolicitante, etProblema, etPrioridad, etEstado;
    private TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ajustarBarrasDelSistema(findViewById(R.id.rootScroll));

        db = new DatabaseHelper(this);

        etId = findViewById(R.id.etId);
        etSolicitante = findViewById(R.id.etSolicitante);
        etProblema = findViewById(R.id.etProblema);
        etPrioridad = findViewById(R.id.etPrioridad);
        etEstado = findViewById(R.id.etEstado);
        tvResultado = findViewById(R.id.tvResultado);

        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnListar = findViewById(R.id.btnListar);
        Button btnActualizar = findViewById(R.id.btnActualizar);
        Button btnEliminar = findViewById(R.id.btnEliminar);

        btnGuardar.setOnClickListener(v -> guardar());
        btnListar.setOnClickListener(v -> listar());
        btnActualizar.setOnClickListener(v -> actualizar());
        btnEliminar.setOnClickListener(v -> eliminar());
    }

    private void ajustarBarrasDelSistema(View vista) {
        int izquierda = vista.getPaddingLeft();
        int arriba = vista.getPaddingTop();
        int derecha = vista.getPaddingRight();
        int abajo = vista.getPaddingBottom();

        vista.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(
                    izquierda + insets.getSystemWindowInsetLeft(),
                    arriba + insets.getSystemWindowInsetTop(),
                    derecha + insets.getSystemWindowInsetRight(),
                    abajo + insets.getSystemWindowInsetBottom()
            );
            return insets;
        });
        vista.requestApplyInsets();
    }

    private String texto(EditText campo) {
        return campo.getText().toString().trim();
    }

    private boolean camposCompletos() {
        if (texto(etSolicitante).isEmpty() || texto(etProblema).isEmpty()
                || texto(etPrioridad).isEmpty() || texto(etEstado).isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void guardar() {
        if (!camposCompletos()) return;

        long id = db.insertarSolicitud(texto(etSolicitante), texto(etProblema),
                texto(etPrioridad), texto(etEstado));

        if (id != -1) {
            Toast.makeText(this, "Solicitud guardada con ID " + id, Toast.LENGTH_SHORT).show();
            listar();
        } else {
            Toast.makeText(this, "No se pudo guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void listar() {
        StringBuilder sb = new StringBuilder();
        try (Cursor cursor = db.listarSolicitudes()) {
            while (cursor.moveToNext()) {
                sb.append("ID: ").append(cursor.getInt(0)).append("\n")
                        .append("Solicitante: ").append(cursor.getString(1)).append("\n")
                        .append("Problema: ").append(cursor.getString(2)).append("\n")
                        .append("Prioridad: ").append(cursor.getString(3)).append("\n")
                        .append("Estado: ").append(cursor.getString(4)).append("\n\n");
            }
        }
        tvResultado.setText(sb.length() == 0 ? "No hay solicitudes registradas." : sb.toString());
    }

    private Integer leerId() {
        String valor = texto(etId);
        if (valor.isEmpty()) {
            Toast.makeText(this, "Ingresa el ID", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            int id = Integer.parseInt(valor);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID no válido", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void actualizar() {
        Integer id = leerId();
        if (id == null || !camposCompletos()) return;

        int filas = db.actualizarSolicitud(id, texto(etSolicitante), texto(etProblema),
                texto(etPrioridad), texto(etEstado));

        Toast.makeText(this, filas > 0 ? "Solicitud actualizada" : "ID no encontrado",
                Toast.LENGTH_SHORT).show();
        listar();
    }

    private void eliminar() {
        Integer id = leerId();
        if (id == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Eliminar solicitud")
                .setMessage("¿Deseas eliminar la solicitud con ID " + id + "?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    int filas = db.eliminarSolicitud(id);
                    Toast.makeText(this, filas > 0 ? "Solicitud eliminada" : "ID no encontrado",
                            Toast.LENGTH_SHORT).show();
                    listar();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
