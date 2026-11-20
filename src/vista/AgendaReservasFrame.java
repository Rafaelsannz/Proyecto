package vista;

import controlador.GestionCanchas;
import controlador.GestionJugadores;
import controlador.GestionReservas;
import controlador.GestionServicios;
import modelo.Cancha;
import modelo.Empleado;
import modelo.Incidencia;
import modelo.Jugador;
import modelo.PaquetePromocional;
import modelo.Reserva;
import modelo.ServicioAdicional;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AgendaReservasFrame extends JFrame {
    private GestionReservas gestionReservas;
    private GestionCanchas gestionCanchas;
    private GestionJugadores gestionJugadores;
    private GestionServicios gestionServicios;
    private Empleado usuario;

    // Pestaña 1: Reservas
    private JComboBox<String> cmbJugadores, cmbCanchas, cmbHoraInicio, cmbDuracion;
    private JTextField txtFecha, txtParticipantes, txtFiltroFecha;
    private JTable tablaReservas;
    private DefaultTableModel modeloReservas;

    // Pestaña 2: Agenda Diaria (RF11)
    private JComboBox<String> cmbCanchaAgenda;
    private JTextField txtFechaAgenda;
    private JTable tablaAgenda;
    private DefaultTableModel modeloAgenda;

    public AgendaReservasFrame(GestionReservas gr, GestionCanchas gc,
                               GestionJugadores gj, GestionServicios gs, Empleado emp) {
        super("Agenda Diaria y Control de Reservas (RF05, RF06, RF11)");
        this.gestionReservas = gr;
        this.gestionCanchas = gc;
        this.gestionJugadores = gj;
        this.gestionServicios = gs;
        this.usuario = emp;

        inicializarComponentes();
        cargarCombos();
        cargarTablaReservas("");
    }

    private void inicializarComponentes() {
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Gestión de Reservas", crearPanelReservas());
        pestanas.addTab("Agenda Diaria (Libres / Ocupados)", crearPanelAgendaDiaria());
        add(pestanas);
    }

    private JPanel crearPanelReservas() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        boolean esRecepcionista = usuario.getRol().equalsIgnoreCase("Recepcionista");
        boolean esEncargado = usuario.getRol().equalsIgnoreCase("Encargado de Cancha");

        // Panel superior: Formulario de creación (solo visible para Recepcionista)
        JPanel panelNorte = new JPanel(new BorderLayout(5, 5));

        if (esRecepcionista) {
            JPanel form = new JPanel(new GridLayout(3, 4, 6, 6));
            form.setBorder(BorderFactory.createTitledBorder("Nueva Reserva (RF05)"));

            form.add(new JLabel("Jugador Titular:"));
            cmbJugadores = new JComboBox<>();
            form.add(cmbJugadores);

            form.add(new JLabel("Cancha:"));
            cmbCanchas = new JComboBox<>();
            form.add(cmbCanchas);

            form.add(new JLabel("Fecha (YYYY-MM-DD):"));
            txtFecha = new JTextField("2026-11-20");
            form.add(txtFecha);

            form.add(new JLabel("Hora Inicio (08-22):"));
            cmbHoraInicio = new JComboBox<>();
            for (int h = 8; h <= 22; h++) cmbHoraInicio.addItem(String.valueOf(h));
            form.add(cmbHoraInicio);

            form.add(new JLabel("Duración (horas):"));
            cmbDuracion = new JComboBox<>(new String[]{"1", "2", "3"});
            form.add(cmbDuracion);

            form.add(new JLabel("N° Participantes:"));
            txtParticipantes = new JTextField("10");
            form.add(txtParticipantes);

            JButton btnCrear = new JButton("Crear Reserva (Pendiente)");
            btnCrear.addActionListener(e -> crearReserva());

            JPanel wrapCrear = new JPanel(new BorderLayout());
            wrapCrear.add(form, BorderLayout.CENTER);
            wrapCrear.add(btnCrear, BorderLayout.SOUTH);
            panelNorte.add(wrapCrear, BorderLayout.NORTH);
        }

        // Filtro por fecha
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltro.add(new JLabel("Filtrar Fecha (YYYY-MM-DD):"));
        txtFiltroFecha = new JTextField(10);
        panelFiltro.add(txtFiltroFecha);
        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnVerTodas = new JButton("Ver Todas");
        panelFiltro.add(btnFiltrar);
        panelFiltro.add(btnVerTodas);
        panelNorte.add(panelFiltro, BorderLayout.SOUTH);

        panel.add(panelNorte, BorderLayout.NORTH);

        // Tabla central
        String[] cols = {"N°", "Fecha", "Horario", "Cancha", "Titular", "Total (S/.)", "Adelanto", "Incidencias", "Estado"};
        modeloReservas = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaReservas = new JTable(modeloReservas);
        panel.add(new JScrollPane(tablaReservas), BorderLayout.CENTER);

        // Panel sur: Acciones según rol (RF06 / RF10)
        JPanel panelSur = new JPanel(new FlowLayout());

        JButton btnAgregarServicio = new JButton("+ Servicio");
        JButton btnAgregarPaquete = new JButton("+ Paquete");
        JButton btnConfirmar = new JButton("Confirmar (Adelanto 50%)");
        JButton btnCancelar = new JButton("Cancelar Reserva");
        JButton btnIniciar = new JButton("Iniciar Partido");
        JButton btnIncidencia = new JButton("Registrar Incidencia");
        JButton btnFinalizar = new JButton("Finalizar Partido");
        JButton btnHistorial = new JButton("Ver Historial");

        if (esRecepcionista) {
            panelSur.add(btnAgregarServicio);
            panelSur.add(btnAgregarPaquete);
            panelSur.add(btnConfirmar);
            panelSur.add(btnCancelar);
        }
        if (esEncargado) {
            panelSur.add(btnIniciar);
            panelSur.add(btnIncidencia);
            panelSur.add(btnFinalizar);
        }
        panelSur.add(btnHistorial);
        panel.add(panelSur, BorderLayout.SOUTH);

        // Eventos
        btnFiltrar.addActionListener(e -> cargarTablaReservas(txtFiltroFecha.getText().trim()));
        btnVerTodas.addActionListener(e -> cargarTablaReservas(""));
        btnAgregarServicio.addActionListener(e -> agregarServicioAReserva());
        btnAgregarPaquete.addActionListener(e -> agregarPaqueteAReserva());
        btnConfirmar.addActionListener(e -> confirmarReserva());
        btnCancelar.addActionListener(e -> cancelarReserva());
        btnIniciar.addActionListener(e -> iniciarPartido());
        btnIncidencia.addActionListener(e -> registrarIncidencia());
        btnFinalizar.addActionListener(e -> finalizarPartido());
        btnHistorial.addActionListener(e -> verHistorial());

        return panel;
    }

    private JPanel crearPanelAgendaDiaria() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.add(new JLabel("Cancha:"));
        cmbCanchaAgenda = new JComboBox<>();
        panelTop.add(cmbCanchaAgenda);

        panelTop.add(new JLabel("Fecha (YYYY-MM-DD):"));
        txtFechaAgenda = new JTextField("2026-11-20", 10);
        panelTop.add(txtFechaAgenda);

        JButton btnConsultarAgenda = new JButton("Consultar Disponibilidad");
        panelTop.add(btnConsultarAgenda);
        panel.add(panelTop, BorderLayout.NORTH);

        String[] cols = {"Franja Horaria", "Disponibilidad", "Reserva N°", "Titular", "Estado Reserva"};
        modeloAgenda = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaAgenda = new JTable(modeloAgenda);
        panel.add(new JScrollPane(tablaAgenda), BorderLayout.CENTER);

        btnConsultarAgenda.addActionListener(e -> cargarAgendaDiaria());
        return panel;
    }

    private void cargarCombos() {
        if (cmbJugadores != null) {
            cmbJugadores.removeAllItems();
            for (int i = 0; i < gestionJugadores.getCantidad(); i++) {
                Jugador j = gestionJugadores.obtener(i);
                cmbJugadores.addItem(j.getDni() + " - " + j.getNombres() + " " + j.getApellidos() + " (" + j.getTipo() + ")");
            }
        }
        if (cmbCanchas != null) cmbCanchas.removeAllItems();
        cmbCanchaAgenda.removeAllItems();
        for (int i = 0; i < gestionCanchas.getCantidad(); i++) {
            Cancha c = gestionCanchas.obtener(i);
            String item = c.getCodigo() + " - " + c.getNombre() + " [" + c.getTipo() + "]";
            if (cmbCanchas != null) cmbCanchas.addItem(item);
            cmbCanchaAgenda.addItem(item);
        }
    }

    private Reserva obtenerReservaSeleccionada() {
        int fila = tablaReservas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int nro = (int) modeloReservas.getValueAt(fila, 0);
        return gestionReservas.buscarPorNumero(nro);
    }

    private void crearReserva() {
        if (cmbJugadores.getSelectedIndex() == -1 || cmbCanchas.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debe haber al menos un jugador y una cancha registrados.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String dniJugador = cmbJugadores.getSelectedItem().toString().split(" - ")[0];
        String codCancha = cmbCanchas.getSelectedItem().toString().split(" - ")[0];
        String fecha = txtFecha.getText().trim();

        if (fecha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una fecha válida (YYYY-MM-DD).", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int horaInicio = Integer.parseInt(cmbHoraInicio.getSelectedItem().toString());
        int duracion = Integer.parseInt(cmbDuracion.getSelectedItem().toString());
        int cantPart;
        try {
            cantPart = Integer.parseInt(txtParticipantes.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad de participantes debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Jugador titular = gestionJugadores.buscarPorDni(dniJugador);
        Cancha cancha = gestionCanchas.buscarPorCodigo(codCancha);

        String error = gestionReservas.registrarReserva(titular, cancha, fecha, horaInicio, duracion, cantPart, usuario);
        if (error.isEmpty()) {
            Reserva creada = gestionReservas.obtenerUltimaReserva();
            for (int i = 1; i <= cantPart; i++) {
                creada.agregarParticipante("Participante " + i);
            }
            JOptionPane.showMessageDialog(this, "Reserva N° " + creada.getNumeroCorrelativo() + " registrada en estado Pendiente.");
            cargarTablaReservas("");
        } else {
            JOptionPane.showMessageDialog(this, error, "Reserva Rechazada", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarServicioAReserva() {
        Reserva r = obtenerReservaSeleccionada();
        if (r == null) return;
        if (r.estaCerrada()) {
            JOptionPane.showMessageDialog(this, "Una reserva Finalizada o Cancelada no puede modificarse.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (gestionServicios.getCantServicios() == 0) {
            JOptionPane.showMessageDialog(this, "No hay servicios adicionales registrados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = new String[gestionServicios.getCantServicios()];
        for (int i = 0; i < gestionServicios.getCantServicios(); i++) {
            ServicioAdicional s = gestionServicios.obtenerServicio(i);
            opciones[i] = s.getCodigo() + " - " + s.getNombre() + " (S/. " + s.getPrecioUnitario() + " | Stock: " + s.getStockDisponible() + ")";
        }

        String seleccion = (String) JOptionPane.showInputDialog(this, "Seleccione servicio:",
                "Añadir Servicio", JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (seleccion == null) return;

        String cantStr = JOptionPane.showInputDialog(this, "Ingrese cantidad:", "1");
        if (cantStr == null) return;

        try {
            int cant = Integer.parseInt(cantStr.trim());
            String cod = seleccion.split(" - ")[0];
            ServicioAdicional serv = gestionServicios.buscarServicio(cod);
            if (r.agregarServicio(serv, cant)) {
                JOptionPane.showMessageDialog(this, "Servicio agregado y stock descontado.");
                cargarTablaReservas("");
            } else {
                JOptionPane.showMessageDialog(this, "Stock insuficiente o límite de servicios alcanzado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarPaqueteAReserva() {
        Reserva r = obtenerReservaSeleccionada();
        if (r == null) return;
        if (r.estaCerrada()) {
            JOptionPane.showMessageDialog(this, "Una reserva Finalizada o Cancelada no puede modificarse.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (gestionServicios.getCantPaquetes() == 0) {
            JOptionPane.showMessageDialog(this, "No hay paquetes promocionales registrados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = new String[gestionServicios.getCantPaquetes()];
        for (int i = 0; i < gestionServicios.getCantPaquetes(); i++) {
            PaquetePromocional p = gestionServicios.obtenerPaquete(i);
            opciones[i] = p.getCodigo() + " - " + p.getNombre() + " (S/. " + p.getPrecioPromocional() + ")";
        }

        String seleccion = (String) JOptionPane.showInputDialog(this, "Seleccione paquete promocional:",
                "Añadir Paquete", JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (seleccion == null) return;

        String cod = seleccion.split(" - ")[0];
        PaquetePromocional paq = gestionServicios.buscarPaquete(cod);
        if (r.agregarPaquete(paq)) {
            JOptionPane.showMessageDialog(this, "Paquete promocional agregado.");
            cargarTablaReservas("");
        } else {
            JOptionPane.showMessageDialog(this, "Stock insuficiente en alguno de los servicios del paquete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmarReserva() {
        Reserva r = obtenerReservaSeleccionada();
        if (r == null) return;
        if (r.registrarAdelanto()) {
            JOptionPane.showMessageDialog(this, String.format("Reserva confirmada. Adelanto 50%%: S/. %.2f", r.getAdelantoPagado()));
            cargarTablaReservas("");
        } else {
            JOptionPane.showMessageDialog(this, "Solo reservas 'Pendiente' pueden confirmarse.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarReserva() {
        Reserva r = obtenerReservaSeleccionada();
        if (r == null) return;
        if (r.estaCerrada() || r.getEstado().equals("En Juego")) {
            JOptionPane.showMessageDialog(this, "Solo se pueden cancelar reservas en estado 'Pendiente' o 'Confirmada'.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int resp = JOptionPane.showConfirmDialog(this,
                "¿La cancelación se realiza con 24 horas o más de anticipación?\n(Sí = devuelve adelanto / No = retiene adelanto)",
                "Regla de 24 horas (RF06)", JOptionPane.YES_NO_CANCEL_OPTION);
        if (resp == JOptionPane.CANCEL_OPTION) return;

        boolean con24h = (resp == JOptionPane.YES_OPTION);
        if (r.cancelarReserva(con24h)) {
            String msg = con24h
                    ? String.format("Reserva cancelada. Adelanto devuelto: S/. %.2f", r.getAdelantoDevuelto())
                    : String.format("Reserva cancelada (<24h). Adelanto retenido: S/. %.2f", r.getAdelantoPagado());
            JOptionPane.showMessageDialog(this, msg);
            cargarTablaReservas("");
        }
    }

    private void iniciarPartido() {
        Reserva r = obtenerReservaSeleccionada();
        if (r == null) return;
        if (r.iniciarPartido()) {
            r.setEncargadoCancha(usuario);
            JOptionPane.showMessageDialog(this, "Partido iniciado. La cancha ahora está Ocupada.");
            cargarTablaReservas("");
        } else {
            JOptionPane.showMessageDialog(this, "Solo reservas 'Confirmada' pueden pasar a 'En Juego'.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarIncidencia() {
        Reserva r = obtenerReservaSeleccionada();
        if (r == null) return;
        if (!r.getEstado().equals("En Juego")) {
            JOptionPane.showMessageDialog(this, "Solo se pueden registrar incidencias en reservas 'En Juego'.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String desc = JOptionPane.showInputDialog(this, "Descripción de la incidencia (ej. Daño de malla, pérdida de balón):");
        if (desc == null || desc.trim().isEmpty()) return;

        String montoStr = JOptionPane.showInputDialog(this, "Monto de cargo por incidencia (S/.):", "0.0");
        if (montoStr == null) return;

        try {
            double monto = Double.parseDouble(montoStr.trim());
            if (monto < 0) throw new NumberFormatException();
            if (r.agregarIncidencia(new Incidencia(desc.trim(), monto))) {
                JOptionPane.showMessageDialog(this, "Incidencia registrada correctamente.");
                cargarTablaReservas("");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finalizarPartido() {
        Reserva r = obtenerReservaSeleccionada();
        if (r == null) return;
        if (r.finalizarPartido()) {
            Jugador actualizado = gestionJugadores.registrarCierreReserva(r.getTitular().getDni());
            gestionReservas.actualizarTitular(actualizado);
            JOptionPane.showMessageDialog(this, "Partido finalizado. Cancha liberada a Disponible.\n" +
                    "Reservas finalizadas del titular: " + actualizado.getReservasFinalizadas() +
                    " (" + actualizado.getTipo() + ").");
            cargarCombos();
            cargarTablaReservas("");
        } else {
            JOptionPane.showMessageDialog(this, "Solo reservas 'En Juego' pueden finalizarse.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verHistorial() {
        Reserva r = obtenerReservaSeleccionada();
        if (r == null) return;
        JOptionPane.showMessageDialog(this, r.getHistorialTexto(),
                "Historial de Reserva N° " + r.getNumeroCorrelativo(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarTablaReservas(String filtroFecha) {
        modeloReservas.setRowCount(0);
        for (int i = 0; i < gestionReservas.getCantidad(); i++) {
            Reserva r = gestionReservas.obtener(i);
            if (filtroFecha.isEmpty() || r.getFecha().equals(filtroFecha)) {
                modeloReservas.addRow(new Object[]{
                    r.getNumeroCorrelativo(),
                    r.getFecha(),
                    r.getHoraInicio() + ":00 - " + r.getHoraFin() + ":00",
                    r.getCancha().getCodigo() + " (" + r.getCancha().getNombre() + ")",
                    r.getTitular().getNombres() + " " + r.getTitular().getApellidos(),
                    String.format("%.2f", r.calcularMontoTotal()),
                    String.format("%.2f", r.getAdelantoPagado()),
                    String.format("%.2f", r.calcularTotalIncidencias()),
                    r.getEstado()
                });
            }
        }
    }

    private void cargarAgendaDiaria() {
        modeloAgenda.setRowCount(0);
        if (cmbCanchaAgenda.getSelectedIndex() == -1) return;

        String codCancha = cmbCanchaAgenda.getSelectedItem().toString().split(" - ")[0];
        String fecha = txtFechaAgenda.getText().trim();

        for (int h = 8; h < 23; h++) {
            Reserva r = gestionReservas.buscarReservaEnHora(codCancha, fecha, h);
            String franja = String.format("%02d:00 - %02d:00", h, h + 1);
            if (r == null) {
                modeloAgenda.addRow(new Object[]{franja, "LIBRE", "-", "-", "-"});
            } else {
                modeloAgenda.addRow(new Object[]{
                    franja,
                    "OCUPADO",
                    r.getNumeroCorrelativo(),
                    r.getTitular().getNombres() + " " + r.getTitular().getApellidos(),
                    r.getEstado()
                });
            }
        }
    }
}