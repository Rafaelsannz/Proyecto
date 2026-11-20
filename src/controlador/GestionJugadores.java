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

    // 1. REGISTRAR (DNI único - RF03)
    public boolean registrar(String dni, String nombres, String apellidos, String tel, String correo) {
        if (cantidad >= lista.length) return false;
        if (buscarPorDni(dni) != null) return false;

        lista[cantidad++] = new JugadorRegular(dni, nombres, apellidos, tel, correo, 0);
        return true;
    }

    // 2. MODIFICAR (RF03)
    public boolean modificar(String dni, String nombres, String apellidos, String tel, String correo) {
        Jugador j = buscarPorDni(dni);
        if (j == null) return false;

        j.setNombres(nombres);
        j.setApellidos(apellidos);
        j.setTelefono(tel);
        j.setCorreo(correo);
        return true;
    }

    // 3. BUSCAR POR DNI
    public Jugador buscarPorDni(String dni) {
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getDni().equalsIgnoreCase(dni)) return lista[i];
        }
        return null;
    }

    // 4. ACTUALIZAR RESERVA Y ASCENDER A FRECUENTE (RF03)
    public Jugador registrarCierreReserva(String dni) {
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getDni().equalsIgnoreCase(dni)) {
                lista[i].incrementarReservaFinalizada();

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
                return lista[i];
            }
        }
        return null;
    }

    // 5. ELIMINAR CON DESPLAZAMIENTO HACIA LA IZQUIERDA
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

    // 6. LISTAR TODOS
    public void listarJugadores() {
        for (int i = 0; i < cantidad; i++) {
            lista[i].verInfo();
        }
    }
}