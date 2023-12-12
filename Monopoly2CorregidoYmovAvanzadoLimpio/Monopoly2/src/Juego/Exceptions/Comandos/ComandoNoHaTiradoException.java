package Juego.Exceptions.Comandos;

public class ComandoNoHaTiradoException extends ComandoException {
    public ComandoNoHaTiradoException() {
        super("Aun no has tirado los dados. Tiralos para poder comprar propiedades");
    }
}
