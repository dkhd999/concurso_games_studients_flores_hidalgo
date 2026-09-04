package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModeloRonda {

    private final ConexionBDD conexionBDD = new ConexionBDD();

    public boolean registrar(int idTorneo, int numero, String nombre) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_registrar_ronda(?, ?, ?)}");
        cs.setInt(1, idTorneo);
        cs.setInt(2, numero);
        cs.setString(3, nombre);
        return cs.executeUpdate() > 0;
    }

    public ResultSet consultarPorTorneo(int idTorneo) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_consultar_rondas(?)}");
        cs.setInt(1, idTorneo);
        return cs.executeQuery();
    }
}
