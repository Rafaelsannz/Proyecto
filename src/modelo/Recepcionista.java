package modelo;

public class Recepcionista extends Empleado {
    public Recepcionista(String dni, String nombres, String apellidos, String usuario, String password) {
        super(dni, nombres, apellidos, usuario, password);
    }

    @Override
    public String getRol() {
        return "Recepcionista";
    }
}
