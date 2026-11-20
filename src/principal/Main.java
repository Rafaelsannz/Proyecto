package principal;

import controlador.*;
import modelo.*;
import vista.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // 1. Instanciamos los controladores en memoria con capacidades fijas
        GestionPersonal gp = new GestionPersonal(50);
        GestionCanchas gc = new GestionCanchas(30);
        GestionReservas gr = new GestionReservas(200);
        GestionJugadores gj = new GestionJugadores(150);
        GestionServicios gs = new GestionServicios(40, 20);
        GestionComprobantes gcomp = new GestionComprobantes(200);

        // 2. Precargamos usuarios de prueba para evaluar cada rol (RF01/RF10)
        gp.registrar(new Administrador("11111111", "Admin", "General", "admin", "admin123"));
        gp.registrar(new Recepcionista("22222222", "Carlos", "Recepción", "recep", "recep123"));
        gp.registrar(new EncargadoCancha("33333333", "Marcos", "Canchas", "campo", "campo123"));

        // 3. Precargamos canchas de cada tipo con sus tarifas base (RF02)
        gc.registrar(new CanchaFutbol5("F5-01", "Cancha Los Galácticos", 60.0));
        gc.registrar(new CanchaFutbol7("F7-01", "Cancha El Monumental", 90.0));
        gc.registrar(new CanchaFutbol11("F11-01", "Cancha Estadio Central", 150.0));

        // 4. Precargamos un jugador titular inicial (RF03)
        gj.registrar("44444444", "Luis", "Guerrero", "987654321", "luis@mail.com");

        // 5. Lanzamos la GUI en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            new LoginFrame(gp, gc, gr, gj, gs, gcomp).setVisible(true);
        });
    }
}
