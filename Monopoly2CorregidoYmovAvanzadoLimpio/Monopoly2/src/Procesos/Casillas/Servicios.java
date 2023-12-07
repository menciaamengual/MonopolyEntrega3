package Procesos.Casillas;

import Procesos.Dado;
import Procesos.Jugador;

public class Servicios extends Propiedad {
    private static int alquilerBase;
    public Servicios(int posicion, String nombre, Jugador propietario, int pSalida) {
        super(posicion, nombre, propietario);
        Servicios.alquilerBase = pSalida/200;
    }
    public int calcularAlquiler(Dado dado){
        if (getPropietario().getNServicios() == 1) return dado.getSuma()*alquilerBase*4;
                else if (getPropietario().getNServicios() == 2) return dado.getSuma()*alquilerBase*10;
                return 0;
    }
}
