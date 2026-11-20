package controlador;

import modelo.Cancha;
import modelo.Jugador;
import modelo.Reserva;

public class GestionReservas {
    private Reserva[] lista;
    private int cantidad;
    private int contadorCorrelativo;

    public GestionReservas(int capacidad) {
        this.lista = new Reserva[capacidad]; // Colección en memoria fija
        this.cantidad = 0;
        this.contadorCorrelativo = 1001;     // Número correlativo inicial
    }

    public int getCantidad() { return cantidad; }

    public Reserva obtener(int index) {
        if (index >= 0 && index < cantidad) return lista[index];
        return null;
    }

    // Validación A: Rango de atención 08:00 a 23:00 y duración de 1 a 3 horas (RF05)
    public boolean validarHorarioAtencion(int horaInicio, int duracion) {
        if (duracion < 1 || duracion > 3) return false;
        int horaFin = horaInicio + duracion;
        return (horaInicio >= 8 && horaFin <= 23);
    }

    // Validación B: Sin cruces de horario para la misma cancha y fecha (RF05)
    public boolean hayCruceHorario(String codCancha, String fecha, int horaInicio, int duracion) {
        int horaFinNueva = horaInicio + duracion;

        for (int i = 0; i < cantidad; i++) {
            Reserva r = lista[i];
            // Solo comparamos si es la misma cancha, misma fecha y no está cancelada
            if (r.getCancha().getCodigo().equalsIgnoreCase(codCancha) &&
                r.getFecha().equals(fecha) &&
                !r.getEstado().equals("Cancelada")) {

                int inicioExistente = r.getHoraInicio();
                int finExistente = r.getHoraFin();

                // Condición de traslape:
                // (InicioNueva < FinExistente) Y (FinNueva > InicioExistente)
                if (horaInicio < finExistente && horaFinNueva > inicioExistente) {
                    return true; // Existe cruce de horario
                }
            }
        }
        return false;
    }

    // REGISTRAR RESERVA (RF05)
    // Retorna String vacío "" si fue exitoso, o el mensaje exacto de rechazo
    public String registrarReserva(Jugador titular, Cancha cancha, String fecha,
                                   int horaInicio, int duracion, int cantParticipantes) {
        if (cantidad >= lista.length) {
            return "Error: Capacidad máxima del sistema de reservas alcanzada.";
        }
        if (cancha.getEstado().equalsIgnoreCase("En Mantenimiento")) {
            return "Rechazo: La cancha " + cancha.getCodigo() + " se encuentra En Mantenimiento.";
        }
        if (!validarHorarioAtencion(horaInicio, duracion)) {
            return "Rechazo: El horario debe ser entre las 08:00 y las 23:00 (duración de 1 a 3 horas).";
        }
        if (cantParticipantes > cancha.getCapacidadMax()) {
            return "Rechazo: La cantidad de participantes (" + cantParticipantes +
                   ") supera la capacidad máxima de la cancha (" + cancha.getCapacidadMax() + ").";
        }
        if (hayCruceHorario(cancha.getCodigo(), fecha, horaInicio, duracion)) {
            return "Rechazo: La cancha " + cancha.getCodigo() +
                   " ya está reservada en ese horario para la fecha " + fecha + ".";
        }

        // Si cumple todas las validaciones (RF05)
        Reserva nueva = new Reserva(contadorCorrelativo++, titular, cancha, fecha, horaInicio, duracion);
        lista[cantidad++] = nueva;
        return ""; // Registro exitoso
    }

    // BUSCAR POR NÚMERO CORRELATIVO
    public Reserva buscarPorNumero(int numero) {
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getNumeroCorrelativo() == numero) return lista[i];
        }
        return null;
    }

    // ELIMINAR CON DESPLAZAMIENTO HACIA LA IZQUIERDA
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
