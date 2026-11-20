package modelo;

public class PaquetePromocional {
    private String codigo;
    private String nombre;
    private double precioPromocional; // Precio cerrado (RF04)

    // Arreglo de referencias a los servicios que componen el paquete
    private ServicioAdicional[] serviciosIncluidos;
    private int cantidadServicios;

    public PaquetePromocional(String codigo, String nombre, double precioPromocional, int maxServicios) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioPromocional = precioPromocional;
        this.serviciosIncluidos = new ServicioAdicional[maxServicios];
        this.cantidadServicios = 0;
    }

    // Agregar un servicio al paquete promocional
    public boolean agregarServicio(ServicioAdicional s) {
        if (cantidadServicios < serviciosIncluidos.length) {
            serviciosIncluidos[cantidadServicios++] = s;
            return true;
        }
        return false;
    }

    // Valida si todos los servicios del paquete tienen stock
    public boolean validarStockServicios() {
        for (int i = 0; i < cantidadServicios; i++) {
            if (!serviciosIncluidos[i].hayStock(1)) {
                return false;
            }
        }
        return true;
    }

    // Descuenta 1 unidad de stock por cada servicio incluido en el paquete
    public void descontarStockPaquete() {
        for (int i = 0; i < cantidadServicios; i++) {
            serviciosIncluidos[i].descontarStock(1);
        }
    }

    // Getters
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPrecioPromocional() { return precioPromocional; }
    public int getCantidadServicios() { return cantidadServicios; }

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
            System.out.println("  * " + serviciosIncluidos[i].getNombre());
        }
    }
}
