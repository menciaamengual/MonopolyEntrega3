package Juego.Exceptions.Comandos;

import Juego.Exceptions.JuegoException;

public class ComandoBancarrotaException extends ComandoException{
    public ComandoBancarrotaException() {
        super("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
    }
}
