package vista;

import controlador.GestionPersonal;
import modelo.Administrador;
import modelo.Empleado;
import modelo.EncargadoCancha;
import modelo.Recepcionista;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GestionPersonalFrame extends JFrame {
    private GestionPersonal gestionPersonal;

    private JTextField txtDni, txtNombres, txtApellidos, txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private JButton btnRegistrar, btnModificar, btnEliminar, btnLimpiar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public GestionPersonalFrame(GestionPersonal gp) {
        super("Gestión de Personal (RF01)");
        this.gestionPersonal = gp;
        inicializarComponentes();
        cargarTabla();
    }

    private void inicializarComponentes() {
        setSize(820, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Formulario superior
        JPanel panelForm = new JPanel(new GridLayout(3, 4, 8, 8));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Empleado"));

        panelForm.add(new JLabel("DNI:"));
        txtDni = new JTextField();
        panelForm.add(txtDni);

        panelForm.add(new JLabel("Rol:"));
        cmbRol = new JComboBox<>(new String[]{"Administrador", "Recepcionista", "Encargado de Cancha"});
        panelForm.add(cmbRol);

        panelForm.add(new JLabel("Nombres:"));
        txtNombres = new JTextField();
        panelForm.add(txtNombres);

        panelForm.add(new JLabel("Apellidos:"));
        txtApellidos = new JTextField();
        panelForm.add(txtApellidos);

        panelForm.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField();
        panelForm.add(txtUsuario);

        panelForm.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        panelForm.add(txtPassword);

        add(panelForm, BorderLayout.NORTH);

        // Tabla central
        String[] cols = {"DNI", "Nombres", "Apellidos", "Usuario", "Rol"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Botones inferiores
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrar = new JButton("Registrar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        add(panelBotones, BorderLayout.SOUTH);

        // Cargar datos al seleccionar fila
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                txtDni.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtNombres.setText(modeloTabla.getValueAt(fila, 1).toString());
                txtApellidos.setText(modeloTabla.getValueAt(fila, 2).toString());
                txtUsuario.setText(modeloTabla.getValueAt(fila, 3).toString());
                cmbRol.setSelectedItem(modeloTabla.getValueAt(fila, 4).toString());
                txtPassword.setText("");
            }
        });

        btnRegistrar.addActionListener(e -> registrarEmpleado());
        btnModificar.addActionListener(e -> modificarEmpleado());
        btnEliminar.addActionListener(e -> eliminarEmpleado());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void registrarEmpleado() {
        String dni = txtDni.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        String rol = cmbRol.getSelectedItem().toString();

        if (dni.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || usuario.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Empleado nuevo;
        if (rol.equals("Administrador")) {
            nuevo = new Administrador(dni, nombres, apellidos, usuario, pass);
        } else if (rol.equals("Recepcionista")) {
            nuevo = new Recepcionista(dni, nombres, apellidos, usuario, pass);
        } else {
            nuevo = new EncargadoCancha(dni, nombres, apellidos, usuario, pass);
        }

        if (gestionPersonal.registrar(nuevo)) {
            JOptionPane.showMessageDialog(this, "Empleado registrado correctamente.");
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar: DNI o usuario duplicado (o capacidad llena).",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarEmpleado() {
        String dni = txtDni.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (dni.isEmpty() || nombres.isEmpty() || apellidos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado y complete nombres y apellidos.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (gestionPersonal.modificar(dni, nombres, apellidos, pass)) {
            JOptionPane.showMessageDialog(this, "Empleado modificado correctamente.");
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el empleado con DNI " + dni,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEmpleado() {
        String dni = txtDni.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado de la tabla.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar al empleado con DNI " + dni + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (gestionPersonal.eliminar(dni)) {
                JOptionPane.showMessageDialog(this, "Empleado eliminado.");
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < gestionPersonal.getCantidad(); i++) {
            Empleado e = gestionPersonal.obtener(i);
            modeloTabla.addRow(new Object[]{
                e.getDni(), e.getNombres(), e.getApellidos(), e.getUsuario(), e.getRol()
            });
        }
    }

    private void limpiarCampos() {
        txtDni.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtUsuario.setText("");
        txtPassword.setText("");
        cmbRol.setSelectedIndex(0);
        tabla.clearSelection();
    }
}