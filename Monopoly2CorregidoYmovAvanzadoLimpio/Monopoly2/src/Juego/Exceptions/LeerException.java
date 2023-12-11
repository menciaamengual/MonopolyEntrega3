package Juego.Exceptions;

import Juego.Juego;

public class LeerException extends JuegoException{

    public LeerException(String mensaje) {
        super(mensaje);
        Juego.getConsolaNormal().imprimir("\u001B[31mDebes introducir un número entero...\u001B[0m");
    }
}
