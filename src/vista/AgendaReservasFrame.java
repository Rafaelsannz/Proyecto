package vista;

import controlador.GestionCanchas;
import controlador.GestionReservas;
import modelo.Empleado;
import modelo.Reserva;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AgendaReservasFrame extends JFrame {
    private GestionReservas gestionReservas;
    private GestionCanchas gestionCanchas;
    private Empleado usuario;

    private JTable tablaReservas;
    private DefaultTableModel modeloTabla;
    private JTextField txtFiltroFecha;
    private JButton btnFiltrar;
    private JButton btnActualizar;
    private JButton btnIniciarPartido;
    private JButton btnFinalizarPartido;

    public AgendaReservasFrame(GestionReservas gr, GestionCanchas gc, Empleado emp) {
        super("Agenda Diaria y Gestión de Reservas (RF05, RF06, RF11)");
        this.gestionReservas = gr;
        this.gestionCanchas = gc;
        this.usuario = emp;

        inicializarComponentes();
        cargarDatosTabla("");
    }

    private void inicializarComponentes() {
        setSize(780, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cierra esta ventana
        setLayout(new BorderLayout());

        // Panel Superior: Filtros
        JPanel panelNorte = new JPanel(new FlowLayout());
        panelNorte.add(new JLabel("Fecha (YYYY-MM-DD):"));
        txtFiltroFecha = new JTextField(10);
        panelNorte.add(txtFiltroFecha);

        btnFiltrar = new JButton("Consultar Fecha");
        panelNorte.add(btnFiltrar);

        btnActualizar = new JButton("Ver Todas");
        panelNorte.add(btnActualizar);

        add(panelNorte, BorderLayout.NORTH);

        // Centro: Tabla JTable
        String[] columnas = {"N°", "Fecha", "Horario", "Cancha", "Titular", "Total (S/.)", "Adelanto", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaReservas = new JTable(modeloTabla);
        add(new JScrollPane(tablaReservas), BorderLayout.CENTER);

        // Panel Sur: Acciones de Estado (RF06)
        JPanel panelSur = new JPanel(new FlowLayout());
        btnIniciarPartido = new JButton("Marcar: En Juego");
        btnFinalizarPartido = new JButton("Marcar: Finalizar");
        panelSur.add(btnIniciarPartido);
        panelSur.add(btnFinalizarPartido);
        add(panelSur, BorderLayout.SOUTH);

        // Eventos
        btnFiltrar.addActionListener(e -> cargarDatosTabla(txtFiltroFecha.getText().trim()));
        btnActualizar.addActionListener(e -> cargarDatosTabla(""));

        btnIniciarPartido.addActionListener(e -> {
            int fila = tablaReservas.getSelectedRow();
            if (fila != -1) {
                int nro = (int) modeloTabla.getValueAt(fila, 0);
                Reserva r = gestionReservas.buscarPorNumero(nro);
                if (r != null && r.iniciarPartido()) {
                    r.setEncargadoCancha(usuario);
                    JOptionPane.showMessageDialog(this, "Partido iniciado. Cancha marcada como Ocupada.");
                    cargarDatosTabla("");
                } else {
                    JOptionPane.showMessageDialog(this, "Solo reservas 'Confirmadas' pueden pasar a 'En Juego'.",
                                                  "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnFinalizarPartido.addActionListener(e -> {
            int fila = tablaReservas.getSelectedRow();
            if (fila != -1) {
                int nro = (int) modeloTabla.getValueAt(fila, 0);
                Reserva r = gestionReservas.buscarPorNumero(nro);
                if (r != null && r.finalizarPartido()) {
                    JOptionPane.showMessageDialog(this, "Partido finalizado. Cancha liberada a Disponible.");
                    cargarDatosTabla("");
                } else {
                    JOptionPane.showMessageDialog(this, "Solo reservas 'En Juego' pueden finalizarse.",
                                                  "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void cargarDatosTabla(String filtroFecha) {
        modeloTabla.setRowCount(0); // Limpiar filas

        for (int i = 0; i < gestionReservas.getCantidad(); i++) {
            Reserva r = gestionReservas.obtener(i);

            // Filtro opcional por fecha
            if (filtroFecha.isEmpty() || r.getFecha().equals(filtroFecha)) {
                String horario = r.getHoraInicio() + ":00 - " + r.getHoraFin() + ":00";
                Object[] fila = {
                    r.getNumeroCorrelativo(),
                    r.getFecha(),
                    horario,
                    r.getCancha().getCodigo() + " (" + r.getCancha().getNombre() + ")",
                    r.getTitular().getNombres() + " " + r.getTitular().getApellidos(),
                    String.format("%.2f", r.calcularMontoTotal()),
                    String.format("%.2f", r.getAdelantoPagado()),
                    r.getEstado()
                };
                modeloTabla.addRow(fila);
            }
        }
    }
}
