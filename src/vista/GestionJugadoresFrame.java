package vista;

import controlador.GestionJugadores;
import modelo.Jugador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GestionJugadoresFrame extends JFrame {
    private GestionJugadores gestionJugadores;

    private JTextField txtDni, txtNombres, txtApellidos, txtTelefono, txtCorreo;
    private JButton btnRegistrar, btnModificar, btnEliminar, btnLimpiar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public GestionJugadoresFrame(GestionJugadores gj) {
        super("Gestión de Jugadores (RF03)");
        this.gestionJugadores = gj;
        inicializarComponentes();
        cargarTabla();
    }

    private void inicializarComponentes() {
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new GridLayout(3, 4, 8, 8));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Jugador"));

        panelForm.add(new JLabel("DNI:"));
        txtDni = new JTextField();
        panelForm.add(txtDni);

        panelForm.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        panelForm.add(txtTelefono);

        panelForm.add(new JLabel("Nombres:"));
        txtNombres = new JTextField();
        panelForm.add(txtNombres);

        panelForm.add(new JLabel("Apellidos:"));
        txtApellidos = new JTextField();
        panelForm.add(txtApellidos);

        panelForm.add(new JLabel("Correo electrónico:"));
        txtCorreo = new JTextField();
        panelForm.add(txtCorreo);

        panelForm.add(new JLabel("Ascenso automático:"));
        panelForm.add(new JLabel("Frecuente desde 5 reservas finalizadas (10% dcto)"));

        add(panelForm, BorderLayout.NORTH);

        String[] cols = {"DNI", "Nombres", "Apellidos", "Teléfono", "Correo", "Tipo", "Finalizadas", "Descuento"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

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

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                txtDni.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtNombres.setText(modeloTabla.getValueAt(fila, 1).toString());
                txtApellidos.setText(modeloTabla.getValueAt(fila, 2).toString());
                txtTelefono.setText(modeloTabla.getValueAt(fila, 3).toString());
                txtCorreo.setText(modeloTabla.getValueAt(fila, 4).toString());
            }
        });

        btnRegistrar.addActionListener(e -> registrarJugador());
        btnModificar.addActionListener(e -> modificarJugador());
        btnEliminar.addActionListener(e -> eliminarJugador());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void registrarJugador() {
        String dni = txtDni.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String tel = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (dni.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || tel.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (gestionJugadores.registrar(dni, nombres, apellidos, tel, correo)) {
            JOptionPane.showMessageDialog(this, "Jugador registrado correctamente.");
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar: DNI duplicado o capacidad llena.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarJugador() {
        String dni = txtDni.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String tel = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (dni.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || tel.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos para modificar.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (gestionJugadores.modificar(dni, nombres, apellidos, tel, correo)) {
            JOptionPane.showMessageDialog(this, "Jugador modificado correctamente.");
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el jugador con DNI " + dni,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarJugador() {
        String dni = txtDni.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un jugador de la tabla.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar al jugador con DNI " + dni + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (gestionJugadores.eliminar(dni)) {
                JOptionPane.showMessageDialog(this, "Jugador eliminado.");
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el jugador.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < gestionJugadores.getCantidad(); i++) {
            Jugador j = gestionJugadores.obtener(i);
            modeloTabla.addRow(new Object[]{
                j.getDni(),
                j.getNombres(),
                j.getApellidos(),
                j.getTelefono(),
                j.getCorreo(),
                j.getTipo(),
                j.getReservasFinalizadas(),
                String.format("%.0f%%", j.getPorcentajeDescuento() * 100)
            });
        }
    }

    private void limpiarCampos() {
        txtDni.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        tabla.clearSelection();
    }
}