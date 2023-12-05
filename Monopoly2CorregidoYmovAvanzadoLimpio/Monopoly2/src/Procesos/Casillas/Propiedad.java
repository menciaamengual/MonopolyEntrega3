package Procesos.Casillas;

import Procesos.Jugador;

public abstract class Propiedad extends Casilla {
    private Jugador propietario;
    boolean hipotecado;
    int precio; //Precio de compra a la banca (precio básico)
    private int Hipoteca;
    int rentabilidad; //Dinero generado por la propiedad hasta el momento

    //Propietario
    public void setPropietario(Jugador propietario) {
        this.propietario = propietario;
    }

    public Jugador getPropietario() {
        return propietario;
    }

    //Precio
    public void setPrecio(){

    }

    public int getPrecio() {
        return precio;
    }

    //Hipoteca
    public void hipotecar(){
    }
    public void deshipotecar(){

    }
    public boolean getHipotecado(){
        return hipotecado;
    }
    //Generales
    public void comprar(){

    }
    public void vender(){

    }

    //Estadísticas
    //Aquí apáñate tu @menciamengual
}

