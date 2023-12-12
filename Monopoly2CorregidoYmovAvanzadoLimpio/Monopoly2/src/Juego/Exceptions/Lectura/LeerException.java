package Juego.Exceptions.Lectura;

import Juego.Exceptions.JuegoException;
import Juego.Juego;

public class LeerException extends JuegoException {
    public LeerException(String mensaje) {
        super(mensaje);
    }
}

