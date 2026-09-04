package controlador;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloArbitro;
import vista.GestionSedesVista;

public class ControladorGestionArbitros {

    private final GestionSedesVista vista;
    private final ModeloArbitro modelo;

    public ControladorGestionArbitros(GestionSedesVista vista) {
        this.vista = vista;
        this.modelo = new ModeloArbitro();
    }

    public ResultSet consultarArbitros() throws SQLException {
        return modelo.consultarTodos();
    }
}
