package Procesos;
import java.util.*;
public class Carta {
    //Atributos
    private int tipo; //0-1. 0.Suerte 1. Comunidad
    private final List<Integer> cartas = new ArrayList<>(6);
    private final List<String> cartasSuerte = new ArrayList<>(6);
    private final List<String> cartasCajaC = new ArrayList<>(6);


    //CONTRUCTORES
    public Carta(int posicion){
        if (posicion==7 || posicion ==22 || posicion ==36)
            this.tipo=0;
        if (posicion==2 || posicion ==17 || posicion==33)
            this.tipo=1;

        for (int i = 0; i <= 5; i++) {
            cartas.add(i); }

        inicializaSuerte();
        inicializaCajaC();
    }

    //METODOS PRIVADOS
    private void inicializaSuerte(){
        cartasSuerte.add("El aumento del impuesto sobre bienes inmuebles afecta a todas tus propiedades. Paga 100$ por casa, 1000$ por hotel, 1500$ por piscina y 2000$ por pista de deportes.");
        cartasSuerte.add("Tus compañeros descubren tu afición por la evasión de impuestos. Compra su silencio pagando 500$ a cada jugador.");
        cartasSuerte.add("¡Yuhuu!¡Has ganado el bote de la lotería! Recibes 1000$.");
        cartasSuerte.add("¡Hora punta de tráfico! Retrocede tres casillas.");
        cartasSuerte.add("Avanza hasta la casilla de transporte más cercana. Si no tiene dueño, puedes comprársela a la banca. Si tiene dueño, paga al dueño el doble de la operación indicada.");
        cartasSuerte.add("¡Felicidades! Hoy es tu cumpleaños y tu abuela te da tu regalo. Recibe 2000$");
    }

    private void inicializaCajaC(){
        cartasCajaC.add("Alquilas a tus compañeros una villa en San Vicente do Mar durante una semana. Paga 200$ a cada jugador.");
        cartasCajaC.add("Uy... Te investigan por fraude de identidad... Vas a la Cárcel directamente sin pasar por la casilla de Salida.");
        cartasCajaC.add("Colócate en la casilla de Salida y cobra el premio :).");
        cartasCajaC.add("Vas a Circus para disfrutar de una  gran noche de fiesta. Si pasas por la casilla de Salida, cobra el premio");
        cartasCajaC.add("Decides irte de viaje a Bali con tus amigos. Aunque os lo pasais en grande, ahora te toca pagar un total de 1000$.");
        cartasCajaC.add("Obtienes 2000€ de beneficios por tus magníficas gestiones empresariales. ¡Buen trabajo!");
    }

    //METODOS PUBLICOS
    public void barajar(){ //se altera la lista 0,1,2,3,4 y luego depende del numero q escojas accede a esa posicion
        Collections.shuffle(cartas); //los numeros de "cartas" estan relacionados con una accion
    }

    //GETTERS
    public int getTipo(){
        return tipo;}

    public List<String> getCartasSuerte() {
        return cartasSuerte;
    }

    public List<String> getCartasCajaC(){
        return cartasCajaC;
    }

    public List<Integer> getcartas(){
        return cartas;
    }

}