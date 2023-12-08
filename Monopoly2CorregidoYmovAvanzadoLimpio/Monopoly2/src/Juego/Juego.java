package Juego;

import java.util.*;

import Procesos.*;
import Procesos.Casillas.*;

public class Juego implements Comando{
    //ATRIBUTOS
    private static Tablero tablero;
    private final int dineroInicial;
    private final int pSalida; //Premio salida
    private int pBase; //Precio casilla primer grupo
    private Jugador jugadorActual;
    private final ArrayList<Jugador> jugadores;
    // private boolean hayBug; //Encontramos un error por el cual, tras la finalización del turno de un coche con movimiento avanzado, el siguiente jugador no puede tirar. Este booleano lo detectará y nos permitirá solucionarlo en menuAccion.

    private final Jugador banca;
    private final Dado dado;
    private Carta carta;
    private final int precioCarcel;
    private static boolean pagando; //Un booleano que se activa si el usuario está en trámites de pagar una deuda (para darle opciones de vender, hipotecar, etc)...
    private static int pagoPendiente;
    private static Jugador cobradorPendiente;
    private final StringBuilder avataresRep = new StringBuilder("!");

    Map<Jugador, Map<Casilla, Integer>> contadorCasillas = new HashMap<>();

    private char generaCharRandom() {
        Random randomC = new Random();
        return (char) (randomC.nextInt(26) + 'A');
    }

    //CONSTRUCTORES
    public Juego() {
        jugadores = new ArrayList<Jugador>(6); //La banca se maneja como un jugador externo (banca).
        banca = new Jugador();
        pSalida = Grupo.mediaSolares();
        tablero = new Tablero(pBase, pSalida, banca);
        dado = new Dado();
        dineroInicial = tablero.precioTotal() / 3;
        precioCarcel = (int) (pSalida * 0.25);
        pBase = 100;
        pagando = false;
        cobradorPendiente = banca;
        pagoPendiente = 0;
    }

    //METODOS PUBLICOS
    public void darAlta() {
        Scanner entrada = new Scanner(System.in);
        System.out.println("Introduce tu nombre, Jugador " + (jugadores.size() + 1) + " : ");
        String miNombre = entrada.nextLine();
        char miavatar;
        do {
            miavatar = generaCharRandom();
        } while (avataresRep.toString().contains(String.valueOf(miavatar)));
        avataresRep.append(miavatar);
        int tipo;
        String inputTipo;
        do {
            System.out.println("Elige tipo de ficha (coche o pelota):");
            inputTipo = entrada.nextLine();
            if (inputTipo.equalsIgnoreCase("coche")) {
                tipo = 1;
            } else if (inputTipo.equalsIgnoreCase("pelota")) {
                tipo = 0;
            } else {
                System.out.println("Tipo no válido. Por favor, introduce coche o pelota.");
                tipo = -1; // Para que el bucle se ejecute otra vez hasta que el usuario escoja una opción válida
            }
        } while (tipo == -1);
        jugadores.add(new Jugador(dineroInicial, miNombre, miavatar, tipo));
        for (Jugador ite : jugadores)
            ite.setFortuna(dineroInicial);
    }

    //GETTERS
    public Tablero getTablero() {
        return tablero;
    }


    //Ejecución del juego

    /**
     * Este método mueve al jugador y ejecuta las funciones de la casilla de salida
     * Si el movimiento activado está desactivado, no llama al siguiente turno, ni al menú, ni nada, eso va fuera
     * Pero como diría J. Mota, ¿pero y si sí? Si está activado, es necesario que se ejecuten ciertas acciones dentro de la función.
     *
     * @param avance, movAvanzado
     */
    private void avanzarCasillas(int avance, boolean movAvanzado) {
        if (!movAvanzado) {
            if (avance >= 0) { // Funcionamiento normal (realmente podríamos poner "if getposicion+avance >0" pero así queda más legible)
                if ((jugadorActual.getPosicion() + avance) > 39) {
                    addVuelta(jugadorActual);
                    System.out.println("Pasas por la casilla de salida y cobras " + pSalida + "$");
                    jugadorActual.setDinero(jugadorActual.getDinero() + pSalida);
                    jugadorActual.setPosicion((jugadorActual.getPosicion() + avance) - 40, tablero.getCasillas());
                } else {
                    jugadorActual.setPosicion(jugadorActual.getPosicion() + avance, tablero.getCasillas());
                }
            } else { // Avance negativo
                if ((jugadorActual.getPosicion() + avance) <= 0) { // Si se pasa por la casilla de salida
                    jugadorActual.setVueltas(jugadorActual.getVueltas() - 1); // Se resta una vuelta
                    System.out.println("Pasas por la casilla de salida en sentido contrario y pagas " + pSalida + "$");
                    jugadorActual.pagar(pSalida, banca);
                    jugadorActual.setPosicion((jugadorActual.getPosicion() + avance) + 40, tablero.getCasillas());
                } else {
                    jugadorActual.setPosicion(jugadorActual.getPosicion() + avance, tablero.getCasillas());
                }
            }
        } else { // Movimiento avanzado activado
            if (jugadorActual.getTipoMov() == 0) { // PELOTA
                jugadorActual.setAuxMovAvanzado(1); // Indica que el turno aún está en curso
                if (avance > 4) {
                    avanzarCasillas(4, false);
                    int aux = jugadorActual.getPosicion(); // guardamos la posición tras avanzar 4 casillas para comprobar paridades
                    for (int i = 5; i <= avance; i++) {
                        avanzarCasillas(1, false); // avanzamos una casilla
                        if (jugadorActual.getPosicion() == 30) { // Si cae en la casilla de ir a la cárcel, se acaba la función. Recordemos que accionCasilla y menuAccion de la última parada/casilla se ejecutan fuera de aquí.
                            return;
                        }
                        if (i == avance) {
                            jugadorActual.setAuxMovAvanzado(0);
                            System.out.println("Ya has pasado por todas tus paradas.");
                            return;
                        }
                        if ((jugadorActual.getPosicion() - aux) % 2 != 0) { // Si es impar, se para en la casilla, se ejecuta la accion correspondiente y el jugador puede interactuar
                            System.out.println("¡Momento de pararse! Introduce \"acabar parada\" para avanzar a la siguiente casilla");
                            tablero.imprimirTablero();
                            accionCasilla();
                            if (jugadorActual.inCarcel()) {
                                break;
                            }
                            menuAccion(true);
                            // menuAccion(true);
                        }
                        // Si es par, no se hace nada.
                    }
                    return;
                } else {
                    int aux = jugadorActual.getPosicion(); // guardamos la posición para comprobar paridades
                    for (int i = avance; i > 0; i--) {
                        avanzarCasillas(-1, false);
                        if (jugadorActual.getPosicion() == 30) { // Si cae en la casilla de ir a la cárcel, se acaba la función. Recordemos que accionCasilla y menuAccion de la última parada/casilla se ejecutan fuera de aquí.
                            return;
                        }
                        if (i == 1) { // Si es la última parada
                            jugadorActual.setAuxMovAvanzado(0);
                            System.out.println("Ya has pasado por todas tus paradas.");
                            return;
                        }
                        if ((jugadorActual.getPosicion() - aux) % 2 != 0) { // Si es impar, se para en la casilla, se ejecuta la accion correspondiente y el jugador puede interactuar
                            System.out.println("¡Momentito de pararse! Introduce \"acabar parada\" para ir a la próxima casilla");
                            tablero.imprimirTablero();
                            accionCasilla();
                            if (jugadorActual.inCarcel()) {
                                break;
                            }
                            menuAccion(true);
                        }
                        // Si es par, no se hace nada.
                    }
                }
            } else if (jugadorActual.getTipoMov() == 1) { // COCHE
                if (avance >= 4) {
                    avanzarCasillas(avance, false);
                    System.out.println("Puedes tirar los dados hasta 3 veces más mientras saques más de un 3.");
                    jugadorActual.setAuxMovAvanzado(3);
                } else {
                    avanzarCasillas(-avance, false);
                    jugadorActual.setAuxMovAvanzado(-3); // Los números negativos sin el "-" indicarán los turnos restantes sin poder tirar (em el turno actual, por eso se inicializa en -3 y no en -2. al acabar el turno se suma 1, con lo que al acabar el turno en el que se estropea el motor tienes -3+1 = (-)2 turnos más sin tirar)
                    System.out.println("Se te ha estropeado el motor y deberás estar dos turnos sin tirar mientras se arregla.");
                }
            }
        }
    }

