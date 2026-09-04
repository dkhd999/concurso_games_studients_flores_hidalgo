package controlador;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloTorneo;
import vista.GestionTorneoVista;

public class ControladorGestionTorneo {

    private final GestionTorneoVista vista;
    private final ModeloTorneo modelo;

    public ControladorGestionTorneo(GestionTorneoVista vista) {
        this.vista = vista;
        this.modelo = new ModeloTorneo();
        initEventListeners();
        cargarTabla();
    }

    private void initEventListeners() {
        vista.getBtnGuardar().addActionListener(this::guardar);
        vista.getBtnModificar().addActionListener(this::modificar);
        vista.getBtnEliminar().addActionListener(this::inhabilitar);
        vista.getBtnVolver().addActionListener(e -> vista.dispose());
        vista.getTblTorneos().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = vista.getTblTorneos().getSelectedRow();
                if (fila >= 0) {
                    vista.getTxtNombre().setText(vista.getTblTorneos().getValueAt(fila, 1).toString());
                    vista.getTxtFechaInicio().setText(vista.getTblTorneos().getValueAt(fila, 2).toString());
                    vista.getTxtFechaFin1().setText(vista.getTblTorneos().getValueAt(fila, 3).toString());
                    vista.getTxtPremio().setText(vista.getTblTorneos().getValueAt(fila, 4).toString());
                }
            }
        });
    }

    private void guardar(ActionEvent e) {
        try {
            String nombre = vista.getTxtNombre().getText().trim();
            String fechaInicio = vista.getTxtFechaInicio().getText().trim();
            String fechaFin = vista.getTxtFechaFin1().getText().trim();
            String premioStr = vista.getTxtPremio().getText().trim();

            if (nombre.isEmpty() || fechaInicio.isEmpty() || fechaFin.isEmpty() || premioStr.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.");
                return;
            }

            int id = obtenerSiguienteId();
            double premio = Double.parseDouble(premioStr);

            if (modelo.registrar(id, nombre, fechaInicio, fechaFin, premio)) {
                JOptionPane.showMessageDialog(vista, "Torneo registrado correctamente.");
                limpiarCampos();
                cargarTabla();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El premio debe ser un numero.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void modificar(ActionEvent e) {
        try {
            int fila = vista.getTblTorneos().getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(vista, "Seleccione un torneo de la tabla.");
                return;
            }

            int id = Integer.parseInt(vista.getTblTorneos().getValueAt(fila, 0).toString());
            String nombre = vista.getTxtNombre().getText().trim();
            String fechaInicio = vista.getTxtFechaInicio().getText().trim();
            String fechaFin = vista.getTxtFechaFin1().getText().trim();
            double premio = Double.parseDouble(vista.getTxtPremio().getText().trim());

            if (modelo.actualizar(id, nombre, fechaInicio, fechaFin, premio)) {
                JOptionPane.showMessageDialog(vista, "Torneo actualizado correctamente.");
                limpiarCampos();
                cargarTabla();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Verifique los datos numericos.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void inhabilitar(ActionEvent e) {
        try {
            int fila = vista.getTblTorneos().getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(vista, "Seleccione un torneo de la tabla.");
                return;
            }

            int confirmar = JOptionPane.showConfirmDialog(vista, "Desea inhabilitar este torneo?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirmar == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(vista.getTblTorneos().getValueAt(fila, 0).toString());
                if (modelo.inhabilitar(id)) {
                    JOptionPane.showMessageDialog(vista, "Torneo inhabilitado.");
                    limpiarCampos();
                    cargarTabla();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void cargarTabla() {
        try {
            ResultSet rs = modelo.consultarTodos();
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Fecha Inicio", "Fecha Fin", "Premio"}, 0
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_torneo"),
                    rs.getString("nombre"),
                    rs.getString("fecha_inicio"),
                    rs.getString("fecha_fin"),
                    rs.getDouble("premio_total")
                });
            }
            vista.getTblTorneos().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorGestionTorneo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void limpiarCampos() {
        vista.getTxtNombre().setText("");
        vista.getTxtFechaInicio().setText("");
        vista.getTxtFechaFin1().setText("");
        vista.getTxtPremio().setText("");
        vista.getTblTorneos().clearSelection();
    }

    private int obtenerSiguienteId() throws SQLException {
        ResultSet rs = modelo.consultarTodos();
        int max = 0;
        while (rs.next()) {
            int id = rs.getInt("id_torneo");
            if (id > max) max = id;
        }
        return max + 1;
    }
}
