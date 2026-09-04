package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModeloPartido {

    private final ConexionBDD conexionBDD = new ConexionBDD();

    public boolean registrar(int idPartido, String fecha, String hora, String marcador,
                             int idTorneo, int numeroRonda, int equipoLocal, int equipoVisita,
                             int idArbitro, int idSede) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_registrar_partido(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
        cs.setInt(1, idPartido);
        cs.setString(2, fecha);
        cs.setString(3, hora);
        cs.setString(4, marcador);
        cs.setInt(5, idTorneo);
        cs.setInt(6, numeroRonda);
        cs.setInt(7, equipoLocal);
        cs.setInt(8, equipoVisita);
        cs.setInt(9, idArbitro);
        cs.setInt(10, idSede);
        return cs.executeUpdate() > 0;
    }

    public ResultSet consultarPorTorneo(int idTorneo) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_consultar_partidos(?)}");
        cs.setInt(1, idTorneo);
        return cs.executeQuery();
    }
}
