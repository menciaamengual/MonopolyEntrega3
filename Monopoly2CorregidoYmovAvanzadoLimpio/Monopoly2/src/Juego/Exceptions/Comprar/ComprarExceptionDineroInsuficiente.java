package Juego.Exceptions.Comprar;

import Procesos.Jugador;

public class ComprarExceptionDineroInsuficiente extends ComprarException {
    public ComprarExceptionDineroInsuficiente() {
        super("Dinero insuficiente");
    }
}
