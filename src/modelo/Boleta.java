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
    public String generarTextoComprobante() {
        return "========================================\n" +
               "           " + getTipoComprobante() + "\n" +
               "Serie: " + numeroSerie + " | Fecha: " + fechaEmision + "\n" +
               "Cliente: " + nombreCliente + " (DNI: " + dniCliente + ")\n" +
               generarDetalleConceptos();
    }

    public String getDniCliente() { return dniCliente; }
    public String getNombreCliente() { return nombreCliente; }
}