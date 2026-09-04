package controlador;

import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import vista.MenuAdminVista;
import vista.GestionTorneoVista;
import vista.GestionEquiposVista;
import vista.GestionJugadoresVista;
import vista.GestionEntrenadorVista;
import vista.InscripcionEquiposVista;
import vista.AsignacionJugadoresVista;
import vista.GestionRondasVista;
import vista.RegistroPartidosVista;
import vista.GestionArbitrosVista1;
import vista.GestionSedesVista;
import vista.GestionPatrocinadoresVista;
import vista.ConsultaTorneoVista;
import vista.ReporteTorneoVista;

public class ControladorMenuPrincipal {

    private final MenuAdminVista vista;

    public ControladorMenuPrincipal(MenuAdminVista vista) {
        this.vista = vista;
        initEventListeners();
        numerarBotones();
    }

    private void numerarBotones() {
        vista.getBtnGestionTorneo().setText("1. Gestion Torneo");
        vista.getBtnAsignacionJugadores().setText("2. Asignacion Jugadores");
        vista.getBtnGestionEquipos().setText("3. Gestion Equipos");
        vista.getBtnGestionRondas().setText("4. Gestion Rondas");
        vista.getBtnGestionJugadores().setText("5. Gestion Jugadores");
        vista.getBtnGestionEntrenadores().setText("6. Gestion Entrenadores");
        vista.getBtnRegistroPartidos().setText("7. Registro Partidos");
        vista.getBtnGestionInscripciones().setText("8. Gestion Inscripciones");
        vista.getBtnHGestionArbitros().setText("9. Gestion Arbitros");
        vista.getBtnGestionSedes().setText("10. Gestion Sedes");
        vista.getBtnPatrocinadores().setText("11. Gestion Patrocinadores");
        vista.getBtnConsultaTorneo().setText("12. Consulta Torneo");
        vista.pack();
        vista.revalidate();
        vista.repaint();
    }

    private void initEventListeners() {
        vista.getBtnGestionTorneo().addActionListener(this::abrirGestionTorneo);
        vista.getBtnGestionEquipos().addActionListener(this::abrirGestionEquipos);
        vista.getBtnGestionJugadores().addActionListener(this::abrirGestionJugadores);
        vista.getBtnGestionEntrenadores().addActionListener(this::abrirGestionEntrenadores);
        vista.getBtnGestionInscripciones().addActionListener(this::abrirInscripcionEquipos);
        vista.getBtnAsignacionJugadores().addActionListener(this::abrirAsignacionJugadores);
        vista.getBtnGestionRondas().addActionListener(this::abrirGestionRondas);
        vista.getBtnRegistroPartidos().addActionListener(this::abrirRegistroPartidos);
        vista.getBtnHGestionArbitros().addActionListener(this::abrirGestionArbitros);
        vista.getBtnGestionSedes().addActionListener(this::abrirGestionSedes);
        vista.getBtnPatrocinadores().addActionListener(this::abrirGestionPatrocinadores);
        vista.getBtnConsultaTorneo().addActionListener(this::abrirConsultaTorneo);
        vista.getBtnReporte().addActionListener(this::abrirReporteTorneo);
        vista.getBtnRegresar().addActionListener(e -> vista.dispose());
    }

    private void abrirVentana(JFrame ventana) {
        ventana.setLocationRelativeTo(vista);
        ventana.setVisible(true);
    }

    private void abrirGestionTorneo(ActionEvent e) {
        abrirVentana(new GestionTorneoVista());
    }

    private void abrirGestionEquipos(ActionEvent e) {
        abrirVentana(new GestionEquiposVista());
    }

    private void abrirGestionJugadores(ActionEvent e) {
        abrirVentana(new GestionJugadoresVista());
    }

    private void abrirGestionEntrenadores(ActionEvent e) {
        abrirVentana(new GestionEntrenadorVista());
    }

    private void abrirInscripcionEquipos(ActionEvent e) {
        abrirVentana(new InscripcionEquiposVista());
    }

    private void abrirAsignacionJugadores(ActionEvent e) {
        abrirVentana(new AsignacionJugadoresVista());
    }

    private void abrirGestionRondas(ActionEvent e) {
        abrirVentana(new GestionRondasVista());
    }

    private void abrirRegistroPartidos(ActionEvent e) {
        abrirVentana(new RegistroPartidosVista());
    }

    private void abrirGestionArbitros(ActionEvent e) {
        abrirVentana(new GestionArbitrosVista1());
    }

    private void abrirGestionSedes(ActionEvent e) {
        abrirVentana(new GestionSedesVista());
    }

    private void abrirGestionPatrocinadores(ActionEvent e) {
        abrirVentana(new GestionPatrocinadoresVista());
    }

    private void abrirConsultaTorneo(ActionEvent e) {
        abrirVentana(new ConsultaTorneoVista());
    }

    private void abrirReporteTorneo(ActionEvent e) {
        abrirVentana(new ReporteTorneoVista());
    }
}
