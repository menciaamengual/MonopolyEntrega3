package Procesos.Casillas;

import Juego.Exceptions.AlquilerDineroInsufException;
import Juego.Juego;
import Procesos.Jugador;

public final class Impuesto extends Casilla {
    private final int valor;
    public Impuesto(int posicion, String nombre, int valor) {
        super(posicion, nombre);
        this.valor = valor;
    }

    public void accionCasilla(Jugador jugador) throws AlquilerDineroInsufException {
        Juego.getConsolaNormal().imprimir("Has caído en la casilla impuestos, debes pagar " + valor + "$");
        if (jugador.pagar(valor))
            ParkingGratuito.addDineroAcumulado(valor);
        else throw new AlquilerDineroInsufException(null,valor);
        jugador.setPagoTasasEImpuestos(jugador.getPagoTasasEImpuestos() + valor);
    }
}
