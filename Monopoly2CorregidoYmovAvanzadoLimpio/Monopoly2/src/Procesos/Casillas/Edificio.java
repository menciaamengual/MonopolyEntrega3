package Procesos.Casillas;


import Procesos.Casillas.Casilla;
import Procesos.Jugador;
import Procesos.Tablero;

public class Edificio {
    int tipo; //0-3
    String identificador;
    Casilla casilla;
    int precio;
    Jugador propietario;

    /**
     *
     * @param tipo
     * @param precioC precio de la casilla sobre la que se construye
     */
    public Edificio(int tipo,int precioC) {
        this.tipo = tipo;
        casilla = null;
        switch(tipo){
            case 0:
                precio = (int) (precioC*0.6);
                identificador = "Casa "+ Tablero.getCcasa();
                break;
            case 1:
                precio = (int) (precioC*0.6);
                identificador = "Hotel "+Tablero.getChotel();
                break;
            case 2:
                precio = (int) (precioC*0.4);
                identificador = "Piscina "+Tablero.getCpiscina();
                break;
            case 3:
                precio = (int) (precioC*1.25);
                identificador = "Pista "+Tablero.getCdeporte();
                break;
        }

    }

    public void setCasilla(Casilla casilla) {
        this.casilla = casilla;
        propietario = casilla.getPropietario();
    }

    public Casilla getCasilla() {
        return casilla;
    }

    public String getIdentificador() {
        return identificador;
    }

    public int getTipo() {
        return tipo;
    }

    public String toString(){
        return identificador;
    }

    public int getPrecio() {
        return precio;
    }
}