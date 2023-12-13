package Juego.Exceptions.Comprar;

public class ComprarExceptionCasillaDistinta extends ComprarException {
    public ComprarExceptionCasillaDistinta() {
        super("No puedes comprar una casilla sobre la que no estás");
    }
}
