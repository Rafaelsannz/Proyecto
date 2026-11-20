package controlador;

import modelo.Jugador;
import modelo.JugadorRegular;
import modelo.JugadorFrecuente;

public class GestionJugadores {
    private Jugador[] lista;
    private int cantidad;

    public GestionJugadores(int capacidad) {
        this.lista = new Jugador[capacidad];
        this.cantidad = 0;
    }

    public int getCantidad() { return cantidad; }

    public Jugador obtener(int index) {
        if (index >= 0 && index < cantidad) return lista[index];
        return null;
    }

    // 1. REGISTRAR (Crea por defecto Regular o Frecuente según sus reservas)
    public boolean registrar(String dni, String nombres, String apellidos, String tel, String correo) {
        if (cantidad >= lista.length) return false;
        if (buscarPorDni(dni) != null) return false; // DNI único

        // Nace como regular con 0 reservas
        lista[cantidad++] = new JugadorRegular(dni, nombres, apellidos, tel, correo, 0);
        return true;
    }

    // 2. BUSCAR POR DNI
    public Jugador buscarPorDni(String dni) {
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getDni().equalsIgnoreCase(dni)) return lista[i];
        }
        return null;
    }

    // 3. ACTUALIZAR RESERVA Y ASCENDER A FRECUENTE (RF03)
    public void registrarCierreReserva(String dni) {
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getDni().equalsIgnoreCase(dni)) {
                lista[i].incrementarReservaFinalizada();

                // Si tiene 5 o más y aún es regular, lo promovemos a Frecuente
                if (lista[i].getReservasFinalizadas() >= 5 && (lista[i] instanceof JugadorRegular)) {
                    Jugador actual = lista[i];
                    lista[i] = new JugadorFrecuente(
                        actual.getDni(),
                        actual.getNombres(),
                        actual.getApellidos(),
                        actual.getTelefono(),
                        actual.getCorreo(),
                        actual.getReservasFinalizadas()
                    );
                }
                break;
            }
        }
    }

    // 4. ELIMINAR CON DESPLAZAMIENTO HACIA LA IZQUIERDA
    public boolean eliminar(String dni) {
        int pos = -1;
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getDni().equalsIgnoreCase(dni)) {
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

    // 5. LISTAR TODOS
    public void listarJugadores() {
        for (int i = 0; i < cantidad; i++) {
            lista[i].verInfo();
        }
    }
}
