package Juego.Exceptions;

import Juego.Exceptions.Hipotecar.HipotecarException;
import Juego.Juego;

public class JuegoException extends Exception{
    public JuegoException(String mensaje) {
        super("\u001B[31m"+mensaje+"\u001B[0m");
        Juego.getConsolaNormal().imprimirError("\u001B[31m" + mensaje + "\u001B[0m");
    }
}
