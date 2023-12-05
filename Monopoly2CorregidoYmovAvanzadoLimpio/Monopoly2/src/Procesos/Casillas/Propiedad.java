package Procesos.Casillas;

import Procesos.Jugador;

public abstract class Propiedad extends Casilla {
    protected Jugador propietario;
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
    public void setPrecio(int precio){
        this.precio = precio;
    }

    public int getPrecio() {
        return precio;
    }

    //Hipoteca

    /**
     * Función general de hipoteca, sin comprobación de eficicios.
     *  Pago al propietario
     *  Hipotecado a true
     */
    public void hipotecar(){
        if (hipotecado){
            System.out.println("No puedes hipotecar una propiedad ya hipotecada.");
            return;
        }

        hipotecado = true;
        propietario.addDinero(precio/2);
        System.out.println("Has hipotecado "+ this+" por "+precio/2+"$");
    }
    public void deshipotecar(){
        if (!hipotecado){
            System.out.println("No puedes deshipotecar una propiedad no hipotecada.");
            return;
        }
        if (propietario.getDinero()<(int) ((-precio/2)*1.1)){
            System.out.println("Dinero insuficiente para deshipotecar, necesitas "+(int) ((-precio/2)*1.1));
            return;
        }
        hipotecado = false;
        propietario.addDinero((int) ((-precio/2)*1.1)); //Add dinero negativo...
        System.out.println("Has deshipotecado "+nombre+" por "+(int) ((precio/2)*1.1)+"$ ¡Qué bien!");
    }

    public boolean getHipotecado(){
        return hipotecado;
    }
    //Generales
    public void comprar(){

    }
    public void vender(){

    }
    public Boolean isComprable(){ //Suponemos que no se puede comprar una casilla que ya sea de otro jugador
        return propietario.isBanca();
    }
    //Estadísticas
    //Aquí apáñate tu @menciamengual
}

