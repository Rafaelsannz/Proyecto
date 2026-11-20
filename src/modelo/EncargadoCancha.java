package modelo;

public class EncargadoCancha extends Empleado {
    public EncargadoCancha(String dni, String nombres, String apellidos, String usuario, String password) {
        super(dni, nombres, apellidos, usuario, password);
    }

    @Override
    public String getRol() {
        return "Encargado de Cancha";
    }
}
