package modelo;

public class CanchaFutbol5 extends Cancha {

    public CanchaFutbol5(String codigo, String nombre, double tarifaBaseHora) {
        super(codigo, nombre, 10, tarifaBaseHora); // Capacidad fija: 10 jugadores
    }

    @Override
    public double calcularTarifaHora(int hora) {
        if (hora >= 18) {
            return this.tarifaBaseHora * 1.15; // +15% recargo nocturno
        }
        return this.tarifaBaseHora;
    }

    @Override
    public String getTipo() {
        return "Fútbol 5";
    }
}
