package principal;

import controlador.*;
import modelo.*;
import vista.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // 1. Controladores en memoria con arreglos de capacidad fija
        GestionPersonal gp = new GestionPersonal(50);
        GestionCanchas gc = new GestionCanchas(30);
        GestionReservas gr = new GestionReservas(200);
        GestionJugadores gj = new GestionJugadores(150);
        GestionServicios gs = new GestionServicios(40, 20);
        GestionComprobantes gcomp = new GestionComprobantes(200);

        // 2. Empleados de prueba por rol (RF01 / RF10)
        Empleado admin = new Administrador("11111111", "Admin", "General", "admin", "admin123");
        Empleado recep = new Recepcionista("22222222", "Carlos", "Recepción", "recep", "recep123");
        Empleado campo = new EncargadoCancha("33333333", "Marcos", "Canchas", "campo", "campo123");
        gp.registrar(admin);
        gp.registrar(recep);
        gp.registrar(campo);

        // 3. Canchas por tipo (RF02)
        Cancha c5 = new CanchaFutbol5("F5-01", "Cancha Los Galácticos", 60.0);
        Cancha c7 = new CanchaFutbol7("F7-01", "Cancha El Monumental", 90.0);
        Cancha c11 = new CanchaFutbol11("F11-01", "Cancha Estadio Central", 150.0);
        gc.registrar(c5);
        gc.registrar(c7);
        gc.registrar(c11);

        // 4. Jugadores de prueba (RF03)
        gj.registrar("44444444", "Luis", "Guerrero", "987654321", "luis@mail.com");
        gj.registrar("55555555", "Mateo", "Quispe", "912345678", "mateo@mail.com");
        Jugador j1 = gj.buscarPorDni("44444444");
        Jugador j2 = gj.buscarPorDni("55555555");

        // 5. Servicios adicionales y paquete promocional (RF04)
        ServicioAdicional sPelota = new ServicioAdicional("S-01", "Alquiler de Pelota", 15.0, 20);
        ServicioAdicional sChalecos = new ServicioAdicional("S-02", "Juego de Chalecos", 20.0, 15);
        ServicioAdicional sArbitro = new ServicioAdicional("S-03", "Servicio de Árbitro", 50.0, 5);
        gs.registrarServicio(sPelota);
        gs.registrarServicio(sChalecos);
        gs.registrarServicio(sArbitro);

        PaquetePromocional paqNocturno = new PaquetePromocional("P-01", "Juego Nocturno", 70.0, 5);
        paqNocturno.agregarServicio(sPelota);
        paqNocturno.agregarServicio(sChalecos);
        paqNocturno.agregarServicio(sArbitro);
        gs.registrarPaquete(paqNocturno);

        // 6. Reservas iniciales para probar Agenda, Facturación y Reportes
        gr.registrarReserva(j1, c5, "2026-11-20", 17, 2, 10, recep);
        Reserva r1 = gr.obtenerUltimaReserva();
        r1.agregarServicio(sPelota, 1);
        r1.registrarAdelanto();
        r1.iniciarPartido();
        r1.setEncargadoCancha(campo);

        gr.registrarReserva(j2, c7, "2026-11-20", 19, 2, 14, recep);
        Reserva r2 = gr.obtenerUltimaReserva();
        r2.agregarPaquete(paqNocturno);
        r2.registrarAdelanto();

        // 7. Lanzar GUI
        SwingUtilities.invokeLater(() -> {
            new LoginFrame(gp, gc, gr, gj, gs, gcomp).setVisible(true);
        });
    }
}