package modelo;

public class Boleta extends Comprobante {
    private String dniCliente;
    private String nombreCliente;

    public Boleta(String numeroSerie, Reserva reservaAsociada, String fechaEmision,
                  String dniCliente, String nombreCliente) {
        super(numeroSerie, reservaAsociada, fechaEmision);
        this.dniCliente = dniCliente;
        this.nombreCliente = nombreCliente;
    }

    @Override
    public String getTipoComprobante() {
        return "BOLETA DE VENTA";
    }

    @Override
    public void imprimirDetalle() {
        System.out.println("========================================");
        System.out.println("          " + getTipoComprobante());
        System.out.println("Serie: " + numeroSerie + " | Fecha: " + fechaEmision);
        System.out.println("Cliente: " + nombreCliente + " (DNI: " + dniCliente + ")");
        System.out.println("Reserva N°: " + reservaAsociada.getNumeroCorrelativo() +
                           " | Cancha: " + reservaAsociada.getCancha().getNombre());
        System.out.println("----------------------------------------");
        System.out.printf("Subtotal:        S/. %.2f\n", subtotal);
        System.out.printf("IGV (18%%):       S/. %.2f\n", calcularIGV());
        System.out.printf("Total:           S/. %.2f\n", calcularTotal());
        System.out.printf("Adelanto pagado: S/. %.2f\n", reservaAsociada.getAdelantoPagado());
        System.out.printf("SALDO A PAGAR:   S/. %.2f\n", calcularSaldoPendiente());
        System.out.println("========================================");
    }

    public String getDniCliente() { return dniCliente; }
    public String getNombreCliente() { return nombreCliente; }
}
