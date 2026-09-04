package controlador;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloSede;
import vista.GestionSedesVista;

public class ControladorGestionSedes {

    private final GestionSedesVista vista;
    private final ModeloSede modelo;

    public ControladorGestionSedes(GestionSedesVista vista) {
        this.vista = vista;
        this.modelo = new ModeloSede();
        initEventListeners();
        cargarTabla();
    }

    private void initEventListeners() {
        vista.getBtnGuardar().addActionListener(this::guardar);
        vista.getBtnModificar().addActionListener(e -> habilitarEdicion());
        vista.getBtnEliminar().addActionListener(this::inhabilitar);
        vista.getBtnVolver().addActionListener(e -> vista.dispose());
        vista.getTblSedes().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = vista.getTblSedes().getSelectedRow();
                if (fila >= 0) {
                    vista.getTxtIdSede().setText(vista.getTblSedes().getValueAt(fila, 0).toString());
                    vista.getTxtNombreSede().setText(vista.getTblSedes().getValueAt(fila, 1).toString());
                    vista.getTxtDireccion().setText(vista.getTblSedes().getValueAt(fila, 2).toString());
                }
            }
        });
    }

    private void guardar(ActionEvent e) {
        try {
            String idStr = vista.getTxtIdSede().getText().trim();
            String nombre = vista.getTxtNombreSede().getText().trim();
            String direccion = vista.getTxtDireccion().getText().trim();

            if (idStr.isEmpty() || nombre.isEmpty() || direccion.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.");
                return;
            }

            int id = Integer.parseInt(idStr);

            if (modelo.registrar(id, nombre, direccion)) {
                JOptionPane.showMessageDialog(vista, "Sede registrada correctamente.");
                limpiarCampos();
                cargarTabla();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El ID debe ser un numero.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void habilitarEdicion() {
        int fila = vista.getTblSedes().getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione una sede de la tabla.");
            return;
        }
        vista.getTxtIdSede().setEditable(true);
        vista.getTxtNombreSede().requestFocus();
    }

    private void inhabilitar(ActionEvent e) {
        try {
            int fila = vista.getTblSedes().getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(vista, "Seleccione una sede de la tabla.");
                return;
            }

            int confirmar = JOptionPane.showConfirmDialog(vista, "Desea inhabilitar esta sede?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirmar == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(vista.getTblSedes().getValueAt(fila, 0).toString());
                if (modelo.inhabilitar(id)) {
                    JOptionPane.showMessageDialog(vista, "Sede inhabilitada.");
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
                new String[]{"ID", "Nombre", "Direccion"}, 0
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_sede"),
                    rs.getString("nombre"),
                    rs.getString("direccion")
                });
            }
            vista.getTblSedes().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorGestionSedes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void limpiarCampos() {
        vista.getTxtIdSede().setText("");
        vista.getTxtNombreSede().setText("");
        vista.getTxtDireccion().setText("");
        vista.getTblSedes().clearSelection();
    }
}
