package Procesos.Casillas;

import Procesos.Jugador;

public final class Impuesto extends Casilla {
    private final int valor;
    public Impuesto(int posicion, String nombre, int valor) {
        super(posicion, nombre);
        this.valor = valor;
    }

    public void accionCasilla(Jugador jugador){
        System.out.println("Has caído en la casilla impuestos, debes pagar " + valor + "$");
        if (jugador.pagar(valor))
            ParkingGratuito.addDineroAcumulado(valor);
        jugador.setPagoTasasEImpuestos(jugador.getPagoTasasEImpuestos() + valor);
    }
}
