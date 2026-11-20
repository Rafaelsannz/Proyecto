package modelo;

public class Reserva {
    private int numeroCorrelativo;
    private Jugador titular;
    private Cancha cancha;
    private String fecha; // Formato "YYYY-MM-DD"
    private int horaInicio; // Rango de 8 a 23
    private int duracion; // 1, 2 o 3 horas
    private String estado; // "Pendiente", "Confirmada", "En Juego", "Finalizada", "Cancelada"
    private double adelantoPagado;
    private double adelantoDevuelto;

    private Empleado recepcionista;
    private Empleado encargadoCancha;

    private String[] participantes;
    private int cantParticipantes;

    private ServicioAdicional[] serviciosContratados;
    private int[] cantidadesServicios;
    private int cantServiciosAgregados;

    private PaquetePromocional[] paquetesContratados;
    private int cantPaquetesAgregados;

    private Incidencia[] incidencias;
    private int cantIncidencias;

    private String[] historialEstados;
    private int cantHistorial;

    public Reserva(int numeroCorrelativo, Jugador titular, Cancha cancha, String fecha,
                   int horaInicio, int duracion) {
        this.numeroCorrelativo = numeroCorrelativo;
        this.titular = titular;
        this.cancha = cancha;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.duracion = duracion;
        this.estado = "Pendiente";
        this.adelantoPagado = 0.0;
        this.adelantoDevuelto = 0.0;

        this.participantes = new String[cancha.getCapacidadMax()];
        this.cantParticipantes = 0;

        this.serviciosContratados = new ServicioAdicional[10];
        this.cantidadesServicios = new int[10];
        this.cantServiciosAgregados = 0;

        this.paquetesContratados = new PaquetePromocional[5];
        this.cantPaquetesAgregados = 0;

        this.incidencias = new Incidencia[10];
        this.cantIncidencias = 0;

        this.historialEstados = new String[20];
        this.cantHistorial = 0;
        registrarHistorial("Creada -> Pendiente");
    }

    private void registrarHistorial(String evento) {
        if (cantHistorial < historialEstados.length) {
            historialEstados[cantHistorial++] = evento;
        }
    }

    public boolean estaCerrada() {
        return estado.equals("Finalizada") || estado.equals("Cancelada");
    }

    // ========================================================
    // MÉTODOS PARA AÑADIR ELEMENTOS A LA RESERVA
    // ========================================================

    public boolean agregarParticipante(String nombreOIdentificacion) {
        if (estaCerrada()) return false;
        if (cantParticipantes < participantes.length) {
            participantes[cantParticipantes++] = nombreOIdentificacion;
            return true;
        }
        return false;
    }

    public boolean agregarServicio(ServicioAdicional servicio, int cantidadPedida) {
        if (estaCerrada() || cantidadPedida <= 0) return false;
        if (cantServiciosAgregados >= serviciosContratados.length) return false;

        if (servicio.hayStock(cantidadPedida)) {
            serviciosContratados[cantServiciosAgregados] = servicio;
            cantidadesServicios[cantServiciosAgregados] = cantidadPedida;
            cantServiciosAgregados++;
            servicio.descontarStock(cantidadPedida);
            return true;
        }
        return false;
    }

    public boolean agregarPaquete(PaquetePromocional paquete) {
        if (estaCerrada()) return false;
        if (cantPaquetesAgregados >= paquetesContratados.length) return false;

        if (paquete.validarStockServicios()) {
            paquetesContratados[cantPaquetesAgregados++] = paquete;
            paquete.descontarStockPaquete();
            return true;
        }
        return false;
    }

    public boolean agregarIncidencia(Incidencia incidencia) {
        if (estaCerrada() || incidencia == null) return false;
        if (cantIncidencias >= incidencias.length) return false;

        incidencias[cantIncidencias++] = incidencia;
        registrarHistorial("Incidencia registrada: " + incidencia.toString());
        return true;
    }

    // ========================================================
    // MÉTODOS DE CÁLCULO MONETARIO
    // ========================================================

    public int getHoraFin() {
        return horaInicio + duracion;
    }

    public double calcularAlquilerBruto() {
        double bruto = 0.0;
        for (int h = horaInicio; h < getHoraFin(); h++) {
            bruto += cancha.calcularTarifaHora(h);
        }
        return bruto;
    }

    public double calcularDescuentoAlquiler() {
        return calcularAlquilerBruto() * titular.getPorcentajeDescuento();
    }

    public double calcularCostoAlquiler() {
        return calcularAlquilerBruto() - calcularDescuentoAlquiler();
    }

    public double calcularTotalServicios() {
        double total = 0.0;
        for (int i = 0; i < cantServiciosAgregados; i++) {
            total += serviciosContratados[i].getPrecioUnitario() * cantidadesServicios[i];
        }
        return total;
    }

    public double calcularTotalPaquetes() {
        double total = 0.0;
        for (int i = 0; i < cantPaquetesAgregados; i++) {
            total += paquetesContratados[i].getPrecioPromocional();
        }
        return total;
    }

    public double calcularTotalAdicionales() {
        return calcularTotalServicios() + calcularTotalPaquetes();
    }