    private void nextJugador() {//Al final de cada turno, avanzamos jugador;
        if (jugadores.indexOf(jugadorActual) == (jugadores.size() - 1))
            jugadorActual = jugadores.get(0);
        else jugadorActual = jugadores.get(jugadores.indexOf(jugadorActual) + 1);
    }

    private void accionSuerte(int numCarta) {
        System.out.println(carta.getCartasSuerte().get(numCarta));
        switch (numCarta) {
            case 0:
                int contC = 0, contH = 0, contP = 0, contD = 0;
                for (Casilla ite : jugadorActual.getPropiedades()) {
                    if (ite instanceof Solar) for (Edificio cite : ((Solar) ite).getEdificios()) {
                        if (cite instanceof Casa) contC++;
                        if (cite instanceof Hotel) contH++;
                        if (cite instanceof Piscina) contP++;
                        if (cite instanceof Pista) contD++;
                    }
                }

                int total = (100 * contC) + (200 * contH) + (300 * contP) + (400 * contD);
                if (total == 0) {
                    System.out.println("Como aun no tienes edificios construidos, te libras");
                } else {
                    System.out.println("En total, tienes que pagar" + total + "$");
                    pagarAv(jugadorActual, total);
                    //jugadorActual.pagar(total); MAL
                }
                break;

            case 1:
                ArrayList<Jugador> aux = new ArrayList<>(6);
                for (Jugador ite : jugadores) {
                    if (!jugadorActual.equals(ite))
                        aux.add(ite); //creamos una lista de los jugadores a los que pagar
                }
                for (Jugador ite : aux) {
                    pagarAv(jugadorActual, 500, ite);
                    jugadorActual.setPagoTasasEImpuestos(jugadorActual.getPagoTasasEImpuestos() + 500);
                    ite.setPremiosInversionesOBote(ite.getPremiosInversionesOBote() + 500);
                }
                break;

            case 2:
                jugadorActual.addDinero(1000);
                jugadorActual.setPremiosInversionesOBote(jugadorActual.getPremiosInversionesOBote() + 1000);
                break;

            case 3:
                jugadorActual.setPosicion((jugadorActual.getPosicion() - 3), tablero.getCasillas());
                tablero.imprimirTablero();
                accionCasilla();
                break;

            case 4:
                int pTCercano = 5;
                switch (jugadorActual.getPosicion()){
                    case 7:
                        pTCercano = 15;
                        break;
                    case 22:
                        pTCercano = 25;
                        break;
                    case 36:
                        //pTCercano a 5 por defecto
                        System.out.println("Has pasado por la casilla de salida, cobras" + pSalida);
                        jugadorActual.addDinero(pSalida);
                        jugadorActual.setPasarPorCasillaDeSalida(jugadorActual.getPasarPorCasillaDeSalida() + pSalida);
                        addVuelta(jugadorActual);
                        break;
                }
                jugadorActual.setPosicion(pTCercano, tablero.getCasillas());
                tablero.imprimirTablero();
                Propiedad tCercano;
                if (tablero.getCasilla(pTCercano) instanceof Transporte)  tCercano = (Propiedad) tablero.getCasilla(pTCercano);
                else break;

                int alquilersuerte = ((Transporte)tCercano).calcularAlquiler() * 2;

                if (!tCercano.getPropietario().equals(jugadorActual) && !tCercano.getPropietario().equals(banca) && !tCercano.getHipotecado()) {
                    pagarAv(jugadorActual, alquilersuerte, tCercano.getPropietario());
                    tCercano.setRentabilidad(tCercano.getRentabilidad() + alquilersuerte);
                    jugadorActual.setPagoDeAlquileres(jugadorActual.getPagoDeAlquileres() + alquilersuerte);
                    tCercano.getPropietario().setCobroDeAlquileres(tCercano.getPropietario().getCobroDeAlquileres() + alquilersuerte);
                    System.out.println("Pagas " + alquilersuerte + "$ por caer en " + tCercano.getNombre());
                } else if (tCercano.getHipotecado()) {
                    System.out.println("La casilla de transporte esta hipotecada... No pagas alquiler :)");
                }
            case 5:
                jugadorActual.addDinero(2000);
                jugadorActual.setPremiosInversionesOBote(jugadorActual.getPremiosInversionesOBote() + 2000);
                break;
        }
    }

    private void accionCajaC(int numCarta) {
        System.out.println(carta.getCartasCajaC().get(numCarta));
        switch (numCarta) {
            case 0:
                ArrayList<Jugador> aux = new ArrayList<>(6);
                for (Jugador ite : jugadores) {
                    if (!jugadorActual.equals(ite))
                        aux.add(ite); //literal igual que en suerte
                }
                for (Jugador ite : aux) {
                    pagarAv(jugadorActual, 200, ite);
                    jugadorActual.setPagoTasasEImpuestos(jugadorActual.getPagoTasasEImpuestos() + 200);
                    ite.setPremiosInversionesOBote(ite.getPremiosInversionesOBote() + 200);
                }
                break;

            case 1:
                jugadorActual.setPosicion(30, tablero.getCasillas());
                accionCasilla();
                break;

            case 2:
                jugadorActual.setPosicion(0, tablero.getCasillas());
                jugadorActual.addDinero(pSalida);
                jugadorActual.setPasarPorCasillaDeSalida(jugadorActual.getPasarPorCasillaDeSalida() + pSalida);
                addVuelta(jugadorActual);
                break;

            case 3:
                jugadorActual.setPosicion(34, tablero.getCasillas());
                accionCasilla(); //un poco cutre pq no hace falta hacer la comprobacion de salida
                break;

            case 4:
                jugadorActual.pagar(1000);
                jugadorActual.setPagoTasasEImpuestos(jugadorActual.getPagoTasasEImpuestos() + 1000);
                break;

            case 5:
                jugadorActual.addDinero(2000);
                jugadorActual.setPremiosInversionesOBote(jugadorActual.getPremiosInversionesOBote() + 2000);
                break;
        }
    }

