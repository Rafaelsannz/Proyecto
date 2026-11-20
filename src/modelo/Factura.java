package modelo;

public class Factura extends Comprobante {
    private String ruc;
    private String razonSocial;

    public Factura(String numeroSerie, Reserva reservaAsociada, String fechaEmision,
                   String ruc, String razonSocial) {
        super(numeroSerie, reservaAsociada, fechaEmision);
        this.ruc = ruc;
        this.razonSocial = razonSocial;
    }

    @Override
    public String getTipoComprobante() {
        return "FACTURA ELECTRÓNICA";
    }

    @Override
    public void imprimirDetalle() {
        System.out.println("========================================");
        System.out.println("        " + getTipoComprobante());
        System.out.println("Serie: " + numeroSerie + " | Fecha: " + fechaEmision);
        System.out.println("Razón Social: " + razonSocial);
        System.out.println("RUC: " + ruc);
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

    public String getRuc() { return ruc; }
    public String getRazonSocial() { return razonSocial; }
}
