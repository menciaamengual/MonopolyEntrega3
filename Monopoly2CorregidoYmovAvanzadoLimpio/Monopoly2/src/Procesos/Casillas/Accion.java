package Procesos.Casillas;

import Juego.Juego;

public abstract class Accion extends Casilla{
    public Accion(int posicion, String nombre) {
        super(posicion, nombre);
    }
    public void accionCasilla(){
        Juego.getConsolaNormal().imprimir("Has caído en "+getNombre()+"!");
    }
}
