package Juego.Exceptions.Hipotecar;

public class HipotecarCasillanNoHipotecableException extends HipotecarException {

    public HipotecarCasillanNoHipotecableException() {
        super("Esta casilla no es hipotecable.");
    }
}
