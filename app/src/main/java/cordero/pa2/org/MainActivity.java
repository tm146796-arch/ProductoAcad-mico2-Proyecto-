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

    private static final int TAB_NUEVO = 0;
    private static final int TAB_LISTAR = 1;
    private static final int TAB_EDITAR = 2;
    private static final int TAB_BORRAR = 3;

    private DatabaseHelper db;
    private EditText etSolicitante, etProblema, etPrioridad, etEstado;
    private EditText etIdActualizar, etSolicitanteActualizar, etProblemaActualizar;
    private EditText etPrioridadActualizar, etEstadoActualizar, etIdEliminar;
    private TextView tvResultado;
    private View tabNuevo, tabListar, tabEditar, tabBorrar;
    private Button btnTabNuevo, btnTabListar, btnTabEditar, btnTabBorrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ajustarBarrasDelSistema(findViewById(R.id.rootScroll));

        db = new DatabaseHelper(this);

        etSolicitante = findViewById(R.id.etSolicitante);
        etProblema = findViewById(R.id.etProblema);
        etPrioridad = findViewById(R.id.etPrioridad);
        etEstado = findViewById(R.id.etEstado);
        etIdActualizar = findViewById(R.id.etIdActualizar);
        etSolicitanteActualizar = findViewById(R.id.etSolicitanteActualizar);
        etProblemaActualizar = findViewById(R.id.etProblemaActualizar);
        etPrioridadActualizar = findViewById(R.id.etPrioridadActualizar);
        etEstadoActualizar = findViewById(R.id.etEstadoActualizar);
        etIdEliminar = findViewById(R.id.etIdEliminar);
        tvResultado = findViewById(R.id.tvResultado);
        tabNuevo = findViewById(R.id.tabNuevo);
        tabListar = findViewById(R.id.tabListar);
        tabEditar = findViewById(R.id.tabEditar);
        tabBorrar = findViewById(R.id.tabBorrar);
        btnTabNuevo = findViewById(R.id.btnTabNuevo);
        btnTabListar = findViewById(R.id.btnTabListar);
        btnTabEditar = findViewById(R.id.btnTabEditar);
        btnTabBorrar = findViewById(R.id.btnTabBorrar);

        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnListar = findViewById(R.id.btnListar);
        Button btnActualizar = findViewById(R.id.btnActualizar);
        Button btnEliminar = findViewById(R.id.btnEliminar);

        btnTabNuevo.setOnClickListener(v -> mostrarTab(TAB_NUEVO));
        btnTabListar.setOnClickListener(v -> {
            mostrarTab(TAB_LISTAR);
            listar();
        });
        btnTabEditar.setOnClickListener(v -> mostrarTab(TAB_EDITAR));
        btnTabBorrar.setOnClickListener(v -> mostrarTab(TAB_BORRAR));
        btnGuardar.setOnClickListener(v -> guardar());
        btnListar.setOnClickListener(v -> listar());
        btnActualizar.setOnClickListener(v -> actualizar());
        btnEliminar.setOnClickListener(v -> eliminar());

        mostrarTab(TAB_NUEVO);
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

    private void mostrarTab(int tab) {
        tabNuevo.setVisibility(tab == TAB_NUEVO ? View.VISIBLE : View.GONE);
        tabListar.setVisibility(tab == TAB_LISTAR ? View.VISIBLE : View.GONE);
        tabEditar.setVisibility(tab == TAB_EDITAR ? View.VISIBLE : View.GONE);
        tabBorrar.setVisibility(tab == TAB_BORRAR ? View.VISIBLE : View.GONE);

        pintarTab(btnTabNuevo, tab == TAB_NUEVO);
        pintarTab(btnTabListar, tab == TAB_LISTAR);
        pintarTab(btnTabEditar, tab == TAB_EDITAR);
        pintarTab(btnTabBorrar, tab == TAB_BORRAR);
    }

    private void pintarTab(Button boton, boolean activo) {
        boton.setBackgroundResource(activo ? R.drawable.bg_tab_activo : R.drawable.bg_tab_inactivo);
        boton.setTextColor(getColor(activo ? android.R.color.white : R.color.color_texto_suave));
    }

    private void marcarCampo(EditText campo, boolean error) {
        campo.setBackgroundResource(error ? R.drawable.bg_campo_error : R.drawable.bg_campo);
    }

    private boolean validarCampo(EditText campo) {
        boolean valido = !texto(campo).isEmpty();
        marcarCampo(campo, !valido);
        return valido;
    }

    private boolean validarSolicitud(EditText solicitante, EditText problema,
                                     EditText prioridad, EditText estado) {
        boolean valido = true;
        valido = validarCampo(solicitante) && valido;
        valido = validarCampo(problema) && valido;
        valido = validarCampo(prioridad) && valido;
        valido = validarCampo(estado) && valido;

        if (!valido) {
            Toast.makeText(this, "Completa los campos marcados en rojo", Toast.LENGTH_SHORT).show();
        }
        return valido;
    }

    private void guardar() {
        if (!validarSolicitud(etSolicitante, etProblema, etPrioridad, etEstado)) return;

        long id = db.insertarSolicitud(texto(etSolicitante), texto(etProblema),
                texto(etPrioridad), texto(etEstado));

        if (id != -1) {
            Toast.makeText(this, "Solicitud guardada con ID " + id, Toast.LENGTH_SHORT).show();
            limpiarCampos(etSolicitante, etProblema, etPrioridad, etEstado);
            listar();
            mostrarTab(TAB_LISTAR);
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

    private Integer leerId(EditText campo) {
        String valor = texto(campo);
        if (valor.isEmpty()) {
            marcarCampo(campo, true);
            Toast.makeText(this, "Ingresa el ID requerido", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            int id = Integer.parseInt(valor);
            if (id <= 0) throw new NumberFormatException();
            marcarCampo(campo, false);
            return id;
        } catch (NumberFormatException e) {
            marcarCampo(campo, true);
            Toast.makeText(this, "El ID debe ser mayor a 0", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void actualizar() {
        Integer id = leerId(etIdActualizar);
        boolean datosValidos = validarSolicitud(etSolicitanteActualizar, etProblemaActualizar,
                etPrioridadActualizar, etEstadoActualizar);
        if (id == null || !datosValidos) return;

        int filas = db.actualizarSolicitud(id, texto(etSolicitanteActualizar), texto(etProblemaActualizar),
                texto(etPrioridadActualizar), texto(etEstadoActualizar));

        Toast.makeText(this, filas > 0 ? "Solicitud actualizada" : "ID no encontrado",
                Toast.LENGTH_SHORT).show();
        listar();
        mostrarTab(TAB_LISTAR);
    }

    private void eliminar() {
        Integer id = leerId(etIdEliminar);
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
                    mostrarTab(TAB_LISTAR);
                })
                .show();
    }

    private void limpiarCampos(EditText... campos) {
        for (EditText campo : campos) {
            campo.setText("");
            marcarCampo(campo, false);
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
