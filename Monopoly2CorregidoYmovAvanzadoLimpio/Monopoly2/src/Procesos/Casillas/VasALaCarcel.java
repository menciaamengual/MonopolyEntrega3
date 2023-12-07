package Procesos.Casillas;

import Procesos.Jugador;

import java.util.ArrayList;

public final class VasALaCarcel extends Accion {

    public VasALaCarcel(int posicion, String nombre) {
        super(posicion, nombre);
    }


    public void accionCasilla(Jugador jugador, ArrayList<Casilla> casillas) {
        System.out.println("Vaya! Vas a la cárcel...");
        jugador.enviarCarcel(casillas);
    }
}
