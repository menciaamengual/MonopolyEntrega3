package Procesos;
import java.util.*;
public abstract class Carta {
    //Atributos
    private final List<Integer> cartas = new ArrayList<>(6);

    //CONTRUCTORES
    public Carta(){
        for (int i = 0; i <= 5; i++) {
            cartas.add(i); }
    }

    //METODOS PUBLICOS
    public void barajar(){ //se altera la lista 0,1,2,3,4 y luego depende del numero q escojas accede a esa posicion
        Collections.shuffle(cartas); //los numeros de "cartas" estan relacionados con una accion
    }

    //GETTERS
    public List<Integer> getCartas(){
        return cartas;
    }

}