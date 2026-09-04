package controlador;

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

    private final EstadisticaTorneos vista;
    private final ModeloReporte modelo;

    public ControladorEstadisticaTorneos(EstadisticaTorneos vista) {
        this.vista = vista;
        this.modelo = new ModeloReporte();
        initEventListeners();
    }

    private void initEventListeners() {
        vista.getBtnVolver().addActionListener(e -> vista.dispose());
        vista.getBtnCerrar().addActionListener(e -> vista.dispose());
    }

    public void generarGrafica(String fechaInicio, String fechaFin, Integer idTorneo, Integer idEquipo) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            ResultSet rs = modelo.consultarGrafica(fechaInicio, fechaFin, idTorneo, idEquipo);
            while (rs.next()) {
                String torneo = rs.getString("torneo");
                int cantidad = rs.getInt("cantidad_equipos");
                dataset.addValue(cantidad, "Equipos", torneo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ControladorEstadisticaTorneos.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(vista, "Error al generar la gráfica: " + ex.getMessage());
            return;
        }

        JFreeChart grafica = ChartFactory.createBarChart(
                "Equipos por Torneo",
                "Torneo",
                "Cantidad de Equipos",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        org.jfree.chart.plot.CategoryPlot plot = grafica.getCategoryPlot();
        org.jfree.chart.axis.CategoryAxis axis = plot.getDomainAxis();
        axis.setCategoryLabelPositions(
                org.jfree.chart.axis.CategoryLabelPositions.UP_45);

        ChartPanel panel = new ChartPanel(grafica);
        panel.setPreferredSize(new java.awt.Dimension(640, 360));
        panel.setMinimumSize(new java.awt.Dimension(560, 300));
        panel.setMaximumSize(new java.awt.Dimension(640, 360));
        panel.setMouseWheelEnabled(true);

        vista.getPnlGrafica().removeAll();
        java.awt.BorderLayout layout = new java.awt.BorderLayout();
        vista.getPnlGrafica().setLayout(layout);
        vista.getPnlGrafica().setPreferredSize(new java.awt.Dimension(640, 360));
        vista.getPnlGrafica().setMinimumSize(new java.awt.Dimension(560, 300));
        vista.getPnlGrafica().setMaximumSize(new java.awt.Dimension(640, 360));
        vista.getPnlGrafica().add(panel, java.awt.BorderLayout.CENTER);
        vista.getPnlGrafica().revalidate();
        vista.getPnlGrafica().repaint();

        vista.pack();
        vista.setLocationRelativeTo(vista);
    }
}
