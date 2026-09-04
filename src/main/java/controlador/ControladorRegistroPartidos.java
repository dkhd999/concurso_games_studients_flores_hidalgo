package controlador;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloPartido;
import modelo.ModeloTorneo;
import modelo.ModeloEquipo;
import modelo.ModeloArbitro;
import modelo.ModeloRonda;
import modelo.ModeloSede;
import vista.RegistroPartidosVista;

public class ControladorRegistroPartidos {

    private final RegistroPartidosVista vista;
    private final ModeloPartido modelo;
    private final ModeloTorneo modeloTorneo;
    private final ModeloEquipo modeloEquipo;
    private final ModeloArbitro modeloArbitro;
    private final ModeloRonda modeloRonda;
    private final ModeloSede modeloSede;
    private boolean cargandoCombos = false;

    public ControladorRegistroPartidos(RegistroPartidosVista vista) {
        this.vista = vista;
        this.modelo = new ModeloPartido();
        this.modeloTorneo = new ModeloTorneo();
        this.modeloEquipo = new ModeloEquipo();
        this.modeloArbitro = new ModeloArbitro();
        this.modeloRonda = new ModeloRonda();
        this.modeloSede = new ModeloSede();
        initEventListeners();
        cargarCombosTorneos();
        cargarCombosEquipos();
        cargarCombosArbitros();
        cargarCombosSedes();
    }

    private void initEventListeners() {
        vista.getBtnGuardarPartido().addActionListener(this::guardar);
        vista.getBtnActualizarMarcador().addActionListener(this::actualizarMarcador);
        vista.getBtnVolver().addActionListener(e -> vista.dispose());
        vista.getCmbTorneo().addActionListener(e -> {
            if (!cargandoCombos) {
                cargarRondas();
                cargarTablaPartidos();
            }
        });
    }

    private void cargarCombosTorneos() {
        try {
            cargandoCombos = true;
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            ResultSet rs = modeloTorneo.consultarTodos();
            while (rs.next()) {
                model.addElement(rs.getInt("id_torneo") + " - " + rs.getString("nombre"));
            }
            vista.getCmbTorneo().setModel(model);
            cargandoCombos = false;
            cargarRondas();
            cargarTablaPartidos();
        } catch (SQLException ex) {
            cargandoCombos = false;
            Logger.getLogger(ControladorRegistroPartidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargarCombosEquipos() {
        try {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            ResultSet rs = modeloEquipo.consultarTodos();
            while (rs.next()) {
                model.addElement(rs.getInt("codigo_equipo") + " - " + rs.getString("nombre"));
            }
            vista.getCmbEquipoLocal().setModel(model);
            vista.getCmbEquipo2().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorRegistroPartidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargarCombosArbitros() {
        try {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            ResultSet rs = modeloArbitro.consultarTodos();
            while (rs.next()) {
                model.addElement(rs.getInt("id_arbitro") + " - " + rs.getString("nombre"));
            }
            vista.getCmbArbitro().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorRegistroPartidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargarCombosSedes() {
        try {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            ResultSet rs = modeloSede.consultarTodos();
            while (rs.next()) {
                model.addElement(rs.getInt("id_sede") + " - " + rs.getString("nombre"));
            }
            vista.getCmbSede().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorRegistroPartidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargarRondas() {
        int torneo = obtenerTorneoSeleccionado();
        try {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            ResultSet rs = modeloRonda.consultarPorTorneo(torneo);
            while (rs.next()) {
                model.addElement(rs.getInt("numero_ronda") + " - " + rs.getString("nombre"));
            }
            vista.getCmbRonda().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorRegistroPartidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int obtenerTorneoSeleccionado() {
        Object sel = vista.getCmbTorneo().getSelectedItem();
        if (sel == null) return 0;
        String txt = sel.toString();
        int idx = txt.indexOf(" - ");
        if (idx > 0) return Integer.parseInt(txt.substring(0, idx).trim());
        return 0;
    }

    private int obtenerIdDeCombo(javax.swing.JComboBox<String> combo) {
        Object sel = combo.getSelectedItem();
        if (sel == null) return 0;
        String txt = sel.toString();
        int idx = txt.indexOf(" - ");
        if (idx > 0) return Integer.parseInt(txt.substring(0, idx).trim());
        return 0;
    }

    private void guardar(ActionEvent e) {
        try {
            String fecha = vista.getTxtFecha().getText().trim();
            String hora = "18:00:00";
            String marcador = vista.getTxtMarcador().getText().trim();
            int torneo = obtenerTorneoSeleccionado();
            int ronda = obtenerIdDeCombo(vista.getCmbRonda());
            int equipoLocal = obtenerIdDeCombo(vista.getCmbEquipoLocal());
            int equipoVisita = obtenerIdDeCombo(vista.getCmbEquipo2());
            int arbitro = obtenerIdDeCombo(vista.getCmbArbitro());
            int sede = obtenerIdDeCombo(vista.getCmbSede());

            if (fecha.isEmpty() || torneo <= 0 || ronda <= 0 || equipoLocal <= 0 || equipoVisita <= 0 || arbitro <= 0 || sede <= 0) {
                JOptionPane.showMessageDialog(vista, "Complete todos los campos del partido.");
                return;
            }

            int idPartido = obtenerSiguienteIdPartido();
            if (modelo.registrar(idPartido, fecha, hora, marcador, torneo, ronda, equipoLocal, equipoVisita, arbitro, sede)) {
                JOptionPane.showMessageDialog(vista, "Partido guardado correctamente.");
                vista.getTxtFecha().setText("");
                vista.getTxtMarcador().setText("");
                cargarTablaPartidos();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void actualizarMarcador(ActionEvent e) {
        int fila = vista.getTblPartidos().getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione un partido de la tabla.");
            return;
        }
        String marcador = vista.getTxtMarcador().getText().trim();
        if (marcador.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el marcador.");
            return;
        }
        JOptionPane.showMessageDialog(vista, "Marcador actualizado a: " + marcador);
    }

    private int obtenerSiguienteIdPartido() throws SQLException {
        ResultSet rs = modelo.consultarPorTorneo(obtenerTorneoSeleccionado());
        int max = 5000;
        while (rs.next()) {
            int id = rs.getInt("id_partido");
            if (id > max) max = id;
        }
        return max + 1;
    }

    private void cargarTablaPartidos() {
        int torneo = obtenerTorneoSeleccionado();
        if (torneo <= 0) return;
        try {
            ResultSet rs = modelo.consultarPorTorneo(torneo);
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Fecha", "Hora", "Ronda", "Local", "Visita", "Marcador", "Arbitro", "Sede"}, 0
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_partido"),
                    rs.getString("fecha"),
                    rs.getString("hora"),
                    rs.getString("ronda"),
                    rs.getString("equipo_local"),
                    rs.getString("equipo_visitante"),
                    rs.getString("marcador_final"),
                    rs.getString("arbitro"),
                    rs.getString("sede")
                });
            }
            vista.getTblPartidos().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorRegistroPartidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
