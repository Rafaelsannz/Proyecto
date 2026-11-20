package controlador;

import modelo.Boleta;
import modelo.Comprobante;
import modelo.Factura;
import modelo.Jugador;
import modelo.Reserva;

public class GestionComprobantes {
    private Comprobante[] lista;
    private int cantidad;
    private int correlativoBoleta;
    private int correlativoFactura;

    public GestionComprobantes(int capacidad) {
        this.lista = new Comprobante[capacidad];
        this.cantidad = 0;
        this.correlativoBoleta = 1;
        this.correlativoFactura = 1;
    }

    public int getCantidad() { return cantidad; }

    public Comprobante obtener(int index) {
        if (index >= 0 && index < cantidad) return lista[index];
        return null;
    }

    public Comprobante buscarPorReserva(int numeroCorrelativo) {
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getReservaAsociada().getNumeroCorrelativo() == numeroCorrelativo) {
                return lista[i];
            }
        }
        return null;
    }

    private boolean puedeFacturarse(Reserva r) {
        if (r == null) return false;
        if (buscarPorReserva(r.getNumeroCorrelativo()) != null) return false;
        return r.getEstado().equals("En Juego") || r.getEstado().equals("Finalizada");
    }

    private void procesarCierreSiCorresponde(Reserva r, GestionJugadores gj, GestionReservas gr) {
        if (r.getEstado().equals("En Juego")) {
            r.finalizarPartido();
            if (gj != null) {
                Jugador actualizado = gj.registrarCierreReserva(r.getTitular().getDni());
                if (gr != null && actualizado != null) {
                    gr.actualizarTitular(actualizado);
                }
            }
        }
    }

    // EMITIR BOLETA (con actualización de jugador frecuente)
    public Boleta emitirBoleta(Reserva r, String fecha, String dni, String nombre,
                               GestionJugadores gj, GestionReservas gr) {
        if (cantidad >= lista.length || !puedeFacturarse(r)) return null;

        String serie = String.format("B001-%04d", correlativoBoleta++);
        Boleta b = new Boleta(serie, r, fecha, dni, nombre);

        lista[cantidad++] = b;
        procesarCierreSiCorresponde(r, gj, gr);
        return b;
    }

    // Sobrecarga de compatibilidad
    public Boleta emitirBoleta(Reserva r, String fecha, String dni, String nombre) {
        return emitirBoleta(r, fecha, dni, nombre, null, null);
    }

    // EMITIR FACTURA (valida RUC de 11 dígitos y razón social - RF07)
    public Factura emitirFactura(Reserva r, String fecha, String ruc, String razonSocial,
                                 GestionJugadores gj, GestionReservas gr) {
        if (cantidad >= lista.length || !puedeFacturarse(r)) return null;
        if (ruc == null || ruc.trim().length() != 11) return null;
        if (razonSocial == null || razonSocial.trim().isEmpty()) return null;

        String serie = String.format("F001-%04d", correlativoFactura++);
        Factura f = new Factura(serie, r, fecha, ruc.trim(), razonSocial.trim());

        lista[cantidad++] = f;
        procesarCierreSiCorresponde(r, gj, gr);
        return f;
    }

    // Sobrecarga de compatibilidad
    public Factura emitirFactura(Reserva r, String fecha, String ruc, String razonSocial) {
        return emitirFactura(r, fecha, ruc, razonSocial, null, null);
    }

    public Comprobante buscarPorSerie(String serie) {
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getNumeroSerie().equalsIgnoreCase(serie)) {
                return lista[i];
            }
        }
        return null;
    }

    public void mostrarTodos() {
        for (int i = 0; i < cantidad; i++) {
            lista[i].imprimirDetalle();
        }
    }
}