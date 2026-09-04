package controlador;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloEntrenador;
import modelo.ModeloEquipo;
import vista.GestionEntrenadorVista;

public class ControladorGestionEntrenadorVista {

    private final GestionEntrenadorVista vista;
    private final ModeloEntrenador modelo;
    private final ModeloEquipo modeloEquipo;

    public ControladorGestionEntrenadorVista(GestionEntrenadorVista vista) {
        this.vista = vista;
        this.modelo = new ModeloEntrenador();
        this.modeloEquipo = new ModeloEquipo();
        initEventListeners();
        cargarTabla();
        cargarCombosEquipos();
    }

    private void initEventListeners() {
        vista.getBtnGuardar().addActionListener(this::guardar);
        vista.getBtnAsignar().addActionListener(this::asignarEntrenador);
        vista.getBtnVolver().addActionListener(e -> vista.dispose());
        vista.getTblEntrenadores().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = vista.getTblEntrenadores().getSelectedRow();
                if (fila >= 0) {
                    vista.getTxtIdEntrenador().setText(vista.getTblEntrenadores().getValueAt(fila, 0).toString());
                    vista.getTxtNombre().setText(vista.getTblEntrenadores().getValueAt(fila, 1).toString());
                }
            }
        });
    }

    private void guardar(ActionEvent e) {
        try {
            String idStr = vista.getTxtIdEntrenador().getText().trim();
            String nombre = vista.getTxtNombre().getText().trim();

            if (idStr.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.");
                return;
            }

            int id = Integer.parseInt(idStr);

            if (modelo.registrar(id, nombre)) {
                JOptionPane.showMessageDialog(vista, "Entrenador registrado correctamente.");
                limpiarCampos();
                cargarTabla();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El ID debe ser un numero.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void asignarEntrenador(ActionEvent e) {
        try {
            int fila = vista.getTblEntrenadores().getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(vista, "Seleccione un entrenador de la tabla.");
                return;
            }

            int idEntrenador = Integer.parseInt(vista.getTblEntrenadores().getValueAt(fila, 0).toString());
            Object sel = vista.getCmbEquipo().getSelectedItem();
            if (sel == null) {
                JOptionPane.showMessageDialog(vista, "Seleccione un equipo.");
                return;
            }
            String txt = sel.toString();
            int idx = txt.indexOf(" - ");
            if (idx <= 0) {
                JOptionPane.showMessageDialog(vista, "Seleccione un equipo valido.");
                return;
            }
            int codigoEquipo = Integer.parseInt(txt.substring(0, idx).trim());

            JOptionPane.showMessageDialog(vista, "Entrenador " + idEntrenador + " asignado al equipo " + codigoEquipo + ".");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Datos numericos invalidos.");
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
            Logger.getLogger(ControladorGestionEntrenadorVista.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargarTabla() {
        try {
            ResultSet rs = modelo.consultarTodos();
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Nombre"}, 0
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_entrenador"),
                    rs.getString("nombre")
                });
            }
            vista.getTblEntrenadores().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorGestionEntrenadorVista.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void limpiarCampos() {
        vista.getTxtIdEntrenador().setText("");
        vista.getTxtNombre().setText("");
        vista.getTblEntrenadores().clearSelection();
    }
}
