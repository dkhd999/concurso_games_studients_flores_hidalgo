package controlador;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloEquipo;
import modelo.ModeloReporte;
import modelo.ModeloTorneo;
import vista.EstadisticaTorneos;
import vista.ReporteTorneoVista;

public class ControladorReporteTorneo {

    private static final String OPCION_TODOS = "Todos";
    private final ReporteTorneoVista vista;
    private final ModeloReporte modelo;
    private boolean cargandoCombos = false;

    public ControladorReporteTorneo(ReporteTorneoVista vista) {
        this.vista = vista;
        this.modelo = new ModeloReporte();
        initEventListeners();
        cargarCombos();
    }

    private void initEventListeners() {
        vista.getBtnVolver().addActionListener(e -> vista.dispose());
        vista.getBtnGenerarReporte().addActionListener(e -> generarReporte());
        vista.getBtnExportarPDF().addActionListener(e -> exportarPDF());
        vista.getBtnVerEstadistica().addActionListener(e -> abrirEstadistica());
    }

    private void cargarCombos() {
        try {
            cargandoCombos = true;

            DefaultComboBoxModel<String> modeloTorneos = new DefaultComboBoxModel<>();
            modeloTorneos.addElement(OPCION_TODOS);
            ModeloTorneo mTorneo = new ModeloTorneo();
            ResultSet rs = mTorneo.consultarTodos();
            while (rs.next()) {
                modeloTorneos.addElement(rs.getInt("id_torneo") + " - " + rs.getString("nombre"));
            }
            vista.getCmbTorneo().setModel(modeloTorneos);

            DefaultComboBoxModel<String> modeloEquipos = new DefaultComboBoxModel<>();
            modeloEquipos.addElement(OPCION_TODOS);
            ModeloEquipo mEquipo = new ModeloEquipo();
            rs = mEquipo.consultarTodos();
            while (rs.next()) {
                modeloEquipos.addElement(rs.getInt("codigo_equipo") + " - " + rs.getString("nombre"));
            }
            vista.getCmbEquipo().setModel(modeloEquipos);

            cargandoCombos = false;
        } catch (SQLException ex) {
            cargandoCombos = false;
            Logger.getLogger(ControladorReporteTorneo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Integer obtenerIdSeleccion(String textoCombo) {
        if (textoCombo == null || textoCombo.equals(OPCION_TODOS)) {
            return null;
        }
        int idx = textoCombo.indexOf(" - ");
        if (idx > 0) {
            return Integer.parseInt(textoCombo.substring(0, idx).trim());
        }
        return null;
    }

    private LocalDate validarFecha() {
        String inicio = vista.getTxtFechaInicio().getText().trim();
        String fin = vista.getTxtFechaFin().getText().trim();
        if (inicio.isEmpty() || fin.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese fecha de inicio y fecha de fin (yyyy-MM-dd).");
            return null;
        }
        try {
            LocalDate fIni = LocalDate.parse(inicio);
            LocalDate fFin = LocalDate.parse(fin);
            if (fFin.isBefore(fIni)) {
                JOptionPane.showMessageDialog(vista, "La fecha de fin no puede ser anterior a la de inicio.");
                return null;
            }
            return fIni;
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(vista, "Formato de fecha inválido. Use yyyy-MM-dd, ej: 2024-03-01.");
            return null;
        }
    }

    private void generarReporte() {
        LocalDate valida = validarFecha();
        if (valida == null) {
            return;
        }
        Object selTorneo = vista.getCmbTorneo().getSelectedItem();
        Object selEquipo = vista.getCmbEquipo().getSelectedItem();
        Integer idTorneo = obtenerIdSeleccion(selTorneo == null ? null : selTorneo.toString());
        Integer idEquipo = obtenerIdSeleccion(selEquipo == null ? null : selEquipo.toString());

        try {
            ResultSet rs = modelo.consultarReporte(
                    vista.getTxtFechaInicio().getText().trim(),
                    vista.getTxtFechaFin().getText().trim(),
                    idTorneo, idEquipo);

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Torneo", "Inicio", "Fin", "Premio", "Cod. Equipo",
                        "Equipo", "País", "F. Inscripción", "Pos. Final"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_torneo"),
                    rs.getString("torneo"),
                    rs.getString("fecha_inicio"),
                    rs.getString("fecha_fin"),
                    rs.getDouble("premio_total"),
                    rs.getInt("codigo_equipo"),
                    rs.getString("equipo"),
                    rs.getString("pais_procedencia"),
                    rs.getString("fecha_inscripcion"),
                    rs.getObject("posicion_final") == null ? "" : rs.getInt("posicion_final")
                });
            }
            vista.getTablaReporte().setModel(model);
            vista.getTablaReporte().setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
            for (int c = 0; c < vista.getTablaReporte().getColumnCount(); c++) {
                javax.swing.table.TableColumn columna = vista.getTablaReporte().getColumnModel().getColumn(c);
                columna.setPreferredWidth(90);
            }
            vista.getTablaReporte().getColumnModel().getColumn(9).setPreferredWidth(70);
            vista.getTablaReporte().revalidate();
            vista.getTablaReporte().repaint();
        } catch (SQLException ex) {
            Logger.getLogger(ControladorReporteTorneo.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(vista, "Error al generar el reporte: " + ex.getMessage());
        }
    }

    private void exportarPDF() {
        if (vista.getTablaReporte().getRowCount() == 0) {
            JOptionPane.showMessageDialog(vista, "No hay datos para exportar. Genere el reporte primero.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Reporte PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));
        if (chooser.showSaveDialog(vista) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = chooser.getSelectedFile();
        String ruta = archivo.getAbsolutePath();
        if (!ruta.toLowerCase().endsWith(".pdf")) {
            ruta += ".pdf";
        }
        try {
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();

            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph titulo = new Paragraph("REPORTE DE TORNEOS", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            documento.add(new Paragraph(" "));

            int cols = vista.getTablaReporte().getColumnCount();
            PdfPTable tabla = new PdfPTable(cols);
            tabla.setWidthPercentage(100);

            for (int c = 0; c < cols; c++) {
                PdfPCell celda = new PdfPCell(new Phrase(vista.getTablaReporte().getColumnName(c)));
                celda.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                tabla.addCell(celda);
            }
            for (int r = 0; r < vista.getTablaReporte().getRowCount(); r++) {
                for (int c = 0; c < cols; c++) {
                    Object valor = vista.getTablaReporte().getValueAt(r, c);
                    tabla.addCell(valor == null ? "" : valor.toString());
                }
            }
            documento.add(tabla);
            documento.close();

            JOptionPane.showMessageDialog(vista, "Reporte exportado correctamente:\n" + ruta);
        } catch (Exception ex) {
            Logger.getLogger(ControladorReporteTorneo.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(vista, "Error al exportar PDF: " + ex.getMessage());
        }
    }

    private void abrirEstadistica() {
        LocalDate valida = validarFecha();
        if (valida == null) {
            return;
        }
        Object selTorneo = vista.getCmbTorneo().getSelectedItem();
        Object selEquipo = vista.getCmbEquipo().getSelectedItem();
        Integer idTorneo = obtenerIdSeleccion(selTorneo == null ? null : selTorneo.toString());
        Integer idEquipo = obtenerIdSeleccion(selEquipo == null ? null : selEquipo.toString());

        EstadisticaTorneos ventana = new EstadisticaTorneos();
        ventana.setLocationRelativeTo(vista);
        ventana.mostrarGrafica(
                vista.getTxtFechaInicio().getText().trim(),
                vista.getTxtFechaFin().getText().trim(),
                idTorneo, idEquipo);
        ventana.setVisible(true);
    }
}
