package Procesos.Casillas;

import Procesos.Tablero;

public final class Pista extends Edificio {

    public Pista(int precioC){
        super();
        setPrecio((int)(precioC*0.6));
        setIdentificador("Pista "+ Tablero.getCdeporte());
    }
}
