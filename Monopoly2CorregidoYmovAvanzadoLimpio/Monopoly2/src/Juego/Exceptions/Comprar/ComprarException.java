package Juego.Exceptions.Comprar;

import Juego.Exceptions.JuegoException;
import Juego.Juego;

public class ComprarException extends JuegoException {
    public ComprarException(String mensaje) {
        super(mensaje);
        Juego.getConsolaNormal().imprimir("Error al comprar:");
    }
}
