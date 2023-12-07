package Procesos.Casillas;

import Procesos.Carta;
import Procesos.Jugador;

import java.util.Scanner;

public abstract class CasillasCarta extends Accion {

    public CasillasCarta(int posicion, String nombre) {
        super(posicion, nombre);
    }

    public void accionCasilla(Jugador jugador){
        Carta carta = new Carta(jugador.getPosicion());
        carta.barajar();
        int indice;
        String entradaString;
        do {
            System.out.println("Elige una carta (introduciendo un numero del 1 al 6)");
            Scanner entrada = new Scanner(System.in);
            entradaString = entrada.nextLine();
        } while (!entradaString.equals("1") && !entradaString.equals("2") && !entradaString.equals("3") && !entradaString.equals("4") && !entradaString.equals("5") && !entradaString.equals("6"));

        indice = Integer.parseInt(entradaString);

        int numCarta = (carta.getcartas().get(indice - 1));
        if (carta.getTipo() == 0)
            accionSuerte(numCarta);
        if (carta.getTipo() == 1)
            accionCajaC(numCarta); //todo Mirar si cunde meter esto dentro de cada clase, o todo acción casilla dentro de cada clase...
    }
}
