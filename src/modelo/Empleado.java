package modelo;

public abstract class Empleado {
    protected String dni;
    protected String nombres;
    protected String apellidos;
    protected String usuario;
    protected String password;

    public Empleado(String dni, String nombres, String apellidos, String usuario, String password) {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.usuario = usuario;
        this.password = password;
    }

    // Método abstracto obligatorio: retorna el rol específico del empleado
    public abstract String getRol();

    // Verificación de credenciales (Semana 11 - Login)
    public boolean autenticar(String user, String pass) {
        return this.usuario.equals(user) && this.password.equals(pass);
    }

    // Getters y Setters
    public String getDni() { return dni; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getUsuario() { return usuario; }
    public String getPassword() { return password; }

    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setPassword(String password) { this.password = password; }

    public void verInfo() {
        System.out.println("[" + getRol() + "] DNI: " + dni +
                           " | Nombre: " + nombres + " " + apellidos +
                           " | Usuario: " + usuario);
    }
}
