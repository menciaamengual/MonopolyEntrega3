package Procesos.Casillas;

import Juego.Juego;
import Procesos.Dado;
import Procesos.Jugador;
import Procesos.Avatares.*;

import java.util.ArrayList;

public final class VasALaCarcel extends Accion {

    public VasALaCarcel(int posicion, String nombre) {
        super(posicion, nombre);
    }


    public void accionCasilla(Jugador jugador, ArrayList<Casilla> casillas, Dado dado) {
        jugador.setVecesEnLaCarcel(jugador.getVecesEnLaCarcel() + 1);
        Juego.getConsolaNormal().imprimir("Vaya! Vas a la cárcel...");
        jugador.getAvatar().enviarCarcel(casillas);
        dado.tirarDados(1, 2);//Ponemos valores arbitrarios (pero distintos), así NUNCA entras en la cárcel con dobles y se acaba el turno
    }
}
