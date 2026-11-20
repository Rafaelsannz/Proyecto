package vista;

import controlador.GestionServicios;
import modelo.PaquetePromocional;
import modelo.ServicioAdicional;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GestionServiciosFrame extends JFrame {
    private GestionServicios gestionServicios;

    // Pestaña Servicios
    private JTextField txtCodServ, txtNomServ, txtPrecioServ, txtStockServ;
    private JTable tablaServicios;
    private DefaultTableModel modeloServicios;

    // Pestaña Paquetes
    private JTextField txtCodPaq, txtNomPaq, txtPrecioPaq;
    private JComboBox<String> cmbServiciosDisponibles;
    private JTable tablaPaquetes;
    private DefaultTableModel modeloPaquetes;

    public GestionServiciosFrame(GestionServicios gs) {
        super("Gestión de Servicios Adicionales y Paquetes (RF04)");
        this.gestionServicios = gs;
        inicializarComponentes();
        cargarTablaServicios();
        cargarTablaPaquetes();
        actualizarComboServicios();
    }

    private void inicializarComponentes() {
        setSize(860, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Servicios Adicionales", crearPanelServicios());
        pestanas.addTab("Paquetes Promocionales", crearPanelPaquetes());
        add(pestanas);
    }

    private JPanel crearPanelServicios() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Datos del Servicio"));

        form.add(new JLabel("Código:"));
        txtCodServ = new JTextField();
        form.add(txtCodServ);

        form.add(new JLabel("Nombre:"));
        txtNomServ = new JTextField();
        form.add(txtNomServ);

        form.add(new JLabel("Precio Unitario (S/.):"));
        txtPrecioServ = new JTextField();
        form.add(txtPrecioServ);

        form.add(new JLabel("Stock Disponible:"));
        txtStockServ = new JTextField();
        form.add(txtStockServ);

        panel.add(form, BorderLayout.NORTH);

        String[] cols = {"Código", "Nombre", "Precio Unitario", "Stock"};
        modeloServicios = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaServicios = new JTable(modeloServicios);
        panel.add(new JScrollPane(tablaServicios), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout());
        JButton btnReg = new JButton("Registrar");
        JButton btnMod = new JButton("Modificar");
        JButton btnEli = new JButton("Eliminar");
        botones.add(btnReg);
        botones.add(btnMod);
        botones.add(btnEli);
        panel.add(botones, BorderLayout.SOUTH);

        tablaServicios.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaServicios.getSelectedRow();
            if (fila != -1) {
                txtCodServ.setText(modeloServicios.getValueAt(fila, 0).toString());
                txtNomServ.setText(modeloServicios.getValueAt(fila, 1).toString());
                txtPrecioServ.setText(modeloServicios.getValueAt(fila, 2).toString().replace("S/. ", ""));
                txtStockServ.setText(modeloServicios.getValueAt(fila, 3).toString());
            }
        });

        btnReg.addActionListener(e -> {
            try {
                String cod = txtCodServ.getText().trim();
                String nom = txtNomServ.getText().trim();
                double precio = Double.parseDouble(txtPrecioServ.getText().trim());
                int stock = Integer.parseInt(txtStockServ.getText().trim());
                if (cod.isEmpty() || nom.isEmpty() || precio < 0 || stock < 0) {
                    JOptionPane.showMessageDialog(this, "Verifique los datos ingresados.", "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (gestionServicios.registrarServicio(new ServicioAdicional(cod, nom, precio, stock))) {
                    JOptionPane.showMessageDialog(this, "Servicio registrado.");
                    cargarTablaServicios();
                    actualizarComboServicios();
                } else {
                    JOptionPane.showMessageDialog(this, "Código duplicado o capacidad llena.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio y stock deben ser numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnMod.addActionListener(e -> {
            try {
                String cod = txtCodServ.getText().trim();
                String nom = txtNomServ.getText().trim();
                double precio = Double.parseDouble(txtPrecioServ.getText().trim());
                int stock = Integer.parseInt(txtStockServ.getText().trim());
                if (gestionServicios.modificarServicio(cod, nom, precio, stock)) {
                    JOptionPane.showMessageDialog(this, "Servicio actualizado.");
                    cargarTablaServicios();
                    cargarTablaPaquetes();
                    actualizarComboServicios();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el servicio.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio y stock inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEli.addActionListener(e -> {
            String cod = txtCodServ.getText().trim();
            if (gestionServicios.eliminarServicio(cod)) {
                JOptionPane.showMessageDialog(this, "Servicio eliminado.");
                cargarTablaServicios();
                actualizarComboServicios();
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un servicio válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel crearPanelPaquetes() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Datos del Paquete Promocional"));

        form.add(new JLabel("Código Paquete:"));
        txtCodPaq = new JTextField();
        form.add(txtCodPaq);

        form.add(new JLabel("Nombre:"));
        txtNomPaq = new JTextField();
        form.add(txtNomPaq);

        form.add(new JLabel("Precio Cerrado (S/.):"));
        txtPrecioPaq = new JTextField();
        form.add(txtPrecioPaq);

        form.add(new JLabel("Agregar Servicio al Paquete:"));
        cmbServiciosDisponibles = new JComboBox<>();
        form.add(cmbServiciosDisponibles);

        JButton btnAgregarServAPaquete = new JButton("Añadir Servicio al Paquete Seleccionado");
        form.add(new JLabel(""));
        form.add(btnAgregarServAPaquete);
        form.add(new JLabel(""));
        form.add(new JLabel(""));

        panel.add(form, BorderLayout.NORTH);

        String[] cols = {"Código", "Nombre", "Precio Cerrado", "Servicios Incluidos"};
        modeloPaquetes = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaPaquetes = new JTable(modeloPaquetes);
        panel.add(new JScrollPane(tablaPaquetes), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout());
        JButton btnReg = new JButton("Crear Paquete");
        JButton btnMod = new JButton("Modificar Paquete");
        JButton btnEli = new JButton("Eliminar Paquete");
        botones.add(btnReg);
        botones.add(btnMod);
        botones.add(btnEli);
        panel.add(botones, BorderLayout.SOUTH);

        tablaPaquetes.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaPaquetes.getSelectedRow();
            if (fila != -1) {
                txtCodPaq.setText(modeloPaquetes.getValueAt(fila, 0).toString());
                txtNomPaq.setText(modeloPaquetes.getValueAt(fila, 1).toString());
                txtPrecioPaq.setText(modeloPaquetes.getValueAt(fila, 2).toString().replace("S/. ", ""));
            }
        });

        btnReg.addActionListener(e -> {
            try {
                String cod = txtCodPaq.getText().trim();
                String nom = txtNomPaq.getText().trim();
                double precio = Double.parseDouble(txtPrecioPaq.getText().trim());
                if (cod.isEmpty() || nom.isEmpty() || precio < 0) {
                    JOptionPane.showMessageDialog(this, "Complete código, nombre y precio cerrado.", "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (gestionServicios.registrarPaquete(new PaquetePromocional(cod, nom, precio, 10))) {
                    JOptionPane.showMessageDialog(this, "Paquete creado. Ahora puede añadirle servicios.");
                    cargarTablaPaquetes();
                } else {
                    JOptionPane.showMessageDialog(this, "Código duplicado o capacidad llena.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un precio numérico válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAgregarServAPaquete.addActionListener(e -> {
            String codPaq = txtCodPaq.getText().trim();
            PaquetePromocional paq = gestionServicios.buscarPaquete(codPaq);
            if (paq == null) {
                JOptionPane.showMessageDialog(this, "Seleccione o ingrese el código de un paquete existente.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cmbServiciosDisponibles.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "No hay servicios registrados.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String item = cmbServiciosDisponibles.getSelectedItem().toString();
            String codServ = item.split(" - ")[0];
            ServicioAdicional serv = gestionServicios.buscarServicio(codServ);
            if (serv != null && paq.agregarServicio(serv)) {
                JOptionPane.showMessageDialog(this, "Servicio añadido al paquete.");
                cargarTablaPaquetes();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo añadir el servicio (límite del paquete alcanzado).",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnMod.addActionListener(e -> {
            try {
                String cod = txtCodPaq.getText().trim();
                String nom = txtNomPaq.getText().trim();
                double precio = Double.parseDouble(txtPrecioPaq.getText().trim());
                if (gestionServicios.modificarPaquete(cod, nom, precio)) {
                    JOptionPane.showMessageDialog(this, "Paquete modificado.");
                    cargarTablaPaquetes();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el paquete.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEli.addActionListener(e -> {
            String cod = txtCodPaq.getText().trim();
            if (gestionServicios.eliminarPaquete(cod)) {
                JOptionPane.showMessageDialog(this, "Paquete eliminado.");
                cargarTablaPaquetes();
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un paquete válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private void cargarTablaServicios() {
        modeloServicios.setRowCount(0);
        for (int i = 0; i < gestionServicios.getCantServicios(); i++) {
            ServicioAdicional s = gestionServicios.obtenerServicio(i);
            modeloServicios.addRow(new Object[]{
                s.getCodigo(), s.getNombre(), String.format("S/. %.2f", s.getPrecioUnitario()), s.getStockDisponible()
            });
        }
    }

    private void cargarTablaPaquetes() {
        modeloPaquetes.setRowCount(0);
        for (int i = 0; i < gestionServicios.getCantPaquetes(); i++) {
            PaquetePromocional p = gestionServicios.obtenerPaquete(i);
            modeloPaquetes.addRow(new Object[]{
                p.getCodigo(), p.getNombre(), String.format("S/. %.2f", p.getPrecioPromocional()), p.getResumenServicios()
            });
        }
    }

    private void actualizarComboServicios() {
        cmbServiciosDisponibles.removeAllItems();
        for (int i = 0; i < gestionServicios.getCantServicios(); i++) {
            ServicioAdicional s = gestionServicios.obtenerServicio(i);
            cmbServiciosDisponibles.addItem(s.getCodigo() + " - " + s.getNombre());
        }
    }
}