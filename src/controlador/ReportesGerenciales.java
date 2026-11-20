package controlador;

import modelo.Cancha;
import modelo.Empleado;
import modelo.Jugador;
import modelo.Reserva;

public class ReportesGerenciales {
    private GestionCanchas gestionCanchas;
    private GestionReservas gestionReservas;
    private GestionPersonal gestionPersonal;
    private GestionJugadores gestionJugadores;

    public ReportesGerenciales(GestionCanchas gc, GestionReservas gr,
                               GestionPersonal gp, GestionJugadores gj) {
        this.gestionCanchas = gc;
        this.gestionReservas = gr;
        this.gestionPersonal = gp;
        this.gestionJugadores = gj;
    }

    // =========================================================================
    // 1. REPORTE DE OCUPACIÓN POR CANCHA (Horas y % en una fecha)
    // =========================================================================
    public void generarReporteOcupacion(String fecha) {
        System.out.println("====================================================================");
        System.out.println("          REPORTE 1: OCUPACIÓN POR CANCHA - FECHA: " + fecha);
        System.out.println("====================================================================");
        System.out.printf("%-10s %-15s %-12s %-15s %-15s\n",
                          "CÓDIGO", "NOMBRE", "TIPO", "HORAS OCUPADAS", "% OCUPACIÓN");
        System.out.println("--------------------------------------------------------------------");

        // El horario operativo va de 08:00 a 23:00 -> 15 horas posibles por día
        final double HORAS_TOTALES_DIA = 15.0;

        for (int i = 0; i < gestionCanchas.getCantidad(); i++) {
            Cancha c = gestionCanchas.obtener(i);
            int horasReservadas = 0;

            // Recorrer las reservas para sumar las horas en esa fecha
            for (int j = 0; j < gestionReservas.getCantidad(); j++) {
                Reserva r = gestionReservas.obtener(j);
                if (r.getCancha().getCodigo().equalsIgnoreCase(c.getCodigo()) &&
                    r.getFecha().equals(fecha) &&
                    !r.getEstado().equalsIgnoreCase("Cancelada")) {
                    horasReservadas += r.getDuracion();
                }
            }

            double porcentaje = (horasReservadas / HORAS_TOTALES_DIA) * 100.0;
            System.out.printf("%-10s %-15s %-12s %-15d %6.2f%%\n",
                              c.getCodigo(), c.getNombre(), c.getTipo(), horasReservadas, porcentaje);
        }
        System.out.println("====================================================================\n");
    }

    // =========================================================================
    // 2. REPORTE DE PRODUCTIVIDAD DEL PERSONAL
    // =========================================================================
    public void generarReporteProductividad() {
        System.out.println("====================================================================");
        System.out.println("          REPORTE 2: PRODUCTIVIDAD DEL PERSONAL");
        System.out.println("====================================================================");
        System.out.printf("%-10s %-25s %-20s %-10s\n", "DNI", "NOMBRE COMPLETO", "ROL", "PARTICIPACIONES");
        System.out.println("--------------------------------------------------------------------");

        for (int i = 0; i < gestionPersonal.getCantidad(); i++) {
            Empleado emp = gestionPersonal.obtener(i);
            int contadorActividad = 0;

            for (int j = 0; j < gestionReservas.getCantidad(); j++) {
                Reserva r = gestionReservas.obtener(j);

                // Si es recepcionista y registró la reserva
                if (r.getRecepcionista() != null &&
                    r.getRecepcionista().getDni().equalsIgnoreCase(emp.getDni())) {
                    contadorActividad++;
                }
                // Si es encargado y supervisó el partido
                if (r.getEncargadoCancha() != null &&
                    r.getEncargadoCancha().getDni().equalsIgnoreCase(emp.getDni())) {
                    contadorActividad++;
                }
            }

            String nombreCompleto = emp.getNombres() + " " + emp.getApellidos();
            System.out.printf("%-10s %-25s %-20s %-10d\n",
                              emp.getDni(), nombreCompleto, emp.getRol(), contadorActividad);
        }
        System.out.println("====================================================================\n");
    }

