package vista;

import controlador.ReportesGerenciales;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportesFrame extends JFrame {
    private ReportesGerenciales reportes;

    private DefaultTableModel modOcupacion, modProductividad, modTopJugadores, modIngresos;
    private JTextField txtOcuIni, txtOcuFin, txtOcuDias;
    private JTextField txtIngIni, txtIngFin;

    public ReportesFrame(ReportesGerenciales reportes) {
        super("Reportes Gerenciales (RF08)");
        this.reportes = reportes;
        inicializarComponentes();
        cargarTodosLosReportes();
    }

    private void inicializarComponentes() {
        setSize(860, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("1. Ocupación por Cancha", crearPanelOcupacion());
        pestanas.addTab("2. Productividad del Personal", crearPanelProductividad());
        pestanas.addTab("3. Top Jugadores", crearPanelTopJugadores());
        pestanas.addTab("4. Ingresos Desglosados", crearPanelIngresos());
        add(pestanas);
    }

    private JPanel crearPanelOcupacion() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Desde:"));
        txtOcuIni = new JTextField("2026-11-01", 9);
        filtros.add(txtOcuIni);
        filtros.add(new JLabel("Hasta:"));
        txtOcuFin = new JTextField("2026-11-30", 9);
        filtros.add(txtOcuFin);
        filtros.add(new JLabel("Días del rango:"));
        txtOcuDias = new JTextField("1", 4);
        filtros.add(txtOcuDias);
        JButton btn = new JButton("Generar");
        filtros.add(btn);
        panel.add(filtros, BorderLayout.NORTH);

        String[] cols = {"Código", "Nombre", "Tipo", "Horas Reservadas", "% Ocupación"};
        modOcupacion = new DefaultTableModel(cols, 0);
        panel.add(new JScrollPane(new JTable(modOcupacion)), BorderLayout.CENTER);

        btn.addActionListener(e -> cargarOcupacion());
        return panel;
    }

    private JPanel crearPanelProductividad() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        String[] cols = {"DNI", "Nombre Completo", "Rol", "Reservas Registradas", "Partidos Supervisados"};
        modProductividad = new DefaultTableModel(cols, 0);
        panel.add(new JScrollPane(new JTable(modProductividad)), BorderLayout.CENTER);

        JButton btn = new JButton("Actualizar");
        btn.addActionListener(e -> cargarProductividad());
        panel.add(btn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelTopJugadores() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        String[] cols = {"Puesto", "DNI", "Nombre Completo", "Categoría", "Reservas Finalizadas"};
        modTopJugadores = new DefaultTableModel(cols, 0);
        panel.add(new JScrollPane(new JTable(modTopJugadores)), BorderLayout.CENTER);

        JButton btn = new JButton("Actualizar Ranking");
        btn.addActionListener(e -> cargarTopJugadores());
        panel.add(btn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelIngresos() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Desde:"));
        txtIngIni = new JTextField("2026-11-01", 9);
        filtros.add(txtIngIni);
        filtros.add(new JLabel("Hasta:"));
        txtIngFin = new JTextField("2026-11-30", 9);
        filtros.add(txtIngFin);
        JButton btn = new JButton("Calcular Ingresos");
        filtros.add(btn);
        panel.add(filtros, BorderLayout.NORTH);

        String[] cols = {"Concepto de Ingreso", "Monto Acumulado"};
        modIngresos = new DefaultTableModel(cols, 0);
        panel.add(new JScrollPane(new JTable(modIngresos)), BorderLayout.CENTER);

        btn.addActionListener(e -> cargarIngresos());
        return panel;
    }

    private void cargarTodosLosReportes() {
        cargarOcupacion();
        cargarProductividad();
        cargarTopJugadores();
        cargarIngresos();
    }

    private void cargarOcupacion() {
        int dias = 1;
        try {
            dias = Integer.parseInt(txtOcuDias.getText().trim());
        } catch (NumberFormatException ignored) {}
        llenarModelo(modOcupacion, reportes.obtenerDatosOcupacion(
                txtOcuIni.getText().trim(), txtOcuFin.getText().trim(), dias));
    }

    private void cargarProductividad() {
        llenarModelo(modProductividad, reportes.obtenerDatosProductividad());
    }

    private void cargarTopJugadores() {
        llenarModelo(modTopJugadores, reportes.obtenerDatosTopJugadores());
    }

    private void cargarIngresos() {
        llenarModelo(modIngresos, reportes.obtenerDatosIngresos(
                txtIngIni.getText().trim(), txtIngFin.getText().trim()));
    }

    private void llenarModelo(DefaultTableModel modelo, Object[][] datos) {
        modelo.setRowCount(0);
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
}