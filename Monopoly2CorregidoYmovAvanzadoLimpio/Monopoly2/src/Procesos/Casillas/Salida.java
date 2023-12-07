package Procesos.Casillas;

public final class Salida extends Especial {

    public Salida(int posicion, String nombre) {
        super(posicion, nombre);
    }

    public void accionCasilla(){
        System.out.println("Has caído en la salida!");
    }
}
