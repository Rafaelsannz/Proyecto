package controlador;

import modelo.Boleta;
import modelo.Comprobante;
import modelo.Factura;
import modelo.Reserva;

public class GestionComprobantes {
    private Comprobante[] lista;
    private int cantidad;
    private int correlativoBoleta;
    private int correlativoFactura;

    public GestionComprobantes(int capacidad) {
        this.lista = new Comprobante[capacidad]; // Arreglo en memoria fija
        this.cantidad = 0;
        this.correlativoBoleta = 1;
        this.correlativoFactura = 1;
    }

    public int getCantidad() { return cantidad; }

    public Comprobante obtener(int index) {
        if (index >= 0 && index < cantidad) return lista[index];
        return null;
    }

    // EMITIR BOLETA
    public Boleta emitirBoleta(Reserva r, String fecha, String dni, String nombre) {
        if (cantidad >= lista.length) return null;

        // Genera serie: B001-0001, B001-0002...
        String serie = String.format("B001-%04d", correlativoBoleta++);
        Boleta b = new Boleta(serie, r, fecha, dni, nombre);

        lista[cantidad++] = b;
        r.finalizarPartido(); // La reserva pasa a Finalizada y la cancha a Disponible
        return b;
    }

    // EMITIR FACTURA (Valida que el RUC tenga 11 dígitos según RF07)
    public Factura emitirFactura(Reserva r, String fecha, String ruc, String razonSocial) {
        if (cantidad >= lista.length) return null;
        if (ruc == null || ruc.trim().length() != 11) {
            return null; // RUC inválido
        }

        // Genera serie: F001-0001, F001-0002...
        String serie = String.format("F001-%04d", correlativoFactura++);
        Factura f = new Factura(serie, r, fecha, ruc, razonSocial);

        lista[cantidad++] = f;
        r.finalizarPartido(); // La reserva pasa a Finalizada y la cancha a Disponible
        return f;
    }

    // BUSCAR POR SERIE
    public Comprobante buscarPorSerie(String serie) {
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getNumeroSerie().equalsIgnoreCase(serie)) {
                return lista[i];
            }
        }
        return null;
    }

    // LISTAR TODOS LOS COMPROBANTES
    public void mostrarTodos() {
        for (int i = 0; i < cantidad; i++) {
            lista[i].imprimirDetalle(); // Polimorfismo puro en ejecución
        }
    }
}
