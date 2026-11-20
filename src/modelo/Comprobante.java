package modelo;

public abstract class Comprobante implements Facturable {
    protected String numeroSerie;       // Ej: "B001-0001" o "F001-0001"
    protected Reserva reservaAsociada;  // Asociación con la reserva liquidada
    protected String fechaEmision;      // Formato "YYYY-MM-DD"
    protected double subtotal;

    public Comprobante(String numeroSerie, Reserva reservaAsociada, String fechaEmision) {
        this.numeroSerie = numeroSerie;
        this.reservaAsociada = reservaAsociada;
        this.fechaEmision = fechaEmision;
        this.subtotal = reservaAsociada.calcularMontoTotal();
    }

    @Override
    public double calcularIGV() {
        return this.subtotal * TASA_IGV;
    }

    @Override
    public double calcularTotal() {
        return this.subtotal + calcularIGV();
    }

    @Override
    public double calcularSaldoPendiente() {
        // Total menos el adelanto que ya pagó al confirmar la reserva
        return calcularTotal() - reservaAsociada.getAdelantoPagado();
    }

    public abstract String getTipoComprobante();

    // Getters
    public String getNumeroSerie() { return numeroSerie; }
    public Reserva getReservaAsociada() { return reservaAsociada; }
    public String getFechaEmision() { return fechaEmision; }
    public double getSubtotal() { return subtotal; }
}
