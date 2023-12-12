package Juego.Exceptions.Lectura;

public class LeerIncorrectoException extends LeerException {

    public LeerIncorrectoException() {
        super("No se reconoce la acción... Introduce 'ayuda' para ver tus opciones.");
    }
}
