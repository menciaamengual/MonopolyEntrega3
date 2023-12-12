package Juego.Exceptions;

public class JuegoException extends Exception{
    public JuegoException(String mensaje) {
        super("\u001B[31m"+mensaje+"\u001B[0m");
    }
}
