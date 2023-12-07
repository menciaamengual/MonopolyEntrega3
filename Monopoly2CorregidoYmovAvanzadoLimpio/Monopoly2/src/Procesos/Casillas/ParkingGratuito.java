package Procesos.Casillas;

import Procesos.Jugador;

public final class ParkingGratuito extends Accion {
    private static int dineroAcumulado;
    public ParkingGratuito(int posicion, String nombre) {
        super(posicion, nombre);
    }

    public static void addDineroAcumulado(int valor) {
        dineroAcumulado+=valor;
    }

    public void accionCasilla(Jugador jugador){
        System.out.println("Has caído en el Parking! cobras " + dineroAcumulado + "$.");
        jugador.addDinero(dineroAcumulado);
        dineroAcumulado = 0;
    }


    //Dinero acumulado
    public void setDineroAcumulado(int dineroAcumulado) {
        ParkingGratuito.dineroAcumulado = dineroAcumulado;
    }
    public int getDineroAcumulado() {
        return dineroAcumulado;
    }

}
