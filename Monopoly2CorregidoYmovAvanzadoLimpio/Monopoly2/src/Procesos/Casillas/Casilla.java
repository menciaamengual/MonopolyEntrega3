package Procesos.Casillas;

import Procesos.Jugador;

import java.util.ArrayList;

public abstract class Casilla{
    private final int posicion; //0-43
    private String nombre;
    private int visitas;
    private ArrayList<Jugador> ocupantes;
    public static final int lonMaxNombre = 11; //Longitud del nombre en impresión. Función que mira long del nombre + largo

    public Casilla(int posicion, String nombre){
        this.posicion = posicion;
        this.nombre = nombre;

        visitas = 0;
        ocupantes = new ArrayList<>(6);
    }

    //Impresión por pantalla
    String StringAvatares(){
        StringBuilder avs = new StringBuilder();
        for (Jugador jugador:ocupantes){
            avs.append("&").append(jugador.getAvatar().getIdentificador());
        }
        return avs.toString();
    }
    public String toString (){
        String formato = "|\u001B[4m%"+lonMaxNombre+"s %"+6+"s\u001B[0m|";
        /*if (grupo!=null) formato = grupo.colorFormato() + formato + "\u001B[0m";*/
        return String.format(formato,nombre,StringAvatares());
    }

    //Trabajar con ocupantes
    public ArrayList<Jugador> getOcupantes() {
        return ocupantes;
    }

    /**
     * No hay ningún motivo para tener que usar esta función más que en la inicialización de la partida (si acaso)
     * @param ocupantes lista de jugadores a la que settear la lista de ocupantes de la casilla
     */
    public void setOcupantes(ArrayList<Jugador> ocupantes) {
        this.ocupantes = ocupantes;
    }

    /**
     * Esta función itera en la lista de ocupantes de la casilla, y crea una lista nueva dejando fuera al jugador específicado (en caso de que esté; en caso contrario no pasa nada).
     * @param jugador jugador que va a ser quitado de esa casilla
     */
    public void removeOcupante(Jugador jugador){
        ArrayList<Jugador> aux = new ArrayList<>(6);
        for (Jugador ite: ocupantes){
            if (!ite.equals(jugador)) aux.add(ite);
        }
        ocupantes = aux;
    }

    /**
     * Añade el jugador especificado a la lista de ocupantes de la casilla
     * @param jugador jugador a añadir a la lista de ocupantes de la casilla
     */
    public void addOcupante(Jugador jugador) {
        ocupantes.add(jugador);
    }
    public boolean isOcupante(Jugador jugador){
        return (ocupantes.contains(jugador));
    }
    public Boolean isComprable(){ //Suponemos que no se puede comprar una casilla que ya sea de otro jugador
        return false; //En todos los casos que no este overrideado (hijos comprables), devuelve falso
    }

    //Nombre
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getNombre() {
        return nombre;
    }

    //Visitas
    public void setVisitas(int visitas) {
        this.visitas = visitas;
    }

    public int getVisitas() {
        return visitas;
    }

    public String descripcionDetallada() {
        return "Descripción genérica";
    }

    public String descripcion(){
        return "{\nnombre: " + getNombre() +"\n"+
                "tipo: "+ getClass().getName() +" \n" +
                "}\n";
    }

    public int getPosicion() {
        return posicion;
    }
}
