package Procesos.Avatares;

import Procesos.Jugador;
import Procesos.Casillas.*;
import Procesos.Avatares.*;
import java.util.ArrayList;
import Juego.Juego;
import Procesos.Tablero;


public abstract class Avatar {

    // ATRIBUTOS

    private char identificador; // La letra que identifica al jugador
    private int posicion;
    private int turnosCarcel; //informa de la situación del jugador respecto de la cárcel:
    // 0: No está en la cárcel / está en la casilla de cárcel pero si tira dados mueve
    // 1: Está en la cárcel, y no tiene oportunidad para salir sacando dobles
    // 2: Está en la cárcel, y tiene UNA oportunidad para tirar dados, sacar dobles y salir
    // 3: Está en la cárcel, y tiene DOS oportunidades para tirar dados, sacar dobles y salir
    // 4: Está en la cárcel, y tiene TRES oportunidades para tirar dados, sacar dobles y salir
    private Jugador jugador; // El jugador que lleva el avatar
    private boolean movAvanzadoActivado;
    private int auxMovAvanzado;



    // CONSTRUCTORES

    public Avatar(char identificador){
        this.identificador = identificador;
        this.movAvanzadoActivado = false;
        this.turnosCarcel = 0;
        this.posicion = 0;
        this.auxMovAvanzado = 999;
        // el jugador se mete después de inicializar el avatar y pasárselo como argumento al constructor de jugador.
    }



    // GETTERS

    public Jugador getJugador() {
        return jugador;
    }

    public int getAuxMovAvanzado() {
        return auxMovAvanzado;
    }

    public int getPosicion() {
        return posicion;
    }

    public int getTurnosCarcel() {
        return turnosCarcel;
    }

    public char getIdentificador() {
        return identificador;
    }

    /**
     * @param casillas
     * @return
     */
    public Casilla getCasilla(ArrayList<Casilla> casillas) {
        return casillas.get(posicion);
    }

    public boolean getMovAvanzadoActivado() {
        return movAvanzadoActivado;
    }



    // SETTERS

    public void setTurnosCarcel(int turnosCarcel) {
        this.turnosCarcel = turnosCarcel;
    }

    public void setAuxMovAvanzado(int auxMovAvanzado) {
        this.auxMovAvanzado = auxMovAvanzado;
    }

    public void setMovAvanzadoActivado(boolean movAvanzadoActivado){
        this.movAvanzadoActivado = movAvanzadoActivado;
    }

    public void setPosicion(int posicion, ArrayList<Casilla> casillas) {
        casillas.get(this.posicion).removeOcupante(jugador);
        this.posicion = posicion;
        casillas.get(posicion).addOcupante(jugador);
        casillas.get(posicion).setVisitas(casillas.get(posicion).getVisitas()+1);
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }


    // MÉTODOS PÚBLICOS

    public Boolean inCarcel() {
        return this.turnosCarcel != 0;
    }



    // UTILIDAD
    public void enviarCarcel(ArrayList<Casilla> casillas) {
        turnosCarcel = 4;
        setPosicion(10, casillas);
    }

    public void avanzarCasillasSimple(Juego juego, Tablero tablero, int avance, Jugador jugadorActual, Jugador banca) {
        if (avance >= 0) { // Funcionamiento normal (realmente podríamos poner "if getposicion+avance >0" pero así queda más legible)
            if ((jugadorActual.getAvatar().getPosicion() + avance) > 39) {
                juego.addVuelta(jugadorActual);
                Juego.getConsolaNormal().imprimir("Pasas por la casilla de salida y cobras " + juego.getPSalida() + "$");
                jugadorActual.setDinero(jugadorActual.getDinero() + juego.getPSalida());
                jugadorActual.getAvatar().setPosicion((jugadorActual.getAvatar().getPosicion() + avance) - 40, tablero.getCasillas());
            } else {
                jugadorActual.getAvatar().setPosicion(jugadorActual.getAvatar().getPosicion() + avance, tablero.getCasillas());
            }
        } else { // Avance negativo
            if ((jugadorActual.getAvatar().getPosicion() + avance) <= 0) { // Si se pasa por la casilla de salida
                jugadorActual.setVueltas(jugadorActual.getVueltas() - 1); // Se resta una vuelta
                Juego.getConsolaNormal().imprimir("Pasas por la casilla de salida en sentido contrario y pagas " + juego.getPSalida() + "$");
                jugadorActual.pagar(juego.getPSalida(), banca);
                jugadorActual.getAvatar().setPosicion((jugadorActual.getAvatar().getPosicion() + avance) + 40, tablero.getCasillas());
            } else {
                jugadorActual.getAvatar().setPosicion(jugadorActual.getAvatar().getPosicion() + avance, tablero.getCasillas());
            }
        }
    }
    public abstract void avanzarCasillasAvanzado(Juego juego, Tablero tablero, int avance, Jugador jugadorActual, Jugador banca);
    public abstract int getTipoMov();
    // Podríamos haber optado por poner if(avatar instanceof Coche), por ejemplo, pero nos quedamos con getTipoMov porque cambiar la implementación anterior era más sencillo.
}

