package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModeloConsultaTorneo {

    private final ConexionBDD conexionBDD = new ConexionBDD();

    public ResultSet consultarInformacion(int idTorneo) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_consultar_informacion_torneo(?)}");
        cs.setInt(1, idTorneo);
        return cs.executeQuery();
    }
}
