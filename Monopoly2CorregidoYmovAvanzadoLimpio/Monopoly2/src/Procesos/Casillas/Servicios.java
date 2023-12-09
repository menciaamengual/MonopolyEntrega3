package Procesos.Casillas;
import  Juego.Juego;

import Procesos.Dado;
import Procesos.Jugador;

public class Servicios extends Propiedad {
    private static int alquilerBase;
    public Servicios(int posicion, String nombre, Jugador propietario, int pSalida) {
        super(posicion, nombre, propietario);
        Servicios.alquilerBase = pSalida/200;
        setPrecio((int) (pSalida*0.75));
    }
    public int calcularAlquiler(Dado dado){
        if (getPropietario().getNServicios() == 1) return dado.getSuma()*alquilerBase*4;
                else if (getPropietario().getNServicios() == 2) return dado.getSuma()*alquilerBase*10;
                return 0;
    }

    public void accionCasilla(Jugador jugador, Dado dado){
        if (!getPropietario().equals(jugador) && !getPropietario().isBanca() && !getHipotecado()) {
            jugador.pagar(calcularAlquiler(dado), getPropietario());
            setRentabilidad(getRentabilidad() + calcularAlquiler(dado)); //edificios??
            jugador.setPagoDeAlquileres(jugador.getPagoDeAlquileres() + calcularAlquiler(dado));
            getPropietario().setCobroDeAlquileres(getPropietario().getCobroDeAlquileres() + calcularAlquiler(dado));
            Juego.getConsolaNormal().imprimir("Pagas " + calcularAlquiler(dado) + "$ por caer en " + getNombre());
        } else if (getHipotecado()) {
            Juego.getConsolaNormal().imprimir("Casilla hipotecada... No pagas alquiler :)");
        } else if (getPropietario().isBanca()) {
            Juego.getConsolaNormal().imprimir("Esta propiedad aun no tiene dueño, la puedes comprar.");
        } else if (getPropietario().equals(jugador)) {
            Juego.getConsolaNormal().imprimir("Has caido en una casilla de tu propiedad, disfruta de tu estancia");
        }
    }
}
