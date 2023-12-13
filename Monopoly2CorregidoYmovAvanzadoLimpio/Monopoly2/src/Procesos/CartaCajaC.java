package Procesos;

import java.util.ArrayList;
import java.util.List;

public final class CartaCajaC extends Carta{
    private final List<String> cartasCajaC= new ArrayList<>(6);
    public CartaCajaC(){
        super();
        inicializaCajaC();
    }
    private void inicializaCajaC(){
        cartasCajaC.add("Alquilas a tus compañeros una villa en San Vicente do Mar durante una semana. Paga 200$ a cada jugador.");
        cartasCajaC.add("Uy... Te investigan por fraude de identidad... Vas a la Cárcel directamente sin pasar por la casilla de Salida.");
        cartasCajaC.add("Colócate en la casilla de Salida y cobra el premio :).");
        cartasCajaC.add("Vas a Circus para disfrutar de una  gran noche de fiesta. Si pasas por la casilla de Salida, cobra el premio");
        cartasCajaC.add("Decides irte de viaje a Bali con tus amigos. Aunque os lo pasais en grande, ahora te toca pagar un total de 1000$.");
        cartasCajaC.add("Obtienes 2000€ de beneficios por tus magníficas gestiones empresariales. ¡Buen trabajo!");
    }
    public List<String> getCartasCajaC(){
        return cartasCajaC;
    }
}
