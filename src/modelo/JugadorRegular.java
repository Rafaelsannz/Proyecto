package modelo;

public class JugadorRegular extends Jugador {
    public JugadorRegular(String dni, String nombres, String apellidos, String telefono, String correo, int reservasFinalizadas) {
        super(dni, nombres, apellidos, telefono, correo, reservasFinalizadas);
    }

    @Override
    public double getPorcentajeDescuento() {
        return 0.0; // Sin descuento
    }

    @Override
    public String getTipo() {
        return "Regular";
    }
}
