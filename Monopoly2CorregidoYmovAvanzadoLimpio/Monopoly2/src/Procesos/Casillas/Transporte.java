package Procesos.Casillas;
import Procesos.Jugador;

public class Transporte extends Propiedad{
    private static int factorTransporte;

    public Transporte(int posicion, String nombre, Jugador propietario, int pSalida) {
        super(posicion, nombre, propietario);
        Transporte.factorTransporte = pSalida/4;
        setPrecio(pSalida);
    }

    public int calcularAlquiler(){
        return getPropietario().getNTrans() * factorTransporte;
    }

}