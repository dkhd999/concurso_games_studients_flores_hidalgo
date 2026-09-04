package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ModeloReporte {

    private final ConexionBDD conexionBDD = new ConexionBDD();

    public ResultSet consultarReporte(String fechaInicio, String fechaFin,
            Integer idTorneo, Integer idEquipo) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_reporte_estadistico(?, ?, ?, ?)}");
        cs.setString(1, fechaInicio);
        cs.setString(2, fechaFin);
        setParametroOpcional(cs, 3, idTorneo);
        setParametroOpcional(cs, 4, idEquipo);
        return cs.executeQuery();
    }

    public ResultSet consultarGrafica(String fechaInicio, String fechaFin,
            Integer idTorneo, Integer idEquipo) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_reporte_estadistico_grafica(?, ?, ?, ?)}");
        cs.setString(1, fechaInicio);
        cs.setString(2, fechaFin);
        setParametroOpcional(cs, 3, idTorneo);
        setParametroOpcional(cs, 4, idEquipo);
        return cs.executeQuery();
    }

    private void setParametroOpcional(CallableStatement cs, int index, Integer valor) throws SQLException {
        if (valor == null) {
            cs.setNull(index, Types.INTEGER);
        } else {
            cs.setInt(index, valor);
        }
    }
}
