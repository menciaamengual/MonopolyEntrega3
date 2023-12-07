package Juego;
//MONOPOLY V2
//Mateito, Cese y Mencía


public class Main {
    public static void main(String[] args) {
        System.out.println("¡--Empieza el juego!--");
        //INICIALIZACIÓN
        Juego mono = new Juego();

        //DAR DE ALTA A LOS JUGADORES
        mono.turnoInicial();

        boolean continuar = true;
        while (continuar) {
            continuar = mono.menuAccion(false);
        }

        //FIN DE JUEGO
        System.out.println("Muchas gracias por jugar, ¡hasta la próxima!");
    }
}