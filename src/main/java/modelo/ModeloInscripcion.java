package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModeloInscripcion {

    private final ConexionBDD conexionBDD = new ConexionBDD();

    public boolean inscribir(int codigoEquipo, int idTorneo, String fecha) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_inscribir_equipo(?, ?, ?)}");
        cs.setInt(1, codigoEquipo);
        cs.setInt(2, idTorneo);
        cs.setString(3, fecha);
        return cs.executeUpdate() > 0;
    }

    public ResultSet consultarPorTorneo(int idTorneo) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_consultar_inscripciones(?)}");
        cs.setInt(1, idTorneo);
        return cs.executeQuery();
    }
}
