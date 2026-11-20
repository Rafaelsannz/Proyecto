package modelo;

public class CanchaFutbol7 extends Cancha {

    public CanchaFutbol7(String codigo, String nombre, double tarifaBaseHora) {
        super(codigo, nombre, 14, tarifaBaseHora); // Capacidad fija: 14 jugadores
    }

    @Override
    public double calcularTarifaHora(int hora) {
        if (hora >= 18) {
            return this.tarifaBaseHora * 1.20; // +20% recargo nocturno
        }
        return this.tarifaBaseHora;
    }

    @Override
    public String getTipo() {
        return "Fútbol 7";
    }
}