    /**
     * Llamamos a funciones internas para todos los tipos de casilla menos para cartas
     * En este caso necesitábamos acceder a demasiados métodos y atributos de juego para que tuviese sentido
     */
    public void accionCasilla() {
        contadorCasillas.get(jugadorActual).put(jugadorActual.getCasilla(tablero.getCasillas()), contadorCasillas.get(jugadorActual).get(jugadorActual.getCasilla(tablero.getCasillas())) + 1);
        Casilla casilla = jugadorActual.getCasilla(tablero.getCasillas());
        if (casilla instanceof Solar) ((Solar) casilla).accionCasilla(jugadorActual);
        else if (casilla instanceof Transporte) ((Transporte) casilla).accionCasilla(jugadorActual);
        else if (casilla instanceof Servicios) ((Servicios) casilla).accionCasilla(jugadorActual,dado);
        else if (casilla instanceof Impuesto) ((Impuesto) casilla).accionCasilla(jugadorActual);
        else if (casilla instanceof ParkingGratuito) ((ParkingGratuito) casilla).accionCasilla(jugadorActual);
        else if (casilla instanceof VasALaCarcel) ((VasALaCarcel) casilla).accionCasilla(jugadorActual,tablero.getCasillas(),dado);
        else if (casilla instanceof Salida) ((Salida) casilla).accionCasilla();
        else if (casilla instanceof Carcel) ((Carcel) casilla).accionCasilla(jugadorActual);

        else if (casilla instanceof Comunidad || casilla instanceof Suerte) {
            carta = new Carta(jugadorActual.getPosicion());
            carta.barajar();
            int indice;
            String entradaString;
            do {
                System.out.println("Elige una carta (introduciendo un numero del 1 al 6)");
                Scanner entrada = new Scanner(System.in);
                entradaString = entrada.nextLine();
            } while (!entradaString.equals("1") && !entradaString.equals("2") && !entradaString.equals("3") && !entradaString.equals("4") && !entradaString.equals("5") && !entradaString.equals("6"));
            indice = Integer.parseInt(entradaString);
            int numCarta = (carta.getcartas().get(indice - 1));
            if (carta.getTipo() == 0)
                accionSuerte(numCarta);
            if (carta.getTipo() == 1)
                accionCajaC(numCarta);
        }
    }



    /**
     * Función de compra de la casilla, con comprobación de si el jugadorActual tienen suficiente dinero
     * Realiza la transacción correspondiente entre el jugador y la banca, y mueve la propiedad de la Procesos.Casilla, y del grupo en caso de que corresponda
     */
    public void comprarCasilla() { //todo garantizar que se añade a propie
        Casilla casillac = jugadorActual.getCasilla(tablero.getCasillas());
        if (!(casillac instanceof Propiedad)) { //RARO
            System.out.println("No puedes comprar esta casilla");
            return;
        }
        Propiedad casilla = (Propiedad) casillac;
        if (jugadorActual.getDinero() >= casilla.getPrecio()) {
            casilla.getPropietario().addDinero(casilla.getPrecio());
            casilla.setPropietario(jugadorActual);
            jugadorActual.setDinero(jugadorActual.getDinero() - casilla.getPrecio());
            jugadorActual.setDineroInvertido(jugadorActual.getDineroInvertido() + casilla.getPrecio());
            jugadorActual.setFortuna(jugadorActual.getFortuna() + casilla.getPrecio());
        } else System.out.println("Cuidado... Ya no tienes dinero suficiente para comprar esta casilla.");
    }


