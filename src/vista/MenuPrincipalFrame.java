package vista;

import controlador.*;
import modelo.Empleado;
import javax.swing.*;
import java.awt.*;

public class MenuPrincipalFrame extends JFrame {
    private Empleado usuarioActual;

    private GestionPersonal gestionPersonal;
    private GestionCanchas gestionCanchas;
    private GestionReservas gestionReservas;
    private GestionJugadores gestionJugadores;
    private GestionServicios gestionServicios;
    private GestionComprobantes gestionComprobantes;

    public MenuPrincipalFrame(Empleado usuario, GestionPersonal gp, GestionCanchas gc,
                              GestionReservas gr, GestionJugadores gj, GestionServicios gs,
                              GestionComprobantes gcomp) {
        super("Sistema de Complejo Deportivo - Panel Principal");
        this.usuarioActual = usuario;
        this.gestionPersonal = gp;
        this.gestionCanchas = gc;
        this.gestionReservas = gr;
        this.gestionJugadores = gj;
        this.gestionServicios = gs;
        this.gestionComprobantes = gcomp;

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setSize(520, 440);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Cabecera
        JPanel panelTop = new JPanel(new GridLayout(2, 1));
        panelTop.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        JLabel lblTitulo = new JLabel("PANEL DE CONTROL", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel lblSesion = new JLabel("Sesión: " + usuarioActual.getNombres() + " " +
                usuarioActual.getApellidos() + " [" + usuarioActual.getRol() + "]", SwingConstants.CENTER);
        lblSesion.setFont(new Font("Arial", Font.ITALIC, 13));
        panelTop.add(lblTitulo);
        panelTop.add(lblSesion);
        add(panelTop, BorderLayout.NORTH);

        // Panel central dinámico según rol (RF10)
        JPanel panelBotones = new JPanel(new GridLayout(0, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        String rol = usuarioActual.getRol();

        if (rol.equalsIgnoreCase("Administrador")) {
            JButton btnPersonal = new JButton("1. Gestión de Personal (RF01)");
            btnPersonal.addActionListener(e -> new GestionPersonalFrame(gestionPersonal).setVisible(true));
            panelBotones.add(btnPersonal);

            JButton btnCanchas = new JButton("2. Gestión de Canchas y Tarifas (RF02)");
            btnCanchas.addActionListener(e -> new GestionCanchasFrame(gestionCanchas).setVisible(true));
            panelBotones.add(btnCanchas);

            JButton btnServicios = new JButton("3. Servicios Adicionales y Paquetes (RF04)");
            btnServicios.addActionListener(e -> new GestionServiciosFrame(gestionServicios).setVisible(true));
            panelBotones.add(btnServicios);

            JButton btnReportes = new JButton("4. Reportes Gerenciales (RF08)");
            btnReportes.addActionListener(e -> {
                ReportesGerenciales rep = new ReportesGerenciales(gestionCanchas, gestionReservas,
                        gestionPersonal, gestionJugadores);
                new ReportesFrame(rep).setVisible(true);
            });
            panelBotones.add(btnReportes);

        } else if (rol.equalsIgnoreCase("Recepcionista")) {
            JButton btnJugadores = new JButton("1. Gestión de Jugadores (RF03)");
            btnJugadores.addActionListener(e -> new GestionJugadoresFrame(gestionJugadores).setVisible(true));
            panelBotones.add(btnJugadores);

            JButton btnReservas = new JButton("2. Reservas y Agenda Diaria (RF05 / RF06 / RF11)");
            btnReservas.addActionListener(e -> new AgendaReservasFrame(gestionReservas, gestionCanchas,
                    gestionJugadores, gestionServicios, usuarioActual).setVisible(true));
            panelBotones.add(btnReservas);

            JButton btnFacturacion = new JButton("3. Liquidación y Facturación (RF07)");
            btnFacturacion.addActionListener(e -> new FacturacionFrame(gestionReservas, gestionComprobantes,
                    gestionJugadores).setVisible(true));
            panelBotones.add(btnFacturacion);

        } else if (rol.equalsIgnoreCase("Encargado de Cancha")) {
            JButton btnCampo = new JButton("1. Control de Partidos, Incidencias y Agenda (RF06 / RF11)");
            btnCampo.addActionListener(e -> new AgendaReservasFrame(gestionReservas, gestionCanchas,
                    gestionJugadores, gestionServicios, usuarioActual).setVisible(true));
            panelBotones.add(btnCampo);
        }

        add(panelBotones, BorderLayout.CENTER);

        // Botón cerrar sesión
        JPanel panelSur = new JPanel(new FlowLayout());
        JButton btnCerrar = new JButton("Cerrar Sesión");
        btnCerrar.addActionListener(e -> {
            new LoginFrame(gestionPersonal, gestionCanchas, gestionReservas,
                    gestionJugadores, gestionServicios, gestionComprobantes).setVisible(true);
            this.dispose();
        });
        panelSur.add(btnCerrar);
        add(panelSur, BorderLayout.SOUTH);
    }
}