package Procesos.Casillas;


import Procesos.Casillas.Casilla;
import Procesos.Jugador;
import Procesos.Tablero;

public abstract class Edificio {
    private String identificador;
    private int precio;
    private Solar casilla;
    private Jugador propietario;

    /**
     *
     */
    public Edificio() { //Puede que se pueda meter ya aquí la casilla
        casilla = null;
    }

    public void setCasilla(Solar casilla) {
        this.casilla = casilla;
        propietario = casilla.getPropietario();
    }

    public Solar getCasilla() {
        return casilla;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public void setPropietario(Jugador propietario) {
        this.propietario = propietario;
    }

    public String toString(){
        return identificador;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getPrecio() {
        return precio;
    }
}