    //INTERFAZ COMANDO
    /**
     * ayuda copia los condicionales de menuAcción para enseñar al jugador las opciones que tiene disponibles.
     *
     * @param haTirado si el jugadorActual ha tirado ya en este turno
     */
    public void ayuda(boolean haTirado){
        System.out.println("Listado de acciones");
    }
    public void imprimirTablero(){
        tablero.imprimirTablero();
    }
    public void jugador(){
        System.out.println("Nombre: " + jugadorActual.getNombre());
        System.out.println("Avatar: " + jugadorActual.getAvatar());
        System.out.println("Dinero: " + jugadorActual.getDinero());
        System.out.println("Posición: " + jugadorActual.getPosicion());
    }
    public void tirarDados(){

    }
    public void tirarDadosTrucados(){

    }
    public void comprar(String[] entradaPartida, boolean haTirado){ //todo comprobar si funciona; PQ FUERON MAZO DE CAMBIOS
        if (jugadorActual.getBancarrota()) {
            System.out.println("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
        }
        else if (!haTirado) {
            System.out.println("Aun no has tirado los dados. Tiralos para poder comprar propiedades");
        }
        else if (entradaPartida.length > 1 && entradaPartida[1].equals("propiedad") || entradaPartida[1].equals(jugadorActual.getCasilla(tablero.getCasillas()).getNombre())) {
            if (jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov() == 1) { // Si es coche
                if (jugadorActual.getPuedeComprarPropiedades()) {
                    if (!jugadorActual.getCasilla(tablero.getCasillas()).isComprable()) {
                        System.out.println("No puedes comprar esta propiedad");
                    } else {
                        comprarCasilla();
                        System.out.println("Propiedad comprada con éxito por " + ((Propiedad) jugadorActual.getCasilla(tablero.getCasillas())).getPrecio() + "$");
                        System.out.println("No podrás comprar más propiedades, casillas, servicios o transportes hasta que acabe tu turno.");
                        jugadorActual.setPuedeComprarPropiedades(false);
                    }
                } else {
                    System.out.println("No puedes comprar más propiedades, casillas, servicios o transportes hasta que acabe tu turno.");
                }
            }
            else if (!jugadorActual.getCasilla(tablero.getCasillas()).isComprable()) {
                System.out.println("No puedes comprar esta propiedad");
                //break;
            } else {
                comprarCasilla();
                System.out.println("Propiedad comprada con éxito por " + ((Propiedad) jugadorActual.getCasilla(tablero.getCasillas())).getPrecio() + "$");
            }
        } else { //Si está intentando comprar otra propiedad le avisamos...
            boolean c = true;
            for (Casilla ite : getTablero().getCasillas()) {
                if (entradaPartida[1].equals(ite.getNombre())) {
                    System.out.println("Solo puedes comprar la casilla en la que caes...");
                    c = false;
                }
            }
            if (c) System.out.println("Esta casilla no existe");
        }
    }
    public void edificar(String[] entradaPartida){
        boolean caidas = false;
        caidas = contadorCasillas.get(jugadorActual).get(jugadorActual.getCasilla(tablero.getCasillas())) == 2;

        if (entradaPartida.length > 1)
            if (jugadorActual.getCasilla(tablero.getCasillas()) instanceof Solar)
                switch (entradaPartida[1]) {
                    case "casa":
                    case "Casa":
                        ((Solar) jugadorActual.getCasilla(tablero.getCasillas())).construir(0, jugadorActual, caidas);
                        break;
                    case "hotel":
                    case "Hotel":
                        ((Solar) jugadorActual.getCasilla(tablero.getCasillas())).construir(1, jugadorActual, caidas);
                        break;
                    case "piscina":
                    case "Piscina":
                        ((Solar) jugadorActual.getCasilla(tablero.getCasillas())).construir(2, jugadorActual, caidas);
                        break;
                    case "pista":
                    case "Pista":
                        ((Solar) jugadorActual.getCasilla(tablero.getCasillas())).construir(3, jugadorActual, caidas);
                        break;
                    default:
                        System.out.println("No existe este edificio. Prueba con casa, hotel, piscina o pista :)");
                }
            else
                System.out.println("No estás en un solar >: (");
    }
    public void venderEdificio(String[] entradaPartida){
        Edificio aux = null;
        if (entradaPartida.length > 2) {
            String identificador = entradaPartida[1] + " " + entradaPartida[2];
            if (jugadorActual.getPropiedades() != null) for (Propiedad cite : jugadorActual.getPropiedades())
                if (cite instanceof Solar)

                    if (((Solar) cite).getEdificios() != null)
                        for (Edificio eite : ((Solar) cite).getEdificios()) {
                            if (eite.getIdentificador().equals(identificador)) aux = eite;
                        }

            if (aux != null) aux.getCasilla().venderEdificio(aux, jugadorActual);
            else System.out.println("Identificador inválido...");
        } else System.out.println("Identificador inválido...");
    }
    public void hipotecar(String[] entradaPartida){
        if (jugadorActual.getBancarrota())
            System.out.println("Ya no puedes hipotecar, desgraciadamente estas en bancarrota y la partida se ha acabado para ti.");
        else if (entradaPartida.length > 1) {
            Casilla casilla = tablero.getCasilla(entradaPartida[1]);
            if (casilla == null)
                System.out.println("Esta casilla no existe");
            else if (!(casilla instanceof Propiedad))
                System.out.println("Esta casilla no es hipotecable");
            else if (!((Propiedad) casilla).getPropietario().equals(jugadorActual))
                System.out.println("No puedes hipotecar una casilla que no es tuya");
            else
                ((Propiedad) casilla).hipotecar();
        }
    }
    public void deshipotecar(String[] entradaPartida){
        if (jugadorActual.getBancarrota())
            System.out.println("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
        else if (entradaPartida.length > 1) {
            Casilla casilla = tablero.getCasilla(entradaPartida[1]);
            if (casilla == null)
                System.out.println("Esta casilla no existe");
            else if (!(casilla instanceof Propiedad))
                System.out.println("Esta casilla no es hipotecable");
            else if (!((Propiedad) casilla).getPropietario().equals(jugadorActual)) {
                System.out.println("No puedes deshipotecar una casilla que no es tuya");
            }
            else ((Propiedad) casilla).deshipotecar();
        }
    }
    public void bancarrota(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("¿Estás seguro de que quieres declararte en bancarrota?");
        String respuesta = scanner.nextLine();
        if (respuesta.equals("si") || respuesta.equals("Si") || respuesta.equals("SI"))
            jugadorActual.declararBancarrota(cobradorPendiente);
    }
    public void describirJugador(String[] entradaPartida){
        boolean c = true;
        for (Jugador ite : jugadores) {
            if (entradaPartida.length > 2 && entradaPartida[2].equals(ite.getNombre())) {
                System.out.print(ite);
                c = false;
            }
        }
        if (c) System.out.println("Vaya! No existe este jugador...");
    }
    public void describirCasilla(String s){
        if (tablero.getCasilla(s) != null) {
            System.out.print(tablero.getCasilla(s).descripcionDetallada());
        } else System.out.println("No hay ninguna casilla que se llame así");
    }
    public void describirAvatar(String[] entradaPartida){
        boolean c = true;
        for (Jugador ite : jugadores) {
            if (entradaPartida.length > 2 && entradaPartida[2].equals(String.valueOf(ite.getAvatar()))) {
                System.out.print("{\n" +
                        "id: " + "A" + ",\n" + //El avatar que corresponda
                        "tipo: " + "N/A" + ",\n" +
                        "casilla: " + ite.getCasilla(tablero.getCasillas()).getNombre() + ",\n" +
                        "jugador: [" + ite.getNombre() + "]\n" +
                        "}");
                c = false;
            }
        }
        if (c) System.out.println("Vaya! No existe este avatar...");
    }
    public void listarJugadores(){
        for (Jugador jugadore : jugadores) {
            System.out.println(jugadore);
        }
    }
    public void listarEnVenta(){
        for (Casilla casilla : tablero.getCasillas()) {
            if (casilla.isComprable()) System.out.println(" " + casilla.descripcion());
        }
    }
    public void listarAvatares(){
        for (Jugador ite : jugadores) {
            System.out.println("{\n" +
                    "id: " + ite.getAvatar() + "\n" +
                    "tipo: " + "N/A" + ",\n" +
                    "casilla: " + ite.getCasilla(tablero.getCasillas()) + "\n" +
                    "jugador: " + ite.getNombre() + "\n" +
                    "}");
        }
    }
    public void listarEdificios() {
        for (Casilla cite : tablero.getCasillas()) {
            if (cite instanceof Solar)
                if (((Solar) cite).getEdificios() != null)
                    for (Edificio eite : ((Solar) cite).getEdificios())
                        System.out.println(" " + eite.getIdentificador() + " - " + eite.getCasilla());
        }
    }
    public void listarEdificiosColor(String[] entradaPartida){
        for (Grupo ite : tablero.getGrupos()) {
            if (ite.getColor().equals(entradaPartida[2])) {
                for (Solar cite : ite.getCasillas()) {
                    if (cite != null)
                        for (Edificio eite : cite.getEdificios())
                            System.out.println(" " + eite.getIdentificador() + " - " + eite.getCasilla());
                }
            }
        }
        System.out.println("No se reconoce el grupo/color");
    }
    public void salirCarcel(){
        if (!jugadorActual.inCarcel()) {
            System.out.println("No estas en la cárcel...");
        } else {
            System.out.println("Pagas " + precioCarcel + " para salir de la cárcel.");
            pagarAv(jugadorActual, precioCarcel);
            jugadorActual.setPagoTasasEImpuestos(jugadorActual.getPagoTasasEImpuestos() + precioCarcel);
            jugadorActual.setTurnosCarcel(0);
        }
    }
    public void pagarDeuda(){
        if (pagando) {
            pagarAv(jugadorActual, pagoPendiente, cobradorPendiente);
        } else System.out.println("No tienes nada que pagar :D");
    }
    public boolean acabarTurno(boolean haTirado){
        boolean c = false;
        if (puedeTirarOtraVez(haTirado) && !jugadorActual.getBancarrota()) { // SI PUEDE TIRAR OTRA VEZ, DEBE HACERLO. AVISAMOS.
            System.out.println("Tienes que tirar antes de terminar el turno");
        }
        // Sin embargo, existe un caso en el que el jugador no puede tirar, pero tampoco puede acabar el turno. Lo tratamos:
        else if (jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov() == 0 && jugadorActual.getAuxMovAvanzado() == 1) {
            // (Si es tipo Pelota, el movimiento avanzado está activado y aún le quedan paradas por recorrer)
            System.out.println("¡Aún te quedan paradas por recorrer!");
        }
        else if (jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov() == 1) { // MOVIMIENTO AVANZADO "COCHE" ------------------------------------------------------------------------------
            if (jugadorActual.getAuxMovAvanzado() < 0) { // SI EL JUGADOR QUE ACABA EL TURNO ES UN COCHE CON MOTOR ROTO
                jugadorActual.setAuxMovAvanzado(jugadorActual.getAuxMovAvanzado() + 1);
                if (jugadorActual.getAuxMovAvanzado() == 0) {
                    jugadorActual.setAuxMovAvanzado(999);
                    System.out.println("¡Motor reparado! En el siguiente turno podrás moverte.");
                } else
                    System.out.printf("\nTurnos restantes para poder moverte: %d\n", -jugadorActual.getAuxMovAvanzado());
                jugadorActual.setPuedeComprarPropiedades(true);
                // jugadorActual.setAuxMovAvanzado(999);
                jugadorActual.setPuedeComprarPropiedades(true);
                            /* if(jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov()==0){
                                hayBug=true;
                            } */
                nextJugador();
                System.out.println("Turno de: " + jugadorActual.getNombre());
                c = true;
            }
        }
        else if (pagando && !jugadorActual.getBancarrota()) {
            System.out.println("Debes saldar tu deuda antes de acabar el turno, o declararte en bancarrota");
        } else {
            if (!(jugadorActual.getTipoMov() == 1 && jugadorActual.getMovAvanzadoActivado() && jugadorActual.getAuxMovAvanzado() < 0)) {
                jugadorActual.setAuxMovAvanzado(999);
            }
            jugadorActual.setPuedeComprarPropiedades(true);
                        /* if(jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov()==0){
                            hayBug=true;
                        } */
            nextJugador();
            System.out.println("Turno de: " + jugadorActual.getNombre());
            c = true;
            pagando = false;
            cobradorPendiente = banca;
        }
        return c;
    }

    public void acabarParada(){
        if (jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov() == 0 && jugadorActual.getAuxMovAvanzado() == 0) {
            System.out.println("Ya has pasado por todas tus paradas.");
        } else if (!jugadorActual.getMovAvanzadoActivado() || (jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov() == 1)) {
            System.out.println("¿Paradas? Bro think he pelota con movAvanzadoActivado");
        } else if (!(jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov() == 0))
            tablero.imprimirTablero();
    }
    public void cambiarMovimiento(boolean haTirado) {
        if (haTirado) {
            System.out.println("¡Solo puedes cambiar de movimiento al inicio de tu turno!");
        } else {
            jugadorActual.setMovAvanzadoActivado(!jugadorActual.getMovAvanzadoActivado());
            if (jugadorActual.getMovAvanzadoActivado())
                System.out.println("El movimiento avanzado está ahora activado");
            else
                System.out.println("El movimiento avanzado está ahora desactivado");
        }
    }
    public void estadisticas(){
        System.out.println("Casilla(s) mas rentables:  ");
        imprimirCasillas(casillaMasRentable());
        System.out.println("Grupo(s) mas rentables:    ");
        imprimirGrupos(grupoMasRentable(tablero.getCasillas()));
        System.out.println("Casilla(s) mas frecuentadas:   ");
        imprimirCasillas(casillaMasFrecuentada(tablero.getCasillas()));
        System.out.println("Jugador(es) con mas vueltas:   ");
        imprimirJugadores(jugadorMasVueltas(jugadores));
        System.out.println("Jugador(es) con mas tiradas de dado:   ");
        imprimirJugadores(jugadorMasDados(jugadores));
        System.out.println("Jugador(es) en cabeza:    ");
        imprimirJugadores(jugadorEnCabeza(jugadores));
    }
    public void estadisticasJugador(String[] entradaPartida){
        for (Jugador ite : jugadores) {
            if (entradaPartida[1].equals(ite.getNombre())) {
                System.out.println("Dinero invertido: " + ite.getDineroInvertido());
                System.out.println("Pago Tasas e Impuestos: " + ite.getPagoTasasEImpuestos());
                System.out.println("Pago De Alquileres: " + ite.getPagoDeAlquileres());
                System.out.println("Cobro de Alquileres: " + ite.getCobroDeAlquileres());
                System.out.println("Dinero por pasar por la casilla de salida:" + ite.getPasarPorCasillaDeSalida());
                System.out.println("Premios por inversiones o bote: " + ite.getPremiosInversionesOBote());
                System.out.println("Veces en la carcel: " + ite.getVecesEnLaCarcel());
                break;
            }
        }
    }


    public boolean menuAccion(boolean haTirado) { //Lee y llama a la acción indicada sobre el jugadorActual
        //Comprobaciones/actualizaciones en cada turno
        actualizarPropietarioCasillas();
        // System.out.printf("\nAuxiliar es %d\n", jugadorActual.getAuxMovAvanzado());

        System.out.print("Introduce una acción. Puedes escribir \"ayuda\" para obtener un listado de acciones.\n $>");

        Scanner entrada = new Scanner(System.in);
        String entradaString = entrada.nextLine();
        String[] entradaPartida = entradaString.split(" ");

        switch (entradaPartida[0]) { //Si no reconoce lo introducido, no llamamos a nada, se llama después del switch
            //case "numero": System.out.println(jugadores.size());break;
            case "ayuda":
            case "Ayuda":
                ayuda(haTirado);
                break;
            case "jugador":
            case "Jugador":
                jugador();
            case "listar":
                if (entradaPartida.length > 1) switch (entradaPartida[1]) {
                    case "Jugadores":
                    case "jugadores":
                        listarJugadores();
                        break;
                    case "en":
                    case "En":
                        if (entradaPartida.length > 2 && (entradaPartida[2].equals("venta") || entradaPartida[2].equals("Venta"))) {
                            listarEnVenta();
                        }
                        break;
                    case "Avatares":
                    case "avatares":
                        listarAvatares();
                        break;
                    case "Edificios":
                    case "edificios":
                        if (entradaPartida.length > 2) {
                            listarEdificiosColor(entradaPartida);
                        } else listarEdificios();
                        break;
                }
                break;
            case "hipotecar":
            case "Hipotecar":
                hipotecar(entradaPartida);
                break;
            case "deshipotecar":
            case "Deshipotecar":
                deshipotecar(entradaPartida);
                break;
            case "comprar":
                comprar(entradaPartida, haTirado);
                break;
            case "salir":
                if (jugadorActual.getBancarrota()) {
                    System.out.println("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
                    break;
                }
                if (entradaPartida.length > 1)
                    if (entradaPartida[1].equals("carcel") || entradaPartida[1].equals("cárcel")) {
                        salirCarcel();
                    }
                break;
            case "ver":
                if (entradaPartida[1].equals("tablero")) {
                    imprimirTablero();
                }
                break;
            case "tirar":
            case "lanzar":
                if (jugadorActual.getBancarrota()) {
                    System.out.println("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
                    break;
                }
                if (entradaPartida[1].equals("dados")) {

                    // EN PRIMER LUGAR, COMPROBAMOS SI EL JUGADOR ESTÁ EN LA CÁRCEL Y EJECUTAMOS LAS ACCIONES CORRESPONDIENTES

                    if (jugadorActual.inCarcel()) {
                        if (jugadorActual.getTurnosCarcel() == 1) {
                            //En el último turno de carcel, tiene que pagar, y no juega, no puede tirar más veces
                            System.out.println("Ya no puedes tirar más veces los dados :(");
                            return menuAccion(false);
                        }

                        if (jugadorActual.getTurnosCarcel() > 1) {
                            System.out.println("Te quedan " + (jugadorActual.getTurnosCarcel() - 1) + " oportunidades para salir de la cárcel tirando los dados. ¡Adelante!");
                            if (entradaPartida.length == 5 && entradaPartida[2].equals("trucados")) {
                                dado.tirarDados(Integer.parseInt(entradaPartida[3]), Integer.parseInt(entradaPartida[4]));
                                jugadorActual.addVecesDados();
                            } else {
                                dado.tirarDados();
                                jugadorActual.addVecesDados();
                            }
                            if (dado.areEqual()) {
                                System.out.println("¡Dados dobles! Enhorabuena, sales de la cárcel :)");
                                return menuAccion(true);//t/f
                            } else {
                                System.out.println("Oh no... No has sacado dados dobles. Te quedas en la cárcel.");
                                jugadorActual.setTurnosCarcel(jugadorActual.getTurnosCarcel() - 1);
                                return menuAccion(true);
                            }
                        }
                        break;

                    } else { // SI NO ESTÁ EN LA CÁRCEL...

                        // 1. NO LE DEJAMOS TIRAR SI TIENE EL MOTOR ROTO
                        if (jugadorActual.getTipoMov() == 1 && jugadorActual.getAuxMovAvanzado() < 0) { // importante no poner movAuxActivado para evitar exploit de que el usuario reinicie los turnos sin poder moverse mediante la introducción de cambiar movimiento
                            System.out.println("Tu motor sigue roto. ¡No puedes moverte!");
                            return menuAccion(true);
                        }

                        // 2. HACEMOS RETURN EN LOS CASOS EN LOS QUE *NO* PUEDE LANZAR DADOS

                        if (!puedeTirarOtraVez(haTirado)) {
                            System.out.println("¡No puedes volver a tirar los dados!");
                            return menuAccion(true);
                        }

                        // SI LLEGAMOS HASTA AQUÍ, EL JUGADOR PUEDE TIRAR. PROCEDEMOS A TIRAR:

                        if (entradaPartida.length == 5 && entradaPartida[2].equals("trucados")) {
                            dado.tirarDados(Integer.parseInt(entradaPartida[3]), Integer.parseInt(entradaPartida[4]));
                            jugadorActual.addVecesDados();
                        } else {
                            dado.tirarDados();
                            jugadorActual.addVecesDados();
                        }
                        System.out.println("Has sacado un " + dado.getDado1() + " y un " + dado.getDado2());

                        // REALIZAMOS LA ACCIÓN CORRESPONDIENTE SI SE SACA DOBLES:

                        if (dado.areEqual()) {
                            if (jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov() == 1) { // Si es coche, solo importan los dobles en la última tirada.
                                if (jugadorActual.getAuxMovAvanzado() == 0)
                                    System.out.println("Has sacado dobles! Puedes volver a tirar.");
                            } else { // De ser de otro modo, siempre importan los dobles, así que imprimimos el mensaje correspondiente.
                                System.out.println("Has sacado dobles! Puedes volver a tirar.");
                            }
                            if (jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov() == 0 && jugadorActual.getAuxMovAvanzado() == 999) {
                                System.out.println("Como el movimiento avanzado está activado, la tirada extra de dados será cuando pases por todas las paradas.");
                            }
                        }

                        // DESPUÉS DE TIRAR, AVANZAMOS EN MOVIMIENTO NORMAL O AVANZADO, SEGÚN CORRESPONDA:

                        // Primero, mandamos a la cárcel si corresponde
                        if (dado.getC() == 3) {
                            System.out.println("Has sacado dados dobles 3 veces seguidas. ¡Vas a la cárcel! ");
                            jugadorActual.enviarCarcel(tablero.getCasillas());
                            if (!jugadorActual.getMovAvanzadoActivado() || (jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov() == 1))
                                tablero.imprimirTablero();
                            return menuAccion(true);
                        }
                        // Si el Movimiento Avanzado está activado...
                        if (jugadorActual.getMovAvanzadoActivado()) {
                            if (jugadorActual.getTipoMov() == 0) { // MOVIMIENTO AVANZADO "PELOTA" ------------------------------------------------------------------------------
                                // Si es el primer turno
                                // Si no es el primer turno y puede tirar, significa que ha sacado dobles. No obstante, tira en movimiento simple.
                                avanzarCasillas(dado.getSuma(), jugadorActual.getAuxMovAvanzado() == 999);
                            }
                            if (jugadorActual.getTipoMov() == 1) { // MOVIMIENTO AVANZADO "COCHE" ------------------------------------------------------------------------------
                                if (jugadorActual.getAuxMovAvanzado() == 999) { // Si es el primer turno
                                    avanzarCasillas(dado.getSuma(), true);
                                } else { // Si no es el primer turno...
                                    if (jugadorActual.getAuxMovAvanzado() == 0) { // Si ya acabó sus tiradas extra y puede tirar, significa que ha sacado dobles. Nos movemos en modo simple sin restar auxMovAvanzado.
                                        avanzarCasillas(dado.getSuma(), false);
                                    } else { // Si no acabó sus tiradas extra
                                        if (dado.getSuma() > 3) {
                                            jugadorActual.setAuxMovAvanzado(jugadorActual.getAuxMovAvanzado() - 1); // Restamos 1 a las tiradas extra
                                            avanzarCasillas(dado.getSuma(), false);
                                            // Avisamos de cuántas tiradas extra le quedan
                                            if (jugadorActual.getAuxMovAvanzado() > 0) {
                                                System.out.printf("\nLanzamientos extra restantes: %d. Cuando saques menos de un 4, no podrás tirar de nuevo.\n", jugadorActual.getAuxMovAvanzado());
                                            } else {
                                                System.out.print("\nNo te quedan más lanzamientos extra por el movimiento especial.\n");
                                                if (dado.areEqual()) {
                                                    System.out.print("\nPero como has sacado dobles... ¡Tienes otra tirada extra!\n");
                                                }
                                            }
                                        } else { // Si saca menos de 4, no se mueve y además no tiene más tiradas extra.
                                            jugadorActual.setAuxMovAvanzado(0);
                                            System.out.println("\nHas sacado menos de un 4, por lo que no tienes más lanzamientos extra.");
                                            if (dado.areEqual()) {
                                                System.out.print("\nPero como has sacado dobles... ¡Tienes otra tirada más!\n");
                                            }
                                        }
                                    }
                                }
                            }
                        } else { // Si el movimiento avanzado no está activado
                            avanzarCasillas(dado.getSuma(), false);
                        }
                        tablero.imprimirTablero();
                        accionCasilla();
                        return menuAccion(true);
                    } // Finalización del "if" "¿Está en la cárcel?"
                }
                break;
            case "cambiar":
                if (entradaPartida.length > 1 && entradaPartida[1].equals("movimiento")) {
                    cambiarMovimiento(haTirado);
                }
                break;
            case "describir":
            case "descripción":
            case "descripcion":
                if (entradaPartida.length > 1) switch (entradaPartida[1]) {
                    case "jugador":
                    case "Jugador":
                        describirJugador(entradaPartida);
                        break;
                    case "avatar":
                    case "Avatar":
                        describirAvatar(entradaPartida);
                        break;
                    default:
                        describirCasilla(entradaPartida[1]);
                }
            case "fin":
                if (entradaPartida.length > 1 && entradaPartida[1].equals("partida")) return false;
                break;
            case "pagar":
                if (jugadorActual.getBancarrota()) {
                    System.out.println("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
                    break;
                }
                pagarDeuda();
                break;
            case "acabar":
                if (entradaPartida.length > 1 && entradaPartida[1].equals("turno")) {
                    if (acabarTurno(haTirado))
                        return true;
                }
                if (entradaPartida.length > 1 && entradaPartida[1].equals("parada")) {
                    acabarParada();
                }
                break;
            case "bancarrota":
            case "Bancarrota":
                if (jugadorActual.getBancarrota()) {
                    System.out.println("No puedes ejecutar esta acción estando en bancarrota, tristemente la partida se ha acabado para ti.");
                    break;
                }
                bancarrota();
                break;
            case "estadisticas":
            case "estadísticas":
                if (entradaPartida.length > 1) {
                    estadisticasJugador(entradaPartida);
                } else {
                    estadisticas();
                }
                break;
            case "edificar":
            case "construir":
                if (jugadorActual.getBancarrota()) {
                    System.out.println("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
                    break;
                }
                edificar(entradaPartida);
                break;
            case "vender":
            case "Vender":
                if (jugadorActual.getBancarrota()) {
                    System.out.println("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
                    break;
                }
                venderEdificio(entradaPartida);
                break;
            default:
                System.out.println("No se reconoce la acción... Introduce 'ayuda' para ver tus opciones.");
        }
        return menuAccion(haTirado);
    }

    private void pagarAv(Jugador pagador, int importe) {
        pagarAv(pagador, importe, banca);
    }

    private void pagarAv(Jugador pagador, int importe, Jugador cobrador) {
        while (!pagador.pagar(importe, cobrador) && !pagador.getBancarrota()) {
            System.out.println("No tienes suficiente para pagar tu deuda, debes vender o hipotecar propiedades... ");
            cobradorPendiente = cobrador;
            pagoPendiente = importe;
            gestInsuf();
        }
        cobradorPendiente = banca;
        pagando = false;
    }

    /**
     * Gestión de dinero insuficiente. Llama a menú solo con opciones de vender, hipotecar e información...
     */
    private void gestInsuf() {
        pagando = true;
        menuAccion(true);
    }

    private void actualizarPropietarioCasillas() {
        for (Casilla ite : tablero.getCasillas()) {
            if (ite instanceof Propiedad)
                if (((Propiedad) ite).getPropietario() == null) ((Propiedad) ite).setPropietario(banca);
        }
    }

    public void turnoInicial() {
        int cont = 0; //Para controlar el numero de jugadores inicializados
        Scanner entrada = new Scanner(System.in);
        System.out.println("Bienvenidos. Empezaremos definiendo los jugadores.");
        String entradaString;

        do { //J1
            System.out.println("Introduce crear jugador para darte de alta: ");
            entradaString = entrada.nextLine();
        } while (!entradaString.contains("crear jugador"));
        darAlta();
        System.out.println("¡Primer jugador registrado!");

        //entrada = new Scanner(System.in);
        do { //J2
            System.out.println("Introduce crear jugador para darte de alta: ");
            entradaString = entrada.nextLine();
        } while (!entradaString.contains("crear jugador"));
        darAlta();
        System.out.println("¡Segundo jugador registrado!");

        do {
            do { //Jx
                System.out.println("Ahora puedes introducir 'crear jugador' para darte de alta o 'empezar juego' para comenzar a jugar: ");
                entradaString = entrada.nextLine();
            } while (!entradaString.contains("crear jugador") && !entradaString.contains("empezar juego"));

            if (entradaString.contains("crear jugador")) {
                darAlta();
                cont++;
                System.out.println("¡Jugador registrado!");
            }

        } while (!entradaString.contains("empezar juego") && cont < 4);
        if (cont == 4) System.out.println("No se pueden agregar más jugadores");
        System.out.println("¡Jugadores registrados!");

        jugadorActual = jugadores.get(0);

        tablero.getCasilla(0).setOcupantes(jugadores);

        // Inicializar el contador para cada jugador y cada casilla
        for (Jugador jugador : jugadores) {
            contadorCasillas.put(jugador, new HashMap<>());
            for (Casilla casilla : tablero.getCasillas()) {
                contadorCasillas.get(jugador).put(casilla, 0);
            }
        }
    }

    public void addVuelta(Jugador jugador) {
        jugador.setVueltas(jugador.getVueltas() + 1);
        for (Jugador ite : jugadores) { //Comprobamos que sea el jugador que va de último
            if (jugador.getVueltas() > ite.getVueltas()) return;
        }
        if (jugador.getVueltas() % 4 == 0 && jugador.getVueltas() != 0) //Y que comienza una vuelta múltiplo de 4
            actualizarPrecioCasillas();
    }

    private void actualizarPrecioCasillas() {
        for (Casilla ite : getTablero().getCasillas())
            if (ite instanceof Propiedad)
                if (((Propiedad) ite).getPropietario().isBanca()) {
                    ((Propiedad) ite).setPrecio((int) (((Propiedad) ite).getPrecio() * 1.05));
                }
    }

    private ArrayList<Casilla> casillaMasRentable() {
        ArrayList<Casilla> casillas = tablero.getCasillas();
        int maximo = 0;
        for (Casilla ite : casillas) {
            if (ite instanceof Propiedad)
                if (((Propiedad) ite).getRentabilidad() > maximo)
                    maximo = ((Propiedad) ite).getRentabilidad();
        }

        ArrayList<Casilla> casillasConMaximo = new ArrayList<>();
        for (Casilla ite : casillas) {
            if (ite instanceof Propiedad)
                if (((Propiedad) ite).getRentabilidad() == maximo)
                    casillasConMaximo.add(ite);
        }
        return casillasConMaximo;
    }

    private void imprimirCasillas(ArrayList<Casilla> casillasEstad) {
        for (Casilla ite : casillasEstad)
            System.out.print(ite.getNombre() + ", ");
        System.out.println("\n");
    }

    private void imprimirGrupos(ArrayList<Grupo> gruposEstad) {
        for (Grupo ite : gruposEstad)
            System.out.print(ite.getColor() + ", ");
        System.out.println("\n");
    }

    private ArrayList<Grupo> grupoMasRentable(ArrayList<Casilla> tablero) {
        int maximo = 0;
        for (Casilla ite : tablero) {
            if (ite instanceof Solar) {
                if (((Solar) ite).getGrupo().getRentabilidad() > maximo)
                    maximo = ((Solar) ite).getGrupo().getRentabilidad();
            }
        }
        ArrayList<Grupo> gruposConMaximo = new ArrayList<>();
        for (Casilla ite : tablero) {
            if (ite instanceof Solar) {
                if (((Solar) ite).getGrupo().getRentabilidad() == maximo && !gruposConMaximo.contains(((Solar) ite).getGrupo()))
                    gruposConMaximo.add(((Solar) ite).getGrupo());
            }
        }
        return gruposConMaximo;
    }

    private ArrayList<Casilla> casillaMasFrecuentada(ArrayList<Casilla> tablero) {
        int maximo = tablero.get(0).getVisitas();
        for (Casilla ite : tablero) {
            if (ite.getVisitas() > maximo) {
                maximo = ite.getVisitas();
            }
        }
        ArrayList<Casilla> casillasConMaximo = new ArrayList<>();
        for (Casilla ite : tablero) {
            if (ite.getVisitas() == maximo) {
                casillasConMaximo.add(ite);
            }
        }
        return casillasConMaximo;
    }

    private ArrayList<Jugador> jugadorMasVueltas(ArrayList<Jugador> jugadores) {
        int maximo = jugadores.get(0).getVueltas();
        for (Jugador ite : jugadores) {
            if (ite.getVueltas() > maximo) {
                maximo = ite.getVueltas();
            }
        }
        ArrayList<Jugador> jugadoresConMaximo = new ArrayList<>();
        for (Jugador ite : jugadores) {
            if (ite.getVueltas() == maximo) {
                jugadoresConMaximo.add(ite);
            }
        }
        return jugadoresConMaximo;
    }

    private ArrayList<Jugador> jugadorMasDados(ArrayList<Jugador> jugadores) {
        int maximo = jugadores.get(0).getVecesDados();
        for (Jugador ite : jugadores) {
            if (ite.getVecesDados() > maximo) {
                maximo = ite.getVecesDados();
            }
        }
        ArrayList<Jugador> jugadoresConMaximo = new ArrayList<>();
        for (Jugador ite : jugadores) {
            if (ite.getVecesDados() == maximo) {
                jugadoresConMaximo.add(ite);
            }
        }
        return jugadoresConMaximo;
    }

    private ArrayList<Jugador> jugadorEnCabeza(ArrayList<Jugador> jugadores) {
        int maximo = jugadores.get(0).getFortuna();
        for (Jugador ite : jugadores) {
            if (ite.getFortuna() > maximo) {
                maximo = ite.getFortuna();
            }
        }
        ArrayList<Jugador> jugadoresConMaximo = new ArrayList<>();
        for (Jugador ite : jugadores) {
            if (ite.getFortuna() == maximo) {
                jugadoresConMaximo.add(ite);
            }
        }
        return jugadoresConMaximo;
    }

    private void imprimirJugadores(ArrayList<Jugador> jugadoresEstad) {
        for (Jugador ite : jugadoresEstad)
            System.out.print(ite.getNombre() + ", ");
        System.out.println("\n");
    }

    private boolean puedeTirarOtraVez(boolean haTirado) {
        if (jugadorActual.getMovAvanzadoActivado()) { // SI ESTÁ EL MOVIMIENTO AVANZADO ACTIVADO, DISTINGUIMOS ENTRE "PELOTA" Y "COCHE"
            if (jugadorActual.getTipoMov() == 0) { // MOVIMIENTO AVANZADO "PELOTA" ------------------------------------------------------------------------------
                if (jugadorActual.getAuxMovAvanzado() == 1) { // Si le quedan paradas sin recorrer, no puede tirar
                    return false;
                }
                if (jugadorActual.getAuxMovAvanzado() == 0 && !dado.areEqual()) { // Si no le quedan paradas y tampoco sacó dobles, no puede tirar
                    return false;
                }
            }

            if (jugadorActual.getTipoMov() == 1) { // MOVIMIENTO AVANZADO "COCHE" ------------------------------------------------------------------------------
                if (jugadorActual.getAuxMovAvanzado() == 0 && !dado.areEqual()) { // Si se le acabaron las tiradas extra y no sacó dobles en la anterior, no puede tirar
                    return false;
                }
                // Motor roto
                return jugadorActual.getTipoMov() != 1 || jugadorActual.getAuxMovAvanzado() >= 0;
            }
        } else { // SI NO ESTÁ ACTIVADO EL MOV AVANZADO...
            return !haTirado || dado.areEqual();
        }
        return true; // En el resto de los casos, el jugador SÍ puede tirar.
    }
}

