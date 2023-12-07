package Procesos.Casillas;
import Procesos.Jugador;

public class Transporte extends Propiedad{
    private static int factorTransporte;

    public Transporte(int posicion, String nombre, Jugador propietario,int factorTransporte) {
        super(posicion, nombre, propietario);
        Transporte.factorTransporte = factorTransporte;
    }

    public int calcularAlquiler(){
        return getPropietario().getNTrans() * factorTransporte;
    }

}