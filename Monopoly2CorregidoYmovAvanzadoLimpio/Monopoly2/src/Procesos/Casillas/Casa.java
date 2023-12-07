package Procesos.Casillas;

import Procesos.Tablero;

public final class Casa extends Edificio {
    /**
     * @param precioC precio de la casilla sobre la que se construye
     */
    public Casa(int precioC) {
        super();
        setPrecio((int)(precioC*0.6));
        setIdentificador("Casa "+ Tablero.getCcasa());
    }

}
