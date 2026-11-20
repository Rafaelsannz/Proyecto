package vista;

import controlador.*;
import modelo.Empleado;
import javax.swing.*;
import java.awt.*;

public class MenuPrincipalFrame extends JFrame {
    private Empleado usuarioActual;

    // Controladores
    private GestionPersonal gestionPersonal;
    private GestionCanchas gestionCanchas;
    private GestionReservas gestionReservas;
    private GestionJugadores gestionJugadores;
    private GestionServicios gestionServicios;
    private GestionComprobantes gestionComprobantes;

    // Botones del menú
    private JButton btnGestionPersonal;
    private JButton btnGestionCanchas;
    private JButton btnGestionServicios;
    private JButton btnGestionJugadores;
    private JButton btnGestionReservas;
    private JButton btnFacturacion;
    private JButton btnReportes;
    private JButton btnCerrarSesion;
    private JLabel lblUsuarioSesion;

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
        aplicarControlDeAccesoPorRol(); // RF10: Bloquear según rol
    }

    private void inicializarComponentes() {
        setSize(600, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel lblTitulo = new JLabel("PANEL DE CONTROL", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(50, 20, 500, 30);
        add(lblTitulo);

        lblUsuarioSesion = new JLabel("Sesión: " + usuarioActual.getNombres() + " " +
                                      usuarioActual.getApellidos() + " [" + usuarioActual.getRol() + "]");
        lblUsuarioSesion.setFont(new Font("Arial", Font.ITALIC, 12));
        lblUsuarioSesion.setBounds(50, 55, 500, 20);
        add(lblUsuarioSesion);

        // Módulo Administrador
        btnGestionPersonal = new JButton("1. Gestión de Personal (RF01)");
        btnGestionPersonal.setBounds(70, 90, 440, 35);
        add(btnGestionPersonal);

        btnGestionCanchas = new JButton("2. Gestión de Canchas y Tarifas (RF02)");
        btnGestionCanchas.setBounds(70, 135, 440, 35);
        add(btnGestionCanchas);

        btnGestionServicios = new JButton("3. Servicios Adicionales y Paquetes (RF04)");
        btnGestionServicios.setBounds(70, 180, 440, 35);
        add(btnGestionServicios);

        // Módulo Recepcionista
        btnGestionJugadores = new JButton("4. Registro de Jugadores (RF03)");
        btnGestionJugadores.setBounds(70, 225, 440, 35);
        add(btnGestionJugadores);

        btnGestionReservas = new JButton("5. Creación y Control de Reservas (RF05/RF06)");
        btnGestionReservas.setBounds(70, 270, 440, 35);
        add(btnGestionReservas);

        btnFacturacion = new JButton("6. Liquidación y Facturación (RF07)");
        btnFacturacion.setBounds(70, 315, 440, 35);
        add(btnFacturacion);

        // Reportes Gerenciales
        btnReportes = new JButton("7. Reportes Gerenciales (RF08)");
        btnReportes.setBounds(70, 360, 440, 35);
        add(btnReportes);

        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBounds(230, 405, 130, 30);
        add(btnCerrarSesion);

        // Acción Cerrar Sesión
        btnCerrarSesion.addActionListener(e -> {
            new LoginFrame(gestionPersonal, gestionCanchas, gestionReservas,
                           gestionJugadores, gestionServicios, gestionComprobantes).setVisible(true);
            this.dispose();
        });

        // Eventos a las ventanas modulares
        btnGestionReservas.addActionListener(e -> {
            new AgendaReservasFrame(gestionReservas, gestionCanchas, usuarioActual).setVisible(true);
        });

        btnReportes.addActionListener(e -> {
            ReportesGerenciales rep = new ReportesGerenciales(gestionCanchas, gestionReservas,
                                                              gestionPersonal, gestionJugadores);
            // Por consola o en ventana de texto
            rep.generarReporteTopJugadores();
            rep.generarReporteProductividad();
            JOptionPane.showMessageDialog(this, "Reportes impresos en consola correctamente.",
                                          "Reportes RF08", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // LÓGICA DE CONTROL DE ACCESO POR ROLES (RF10)
    private void aplicarControlDeAccesoPorRol() {
        String rol = usuarioActual.getRol();

        if (rol.equalsIgnoreCase("Administrador")) {
            // El administrador tiene acceso a todos los módulos
        } else if (rol.equalsIgnoreCase("Recepcionista")) {
            // No gestiona empleados ni infraestructura
            btnGestionPersonal.setEnabled(false);
            btnGestionCanchas.setEnabled(false);
            btnGestionServicios.setEnabled(false);
            btnReportes.setEnabled(false);
        } else if (rol.equalsIgnoreCase("Encargado de Cancha")) {
            // Solo atiende el flujo de reservas en campo
            btnGestionPersonal.setEnabled(false);
            btnGestionCanchas.setEnabled(false);
            btnGestionServicios.setEnabled(false);
            btnGestionJugadores.setEnabled(false);
            btnFacturacion.setEnabled(false);
            btnReportes.setEnabled(false);
        }
    }
}
