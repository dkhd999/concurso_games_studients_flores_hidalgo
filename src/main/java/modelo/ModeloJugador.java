package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModeloJugador {

    private final ConexionBDD conexionBDD = new ConexionBDD();

    public boolean registrar(String nickname, String nombreReal, String fechaNacimiento, int edad, String rol) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_registrar_jugador(?, ?, ?, ?, ?)}");
        cs.setString(1, nickname);
        cs.setString(2, nombreReal);
        cs.setString(3, fechaNacimiento);
        if (edad > 0) {
            cs.setInt(4, edad);
        } else {
            cs.setNull(4, java.sql.Types.INTEGER);
        }
        cs.setString(5, rol);
        return cs.executeUpdate() > 0;
    }

    public boolean actualizar(String nickname, String nombreReal, String rol) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_actualizar_jugador(?, ?, ?)}");
        cs.setString(1, nickname);
        cs.setString(2, nombreReal);
        cs.setString(3, rol);
        return cs.executeUpdate() > 0;
    }

    public boolean inhabilitar(String nickname) throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_inhabilitar_jugador(?)}");
        cs.setString(1, nickname);
        return cs.executeUpdate() > 0;
    }

    public ResultSet consultarTodos() throws SQLException {
        Connection con = conexionBDD.conectar();
        CallableStatement cs = con.prepareCall("{call sp_consultar_jugadores()}");
        return cs.executeQuery();
    }
}
