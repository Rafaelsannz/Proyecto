package modelo;

public abstract class Jugador {
    protected String dni;
    protected String nombres;
    protected String apellidos;
    protected String telefono;
    protected String correo;
    protected int reservasFinalizadas;

    public Jugador(String dni, String nombres, String apellidos, String telefono, String correo, int reservasFinalizadas) {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
        this.reservasFinalizadas = reservasFinalizadas;
    }

    public abstract double getPorcentajeDescuento();
    public abstract String getTipo();

    public void incrementarReservaFinalizada() {
        this.reservasFinalizadas++;
    }

    // Getters y Setters
    public String getDni() { return dni; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public int getReservasFinalizadas() { return reservasFinalizadas; }

    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCorreo(String correo) { this.correo = correo; }

    public void verInfo() {
        System.out.println("[" + getTipo() + "] DNI: " + dni +
                " | " + nombres + " " + apellidos +
                " | Reservas: " + reservasFinalizadas +
                " | Dcto: " + (getPorcentajeDescuento() * 100) + "%");
    }
}