package modelo;

public interface Facturable {
    // Constante pública, estática y final por defecto en interfaces
    double TASA_IGV = 0.18;

    // Métodos que deben implementar los comprobantes
    double calcularIGV();
    double calcularTotal();
    double calcularSaldoPendiente();
    void imprimirDetalle();
}