    public double calcularTotalIncidencias() {
        double total = 0.0;
        for (int i = 0; i < cantIncidencias; i++) {
            total += incidencias[i].getMonto();
        }
        return total;
    }

    public double calcularMontoTotal() {
        return calcularCostoAlquiler() + calcularTotalAdicionales() + calcularTotalIncidencias();
    }

    // ========================================================
    // CONTROL DE ESTADOS DE LA RESERVA (RF06)
    // ========================================================

    public boolean registrarAdelanto() {
        if (this.estado.equals("Pendiente")) {
            this.adelantoPagado = calcularCostoAlquiler() * 0.50;
            this.estado = "Confirmada";
            registrarHistorial("Pendiente -> Confirmada (Adelanto S/. " +
                    String.format("%.2f", adelantoPagado) + ")");
            return true;
        }
        return false;
    }

    public boolean iniciarPartido() {
        if (this.estado.equals("Confirmada")) {
            this.estado = "En Juego";
            this.cancha.setEstado("Ocupada");
            registrarHistorial("Confirmada -> En Juego (Cancha Ocupada)");
            return true;
        }
        return false;
    }

    public boolean finalizarPartido() {
        if (this.estado.equals("En Juego")) {
            this.estado = "Finalizada";
            this.cancha.setEstado("Disponible");
            registrarHistorial("En Juego -> Finalizada (Cancha Disponible)");
            return true;
        }
        return false;
    }

    // Sobrecarga (Semana 7): cancelación indicando si cumple >= 24h de anticipación
    public boolean cancelarReserva(boolean con24HorasAnticipacion) {
        if (this.estado.equals("Pendiente") || this.estado.equals("Confirmada")) {
            String estadoAnterior = this.estado;
            this.estado = "Cancelada";

            // Repone stock de servicios individuales y paquetes contratados
            for (int i = 0; i < cantServiciosAgregados; i++) {
                serviciosContratados[i].reponerStock(cantidadesServicios[i]);
            }
            for (int i = 0; i < cantPaquetesAgregados; i++) {
                paquetesContratados[i].reponerStockPaquete();
            }

            if (estadoAnterior.equals("Confirmada")) {
                if (con24HorasAnticipacion) {
                    this.adelantoDevuelto = this.adelantoPagado;
                    registrarHistorial("Confirmada -> Cancelada (>=24h: adelanto devuelto S/. " +
                            String.format("%.2f", adelantoDevuelto) + ")");
                } else {
                    this.adelantoDevuelto = 0.0;
                    registrarHistorial("Confirmada -> Cancelada (<24h: adelanto retenido S/. " +
                            String.format("%.2f", adelantoPagado) + ")");
                }
            } else {
                registrarHistorial("Pendiente -> Cancelada");
            }
            return true;
        }
        return false;
    }

    public boolean cancelarReserva() {
        return cancelarReserva(true);
    }

    // ========================================================
    // GETTERS, SETTERS Y CONSULTAS
    // ========================================================

    public int getNumeroCorrelativo() { return numeroCorrelativo; }
    public Jugador getTitular() { return titular; }
    public void setTitular(Jugador titular) { this.titular = titular; }
    public Cancha getCancha() { return cancha; }
    public String getFecha() { return fecha; }
    public int getHoraInicio() { return horaInicio; }
    public int getDuracion() { return duracion; }
    public String getEstado() { return estado; }
    public double getAdelantoPagado() { return adelantoPagado; }
    public double getAdelantoDevuelto() { return adelantoDevuelto; }
    public Empleado getRecepcionista() { return recepcionista; }
    public void setRecepcionista(Empleado recepcionista) { this.recepcionista = recepcionista; }
    public Empleado getEncargadoCancha() { return encargadoCancha; }
    public void setEncargadoCancha(Empleado encargadoCancha) { this.encargadoCancha = encargadoCancha; }
    public int getCantParticipantes() { return cantParticipantes; }
    public int getCantServiciosAgregados() { return cantServiciosAgregados; }
    public int getCantPaquetesAgregados() { return cantPaquetesAgregados; }
    public int getCantIncidencias() { return cantIncidencias; }
    public int getCantHistorial() { return cantHistorial; }

    public ServicioAdicional obtenerServicio(int index) {
        if (index >= 0 && index < cantServiciosAgregados) return serviciosContratados[index];
        return null;
    }

    public int obtenerCantidadServicio(int index) {
        if (index >= 0 && index < cantServiciosAgregados) return cantidadesServicios[index];
        return 0;
    }

    public PaquetePromocional obtenerPaquete(int index) {
        if (index >= 0 && index < cantPaquetesAgregados) return paquetesContratados[index];
        return null;
    }

    public Incidencia obtenerIncidencia(int index) {
        if (index >= 0 && index < cantIncidencias) return incidencias[index];
        return null;
    }

    public String obtenerHistorial(int index) {
        if (index >= 0 && index < cantHistorial) return historialEstados[index];
        return null;
    }

    public String getHistorialTexto() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cantHistorial; i++) {
            sb.append((i + 1)).append(". ").append(historialEstados[i]).append("\n");
        }
        return sb.toString();
    }
}