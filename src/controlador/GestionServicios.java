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
        if (s == null || cantServicios >= listaServicios.length) return false;
        if (buscarServicio(s.getCodigo()) != null) return false;

        listaServicios[cantServicios++] = s;
        return true;
    }

    public boolean modificarServicio(String codigo, String nombre, double precioUnitario, int stockDisponible) {
        ServicioAdicional s = buscarServicio(codigo);
        if (s == null || precioUnitario < 0 || stockDisponible < 0) return false;

        s.setNombre(nombre);
        s.setPrecioUnitario(precioUnitario);
        s.setStockDisponible(stockDisponible);
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
        if (p == null || cantPaquetes >= listaPaquetes.length) return false;
        if (buscarPaquete(p.getCodigo()) != null) return false;

        listaPaquetes[cantPaquetes++] = p;
        return true;
    }

    public boolean modificarPaquete(String codigo, String nombre, double precioPromocional) {
        PaquetePromocional p = buscarPaquete(codigo);
        if (p == null || precioPromocional < 0) return false;

        p.setNombre(nombre);
        p.setPrecioPromocional(precioPromocional);
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

        for (int i = pos; i < cantPaquetes - 1; i++) {
            listaPaquetes[i] = listaPaquetes[i + 1];
        }
        listaPaquetes[cantPaquetes - 1] = null;
        cantPaquetes--;
        return true;
    }

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