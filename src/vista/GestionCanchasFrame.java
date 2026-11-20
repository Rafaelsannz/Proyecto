package vista;

import controlador.GestionCanchas;
import modelo.Cancha;
import modelo.CanchaFutbol11;
import modelo.CanchaFutbol5;
import modelo.CanchaFutbol7;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GestionCanchasFrame extends JFrame {
    private GestionCanchas gestionCanchas;

    private JTextField txtCodigo, txtNombre, txtTarifa;
    private JComboBox<String> cmbTipo, cmbEstado;
    private JButton btnRegistrar, btnModificar, btnEliminar, btnLimpiar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public GestionCanchasFrame(GestionCanchas gc) {
        super("Gestión de Canchas y Tarifas (RF02)");
        this.gestionCanchas = gc;
        inicializarComponentes();
        cargarTabla();
    }

    private void inicializarComponentes() {
        setSize(840, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new GridLayout(3, 4, 8, 8));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos de la Cancha"));

        panelForm.add(new JLabel("Código único:"));
        txtCodigo = new JTextField();
        panelForm.add(txtCodigo);

        panelForm.add(new JLabel("Tipo de Cancha:"));
        cmbTipo = new JComboBox<>(new String[]{"Fútbol 5", "Fútbol 7", "Fútbol 11"});
        panelForm.add(cmbTipo);

        panelForm.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelForm.add(txtNombre);

        panelForm.add(new JLabel("Tarifa Base/Hora (S/.):"));
        txtTarifa = new JTextField();
        panelForm.add(txtTarifa);

        panelForm.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(new String[]{"Disponible", "Ocupada", "En Mantenimiento"});
        panelForm.add(cmbEstado);

        panelForm.add(new JLabel("Recargo nocturno (>=18h):"));
        panelForm.add(new JLabel("F5: +15% | F7: +20% | F11: +30%"));

        add(panelForm, BorderLayout.NORTH);

        String[] cols = {"Código", "Nombre", "Tipo", "Capacidad", "Tarifa Diurna", "Tarifa Nocturna", "Estado"};
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
                txtCodigo.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
                cmbTipo.setSelectedItem(modeloTabla.getValueAt(fila, 2).toString());
                txtTarifa.setText(modeloTabla.getValueAt(fila, 4).toString().replace("S/. ", ""));
                cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 6).toString());
            }
        });

        btnRegistrar.addActionListener(e -> registrarCancha());
        btnModificar.addActionListener(e -> modificarCancha());
        btnEliminar.addActionListener(e -> eliminarCancha());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void registrarCancha() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String tipo = cmbTipo.getSelectedItem().toString();
        String estado = cmbEstado.getSelectedItem().toString();

        if (codigo.isEmpty() || nombre.isEmpty() || txtTarifa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete código, nombre y tarifa base.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double tarifa;
        try {
            tarifa = Double.parseDouble(txtTarifa.getText().trim());
            if (tarifa <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese una tarifa numérica mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cancha nueva;
        if (tipo.equals("Fútbol 5")) {
            nueva = new CanchaFutbol5(codigo, nombre, tarifa);
        } else if (tipo.equals("Fútbol 7")) {
            nueva = new CanchaFutbol7(codigo, nombre, tarifa);
        } else {
            nueva = new CanchaFutbol11(codigo, nombre, tarifa);
        }
        nueva.setEstado(estado);

        if (gestionCanchas.registrar(nueva)) {
            JOptionPane.showMessageDialog(this, "Cancha registrada correctamente.");
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar: código repetido o capacidad llena.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarCancha() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String estado = cmbEstado.getSelectedItem().toString();

        if (codigo.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una cancha de la tabla.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double tarifa;
        try {
            tarifa = Double.parseDouble(txtTarifa.getText().trim());
            if (tarifa <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Tarifa inválida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (gestionCanchas.modificar(codigo, nombre, tarifa, estado)) {
            JOptionPane.showMessageDialog(this, "Cancha actualizada correctamente.");
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró la cancha con código " + codigo,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCancha() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una cancha.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar la cancha " + codigo + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (gestionCanchas.eliminar(codigo)) {
                JOptionPane.showMessageDialog(this, "Cancha eliminada.");
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró la cancha.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < gestionCanchas.getCantidad(); i++) {
            Cancha c = gestionCanchas.obtener(i);
            modeloTabla.addRow(new Object[]{
                c.getCodigo(),
                c.getNombre(),
                c.getTipo(),
                c.getCapacidadMax(),
                String.format("S/. %.2f", c.calcularTarifaHora(10)),
                String.format("S/. %.2f", c.calcularTarifaHora(19)),
                c.getEstado()
            });
        }
    }

    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtTarifa.setText("");
        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        tabla.clearSelection();
    }
}