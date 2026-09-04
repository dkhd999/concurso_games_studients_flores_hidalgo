package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModeloEquipo {

    private final ConexionBDD conexionBDD = new ConexionBDD();

    public boolean registrar(int codigo, String nombre, String pais, String fecha, int entrenador) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_registrar_equipo(?, ?, ?, ?, ?)}");
        cs.setInt(1, codigo);
        cs.setString(2, nombre);
        cs.setString(3, pais);
        cs.setString(4, fecha);
        if (entrenador > 0) {
            cs.setInt(5, entrenador);
        } else {
            cs.setNull(5, java.sql.Types.INTEGER);
        }
        return cs.executeUpdate() > 0;
    }

    public boolean actualizar(int codigo, String nombre, String pais, String fecha, int entrenador) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_actualizar_equipo(?, ?, ?, ?, ?)}");
        cs.setInt(1, codigo);
        cs.setString(2, nombre);
        cs.setString(3, pais);
        cs.setString(4, fecha);
        if (entrenador > 0) {
            cs.setInt(5, entrenador);
        } else {
            cs.setNull(5, java.sql.Types.INTEGER);
        }
        return cs.executeUpdate() > 0;
    }

    public boolean inhabilitar(int codigo) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_inhabilitar_equipo(?)}");
        cs.setInt(1, codigo);
        return cs.executeUpdate() > 0;
    }

    public ResultSet consultarTodos() throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_consultar_equipos()}");
        return cs.executeQuery();
    }
}
