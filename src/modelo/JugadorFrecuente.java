package modelo;

public class JugadorFrecuente extends Jugador {
    public JugadorFrecuente(String dni, String nombres, String apellidos, String telefono, String correo, int reservasFinalizadas) {
        super(dni, nombres, apellidos, telefono, correo, reservasFinalizadas);
    }

    @Override
    public double getPorcentajeDescuento() {
        return 0.10; // 10% de descuento en el alquiler
    }

    @Override
    public String getTipo() {
        return "Frecuente";
    }
}
