package Juego.Exceptions.Comandos;

public class ComandoNoMasComprasException extends ComandoException {

    public ComandoNoMasComprasException() {
        super("No puedes hacer más compras en este turno...");
    }
}
