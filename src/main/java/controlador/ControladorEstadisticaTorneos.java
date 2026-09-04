package controlador;

import java.awt.Dimension;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.ModeloReporte;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import vista.EstadisticaTorneos;

public class ControladorEstadisticaTorneos {

    private static final String PAIS = "Equipos por País";
    private static final String TORNEO = "Equipos Participantes por Torneo";
    private static final String POSICION = "Distribución de Posiciones Finales";

    private final EstadisticaTorneos vista;
    private final ModeloReporte modelo;

    public ControladorEstadisticaTorneos(EstadisticaTorneos vista) {
        this.vista = vista;
        this.modelo = new ModeloReporte();
        initCombobox();
        initEventListeners();
    }

    private void initCombobox() {
        vista.getCmbTipoEstadistica().removeAllItems();
        vista.getCmbTipoEstadistica().addItem(PAIS);
        vista.getCmbTipoEstadistica().addItem(TORNEO);
        vista.getCmbTipoEstadistica().addItem(POSICION);
    }

    private void initEventListeners() {
        vista.getBtnCerrar().addActionListener(e -> vista.dispose());
        vista.getBtnGenerarEstadistica().addActionListener(e -> generarSelector());
    }

    private void generarSelector() {
        Object sel = vista.getCmbTipoEstadistica().getSelectedItem();
        if (sel == null) {
            return;
        }
        String tipo = sel.toString();
        JFreeChart grafica;
        if (tipo.equals(PAIS)) {
            grafica = chartEquiposPorPais();
        } else if (tipo.equals(TORNEO)) {
            grafica = chartEquiposPorTorneo();
        } else {
            grafica = chartPosicionesFinales();
        }
        mostrar(grafica);
    }

    public void mostrarGrafica(String fechaInicio, String fechaFin, Integer idTorneo, Integer idEquipo) {
        JFreeChart grafica;
        if (idTorneo != null) {
            grafica = chartPaisesPorTorneo(idTorneo);
        } else {
            grafica = chartEquiposPorTorneo();
        }
        mostrar(grafica);
    }

    private JFreeChart chartEquiposPorPais() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            ResultSet rs = modelo.consultarEquiposPorPais();
            boolean hay = false;
            while (rs.next()) {
                String pais = rs.getString("pais");
                int cantidad = rs.getInt("cantidad");
                dataset.addValue(cantidad, pais, "País");
                hay = true;
            }
            if (!hay) return null;
        } catch (SQLException ex) {
            error(ex);
            return null;
        }
        return barra(dataset, "Equipos por País", "País", "Cantidad de Equipos");
    }

    private JFreeChart chartEquiposPorTorneo() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            ResultSet rs = modelo.consultarEquiposPorTorneo();
            boolean hay = false;
            while (rs.next()) {
                String torneo = rs.getString("torneo");
                int cantidad = rs.getInt("cantidad");
                dataset.addValue(cantidad, torneo, "Torneo");
                hay = true;
            }
            if (!hay) return null;
        } catch (SQLException ex) {
            error(ex);
            return null;
        }
        return barra(dataset, "Equipos Participantes por Torneo", "Torneo", "Cantidad de Equipos");
    }

    private JFreeChart chartPosicionesFinales() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            ResultSet rs = modelo.consultarPosicionesFinales();
            boolean hay = false;
            while (rs.next()) {
                String posicion = rs.getString("posicion");
                int cantidad = rs.getInt("cantidad");
                dataset.addValue(cantidad, posicion, "Posición");
                hay = true;
            }
            if (!hay) return null;
        } catch (SQLException ex) {
            error(ex);
            return null;
        }
        return barra(dataset, "Distribución de Posiciones Finales", "Posición", "Cantidad de Equipos");
    }

    private JFreeChart chartPaisesPorTorneo(int idTorneo) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            ResultSet rs = modelo.consultarPaisesPorTorneo(idTorneo);
            boolean hay = false;
            while (rs.next()) {
                String pais = rs.getString("pais");
                int cantidad = rs.getInt("cantidad_equipos");
                dataset.addValue(cantidad, pais, "País");
                hay = true;
            }
            if (!hay) return null;
        } catch (SQLException ex) {
            error(ex);
            return null;
        }
        return barra(dataset, "Equipos por País del Torneo", "País", "Cantidad de Equipos");
    }

    private JFreeChart barra(DefaultCategoryDataset dataset, String titulo, String ejeX, String ejeY) {
        JFreeChart grafica = ChartFactory.createBarChart(
                titulo, ejeX, ejeY, dataset, PlotOrientation.VERTICAL,
                true, true, false);
        org.jfree.chart.plot.CategoryPlot plot = grafica.getCategoryPlot();
        plot.setRangePannable(true);
        org.jfree.chart.axis.CategoryAxis axis = plot.getDomainAxis();
        axis.setCategoryLabelPositions(
                org.jfree.chart.axis.CategoryLabelPositions.UP_45);
        return grafica;
    }

    private void error(SQLException ex) {
        Logger.getLogger(ControladorEstadisticaTorneos.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(vista, "Error al generar la gráfica: " + ex.getMessage());
    }

    private void mostrar(JFreeChart grafica) {
        if (grafica == null) {
            return;
        }
        ChartPanel panel = new ChartPanel(grafica);
        panel.setPreferredSize(new Dimension(640, 360));
        panel.setMinimumSize(new Dimension(560, 300));
        panel.setMaximumSize(new Dimension(640, 360));
        panel.setMouseWheelEnabled(true);

        vista.getPnlGrafica().removeAll();
        java.awt.BorderLayout layout = new java.awt.BorderLayout();
        vista.getPnlGrafica().setLayout(layout);
        vista.getPnlGrafica().setPreferredSize(new Dimension(640, 360));
        vista.getPnlGrafica().setMinimumSize(new Dimension(560, 300));
        vista.getPnlGrafica().setMaximumSize(new Dimension(640, 360));
        vista.getPnlGrafica().add(panel, java.awt.BorderLayout.CENTER);
        vista.getPnlGrafica().revalidate();
        vista.getPnlGrafica().repaint();

        vista.pack();
        vista.setLocationRelativeTo(vista);
    }
}
