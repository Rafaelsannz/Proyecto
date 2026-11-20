package modelo;

public class PaquetePromocional {
    private String codigo;
    private String nombre;
    private double precioPromocional; // Precio cerrado (RF04)

    private ServicioAdicional[] serviciosIncluidos;
    private int cantidadServicios;

    public PaquetePromocional(String codigo, String nombre, double precioPromocional, int maxServicios) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioPromocional = precioPromocional;
        this.serviciosIncluidos = new ServicioAdicional[maxServicios];
        this.cantidadServicios = 0;
    }

    public boolean agregarServicio(ServicioAdicional s) {
        if (cantidadServicios < serviciosIncluidos.length) {
            serviciosIncluidos[cantidadServicios++] = s;
            return true;
        }
        return false;
    }

    public boolean validarStockServicios() {
        for (int i = 0; i < cantidadServicios; i++) {
            if (!serviciosIncluidos[i].hayStock(1)) {
                return false;
            }
        }
        return true;
    }

    public void descontarStockPaquete() {
        for (int i = 0; i < cantidadServicios; i++) {
            serviciosIncluidos[i].descontarStock(1);
        }
    }

    public void reponerStockPaquete() {
        for (int i = 0; i < cantidadServicios; i++) {
            serviciosIncluidos[i].reponerStock(1);
        }
    }

    public String getResumenServicios() {
        if (cantidadServicios == 0) return "Sin servicios";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cantidadServicios; i++) {
            sb.append(serviciosIncluidos[i].getNombre());
            if (i < cantidadServicios - 1) sb.append(", ");
        }
        return sb.toString();
    }

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPrecioPromocional() { return precioPromocional; }
    public int getCantidadServicios() { return cantidadServicios; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecioPromocional(double precioPromocional) { this.precioPromocional = precioPromocional; }

    public ServicioAdicional obtenerServicio(int index) {
        if (index >= 0 && index < cantidadServicios) {
            return serviciosIncluidos[index];
        }
        return null;
    }

    public void verInfo() {
        System.out.println("[Paquete] Código: " + codigo +
                " | " + nombre +
                " | Precio Cerrado: S/. " + precioPromocional);
        System.out.println("--- Servicios incluidos ---");
        for (int i = 0; i < cantidadServicios; i++) {
            System.out.println(" * " + serviciosIncluidos[i].getNombre());
        }
    }
}