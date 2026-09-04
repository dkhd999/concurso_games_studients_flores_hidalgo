package controlador;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloConsultaTorneo;
import modelo.ModeloTorneo;
import vista.ConsultaTorneoVista;

public class ControladorConsultaTorneo {

    private final ConsultaTorneoVista vista;
    private final ModeloConsultaTorneo modelo;
    private final ModeloTorneo modeloTorneo;
    private boolean cargandoCombos = false;

    public ControladorConsultaTorneo(ConsultaTorneoVista vista) {
        this.vista = vista;
        this.modelo = new ModeloConsultaTorneo();
        this.modeloTorneo = new ModeloTorneo();
        initEventListeners();
        cargarCombosTorneos();
    }

    private void initEventListeners() {
        vista.getBtnVolver().addActionListener(e -> vista.dispose());
        vista.getCmbTorneo().addActionListener(e -> {
            if (!cargandoCombos) {
                cargarTabla();
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
            cargarTabla();
        } catch (SQLException ex) {
            cargandoCombos = false;
            Logger.getLogger(ControladorConsultaTorneo.class.getName()).log(Level.SEVERE, null, ex);
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

    private void cargarTabla() {
        int torneo = obtenerTorneoSeleccionado();
        if (torneo <= 0) return;
        try {
            ResultSet rs = modelo.consultarInformacion(torneo);
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Torneo", "Inicio", "Fin", "Premio", "Codigo", "Equipo", "Pais", "F. Inscr."}, 0
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_torneo"),
                    rs.getString("torneo"),
                    rs.getString("fecha_inicio"),
                    rs.getString("fecha_fin"),
                    rs.getDouble("premio_total"),
                    rs.getInt("codigo_equipo"),
                    rs.getString("equipo"),
                    rs.getString("pais_procedencia"),
                    rs.getString("fecha_inscripcion")
                });
            }
            vista.getTablaGeneral().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorConsultaTorneo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
