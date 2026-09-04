package controlador;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloJugador;
import vista.GestionJugadoresVista;

public class ControladorGestionJugadores {

    private final GestionJugadoresVista vista;
    private final ModeloJugador modelo;

    public ControladorGestionJugadores(GestionJugadoresVista vista) {
        this.vista = vista;
        this.modelo = new ModeloJugador();
        initEventListeners();
        cargarRoles();
        cargarTabla();
    }

    private void initEventListeners() {
        vista.getBtnGuardar().addActionListener(this::guardar);
        vista.getBtnModificar().addActionListener(this::modificar);
        vista.getBtnEliminar().addActionListener(this::inhabilitar);
        vista.getBtnVolver().addActionListener(e -> vista.dispose());
        vista.getTxtFechaNacimiento().addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                calcularEdad();
            }
        });
        vista.getTxtFechaNacimiento().addActionListener(e -> calcularEdad());
        vista.getTblJugadores().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = vista.getTblJugadores().getSelectedRow();
                if (fila >= 0) {
                    vista.getTxtNickname().setText(vista.getTblJugadores().getValueAt(fila, 0).toString());
                    vista.getTxtNombreReal().setText(vista.getTblJugadores().getValueAt(fila, 1).toString());
                    vista.getTxtFechaNacimiento().setText(vista.getTblJugadores().getValueAt(fila, 2).toString());
                    String edadStr = vista.getTblJugadores().getValueAt(fila, 3).toString();
                    vista.getTxtEdad().setText(edadStr);
                    vista.getCmbRol().setSelectedItem(vista.getTblJugadores().getValueAt(fila, 4).toString());
                }
            }
        });
    }

    private void calcularEdad() {
        String fechaStr = vista.getTxtFechaNacimiento().getText().trim();
        if (fechaStr.isEmpty()) {
            vista.getTxtEdad().setText("");
            return;
        }
        try {
            LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE);
            int edad = Period.between(fecha, LocalDate.now()).getYears();
            if (edad < 0) {
                vista.getTxtEdad().setText("");
                JOptionPane.showMessageDialog(vista, "La fecha de nacimiento no puede ser futura.");
            } else {
                vista.getTxtEdad().setText(String.valueOf(edad));
            }
        } catch (DateTimeParseException ex) {
            vista.getTxtEdad().setText("");
            JOptionPane.showMessageDialog(vista, "Formato de fecha invalido. Use YYYY-MM-DD (ej: 1998-04-12).");
        }
    }

    private void cargarRoles() {
        vista.getCmbRol().removeAllItems();
        vista.getCmbRol().addItem("Top Laner");
        vista.getCmbRol().addItem("Jungler");
        vista.getCmbRol().addItem("Mid Laner");
        vista.getCmbRol().addItem("ADC");
        vista.getCmbRol().addItem("Support");
    }

    private void guardar(ActionEvent e) {
        try {
            String nickname = vista.getTxtNickname().getText().trim();
            String nombreReal = vista.getTxtNombreReal().getText().trim();
            String fechaNac = vista.getTxtFechaNacimiento().getText().trim();
            String rol = (String) vista.getCmbRol().getSelectedItem();
            String edadStr = vista.getTxtEdad().getText().trim();

            if (nickname.isEmpty() || nombreReal.isEmpty() || fechaNac.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.");
                return;
            }

            int edad = 0;
            if (!edadStr.isEmpty()) {
                try {
                    edad = Integer.parseInt(edadStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(vista, "Edad invalida.");
                    return;
                }
            }

            if (modelo.registrar(nickname, nombreReal, fechaNac, edad, rol)) {
                JOptionPane.showMessageDialog(vista, "Jugador registrado correctamente.");
                limpiarCampos();
                cargarTabla();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void modificar(ActionEvent e) {
        try {
            int fila = vista.getTblJugadores().getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(vista, "Seleccione un jugador de la tabla.");
                return;
            }

            String nickname = vista.getTblJugadores().getValueAt(fila, 0).toString();
            String nombreReal = vista.getTxtNombreReal().getText().trim();
            String rol = (String) vista.getCmbRol().getSelectedItem();

            if (modelo.actualizar(nickname, nombreReal, rol)) {
                JOptionPane.showMessageDialog(vista, "Jugador actualizado correctamente.");
                limpiarCampos();
                cargarTabla();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void inhabilitar(ActionEvent e) {
        try {
            int fila = vista.getTblJugadores().getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(vista, "Seleccione un jugador de la tabla.");
                return;
            }

            int confirmar = JOptionPane.showConfirmDialog(vista, "Desea inhabilitar este jugador?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirmar == JOptionPane.YES_OPTION) {
                String nickname = vista.getTblJugadores().getValueAt(fila, 0).toString();
                if (modelo.inhabilitar(nickname)) {
                    JOptionPane.showMessageDialog(vista, "Jugador inhabilitado.");
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
                new String[]{"Nickname", "Nombre Real", "Fecha Nacimiento", "Edad", "Rol"}, 0
            );
            while (rs.next()) {
                String fechaStr = rs.getString("fecha_nacimiento");
                int edad = calcularEdadDesdeFecha(fechaStr);
                model.addRow(new Object[]{
                    rs.getString("nickname"),
                    rs.getString("nombre_real"),
                    fechaStr,
                    edad,
                    rs.getString("rol")
                });
            }
            vista.getTblJugadores().setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(ControladorGestionJugadores.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int calcularEdadDesdeFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isEmpty()) return 0;
        try {
            LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return Math.max(0, Period.between(fecha, LocalDate.now()).getYears());
        } catch (DateTimeParseException ex) {
            return 0;
        }
    }

    private void limpiarCampos() {
        vista.getTxtNickname().setText("");
        vista.getTxtNombreReal().setText("");
        vista.getTxtFechaNacimiento().setText("");
        vista.getTxtEdad().setText("");
        vista.getCmbRol().setSelectedIndex(0);
        vista.getTblJugadores().clearSelection();
    }
}
