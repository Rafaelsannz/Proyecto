package controlador;

import modelo.Cancha;

public class GestionCanchas {
    private Cancha[] lista;
    private int cantidad;

    public GestionCanchas(int capacidad) {
        this.lista = new Cancha[capacidad];
        this.cantidad = 0;
    }

    public int getCantidad() { return cantidad; }

    public Cancha obtener(int index) {
        if (index >= 0 && index < cantidad) return lista[index];
        return null;
    }

    // 1. REGISTRAR
    public boolean registrar(Cancha nueva) {
        if (nueva == null || cantidad >= lista.length) return false;
        if (buscarPosicion(nueva.getCodigo()) != -1) return false;

        lista[cantidad++] = nueva;
        return true;
    }

    // 2. MODIFICAR (RF02)
    public boolean modificar(String codigo, String nombre, double tarifaBaseHora, String estado) {
        Cancha c = buscarPorCodigo(codigo);
        if (c == null || tarifaBaseHora <= 0) return false;

        c.setNombre(nombre);
        c.setTarifaBaseHora(tarifaBaseHora);
        c.setEstado(estado);
        return true;
    }

    // 3. BUSCAR POSICIÓN POR CÓDIGO
    public int buscarPosicion(String codigo) {
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getCodigo().equalsIgnoreCase(codigo)) {
                return i;
            }
        }
        return -1;
    }

    // 4. BUSCAR OBJETO CANCHA
    public Cancha buscarPorCodigo(String codigo) {
        int pos = buscarPosicion(codigo);
        if (pos != -1) return lista[pos];
        return null;
    }

    // 5. ELIMINAR CON DESPLAZAMIENTO HACIA LA IZQUIERDA
    public boolean eliminar(String codigo) {
        int pos = buscarPosicion(codigo);
        if (pos == -1) return false;

        for (int i = pos; i < cantidad - 1; i++) {
            lista[i] = lista[i + 1];
        }
        lista[cantidad - 1] = null;
        cantidad--;
        return true;
    }

    // 6. LISTAR TODAS LAS CANCHAS
    public void listarCanchas() {
        for (int i = 0; i < cantidad; i++) {
            lista[i].verInfo();
        }
    }
}