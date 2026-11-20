
package modelo;

public abstract class Cancha {

    protected String codigo;
    protected String nombre;
    protected int capacidadMax;
    protected double tarifaBaseHora;
    protected String estado; // "Disponible", "Ocupada", "En Mantenimiento" 
    
    public Cancha(String codigo, String nombre, int capacidadMax, double tarifaBaseHora) { 
        this.codigo = codigo;
        this.nombre = nombre; 
        this.capacidadMax = capacidadMax; 
        this.tarifaBaseHora = tarifaBaseHora; 
        this.estado = "Disponible"; // Estado inicial por defecto 
    } 
    // Método abstracto obligatorio: cada cancha calcula su tarifa según la hora    
    public abstract double calcularTarifaHora(int hora); 
    public abstract String getTipo(); // Getters y Setters clásicos de clase 
    
    public String getCodigo() { 
        return codigo; 
    } 
    
    public String getNombre() { 
        return nombre; 
    } 
    
    public int getCapacidadMax() {
        return capacidadMax; 
    } 
    
    public double getTarifaBaseHora() {
        return tarifaBaseHora; 
    } 
    
    public String getEstado() { 
        return estado; 
    } 
    
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    } 
    
    public void setCapacidadMax(int capacidadMax) { 
        this.capacidadMax = capacidadMax; 
    } 
    
    public void setTarifaBaseHora(double tarifaBaseHora) { 
        this.tarifaBaseHora = tarifaBaseHora; 
    } 
    
    public void setEstado(String estado) {
        this.estado = estado; 
    } 
    
    public void verInfo() { 
        System.out.println("[" + getTipo() + "] Código: " + codigo + 
                " | Nombre: " + nombre + 
                " | Capacidad: " + capacidadMax + 
                " | Tarifa Base: S/. " + tarifaBaseHora + 
                " | Estado: " + estado); } 
}
