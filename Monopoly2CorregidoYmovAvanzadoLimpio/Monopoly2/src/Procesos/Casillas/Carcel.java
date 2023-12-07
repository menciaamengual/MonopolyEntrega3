package Procesos.Casillas;

import Procesos.Jugador;

public final class Carcel extends Especial {

    public Carcel(int posicion, String nombre) {
        super(posicion, nombre);
    }

    public void accionCasilla(Jugador jugador){
        if (!jugador.inCarcel()) System.out.println("Has caído en la cárcel, pero no pasa nada, solo es de visita...");
    }
}

