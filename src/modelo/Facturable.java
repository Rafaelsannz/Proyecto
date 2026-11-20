package modelo;

public interface Facturable {
    double TASA_IGV = 0.18;

    double calcularIGV();
    double calcularTotal();
    double calcularSaldoPendiente();
    String generarTextoComprobante();
    void imprimirDetalle();
}