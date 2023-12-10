package Procesos.Avatares;

import Juego.Juego;
import Procesos.Jugador;
import Procesos.Tablero;

public final class Coche extends Avatar {

    // ATRIBUTOS
    private boolean puedeComprarPropiedades;



    // CONSTRUCTOR

    public Coche(char identificador) {
        super(identificador);
        puedeComprarPropiedades=true;
    }



    // GETTERS

    public boolean getPuedeComprarPropiedades() {
        return puedeComprarPropiedades;
    }



    // SETTERS

    public void setPuedeComprarPropiedades(boolean puedeComprarPropiedades) {
        this.puedeComprarPropiedades = puedeComprarPropiedades;
    }

    // OTROS MÉTODOS

    public void avanzarCasillasAvanzado(Juego juego, Tablero tablero, int avance, Jugador jugadorActual, Jugador banca) {
        if (avance >= 4) {
            avanzarCasillasSimple(juego, tablero, avance, jugadorActual, banca);
            Juego.getConsolaNormal().imprimir("Puedes tirar los dados hasta 3 veces más mientras saques más de un 3.");
            setAuxMovAvanzado(3);
        } else {
            avanzarCasillasSimple(juego, tablero, -avance, jugadorActual, banca);
            setAuxMovAvanzado(-3); // Los números negativos sin el "-" indicarán los turnos restantes sin poder tirar (en el turno actual, por eso se inicializa en -3 y no en -2. al acabar el turno se suma 1, con lo que al acabar el turno en el que se estropea el motor tienes -3+1 = (-)2 turnos más sin tirar)
            Juego.getConsolaNormal().imprimir("Se te ha estropeado el motor y deberás estar dos turnos sin tirar mientras se arregla.");

        }
    }

    public int getTipoMov(){
        return 1;
    }

}
