package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModeloTorneo {

    private final ConexionBDD conexionBDD = new ConexionBDD();

    public boolean registrar(int id, String nombre, String fechaInicio, String fechaFin, double premio) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_registrar_torneo(?, ?, ?, ?, ?)}");
        cs.setInt(1, id);
        cs.setString(2, nombre);
        cs.setString(3, fechaInicio);
        cs.setString(4, fechaFin);
        cs.setDouble(5, premio);
        return cs.executeUpdate() > 0;
    }

    public boolean actualizar(int id, String nombre, String fechaInicio, String fechaFin, double premio) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_actualizar_torneo(?, ?, ?, ?, ?)}");
        cs.setInt(1, id);
        cs.setString(2, nombre);
        cs.setString(3, fechaInicio);
        cs.setString(4, fechaFin);
        cs.setDouble(5, premio);
        return cs.executeUpdate() > 0;
    }

    public boolean inhabilitar(int id) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_inhabilitar_torneo(?)}");
        cs.setInt(1, id);
        return cs.executeUpdate() > 0;
    }

    public ResultSet consultarTodos() throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_consultar_torneos()}");
        return cs.executeQuery();
    }
}
