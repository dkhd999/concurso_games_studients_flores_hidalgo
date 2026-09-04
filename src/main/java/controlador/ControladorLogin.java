package controlador;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import modelo.ModeloUsuario;
import vista.Login;
import vista.MenuAdminVista;

public class ControladorLogin {

    private final Login vista;
    private final ModeloUsuario modelo;

    public ControladorLogin(Login vista) {
        this.vista = vista;
        this.modelo = new ModeloUsuario();
    }

    public void validarLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese usuario y contrasena.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            ResultSet rs = modelo.validarUsuario(username, password);
            if (rs.next()) {
                JOptionPane.showMessageDialog(vista, "Bienvenido, " + rs.getString("nombre_completo"));
                MenuAdminVista menu = new MenuAdminVista();
                menu.setLocationRelativeTo(vista);
                menu.setVisible(true);
                vista.dispose();
            } else {
                JOptionPane.showMessageDialog(vista, "Usuario o contrasena incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error de conexion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
