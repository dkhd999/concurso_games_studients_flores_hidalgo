package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModeloUsuario {

    private final ConexionBDD conexionBDD = new ConexionBDD();

    public ResultSet validarUsuario(String username, String password) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_validar_usuario(?, ?)}");
        cs.setString(1, username);
        cs.setString(2, password);
        return cs.executeQuery();
    }
}
