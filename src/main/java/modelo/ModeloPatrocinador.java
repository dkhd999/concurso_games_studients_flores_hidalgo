package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModeloPatrocinador {

    private final ConexionBDD conexionBDD = new ConexionBDD();

    public boolean registrar(int id, String nombre, String contacto) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_registrar_patrocinador(?, ?, ?)}");
        cs.setInt(1, id);
        cs.setString(2, nombre);
        cs.setString(3, contacto);
        return cs.executeUpdate() > 0;
    }

    public boolean inhabilitar(int id) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_inhabilitar_patrocinador(?)}");
        cs.setInt(1, id);
        return cs.executeUpdate() > 0;
    }

    public ResultSet consultarTodos() throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_consultar_patrocinadores()}");
        return cs.executeQuery();
    }
}
