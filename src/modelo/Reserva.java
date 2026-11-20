package modelo;

public class Reserva {
    // 1. Atributos principales de la reserva (RF05 y RF06)
    private int numeroCorrelativo;
    private Jugador titular;
    private Cancha cancha;
    private String fecha;         // Formato "YYYY-MM-DD"
    private int horaInicio;       // Rango de 8 a 23
    private int duracion;         // 1, 2 o 3 horas
    private String estado;        // "Pendiente", "Confirmada", "En Juego", "Finalizada", "Cancelada"
    private double adelantoPagado;

    // 2. Arreglo de participantes (valida aforo según capacidad de la cancha)
    private String[] participantes;
    private int cantParticipantes;

    // 3. Arreglos para Servicios Adicionales (capacidad fija de 10 consumos)
    private ServicioAdicional[] serviciosContratados;
    private int[] cantidadesServicios;
    private int cantServiciosAgregados;

    // 4. Arreglos para Paquetes Promocionales (capacidad fija de 5 paquetes)
    private PaquetePromocional[] paquetesContratados;
    private int cantPaquetesAgregados;

    // CONSTRUCTOR
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

        this.participantes = new String[cancha.getCapacidadMax()];
        this.cantParticipantes = 0;

// LÍNEAS CORREGIDAS (con corchetes y número adentro):
        this.serviciosContratados = new ServicioAdicional[10];
        this.cantidadesServicios = new int[10];
        this.cantServiciosAgregados = 0;

        this.paquetesContratados = new PaquetePromocional[5];
        this.cantPaquetesAgregados = 0;

    }

    // ========================================================
    // MÉTODOS PARA AÑADIR A LA RESERVA
    // ========================================================

    // Añade participante respetando el aforo máximo de la cancha
    public boolean agregarParticipante(String nombreOIdentificacion) {
        if (cantParticipantes < participantes.length) {
            participantes[cantParticipantes++] = nombreOIdentificacion;
            return true;
        }
        return false;
    }

    // Añade servicio individual verificando capacidad y stock en almacén
    public boolean agregarServicio(ServicioAdicional servicio, int cantidadPedida) {
        if (cantServiciosAgregados >= serviciosContratados.length) {
            return false;
        }
        if (servicio.hayStock(cantidadPedida)) {
            serviciosContratados[cantServiciosAgregados] = servicio;
            cantidadesServicios[cantServiciosAgregados] = cantidadPedida;
            cantServiciosAgregados++;
            servicio.descontarStock(cantidadPedida);
            return true;
        }
        return false;
    }

    // Añade paquete cerrado verificando stock conjunto
    public boolean agregarPaquete(PaquetePromocional paquete) {
        if (cantPaquetesAgregados >= paquetesContratados.length) {
            return false;
        }
        if (paquete.validarStockServicios()) {
            paquetesContratados[cantPaquetesAgregados++] = paquete;
            paquete.descontarStockPaquete();
            return true;
        }
        return false;
    }

    // ========================================================
    // MÉTODOS DE CÁLCULO MONETARIO
    // ========================================================

    public int getHoraFin() {
        return horaInicio + duracion;
    }

    // Alquiler con tarifa nocturna (Cancha) y descuento por fidelización (Jugador)
    public double calcularCostoAlquiler() {
        double subtotal = 0.0;
        for (int h = horaInicio; h < getHoraFin(); h++) {
            subtotal += cancha.calcularTarifaHora(h);
        }
        double descuento = subtotal * titular.getPorcentajeDescuento();
        return subtotal - descuento;
    }

    // Total de consumos adicionales (servicios individuales + paquetes cerrados)
    public double calcularTotalAdicionales() {
        double total = 0.0;
        for (int i = 0; i < cantServiciosAgregados; i++) {
            total += serviciosContratados[i].getPrecioUnitario() * cantidadesServicios[i];
        }
        for (int i = 0; i < cantPaquetesAgregados; i++) {
            total += paquetesContratados[i].getPrecioPromocional();
        }
        return total;
    }

    // Monto general antes de liquidar
    public double calcularMontoTotal() {
        return calcularCostoAlquiler() + calcularTotalAdicionales();
    }

    // ========================================================
    // CONTROL DE ESTADOS DE LA RESERVA (RF06)
    // ========================================================

    public boolean registrarAdelanto() {
        if (this.estado.equals("Pendiente")) {
            this.adelantoPagado = calcularCostoAlquiler() * 0.50; // 50% de adelanto
            this.estado = "Confirmada";
            return true;
        }
        return false;
    }

    public boolean iniciarPartido() {
        if (this.estado.equals("Confirmada")) {
            this.estado = "En Juego";
            this.cancha.setEstado("Ocupada");
            return true;
        }
        return false;
    }

    public boolean finalizarPartido() {
        if (this.estado.equals("En Juego")) {
            this.estado = "Finalizada";
            this.cancha.setEstado("Disponible");
            return true;
        }
        return false;
    }

    public boolean cancelarReserva() {
        if (this.estado.equals("Pendiente") || this.estado.equals("Confirmada")) {
            this.estado = "Cancelada";
            this.cancha.setEstado("Disponible");
            // Devuelve el stock de los servicios contratados
            for (int i = 0; i < cantServiciosAgregados; i++) {
                serviciosContratados[i].reponerStock(cantidadesServicios[i]);
            }
            return true;
        }
        return false;
    }

    // ========================================================
    // GETTERS Y MÉTODOS DE CONSULTA
    // ========================================================
    public int getNumeroCorrelativo() { return numeroCorrelativo; }
    public Jugador getTitular() { return titular; }
    public Cancha getCancha() { return cancha; }
    public String getFecha() { return fecha; }
    public int getHoraInicio() { return horaInicio; }
    public int getDuracion() { return duracion; }
    public String getEstado() { return estado; }
    public double getAdelantoPagado() { return adelantoPagado; }
    public int getCantParticipantes() { return cantParticipantes; }
    public int getCantServiciosAgregados() { return cantServiciosAgregados; }
    public int getCantPaquetesAgregados() { return cantPaquetesAgregados; }

    public ServicioAdicional obtenerServicio(int index) {
        if (index >= 0 && index < cantServiciosAgregados) {
            return serviciosContratados[index];
        }
        return null;
    }

    public int obtenerCantidadServicio(int index) {
        if (index >= 0 && index < cantServiciosAgregados) {
            return cantidadesServicios[index];
        }
        return 0;
    }

    public PaquetePromocional obtenerPaquete(int index) {
        if (index >= 0 && index < cantPaquetesAgregados) {
            return paquetesContratados[index];
        }
        return null;
    }
}
