package Procesos.Casillas;

import Juego.Juego;

public final class Salida extends Especial {

    public Salida(int posicion, String nombre) {
        super(posicion, nombre);
    }

    public void accionCasilla(){
        Juego.getConsolaNormal().imprimir("Has caído en la salida!");
    }
}
