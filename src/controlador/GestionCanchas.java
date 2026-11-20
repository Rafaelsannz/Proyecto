package controlador;

import modelo.Cancha;

public class GestionCanchas {
    private Cancha[] lista;
    private int cantidad;

    public GestionCanchas(int capacidad) {
        this.lista = new Cancha[capacidad]; // Capacidad fija en memoria
        this.cantidad = 0;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Cancha obtener(int index) {
        if (index >= 0 && index < cantidad) {
            return lista[index];
        }
        return null;
    }

    // 1. REGISTRAR (Validando que no se repita el código ni se desborde el arreglo)
    public boolean registrar(Cancha nueva) {
        if (cantidad >= lista.length) return false;
        if (buscarPosicion(nueva.getCodigo()) != -1) return false; // Código ya existe

        lista[cantidad++] = nueva;
        return true;
    }

    // 2. BUSCAR POR CÓDIGO (Retorna el índice)
    public int buscarPosicion(String codigo) {
        for (int i = 0; i < cantidad; i++) {
            if (lista[i].getCodigo().equalsIgnoreCase(codigo)) {
                return i;
            }
        }
        return -1;
    }

    // 3. BUSCAR OBJETO CANCHA
    public Cancha buscarPorCodigo(String codigo) {
        int pos = buscarPosicion(codigo);
        if (pos != -1) {
            return lista[pos];
        }
        return null;
    }

    // 4. ELIMINAR CON DESPLAZAMIENTO HACIA LA IZQUIERDA
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

    // 5. LISTAR TODAS LAS CANCHAS (Aprovechando polimorfismo)
    public void listarCanchas() {
        for (int i = 0; i < cantidad; i++) {
            lista[i].verInfo();
        }
    }
}