    // =========================================================================
    // 3. REPORTE TOP JUGADORES (Ordenamiento Burbuja por reservas finalizadas)
    // =========================================================================
    public void generarReporteTopJugadores() {
        System.out.println("====================================================================");
        System.out.println("          REPORTE 3: TOP JUGADORES CON MÁS RESERVAS FINALIZADAS");
        System.out.println("====================================================================");
        System.out.printf("%-10s %-25s %-15s %-10s\n", "DNI", "NOMBRE COMPLETO", "CATEGORÍA", "FINALIZADAS");
        System.out.println("--------------------------------------------------------------------");

        int n = gestionJugadores.getCantidad();
        if (n == 0) {
            System.out.println("No hay jugadores registrados en el sistema.");
            System.out.println("====================================================================\n");
            return;
        }

        // Crear una copia temporal de referencias para no desordenar el arreglo principal
        Jugador[] ranking = new Jugador[n];
        for (int i = 0; i < n; i++) {
            ranking[i] = gestionJugadores.obtener(i);
        }

        // Algoritmo de Ordenamiento Burbuja: De Mayor a Menor
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (ranking[j].getReservasFinalizadas() < ranking[j + 1].getReservasFinalizadas()) {
                    Jugador aux = ranking[j];
                    ranking[j] = ranking[j + 1];
                    ranking[j + 1] = aux;
                }
            }
        }

        // Mostrar el listado ordenado
        for (int i = 0; i < n; i++) {
            String nombre = ranking[i].getNombres() + " " + ranking[i].getApellidos();
            System.out.printf("%-10s %-25s %-15s %-10d\n",
                              ranking[i].getDni(), nombre, ranking[i].getTipo(),
                              ranking[i].getReservasFinalizadas());
        }
        System.out.println("====================================================================\n");
    }

    // =========================================================================
    // 4. REPORTE DE INGRESOS DETALLADO POR CONCEPTOS
    // =========================================================================
    public void generarReporteIngresosDetallado(String fechaInicio, String fechaFin) {
        System.out.println("====================================================================");
        System.out.println("          REPORTE 4: INGRESOS DESGLOSADOS POR CONCEPTO");
        System.out.println("Rango: " + fechaInicio + " hasta " + fechaFin);
        System.out.println("====================================================================");

        double totalAlquilerCanchas = 0.0;
        double totalServiciosAdicionales = 0.0;
        double totalPaquetes = 0.0;

        for (int i = 0; i < gestionReservas.getCantidad(); i++) {
            Reserva r = gestionReservas.obtener(i);

            // Consideramos reservas que no estén canceladas y dentro del rango de fechas
            if (!r.getEstado().equalsIgnoreCase("Cancelada") &&
                r.getFecha().compareTo(fechaInicio) >= 0 &&
                r.getFecha().compareTo(fechaFin) <= 0) {

                // A. Alquiler de Cancha
                totalAlquilerCanchas += r.calcularCostoAlquiler();

                // B. Servicios individuales
                for (int s = 0; s < r.getCantServiciosAgregados(); s++) {
                    totalServiciosAdicionales += r.obtenerServicio(s).getPrecioUnitario()
                                                 * r.obtenerCantidadServicio(s);
                }

                // C. Paquetes cerrados
                for (int p = 0; p < r.getCantPaquetesAgregados(); p++) {
                    totalPaquetes += r.obtenerPaquete(p).getPrecioPromocional();
                }
            }
        }

        double totalGeneral = totalAlquilerCanchas + totalServiciosAdicionales + totalPaquetes;

        System.out.printf("  * Total Alquiler de Canchas:        S/. %10.2f\n", totalAlquilerCanchas);
        System.out.printf("  * Total Servicios Adicionales:      S/. %10.2f\n", totalServiciosAdicionales);
        System.out.printf("  * Total Paquetes Promocionales:     S/. %10.2f\n", totalPaquetes);
        System.out.println("--------------------------------------------------------------------");
        System.out.printf("  TOTAL GENERAL DE INGRESOS (Neto):   S/. %10.2f\n", totalGeneral);
        System.out.println("====================================================================\n");
    }
}
