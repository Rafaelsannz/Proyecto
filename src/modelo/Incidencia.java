package modelo;

public class Incidencia {
    private String descripcion;
    private double monto;

    public Incidencia(String descripcion, double monto) {
        this.descripcion = descripcion;
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getMonto() {
        return monto;
    }

    @Override
    public String toString() {
        return descripcion + " (S/. " + String.format("%.2f", monto) + ")";
    }
}