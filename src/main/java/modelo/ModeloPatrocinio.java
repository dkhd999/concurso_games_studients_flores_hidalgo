package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModeloPatrocinio {

    private final ConexionBDD conexionBDD = new ConexionBDD();

    public boolean registrar(int idPatrocinador, int idTorneo, double monto) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_registrar_patrocinio(?, ?, ?)}");
        cs.setInt(1, idPatrocinador);
        cs.setInt(2, idTorneo);
        cs.setDouble(3, monto);
        return cs.executeUpdate() > 0;
    }

    public ResultSet consultarPorTorneo(int idTorneo) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_consultar_patrocinios(?)}");
        cs.setInt(1, idTorneo);
        return cs.executeQuery();
    }
}
