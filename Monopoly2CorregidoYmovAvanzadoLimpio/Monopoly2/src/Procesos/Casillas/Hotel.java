package Procesos.Casillas;

import Procesos.Tablero;

public final class Hotel extends Edificio {

    public Hotel(int precioC){
        super();
        setPrecio((int)(precioC*0.6));
        setIdentificador("Casa "+ Tablero.getChotel());
    }
}
