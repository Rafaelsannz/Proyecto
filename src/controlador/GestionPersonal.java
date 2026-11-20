package controlador;

import modelo.Empleado;

public class GestionPersonal {
    private Empleado[] lista;
    private int cantidad;

    public GestionPersonal(int capacidad) {
        this.lista = new Empleado[capacidad];
        this.cantidad = 0;
    }

    public int getCantidad() { return cantidad; }

    public Empleado obtener(int index) {
        if (index >= 0 && index < cantidad) return lista[index];
        return null;
    }

    // 1. REGISTRAR (DNI único y usuario único - RF01)
    public boolean registrar(Empleado e) {
        if (e == null || cantidad >= lista.length) return false;
        if (buscarPorDni(e.getDni()) != null) return false;
        if (buscarPorUsuario(e.getUsuario()) != null) return false;

        lista[cantidad++] = e;
        return true;
    }

    // 2. MODIFICAR (RF01)
    public boolean modificar(String dni, String nombres, String apellidos, String password) {
        Empleado e = buscarPorDni(dni);
        if (e == null) return false;

        e.setNombres(nombres);
        e.setApellidos(apellidos);
        if (password != null && !password.trim().isEmpty()) {
            e.setPassword(password);
        }
        return true;
    }

    // 3. BUSCAR POR DNI
    public Empleado buscarPorDni(String dni) {
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getDni().equalsIgnoreCase(dni)) return lista[i];
        }
        return null;
    }

    // 4. BUSCAR POR USUARIO (Para Login)
    public Empleado buscarPorUsuario(String user) {
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getUsuario().equals(user)) return lista[i];
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

    // 6. LOGIN AUTENTICACIÓN (RF10)
    public Empleado autenticar(String user, String pass) {
        Empleado e = buscarPorUsuario(user);
        if (e != null && e.autenticar(user, pass)) {
            return e;
        }
        return null;
    }
}