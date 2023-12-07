package Procesos.Casillas;

public abstract class Accion extends Casilla{
    public Accion(int posicion, String nombre) {
        super(posicion, nombre);
    }
    public void accionCasilla(){
    System.out.println("Has caído en "+getNombre()+"!");
    }
}
