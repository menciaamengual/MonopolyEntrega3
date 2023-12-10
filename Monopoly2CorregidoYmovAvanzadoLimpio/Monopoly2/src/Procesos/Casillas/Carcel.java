package Procesos.Casillas;

import Juego.Juego;
import Procesos.Jugador;

public final class Carcel extends Especial {

    public Carcel(int posicion, String nombre) {
        super(posicion, nombre);
    }

    public void accionCasilla(Jugador jugador){
        if (!jugador.getAvatar().inCarcel()) Juego.getConsolaNormal().imprimir("Has caído en la cárcel, pero no pasa nada, solo es de visita...");
    }
}

