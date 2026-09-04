package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModeloPlantilla {

    private final ConexionBDD conexionBDD = new ConexionBDD();

    public boolean asignar(String nickname, int idTorneo, int codigoEquipo) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_asignar_jugador(?, ?, ?)}");
        cs.setString(1, nickname);
        cs.setInt(2, idTorneo);
        cs.setInt(3, codigoEquipo);
        return cs.executeUpdate() > 0;
    }

    public ResultSet consultarParticipacion(int idTorneo) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_consultar_participacion(?)}");
        cs.setInt(1, idTorneo);
        return cs.executeQuery();
    }
}
