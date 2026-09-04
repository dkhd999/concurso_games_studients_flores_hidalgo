package controlador;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloPlantilla;
import modelo.ModeloTorneo;
import modelo.ModeloEquipo;
import modelo.ModeloJugador;
import vista.AsignacionJugadoresVista;

public class ControladorAsignacionJugadores {

    private final AsignacionJugadoresVista vista;
    private final ModeloPlantilla modelo;
    private final ModeloTorneo modeloTorneo;
    private final ModeloEquipo modeloEquipo;
    private final ModeloJugador modeloJugador;
    private boolean cargandoCombos = false;

    public ControladorAsignacionJugadores(AsignacionJugadoresVista vista) {
        this.vista = vista;
        this.modelo = new ModeloPlantilla();
        this.modeloTorneo = new ModeloTorneo();
        this.modeloEquipo = new ModeloEquipo();
        this.modeloJugador = new ModeloJugador();
        initEventListeners();
        cargarCombosTorneos();
        cargarCombosEquipos();
        cargarCombosJugadores();
    }

    private void initEventListeners() {
        vista.getBtnAgregarJugadorAEquipo().addActionListener(this::asignar);
        vista.getBtnVolver().addActionListener(e -> vista.dispose());
        vista.getCmbTorneo().addActionListener(e -> {
            if (!cargandoCombos) {
                cargarTablaPlantilla();
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
            cargarTablaPlantilla();
        } catch (SQLException ex) {
            cargandoCombos = false;
            Logger.getLogger(ControladorAsignacionJugadores.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargarCombosEquipos() {
        try {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            ResultSet rs = modeloEquipo.consultarTodos();
            while (rs.next()) {
                model.addElement(rs.getInt("codigo_equipo") + " - " + rs.getString("nombre"));
            }
            vista.getCmbEquipo().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorAsignacionJugadores.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargarCombosJugadores() {
        try {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            ResultSet rs = modeloJugador.consultarTodos();
            while (rs.next()) {
                model.addElement(rs.getString("nickname") + " - " + rs.getString("nombre_real"));
            }
            vista.getCmbJugador().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorAsignacionJugadores.class.getName()).log(Level.SEVERE, null, ex);
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

    private int obtenerEquipoSeleccionado() {
        Object sel = vista.getCmbEquipo().getSelectedItem();
        if (sel == null) return 0;
        String txt = sel.toString();
        int idx = txt.indexOf(" - ");
        if (idx > 0) return Integer.parseInt(txt.substring(0, idx).trim());
        return 0;
    }

    private String obtenerJugadorSeleccionado() {
        Object sel = vista.getCmbJugador().getSelectedItem();
        if (sel == null) return "";
        String txt = sel.toString();
        int idx = txt.indexOf(" - ");
        if (idx > 0) return txt.substring(0, idx).trim();
        return txt.trim();
    }

    private void asignar(ActionEvent e) {
        try {
            String jugador = obtenerJugadorSeleccionado();
            int torneo = obtenerTorneoSeleccionado();
            int equipo = obtenerEquipoSeleccionado();

            if (jugador.isEmpty() || torneo <= 0 || equipo <= 0) {
                JOptionPane.showMessageDialog(vista, "Seleccione torneo, equipo y jugador.");
                return;
            }

            if (modelo.asignar(jugador, torneo, equipo)) {
                JOptionPane.showMessageDialog(vista, "Jugador asignado al equipo correctamente.");
                cargarTablaPlantilla();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void cargarTablaPlantilla() {
        int torneo = obtenerTorneoSeleccionado();
        if (torneo <= 0) return;
        try {
            ResultSet rs = modelo.consultarParticipacion(torneo);
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Nickname", "Nombre", "Rol", "Equipo"}, 0
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nickname"),
                    rs.getString("nombre_real"),
                    rs.getString("rol"),
                    rs.getString("equipo")
                });
            }
            vista.getTablaPlantilla().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorAsignacionJugadores.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
