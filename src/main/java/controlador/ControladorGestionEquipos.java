package controlador;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloEquipo;
import vista.GestionEquiposVista;

public class ControladorGestionEquipos {

    private final GestionEquiposVista vista;
    private final ModeloEquipo modelo;

    public ControladorGestionEquipos(GestionEquiposVista vista) {
        this.vista = vista;
        this.modelo = new ModeloEquipo();
        initEventListeners();
        cargarTabla();
    }

    private void initEventListeners() {
        vista.getBtnGuardar().addActionListener(this::guardar);
        vista.getBtnModificar().addActionListener(this::modificar);
        vista.getBtnEliminar().addActionListener(this::inhabilitar);
        vista.getBtnVolver().addActionListener(e -> vista.dispose());
        vista.getTblEquipos().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = vista.getTblEquipos().getSelectedRow();
                if (fila >= 0) {
                    vista.getTxtCodigo().setText(vista.getTblEquipos().getValueAt(fila, 0).toString());
                    vista.getTxtNombre().setText(vista.getTblEquipos().getValueAt(fila, 1).toString());
                    vista.getTxtPais().setText(vista.getTblEquipos().getValueAt(fila, 2).toString());
                    vista.getTxtFechaFundacion().setText(vista.getTblEquipos().getValueAt(fila, 3).toString());
                }
            }
        });
    }

    private void guardar(ActionEvent e) {
        try {
            String codigoStr = vista.getTxtCodigo().getText().trim();
            String nombre = vista.getTxtNombre().getText().trim();
            String pais = vista.getTxtPais().getText().trim();
            String fecha = vista.getTxtFechaFundacion().getText().trim();

            if (codigoStr.isEmpty() || nombre.isEmpty() || pais.isEmpty() || fecha.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.");
                return;
            }

            int codigo = Integer.parseInt(codigoStr);

            if (modelo.registrar(codigo, nombre, pais, fecha, 0)) {
                JOptionPane.showMessageDialog(vista, "Equipo registrado correctamente.");
                limpiarCampos();
                cargarTabla();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El codigo debe ser un numero.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void modificar(ActionEvent e) {
        try {
            int fila = vista.getTblEquipos().getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(vista, "Seleccione un equipo de la tabla.");
                return;
            }

            int codigo = Integer.parseInt(vista.getTblEquipos().getValueAt(fila, 0).toString());
            String nombre = vista.getTxtNombre().getText().trim();
            String pais = vista.getTxtPais().getText().trim();
            String fecha = vista.getTxtFechaFundacion().getText().trim();

            if (modelo.actualizar(codigo, nombre, pais, fecha, 0)) {
                JOptionPane.showMessageDialog(vista, "Equipo actualizado correctamente.");
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
            int fila = vista.getTblEquipos().getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(vista, "Seleccione un equipo de la tabla.");
                return;
            }

            int confirmar = JOptionPane.showConfirmDialog(vista, "Desea inhabilitar este equipo?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirmar == JOptionPane.YES_OPTION) {
                int codigo = Integer.parseInt(vista.getTblEquipos().getValueAt(fila, 0).toString());
                if (modelo.inhabilitar(codigo)) {
                    JOptionPane.showMessageDialog(vista, "Equipo inhabilitado.");
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
                new String[]{"Codigo", "Nombre", "Pais", "Fecha Fundacion", "Entrenador"}, 0
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("codigo_equipo"),
                    rs.getString("nombre"),
                    rs.getString("pais_procedencia"),
                    rs.getString("fecha_fundacion"),
                    rs.getString("entrenador")
                });
            }
            vista.getTblEquipos().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorGestionEquipos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void limpiarCampos() {
        vista.getTxtCodigo().setText("");
        vista.getTxtNombre().setText("");
        vista.getTxtPais().setText("");
        vista.getTxtFechaFundacion().setText("");
        vista.getTblEquipos().clearSelection();
    }
}
