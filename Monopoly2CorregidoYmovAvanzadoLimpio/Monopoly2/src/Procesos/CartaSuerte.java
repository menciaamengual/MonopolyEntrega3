package Procesos;

import java.util.ArrayList;
import java.util.List;

public final class CartaSuerte extends Carta {
    private final List<String> cartasSuerte= new ArrayList<>(6);
    public CartaSuerte(){
        super();
        inicializaSuerte();
    }
    private void inicializaSuerte(){
        cartasSuerte.add("El aumento del impuesto sobre bienes inmuebles afecta a todas tus propiedades. Paga 100$ por casa, 1000$ por hotel, 1500$ por piscina y 2000$ por pista de deportes.");
        cartasSuerte.add("Tus compañeros descubren tu afición por la evasión de impuestos. Compra su silencio pagando 500$ a cada jugador.");
        cartasSuerte.add("¡Yuhuu!¡Has ganado el bote de la lotería! Recibes 1000$.");
        cartasSuerte.add("¡Hora punta de tráfico! Retrocede tres casillas.");
        cartasSuerte.add("Avanza hasta la casilla de transporte más cercana. Si no tiene dueño, puedes comprársela a la banca. Si tiene dueño, paga al dueño el doble de la operación indicada.");
        cartasSuerte.add("¡Felicidades! Hoy es tu cumpleaños y tu abuela te da tu regalo. Recibe 2000$");
    }
    public List<String> getCartasSuerte() {
        return cartasSuerte;
    }
}
