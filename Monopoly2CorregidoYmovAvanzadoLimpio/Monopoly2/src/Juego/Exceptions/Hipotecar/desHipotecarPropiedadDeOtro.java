package Juego.Exceptions.Hipotecar;

public class desHipotecarPropiedadDeOtro extends HipotecarException {

    public desHipotecarPropiedadDeOtro() {
        super("No puedes deshipotecar una casilla que no es tuya");
    }
}
