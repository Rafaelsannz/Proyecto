package vista;

import controlador.*;
import modelo.Empleado;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JLabel lblTitulo;
    private JLabel lblUsuario;
    private JLabel lblPassword;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnSalir;

    // Referencias a los controladores del sistema
    private GestionPersonal gestionPersonal;
    private GestionCanchas gestionCanchas;
    private GestionReservas gestionReservas;
    private GestionJugadores gestionJugadores;
    private GestionServicios gestionServicios;
    private GestionComprobantes gestionComprobantes;

    public LoginFrame(GestionPersonal gp, GestionCanchas gc, GestionReservas gr,
                      GestionJugadores gj, GestionServicios gs, GestionComprobantes gcomp) {
        super("Acceso al Sistema - Complejo Deportivo");
        this.gestionPersonal = gp;
        this.gestionCanchas = gc;
        this.gestionReservas = gr;
        this.gestionJugadores = gj;
        this.gestionServicios = gs;
        this.gestionComprobantes = gcomp;

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setSize(380, 250);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // Posicionamiento por coordenadas para un diseño limpio

        lblTitulo = new JLabel("INICIO DE SESIÓN", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBounds(40, 20, 300, 30);
        add(lblTitulo);

        lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(40, 70, 80, 25);
        add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(130, 70, 190, 25);
        add(txtUsuario);

        lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(40, 110, 80, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(130, 110, 190, 25);
        add(txtPassword);

        btnIngresar = new JButton("Ingresar");
        btnIngresar.setBounds(60, 160, 110, 30);
        add(btnIngresar);

        btnSalir = new JButton("Salir");
        btnSalir.setBounds(190, 160, 110, 30);
        add(btnSalir);

        // Eventos
        btnIngresar.addActionListener(e -> autenticarUsuario());
        btnSalir.addActionListener(e -> System.exit(0));
    }

    private void autenticarUsuario() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar usuario y contraseña.",
                                          "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validación con el controlador
        Empleado emp = gestionPersonal.autenticar(user, pass);
        if (emp != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido(a) " + emp.getNombres() +
                                          "\nRol: " + emp.getRol(), "Acceso Concedido",
                                          JOptionPane.INFORMATION_MESSAGE);

            // Abre el menú principal con permisos según rol (RF10)
            MenuPrincipalFrame menu = new MenuPrincipalFrame(emp, gestionPersonal, gestionCanchas,
                                                            gestionReservas, gestionJugadores,
                                                            gestionServicios, gestionComprobantes);
            menu.setVisible(true);
            this.dispose(); // Cierra el login
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas. Intente nuevamente.",
                                          "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtUsuario.requestFocus();
        }
    }
}
