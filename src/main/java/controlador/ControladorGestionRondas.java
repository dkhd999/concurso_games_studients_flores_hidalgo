package controlador;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloRonda;
import modelo.ModeloTorneo;
import vista.GestionRondasVista;

public class ControladorGestionRondas {

    private final GestionRondasVista vista;
    private final ModeloRonda modelo;
    private final ModeloTorneo modeloTorneo;
    private boolean cargandoCombos = false;

    public ControladorGestionRondas(GestionRondasVista vista) {
        this.vista = vista;
        this.modelo = new ModeloRonda();
        this.modeloTorneo = new ModeloTorneo();
        initEventListeners();
        cargarCombosTorneos();
    }

    private void initEventListeners() {
        vista.getBtnCrearRonda().addActionListener(this::crearRonda);
        vista.getBtnVolver().addActionListener(e -> vista.dispose());
        vista.getCmbTorneo().addActionListener(e -> {
            if (!cargandoCombos) {
                cargarTablaRondas();
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
            cargarTablaRondas();
        } catch (SQLException ex) {
            cargandoCombos = false;
            Logger.getLogger(ControladorGestionRondas.class.getName()).log(Level.SEVERE, null, ex);
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

    private void crearRonda(ActionEvent e) {
        try {
            String numStr = vista.getTxtNumeroRonda().getText().trim();
            String nombre = vista.getTxtNombreRonda().getText().trim();
            int idTorneo = obtenerTorneoSeleccionado();

            if (numStr.isEmpty() || nombre.isEmpty() || idTorneo <= 0) {
                JOptionPane.showMessageDialog(vista, "Seleccione un torneo y complete todos los campos.");
                return;
            }

            int numero = Integer.parseInt(numStr);
            if (modelo.registrar(idTorneo, numero, nombre)) {
                JOptionPane.showMessageDialog(vista, "Ronda creada correctamente.");
                vista.getTxtNumeroRonda().setText("");
                vista.getTxtNombreRonda().setText("");
                cargarTablaRondas();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El numero de ronda debe ser un numero.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void cargarTablaRondas() {
        int idTorneo = obtenerTorneoSeleccionado();
        if (idTorneo <= 0) return;
        try {
            ResultSet rs = modelo.consultarPorTorneo(idTorneo);
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Nº Ronda", "Nombre"}, 0
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("numero_ronda"),
                    rs.getString("nombre")
                });
            }
            vista.getTblRondas().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorGestionRondas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
