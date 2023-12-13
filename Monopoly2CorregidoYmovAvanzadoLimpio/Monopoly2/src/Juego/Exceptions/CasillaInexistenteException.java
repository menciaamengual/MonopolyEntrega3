package Juego.Exceptions;

public class CasillaInexistenteException extends JuegoException {

    public CasillaInexistenteException() {
        super("Esta casilla no existe...");
    }
}
