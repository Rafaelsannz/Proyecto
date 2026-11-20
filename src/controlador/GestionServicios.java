package controlador;

import modelo.PaquetePromocional;
import modelo.ServicioAdicional;

public class GestionServicios {
    private ServicioAdicional[] listaServicios;
    private int cantServicios;

    private PaquetePromocional[] listaPaquetes;
    private int cantPaquetes;

    public GestionServicios(int maxServicios, int maxPaquetes) {
        this.listaServicios = new ServicioAdicional[maxServicios];
        this.cantServicios = 0;

        this.listaPaquetes = new PaquetePromocional[maxPaquetes];
        this.cantPaquetes = 0;
    }

    // ==========================================
    // OPERACIONES DE SERVICIOS ADICIONALES
    // ==========================================

    public boolean registrarServicio(ServicioAdicional s) {
        if (cantServicios >= listaServicios.length) return false;
        if (buscarServicio(s.getCodigo()) != null) return false; // Código único

        listaServicios[cantServicios++] = s;
        return true;
    }

    public ServicioAdicional buscarServicio(String codigo) {
        for (int i = 0; i < cantServicios; i++) {
            if (listaServicios[i].getCodigo().equalsIgnoreCase(codigo)) {
                return listaServicios[i];
            }
        }
        return null;
    }

    public boolean eliminarServicio(String codigo) {
        int pos = -1;
        for (int i = 0; i < cantServicios; i++) {
            if (listaServicios[i].getCodigo().equalsIgnoreCase(codigo)) {
                pos = i;
                break;
            }
        }
        if (pos == -1) return false;

        // Desplazamiento hacia la izquierda
        for (int i = pos; i < cantServicios - 1; i++) {
            listaServicios[i] = listaServicios[i + 1];
        }
        listaServicios[cantServicios - 1] = null;
        cantServicios--;
        return true;
    }

    // ==========================================
    // OPERACIONES DE PAQUETES PROMOCIONALES
    // ==========================================

    public boolean registrarPaquete(PaquetePromocional p) {
        if (cantPaquetes >= listaPaquetes.length) return false;
        if (buscarPaquete(p.getCodigo()) != null) return false; // Código único

        listaPaquetes[cantPaquetes++] = p;
        return true;
    }

    public PaquetePromocional buscarPaquete(String codigo) {
        for (int i = 0; i < cantPaquetes; i++) {
            if (listaPaquetes[i].getCodigo().equalsIgnoreCase(codigo)) {
                return listaPaquetes[i];
            }
        }
        return null;
    }

    public boolean eliminarPaquete(String codigo) {
        int pos = -1;
        for (int i = 0; i < cantPaquetes; i++) {
            if (listaPaquetes[i].getCodigo().equalsIgnoreCase(codigo)) {
                pos = i;
                break;
            }
        }
        if (pos == -1) return false;

        // Desplazamiento hacia la izquierda
        for (int i = pos; i < cantPaquetes - 1; i++) {
            listaPaquetes[i] = listaPaquetes[i + 1];
        }
        listaPaquetes[cantPaquetes - 1] = null;
        cantPaquetes--;
        return true;
    }

    // Getters para recorrer en tablas o listados de la GUI
    public int getCantServicios() { return cantServicios; }
    public int getCantPaquetes() { return cantPaquetes; }

    public ServicioAdicional obtenerServicio(int index) {
        if (index >= 0 && index < cantServicios) return listaServicios[index];
        return null;
    }

    public PaquetePromocional obtenerPaquete(int index) {
        if (index >= 0 && index < cantPaquetes) return listaPaquetes[index];
        return null;
    }
}
