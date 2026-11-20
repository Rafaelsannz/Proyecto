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
    public String generarTextoComprobante() {
        return "========================================\n" +
               "          " + getTipoComprobante() + "\n" +
               "Serie: " + numeroSerie + " | Fecha: " + fechaEmision + "\n" +
               "Razón Social: " + razonSocial + "\n" +
               "RUC: " + ruc + "\n" +
               generarDetalleConceptos();
    }

    public String getRuc() { return ruc; }
    public String getRazonSocial() { return razonSocial; }
}