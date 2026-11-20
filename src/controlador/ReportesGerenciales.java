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
    // 1. OCUPACIÓN POR CANCHA EN RANGO DE FECHAS (Datos para JTable + Total)
    // =========================================================================
    public Object[][] obtenerDatosOcupacion(String fechaInicio, String fechaFin, int diasRango) {
        int n = gestionCanchas.getCantidad();
        Object[][] datos = new Object[n + 1][5];
        if (diasRango < 1) diasRango = 1;

        final double horasDisponiblesPorCancha = 15.0 * diasRango;
        int totalHoras = 0;

        for (int i = 0; i < n; i++) {
            Cancha c = gestionCanchas.obtener(i);
            int horasReservadas = 0;

            for (int j = 0; j < gestionReservas.getCantidad(); j++) {
                Reserva r = gestionReservas.obtener(j);
                if (r.getCancha().getCodigo().equalsIgnoreCase(c.getCodigo()) &&
                    !r.getEstado().equalsIgnoreCase("Cancelada") &&
                    r.getFecha().compareTo(fechaInicio) >= 0 &&
                    r.getFecha().compareTo(fechaFin) <= 0) {
                    horasReservadas += r.getDuracion();
                }
            }

            totalHoras += horasReservadas;
            double porcentaje = (horasReservadas / horasDisponiblesPorCancha) * 100.0;

            datos[i][0] = c.getCodigo();
            datos[i][1] = c.getNombre();
            datos[i][2] = c.getTipo();
            datos[i][3] = horasReservadas;
            datos[i][4] = String.format("%.2f%%", porcentaje);
        }

        double pctGlobal = (n == 0) ? 0.0 : (totalHoras / (horasDisponiblesPorCancha * n)) * 100.0;
        datos[n][0] = "TOTAL";
        datos[n][1] = n + " cancha(s)";
        datos[n][2] = "-";
        datos[n][3] = totalHoras;
        datos[n][4] = String.format("%.2f%%", pctGlobal);

        return datos;
    }

    // =========================================================================
    // 2. PRODUCTIVIDAD DEL PERSONAL (Datos para JTable + Total)
    // =========================================================================
    public Object[][] obtenerDatosProductividad() {
        int n = gestionPersonal.getCantidad();
        Object[][] datos = new Object[n + 1][5];

        int totalRegistradas = 0;
        int totalSupervisados = 0;

        for (int i = 0; i < n; i++) {
            Empleado emp = gestionPersonal.obtener(i);
            int registradas = 0;
            int supervisados = 0;

            for (int j = 0; j < gestionReservas.getCantidad(); j++) {
                Reserva r = gestionReservas.obtener(j);
                if (r.getRecepcionista() != null &&
                    r.getRecepcionista().getDni().equalsIgnoreCase(emp.getDni())) {
                    registradas++;
                }
                if (r.getEncargadoCancha() != null &&
                    r.getEncargadoCancha().getDni().equalsIgnoreCase(emp.getDni())) {
                    supervisados++;
                }
            }

            totalRegistradas += registradas;
            totalSupervisados += supervisados;

            datos[i][0] = emp.getDni();
            datos[i][1] = emp.getNombres() + " " + emp.getApellidos();
            datos[i][2] = emp.getRol();
            datos[i][3] = registradas;
            datos[i][4] = supervisados;
        }

        datos[n][0] = "TOTAL";
        datos[n][1] = n + " empleado(s)";
        datos[n][2] = "-";
        datos[n][3] = totalRegistradas;
        datos[n][4] = totalSupervisados;

        return datos;
    }

    // =========================================================================
    // 3. TOP JUGADORES CON MÁS RESERVAS FINALIZADAS (Burbuja + Total)
    // =========================================================================
    public Object[][] obtenerDatosTopJugadores() {
        int n = gestionJugadores.getCantidad();
        Jugador[] ranking = new Jugador[n];
        for (int i = 0; i < n; i++) {
            ranking[i] = gestionJugadores.obtener(i);
        }

        // Ordenamiento Burbuja descendente (Semana 3)
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (ranking[j].getReservasFinalizadas() < ranking[j + 1].getReservasFinalizadas()) {
                    Jugador aux = ranking[j];
                    ranking[j] = ranking[j + 1];
                    ranking[j + 1] = aux;
                }
            }
        }

        Object[][] datos = new Object[n + 1][5];
        int totalFinalizadas = 0;

        for (int i = 0; i < n; i++) {
            totalFinalizadas += ranking[i].getReservasFinalizadas();
            datos[i][0] = i + 1;
            datos[i][1] = ranking[i].getDni();
            datos[i][2] = ranking[i].getNombres() + " " + ranking[i].getApellidos();
            datos[i][3] = ranking[i].getTipo();
            datos[i][4] = ranking[i].getReservasFinalizadas();
        }

        datos[n][0] = "-";
        datos[n][1] = "TOTAL";
        datos[n][2] = n + " jugador(es)";
        datos[n][3] = "-";
        datos[n][4] = totalFinalizadas;

        return datos;
    }

    // =========================================================================
    // 4. INGRESOS DESGLOSADOS EN RANGO DE FECHAS (Datos para JTable + Total)
    // =========================================================================
    public Object[][] obtenerDatosIngresos(String fechaInicio, String fechaFin) {
        double totalAlquiler = 0.0;
        double totalServicios = 0.0;
        double totalPaquetes = 0.0;
        double totalIncidencias = 0.0;

        for (int i = 0; i < gestionReservas.getCantidad(); i++) {
            Reserva r = gestionReservas.obtener(i);

            if (r.getEstado().equalsIgnoreCase("Finalizada") &&
                r.getFecha().compareTo(fechaInicio) >= 0 &&
                r.getFecha().compareTo(fechaFin) <= 0) {

                totalAlquiler += r.calcularCostoAlquiler();
                totalServicios += r.calcularTotalServicios();
                totalPaquetes += r.calcularTotalPaquetes();
                totalIncidencias += r.calcularTotalIncidencias();
            }
        }

        double totalGeneral = totalAlquiler + totalServicios + totalPaquetes + totalIncidencias;

        return new Object[][] {
            {"Alquiler de Canchas (neto de descuentos)", String.format("S/. %.2f", totalAlquiler)},
            {"Servicios Adicionales", String.format("S/. %.2f", totalServicios)},
            {"Paquetes Promocionales", String.format("S/. %.2f", totalPaquetes)},
            {"Cargos por Incidencias", String.format("S/. %.2f", totalIncidencias)},
            {"TOTAL GENERAL DE INGRESOS", String.format("S/. %.2f", totalGeneral)}
        };
    }

    // Métodos de consola mantenidos por compatibilidad
    public void generarReporteOcupacion(String fecha) {
        Object[][] datos = obtenerDatosOcupacion(fecha, fecha, 1);
        for (Object[] fila : datos) {
            System.out.println(fila[0] + " | " + fila[1] + " | " + fila[2] + " | " + fila[3] + "h | " + fila[4]);
        }
    }

    public void generarReporteProductividad() {
        Object[][] datos = obtenerDatosProductividad();
        for (Object[] fila : datos) {
            System.out.println(fila[0] + " | " + fila[1] + " | " + fila[2] + " | Reg: " + fila[3] + " | Sup: " + fila[4]);
        }
    }

    public void generarReporteTopJugadores() {
        Object[][] datos = obtenerDatosTopJugadores();
        for (Object[] fila : datos) {
            System.out.println(fila[0] + " | " + fila[1] + " | " + fila[2] + " | " + fila[3] + " | " + fila[4]);
        }
    }

    public void generarReporteIngresosDetallado(String fechaInicio, String fechaFin) {
        Object[][] datos = obtenerDatosIngresos(fechaInicio, fechaFin);
        for (Object[] fila : datos) {
            System.out.println(fila[0] + ": " + fila[1]);
        }
    }
}