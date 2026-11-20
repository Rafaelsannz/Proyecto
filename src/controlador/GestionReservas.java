package controlador;

import modelo.Cancha;
import modelo.Empleado;
import modelo.Jugador;
import modelo.Reserva;

public class GestionReservas {
    private Reserva[] lista;
    private int cantidad;
    private int contadorCorrelativo;

    public GestionReservas(int capacidad) {
        this.lista = new Reserva[capacidad];
        this.cantidad = 0;
        this.contadorCorrelativo = 1001;
    }

    public int getCantidad() { return cantidad; }

    public Reserva obtener(int index) {
        if (index >= 0 && index < cantidad) return lista[index];
        return null;
    }

    public Reserva obtenerUltimaReserva() {
        if (cantidad > 0) return lista[cantidad - 1];
        return null;
    }

    // Validación A: Rango 08:00 a 23:00 y duración 1 a 3 horas (RF05)
    public boolean validarHorarioAtencion(int horaInicio, int duracion) {
        if (duracion < 1 || duracion > 3) return false;
        int horaFin = horaInicio + duracion;
        return (horaInicio >= 8 && horaFin <= 23);
    }

    // Busca la reserva exacta con la que existe cruce (RF05)
    public Reserva buscarReservaEnCruce(String codCancha, String fecha, int horaInicio, int duracion) {
        int horaFinNueva = horaInicio + duracion;

        for (int i = 0; i < cantidad; i++) {
            Reserva r = lista[i];
            if (r.getCancha().getCodigo().equalsIgnoreCase(codCancha) &&
                r.getFecha().equals(fecha) &&
                !r.getEstado().equals("Cancelada")) {

                int inicioExistente = r.getHoraInicio();
                int finExistente = r.getHoraFin();

                if (horaInicio < finExistente && horaFinNueva > inicioExistente) {
                    return r;
                }
            }
        }
        return null;
    }

    public boolean hayCruceHorario(String codCancha, String fecha, int horaInicio, int duracion) {
        return buscarReservaEnCruce(codCancha, fecha, horaInicio, duracion) != null;
    }

    // Para la vista de agenda diaria: verifica si una hora puntual está ocupada (RF11)
    public Reserva buscarReservaEnHora(String codCancha, String fecha, int hora) {
        for (int i = 0; i < cantidad; i++) {
            Reserva r = lista[i];
            if (r.getCancha().getCodigo().equalsIgnoreCase(codCancha) &&
                r.getFecha().equals(fecha) &&
                !r.getEstado().equals("Cancelada")) {
                if (hora >= r.getHoraInicio() && hora < r.getHoraFin()) {
                    return r;
                }
            }
        }
        return null;
    }

    // REGISTRAR RESERVA CON RECEPCIONISTA (RF05 + RF08)
    public String registrarReserva(Jugador titular, Cancha cancha, String fecha,
                                   int horaInicio, int duracion, int cantParticipantes,
                                   Empleado recepcionista) {
        if (cantidad >= lista.length) {
            return "Error: Capacidad máxima del sistema de reservas alcanzada.";
        }
        if (titular == null || cancha == null) {
            return "Rechazo: Debe seleccionar un jugador titular y una cancha válidos.";
        }
        if (cancha.getEstado().equalsIgnoreCase("En Mantenimiento")) {
            return "Rechazo: La cancha " + cancha.getCodigo() + " se encuentra En Mantenimiento.";
        }
        if (!validarHorarioAtencion(horaInicio, duracion)) {
            return "Rechazo: El horario debe estar entre 08:00 y 23:00 (duración de 1 a 3 horas).";
        }
        if (cantParticipantes < 1 || cantParticipantes > cancha.getCapacidadMax()) {
            return "Rechazo: La cantidad de participantes (" + cantParticipantes +
                   ") debe estar entre 1 y " + cancha.getCapacidadMax() + ".";
        }

        Reserva conflicto = buscarReservaEnCruce(cancha.getCodigo(), fecha, horaInicio, duracion);
        if (conflicto != null) {
            return "Rechazo: La cancha " + cancha.getCodigo() +
                   " ya está reservada de " + conflicto.getHoraInicio() + ":00 a " +
                   conflicto.getHoraFin() + ":00 para la fecha " + fecha + ".";
        }

        Reserva nueva = new Reserva(contadorCorrelativo++, titular, cancha, fecha, horaInicio, duracion);
        nueva.setRecepcionista(recepcionista);
        lista[cantidad++] = nueva;
        return "";
    }

    // Sobrecarga de compatibilidad
    public String registrarReserva(Jugador titular, Cancha cancha, String fecha,
                                   int horaInicio, int duracion, int cantParticipantes) {
        return registrarReserva(titular, cancha, fecha, horaInicio, duracion, cantParticipantes, null);
    }

    // Sincroniza titular si el jugador ascendió a JugadorFrecuente
    public void actualizarTitular(Jugador jugadorActualizado) {
        if (jugadorActualizado == null) return;
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getTitular().getDni().equalsIgnoreCase(jugadorActualizado.getDni()) &&
                !lista[i].estaCerrada()) {
                lista[i].setTitular(jugadorActualizado);
            }
        }
    }

    public Reserva buscarPorNumero(int numero) {
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getNumeroCorrelativo() == numero) return lista[i];
        }
        return null;
    }

    public boolean eliminar(int numero) {
        int pos = -1;
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getNumeroCorrelativo() == numero) {
                pos = i;
                break;
            }
        }
        if (pos == -1) return false;

        for (int i = pos; i < cantidad - 1; i++) {
            lista[i] = lista[i + 1];
        }
        lista[cantidad - 1] = null;
        cantidad--;
        return true;
    }
}