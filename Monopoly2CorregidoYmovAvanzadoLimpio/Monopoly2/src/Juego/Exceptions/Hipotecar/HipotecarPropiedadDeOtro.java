package Juego.Exceptions.Hipotecar;

public class HipotecarPropiedadDeOtro extends HipotecarException {

    public HipotecarPropiedadDeOtro() {
        super("No puedes hipotecar una casilla que no es tuya");
    }
}
