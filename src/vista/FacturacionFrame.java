package vista;

import controlador.GestionComprobantes;
import controlador.GestionJugadores;
import controlador.GestionReservas;
import modelo.Comprobante;
import modelo.Reserva;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FacturacionFrame extends JFrame {
    private GestionReservas gestionReservas;
    private GestionComprobantes gestionComprobantes;
    private GestionJugadores gestionJugadores;

    private JTable tablaPendientes, tablaComprobantes;
    private DefaultTableModel modeloPendientes, modeloComprobantes;
    private JRadioButton rbBoleta, rbFactura;
    private JTextField txtFechaEmision, txtRuc, txtRazonSocial;
    private JTextArea txtDetalle;

    public FacturacionFrame(GestionReservas gr, GestionComprobantes gcomp, GestionJugadores gj) {
        super("Liquidación y Facturación Automática (RF07)");
        this.gestionReservas = gr;
        this.gestionComprobantes = gcomp;
        this.gestionJugadores = gj;

        inicializarComponentes();
        cargarTablas();
    }

    private void inicializarComponentes() {
        setSize(960, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // Izquierda: Reservas listas para facturar + comprobantes emitidos
        JPanel panelIzq = new JPanel(new GridLayout(2, 1, 6, 6));

        String[] colsRes = {"N° Res.", "Fecha", "Cancha", "Titular", "Subtotal", "Adelanto", "Estado"};
        modeloPendientes = new DefaultTableModel(colsRes, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPendientes = new JTable(modeloPendientes);
        JScrollPane sp1 = new JScrollPane(tablaPendientes);
        sp1.setBorder(BorderFactory.createTitledBorder("Reservas por Facturar (En Juego / Finalizada)"));
        panelIzq.add(sp1);

        String[] colsComp = {"Serie", "Tipo", "Reserva N°", "Fecha", "Total", "Saldo"};
        modeloComprobantes = new DefaultTableModel(colsComp, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaComprobantes = new JTable(modeloComprobantes);
        JScrollPane sp2 = new JScrollPane(tablaComprobantes);
        sp2.setBorder(BorderFactory.createTitledBorder("Comprobantes Emitidos"));
        panelIzq.add(sp2);

        add(panelIzq, BorderLayout.CENTER);

        // Derecha: Formulario de emisión + vista del comprobante
        JPanel panelDer = new JPanel(new BorderLayout(6, 6));
        panelDer.setPreferredSize(new Dimension(380, 580));

        JPanel form = new JPanel(new GridLayout(6, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Emitir Comprobante"));

        rbBoleta = new JRadioButton("Boleta de Venta", true);
        rbFactura = new JRadioButton("Factura (exige RUC)");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbBoleta);
        grupo.add(rbFactura);
        form.add(rbBoleta);
        form.add(rbFactura);

        form.add(new JLabel("Fecha Emisión:"));
        txtFechaEmision = new JTextField("2026-11-20");
        form.add(txtFechaEmision);

        form.add(new JLabel("RUC (11 dígitos):"));
        txtRuc = new JTextField();
        txtRuc.setEnabled(false);
        form.add(txtRuc);

        form.add(new JLabel("Razón Social:"));
        txtRazonSocial = new JTextField();
        txtRazonSocial.setEnabled(false);
        form.add(txtRazonSocial);

        JButton btnEmitir = new JButton("Emitir Comprobante");
        JButton btnRefrescar = new JButton("Refrescar");
        form.add(btnEmitir);
        form.add(btnRefrescar);

        panelDer.add(form, BorderLayout.NORTH);

        txtDetalle = new JTextArea();
        txtDetalle.setEditable(false);
        txtDetalle.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane spDetalle = new JScrollPane(txtDetalle);
        spDetalle.setBorder(BorderFactory.createTitledBorder("Vista Previa del Comprobante"));
        panelDer.add(spDetalle, BorderLayout.CENTER);

        add(panelDer, BorderLayout.EAST);

        // Eventos
        rbBoleta.addActionListener(e -> {
            txtRuc.setEnabled(false);
            txtRazonSocial.setEnabled(false);
        });
        rbFactura.addActionListener(e -> {
            txtRuc.setEnabled(true);
            txtRazonSocial.setEnabled(true);
        });

        btnEmitir.addActionListener(e -> emitirComprobante());
        btnRefrescar.addActionListener(e -> cargarTablas());

        tablaComprobantes.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaComprobantes.getSelectedRow();
            if (fila != -1) {
                String serie = modeloComprobantes.getValueAt(fila, 0).toString();
                Comprobante c = gestionComprobantes.buscarPorSerie(serie);
                if (c != null) txtDetalle.setText(c.generarTextoComprobante());
            }
        });
    }

    private void emitirComprobante() {
        int fila = tablaPendientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva lista para facturar.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int nro = (int) modeloPendientes.getValueAt(fila, 0);
        Reserva r = gestionReservas.buscarPorNumero(nro);
        String fecha = txtFechaEmision.getText().trim();

        Comprobante emitido;
        if (rbBoleta.isSelected()) {
            String nombre = r.getTitular().getNombres() + " " + r.getTitular().getApellidos();
            emitido = gestionComprobantes.emitirBoleta(r, fecha, r.getTitular().getDni(), nombre,
                    gestionJugadores, gestionReservas);
        } else {
            String ruc = txtRuc.getText().trim();
            String razon = txtRazonSocial.getText().trim();
            if (ruc.length() != 11 || razon.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Para Factura debe ingresar RUC (11 dígitos) y Razón Social.",
                        "Validación RF07", JOptionPane.WARNING_MESSAGE);
                return;
            }
            emitido = gestionComprobantes.emitirFactura(r, fecha, ruc, razon,
                    gestionJugadores, gestionReservas);
        }

        if (emitido != null) {
            txtDetalle.setText(emitido.generarTextoComprobante());
            JOptionPane.showMessageDialog(this, "Comprobante " + emitido.getNumeroSerie() + " emitido correctamente.");
            cargarTablas();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo emitir el comprobante (ya facturada o estado inválido).",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTablas() {
        modeloPendientes.setRowCount(0);
        for (int i = 0; i < gestionReservas.getCantidad(); i++) {
            Reserva r = gestionReservas.obtener(i);
            boolean estadoValido = r.getEstado().equals("En Juego") || r.getEstado().equals("Finalizada");
            boolean yaFacturada = gestionComprobantes.buscarPorReserva(r.getNumeroCorrelativo()) != null;
            if (estadoValido && !yaFacturada) {
                modeloPendientes.addRow(new Object[]{
                    r.getNumeroCorrelativo(),
                    r.getFecha(),
                    r.getCancha().getCodigo(),
                    r.getTitular().getNombres() + " " + r.getTitular().getApellidos(),
                    String.format("S/. %.2f", r.calcularMontoTotal()),
                    String.format("S/. %.2f", r.getAdelantoPagado()),
                    r.getEstado()
                });
            }
        }

        modeloComprobantes.setRowCount(0);
        for (int i = 0; i < gestionComprobantes.getCantidad(); i++) {
            Comprobante c = gestionComprobantes.obtener(i);
            modeloComprobantes.addRow(new Object[]{
                c.getNumeroSerie(),
                c.getTipoComprobante(),
                c.getReservaAsociada().getNumeroCorrelativo(),
                c.getFechaEmision(),
                String.format("S/. %.2f", c.calcularTotal()),
                String.format("S/. %.2f", c.calcularSaldoPendiente())
            });
        }
    }
}