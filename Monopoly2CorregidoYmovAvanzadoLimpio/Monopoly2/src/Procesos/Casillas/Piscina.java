package Procesos.Casillas;

import Procesos.Tablero;

public final class Piscina extends Edificio {

    public Piscina(int precioC){
        super();
        setPrecio((int)(precioC*0.6));
        setIdentificador("Piscina "+ Tablero.getCpiscina());
    }
}
