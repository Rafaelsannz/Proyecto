package modelo;

public abstract class Comprobante implements Facturable {
    protected String numeroSerie;
    protected Reserva reservaAsociada;
    protected String fechaEmision;
    protected double subtotal;

    public Comprobante(String numeroSerie, Reserva reservaAsociada, String fechaEmision) {
        this.numeroSerie = numeroSerie;
        this.reservaAsociada = reservaAsociada;
        this.fechaEmision = fechaEmision;
        this.subtotal = reservaAsociada.calcularMontoTotal();
    }

    @Override
    public double calcularIGV() {
        return this.subtotal * TASA_IGV;
    }

    @Override
    public double calcularTotal() {
        return this.subtotal + calcularIGV();
    }

    @Override
    public double calcularSaldoPendiente() {
        return calcularTotal() - reservaAsociada.getAdelantoPagado();
    }

    protected String generarDetalleConceptos() {
        StringBuilder sb = new StringBuilder();
        sb.append("Reserva N°: ").append(reservaAsociada.getNumeroCorrelativo())
          .append(" | Cancha: ").append(reservaAsociada.getCancha().getNombre())
          .append(" (").append(reservaAsociada.getCancha().getTipo()).append(")\n");
        sb.append("Fecha juego: ").append(reservaAsociada.getFecha())
          .append(" | Horario: ").append(reservaAsociada.getHoraInicio()).append(":00 - ")
          .append(reservaAsociada.getHoraFin()).append(":00\n");
        sb.append("----------------------------------------\n");
        sb.append("DETALLE DE CONCEPTOS:\n");
        sb.append(String.format(" - Alquiler cancha (%d h):      S/. %8.2f\n",
                reservaAsociada.getDuracion(), reservaAsociada.calcularAlquilerBruto()));

        double desc = reservaAsociada.calcularDescuentoAlquiler();
        if (desc > 0) {
            sb.append(String.format(" - Descuento %s:       -S/. %8.2f\n",
                    reservaAsociada.getTitular().getTipo(), desc));
        }

        for (int i = 0; i < reservaAsociada.getCantServiciosAgregados(); i++) {
            ServicioAdicional s = reservaAsociada.obtenerServicio(i);
            int cant = reservaAsociada.obtenerCantidadServicio(i);
            sb.append(String.format(" - Servicio: %s (x%d): S/. %8.2f\n",
                    s.getNombre(), cant, s.getPrecioUnitario() * cant));
        }

        for (int i = 0; i < reservaAsociada.getCantPaquetesAgregados(); i++) {
            PaquetePromocional p = reservaAsociada.obtenerPaquete(i);
            sb.append(String.format(" - Paquete: %s:      S/. %8.2f\n",
                    p.getNombre(), p.getPrecioPromocional()));
        }

        for (int i = 0; i < reservaAsociada.getCantIncidencias(); i++) {
            Incidencia inc = reservaAsociada.obtenerIncidencia(i);
            sb.append(String.format(" - Incidencia: %s:   S/. %8.2f\n",
                    inc.getDescripcion(), inc.getMonto()));
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("Subtotal:                       S/. %8.2f\n", subtotal));
        sb.append(String.format("IGV (18%%):                      S/. %8.2f\n", calcularIGV()));
        sb.append(String.format("TOTAL:                          S/. %8.2f\n", calcularTotal()));
        sb.append(String.format("Adelanto pagado:               -S/. %8.2f\n", reservaAsociada.getAdelantoPagado()));
        sb.append(String.format("SALDO A PAGAR:                  S/. %8.2f\n", calcularSaldoPendiente()));
        sb.append("========================================\n");
        return sb.toString();
    }

    @Override
    public void imprimirDetalle() {
        System.out.println(generarTextoComprobante());
    }

    public abstract String getTipoComprobante();

    public String getNumeroSerie() { return numeroSerie; }
    public Reserva getReservaAsociada() { return reservaAsociada; }
    public String getFechaEmision() { return fechaEmision; }
    public double getSubtotal() { return subtotal; }
}