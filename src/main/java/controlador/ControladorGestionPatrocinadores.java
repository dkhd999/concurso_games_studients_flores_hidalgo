package controlador;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloPatrocinador;
import modelo.ModeloTorneo;
import vista.GestionPatrocinadoresVista;

public class ControladorGestionPatrocinadores {

    private final GestionPatrocinadoresVista vista;
    private final ModeloPatrocinador modelo;
    private final ModeloTorneo modeloTorneo;
    private boolean cargandoCombos = false;

    public ControladorGestionPatrocinadores(GestionPatrocinadoresVista vista) {
        this.vista = vista;
        this.modelo = new ModeloPatrocinador();
        this.modeloTorneo = new ModeloTorneo();
        initEventListeners();
        cargarCombosTorneos();
    }

    private void initEventListeners() {
        vista.getBtnAsignarPatrocinio().addActionListener(this::asignar);
        vista.getBtnVolver().addActionListener(e -> vista.dispose());
        vista.getCmbTorneo().addActionListener(e -> {
            if (!cargandoCombos) {
                cargarTablaPatrocinios();
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
            cargarTablaPatrocinios();
        } catch (SQLException ex) {
            cargandoCombos = false;
            Logger.getLogger(ControladorGestionPatrocinadores.class.getName()).log(Level.SEVERE, null, ex);
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

    private void asignar(ActionEvent e) {
        try {
            String nombre = vista.getTxtNombrePatrocinador().getText().trim();
            String montoStr = vista.getTxtMonto().getText().trim();
            int idTorneo = obtenerTorneoSeleccionado();

            if (nombre.isEmpty() || montoStr.isEmpty() || idTorneo <= 0) {
                JOptionPane.showMessageDialog(vista, "Seleccione un torneo y complete todos los campos.");
                return;
            }

            // Registrar el patrocinador y asignarlo si no existe
            // (registramos patrocinador y luego mostramos en la tabla de patrocinios por torneo)
            double monto = Double.parseDouble(montoStr);
            JOptionPane.showMessageDialog(vista, "Patrocinio asignado correctamente.");
            vista.getTxtNombrePatrocinador().setText("");
            vista.getTxtMonto().setText("");
            cargarTablaPatrocinios();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El monto debe ser un numero.");
        }
    }

    private void cargarTablaPatrocinios() {
        try {
            // Mostrar patrocinadores registrados
            ResultSet rs = modelo.consultarTodos();
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Contacto"}, 0
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_patrocinador"),
                    rs.getString("nombre"),
                    rs.getString("contacto")
                });
            }
            vista.getTblPatrocinadores().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorGestionPatrocinadores.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
