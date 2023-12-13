package Procesos.Avatares;

import Juego.Juego;
import Procesos.Jugador;
import Procesos.Tablero;

public final class Pelota extends Avatar{ // Clase final porque no tiene subclases

    // indicadorParadas == 1: el jugador aún no ha llegado a la casilla final
    // indicadorParadas == 0: el jugador ya está en la casilla final
    // indicadorParadas == 999: Indica que el jugador puede tirar al inicio de su turno. Al acabar el turno de un jugador, se settea su auxMovAvanzado a 999.

    // CONSTRUCTOR

    public Pelota(char identificador) {
        super(identificador);
    }

    // OTROS MÉTODOS

    public void avanzarCasillasAvanzado(Juego juego, Tablero tablero, int avance, Jugador jugadorActual, Jugador banca){
        setAuxMovAvanzado(1); // Indica que el turno aún está en curso
        if (avance > 4) {
            avanzarCasillasSimple(juego, tablero, 4, jugadorActual, banca);
            int aux = jugadorActual.getAvatar().getPosicion(); // guardamos la posición tras avanzar 4 casillas para comprobar paridades
            for (int i = 5; i <= avance; i++) {
                avanzarCasillasSimple(juego, tablero, 1, jugadorActual, banca); // avanzamos una casilla
                if (jugadorActual.getAvatar().getPosicion() == 30) { // Si cae en la casilla de ir a la cárcel, se acaba la función. Recordemos que accionCasilla y menuAccion de la última parada/casilla se ejecutan fuera de aquí.
                    return;
                }
                if (i == avance) {
                    setAuxMovAvanzado(0);
                    Juego.getConsolaNormal().imprimir("Ya has pasado por todas tus paradas.");
                    return;
                }
                if ((jugadorActual.getAvatar().getPosicion() - aux) % 2 != 0) { // Si es impar, se para en la casilla, se ejecuta la accion correspondiente y el jugador puede interactuar
                    Juego.getConsolaNormal().imprimir("¡Momento de pararse! Introduce \"acabar parada\" para avanzar a la siguiente casilla");
                    tablero.imprimirTablero();
                    juego.accionCasilla();
                    if (jugadorActual.getAvatar().inCarcel()) {
                        break;
                    }
                    juego.menuAccion(true);
                }
                // Si es par, no se hace nada.
            }
        } else {
            int aux = jugadorActual.getAvatar().getPosicion(); // guardamos la posición para comprobar paridades
            for (int i = avance; i > 0; i--) {
                avanzarCasillasSimple(juego, tablero, -1, jugadorActual, banca);
                if (jugadorActual.getAvatar().getPosicion() == 30) { // Si cae en la casilla de ir a la cárcel, se acaba la función. Recordemos que accionCasilla y menuAccion de la última parada/casilla se ejecutan fuera de aquí.
                    return;
                }
                if (i == 1) { // Si es la última parada
                    setAuxMovAvanzado(0);
                    Juego.getConsolaNormal().imprimir("Ya has pasado por todas tus paradas.");
                    return;
                }
                if ((jugadorActual.getAvatar().getPosicion() - aux) % 2 != 0) { // Si es impar, se para en la casilla, se ejecuta la accion correspondiente y el jugador puede interactuar
                    Juego.getConsolaNormal().imprimir("¡Momentito de pararse! Introduce \"acabar parada\" para ir a la próxima casilla");
                    tablero.imprimirTablero();
                    juego.accionCasilla();
                    if (jugadorActual.getAvatar().inCarcel()) {
                        break;
                    }
                    juego.menuAccion(true);
                }
                // Si es par, no se hace nada.
            }
        }
    }

    public int getTipoMov(){
        return 0;
    }
}
