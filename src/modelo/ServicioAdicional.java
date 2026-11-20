package modelo;

public class ServicioAdicional {
    private String codigo;
    private String nombre;
    private double precioUnitario;
    private int stockDisponible;

    public ServicioAdicional(String codigo, String nombre, double precioUnitario, int stockDisponible) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.stockDisponible = stockDisponible;
    }

    // Métodos para control de stock
    public boolean hayStock(int cantidad) {
        return this.stockDisponible >= cantidad;
    }

    public boolean descontarStock(int cantidad) {
        if (hayStock(cantidad)) {
            this.stockDisponible -= cantidad;
            return true;
        }
        return false;
    }

    public void reponerStock(int cantidad) {
        this.stockDisponible += cantidad;
    }

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPrecioUnitario() { return precioUnitario; }
    public int getStockDisponible() { return stockDisponible; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public void setStockDisponible(int stockDisponible) { this.stockDisponible = stockDisponible; }

    public void verInfo() {
        System.out.println("[Servicio] Código: " + codigo +
                           " | " + nombre +
                           " | Precio: S/. " + precioUnitario +
                           " | Stock: " + stockDisponible);
    }
}
