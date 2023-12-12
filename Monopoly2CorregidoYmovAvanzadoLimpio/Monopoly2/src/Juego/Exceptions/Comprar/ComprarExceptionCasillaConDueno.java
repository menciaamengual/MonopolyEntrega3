package Juego.Exceptions.Comprar;

public class ComprarExceptionCasillaConDueno extends ComprarException {
    public ComprarExceptionCasillaConDueno() {
        super("Esta casilla ya tiene dueño");
    }
}
