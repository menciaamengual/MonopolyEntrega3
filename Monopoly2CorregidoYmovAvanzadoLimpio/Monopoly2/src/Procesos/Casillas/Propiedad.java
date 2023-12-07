package Procesos.Casillas;

import Procesos.Jugador;

public abstract class Propiedad extends Casilla {
    private Jugador propietario;
    private boolean hipotecado;
    private int precio; //Precio de compra a la banca (precio básico)
    private int rentabilidad; //Dinero generado por la propiedad hasta el momento

    //Propietario

    public Propiedad(int posicion, String nombre, Jugador propietario){
        super(posicion,nombre);
        this.propietario = propietario;
        precio = 0;
        rentabilidad = 0;
        int hipoteca = 0;
        hipotecado = false;
    }

    /**
     * Settea la propiedad y actualiza las listas de propiedad de los propietarios de la propiedad.
     * @param propietario al que se traslada la propiedad
     */
    public void setPropietario(Jugador propietario) {
        this.propietario.removePropiedad(this);
        this.propietario = propietario;
        propietario.addPropiedad(this);
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
        System.out.println("Has deshipotecado "+getNombre()+" por "+(int) ((precio/2)*1.1)+"$ ¡Qué bien!");
    }

    public boolean getHipotecado(){
        return hipotecado;
    }

    public void setHipotecado(boolean hipotecado) {
        this.hipotecado = hipotecado;
    }

    //Generales

    /**
     * Comprar una casilla a la banca
     * @param jugador comprador
     */
    public void comprar(Jugador jugador){
        if (jugador.getDinero() >= getPrecio()) {
            getPropietario().addDinero(getPrecio());
            setPropietario(jugador);
            jugador.setDinero(jugador.getDinero() - getPrecio());
            jugador.setDineroInvertido(jugador.getDineroInvertido() + getPrecio());
            jugador.setFortuna(jugador.getFortuna()+ getPrecio());
        } else System.out.println("Cuidado... Ya no tienes dinero suficiente para comprar esta casilla.");
    }

    /**
     * Vender una casilla de un jugador a otro
     * @param vendedor SE
     * @param comprador SE
     */
    public void vender(Jugador vendedor, Jugador comprador){

    }
    public Boolean isComprable(){ //Suponemos que no se puede comprar una casilla que ya sea de otro jugador
        return propietario.isBanca();
    }
    //Estadísticas
    //Aquí apáñate tu @menciamengual

    public void setRentabilidad(int rentabilidad) {
        this.rentabilidad = rentabilidad;
    }

    public int getRentabilidad() {
        return rentabilidad;
    }

    public String descripcion(){
        return "{\nnombre: " + getNombre() +"\n"+
                "tipo: "+ getClass() +" \n" +
                "valor: "+ precio +" \n" +
                "Propietario: " + getPropietario().getNombre() +"\n" +
                "}\n";
    }
    public void accionCasilla(Jugador jugador){
        if (!propietario.equals(jugador) && !propietario.isBanca() && !hipotecado) {
            jugador.pagar(calcularAlquiler(), propietario);
            setRentabilidad(rentabilidad + calcularAlquiler()); //edificios??
            jugador.setPagoDeAlquileres(jugador.getPagoDeAlquileres() + calcularAlquiler());
            getPropietario().setCobroDeAlquileres(getPropietario().getCobroDeAlquileres() + calcularAlquiler());
            System.out.println("Pagas " + calcularAlquiler() + "$ por caer en " + getNombre());
        } else if (getHipotecado()) {
            System.out.println("Casilla hipotecada... No pagas alquiler :)");
        } else if (propietario.isBanca()) {
            System.out.println("Esta propiedad aun no tiene dueño, la puedes comprar.");
        } else if (propietario.equals(jugador)) {
            System.out.println("Has caido en una casilla de tu propiedad, disfruta de tu estancia");
        }
    }

    private int calcularAlquiler() {
        return 0;
    }
}

