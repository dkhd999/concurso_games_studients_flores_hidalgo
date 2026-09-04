package controlador;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloInscripcion;
import modelo.ModeloTorneo;
import modelo.ModeloEquipo;
import vista.InscripcionEquiposVista;

public class ControladorInscripcionEquipos {

    private final InscripcionEquiposVista vista;
    private final ModeloInscripcion modelo;
    private final ModeloTorneo modeloTorneo;
    private final ModeloEquipo modeloEquipo;
    private boolean cargandoCombos = false;

    public ControladorInscripcionEquipos(InscripcionEquiposVista vista) {
        this.vista = vista;
        this.modelo = new ModeloInscripcion();
        this.modeloTorneo = new ModeloTorneo();
        this.modeloEquipo = new ModeloEquipo();
        initEventListeners();
        cargarCombosTorneos();
        cargarCombosEquipos();
    }

    private void initEventListeners() {
        vista.getBtnInscribirEquipo().addActionListener(this::inscribir);
        vista.getBtnVolver().addActionListener(e -> vista.dispose());
        vista.getCmbTorneo().addActionListener(e -> {
            if (!cargandoCombos) {
                cargarTablaInscripciones();
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
            cargarTablaInscripciones();
        } catch (SQLException ex) {
            cargandoCombos = false;
            Logger.getLogger(ControladorInscripcionEquipos.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ControladorInscripcionEquipos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int obtenerTorneoSeleccionado() {
        Object sel = vista.getCmbTorneo().getSelectedItem();
        if (sel == null) return 0;
        String txt = sel.toString();
        int idx = txt.indexOf(" - ");
        if (idx > 0) {
            return Integer.parseInt(txt.substring(0, idx).trim());
        }
        return 0;
    }

    private int obtenerEquipoSeleccionado() {
        Object sel = vista.getCmbEquipo().getSelectedItem();
        if (sel == null) return 0;
        String txt = sel.toString();
        int idx = txt.indexOf(" - ");
        if (idx > 0) {
            return Integer.parseInt(txt.substring(0, idx).trim());
        }
        return 0;
    }

    private void inscribir(ActionEvent e) {
        try {
            int equipo = obtenerEquipoSeleccionado();
            int torneo = obtenerTorneoSeleccionado();
            String fecha = vista.getTxtFechaInscripcion().getText().trim();

            if (equipo <= 0 || torneo <= 0 || fecha.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Seleccione torneo, equipo y la fecha.");
                return;
            }

            if (modelo.inscribir(equipo, torneo, fecha)) {
                JOptionPane.showMessageDialog(vista, "Equipo inscrito correctamente.");
                vista.getTxtFechaInscripcion().setText("");
                cargarTablaInscripciones();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void cargarTablaInscripciones() {
        int torneo = obtenerTorneoSeleccionado();
        if (torneo <= 0) return;
        try {
            ResultSet rs = modelo.consultarPorTorneo(torneo);
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Codigo Equipo", "Equipo", "Fecha Inscripcion"}, 0
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("codigo_equipo"),
                    rs.getString("equipo"),
                    rs.getString("fecha_inscripcion")
                });
            }
            vista.getTblInscripciones().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorInscripcionEquipos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
