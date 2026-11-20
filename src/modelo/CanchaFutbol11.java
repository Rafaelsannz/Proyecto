package modelo;

public class CanchaFutbol11 extends Cancha {

    public CanchaFutbol11(String codigo, String nombre, double tarifaBaseHora) {
        super(codigo, nombre, 22, tarifaBaseHora); // Capacidad fija: 22 jugadores
    }

    @Override
    public double calcularTarifaHora(int hora) {
        if (hora >= 18) {
            return this.tarifaBaseHora * 1.30; // +30% recargo nocturno
        }
        return this.tarifaBaseHora;
    }

    @Override
    public String getTipo() {
        return "Fútbol 11";
    }
}
