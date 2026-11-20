package modelo;

public class Administrador extends Empleado {
    public Administrador(String dni, String nombres, String apellidos, String usuario, String password) {
        super(dni, nombres, apellidos, usuario, password);
    }

    @Override
    public String getRol() {
        return "Administrador";
    }
}
