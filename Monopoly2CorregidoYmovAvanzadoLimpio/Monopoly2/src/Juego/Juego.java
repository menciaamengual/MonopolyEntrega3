package Juego;

import java.util.*;

import Juego.Exceptions.*;
import Juego.Exceptions.Comandos.*;
import Juego.Exceptions.Lectura.*;
import Juego.Exceptions.Hipotecar.*;
import Juego.Exceptions.Comprar.*;
import Procesos.*;
import Procesos.Casillas.*;
import Procesos.Avatares.*;

public final class Juego implements Comando{
    private static ConsolaNormal consolaNormal;
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
    private final CartaSuerte cartaSuerte;
    private final CartaCajaC cartaCajaC;
    private final int precioCarcel;
    private static boolean pagando; //Un booleano que se activa si el usuario está en trámites de pagar una deuda (para darle opciones de vender, hipotecar, etc)...
    private static int pagoPendiente;
    private static Jugador cobradorPendiente;
    private final StringBuilder avataresRep = new StringBuilder("!");

    Map<Jugador, Map<Casilla, Integer>> contadorCasillas = new HashMap<>();

    public static ConsolaNormal getConsolaNormal() {
        return consolaNormal;
    }

    private char generaCharRandom() {
        Random randomC = new Random();
        return (char) (randomC.nextInt(26) + 'A');
    }

    //CONSTRUCTORES
    public Juego() {
        jugadores = new ArrayList<>(6); //La banca se maneja como un jugador externo (banca).
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
        consolaNormal = new ConsolaNormal();
        cartaSuerte= new CartaSuerte();
        cartaCajaC= new CartaCajaC();
    }

    //METODOS PUBLICOS
    public void darAlta(String nombre,int tipoMov){
        char miAvatar = generaAvatar();

        Avatar avatar;
        if (tipoMov == 0) {
            avatar = new Coche(miAvatar);
        } else {
            avatar = new Pelota(miAvatar);
        }
        Jugador j = new Jugador(dineroInicial, nombre, avatar);
        avatar.setJugador(j);
        jugadores.add(j);
        for (Jugador ite : jugadores)
            ite.setFortuna(dineroInicial);
    }

    public void darAlta() {
        String miNombre = consolaNormal.leer("Introduce tu nombre, Jugador " + (jugadores.size() + 1) + " : \n");
        char miAvatar = generaAvatar();
        String inputTipo;
        do {
            inputTipo = consolaNormal.leer("Elige tipo de ficha (coche o pelota): ");
            if (inputTipo.equalsIgnoreCase("coche")) {
                Coche coche = new Coche(miAvatar);
                Jugador jugador = new Jugador(dineroInicial, miNombre, coche);
                coche.setJugador(jugador); // Establecer la relación inversa
                jugadores.add(jugador);
                break;
            }
            else if (inputTipo.equalsIgnoreCase("pelota")) {
                Pelota pelota = new Pelota(miAvatar);
                Jugador jugador = new Jugador(dineroInicial, miNombre, pelota);
                pelota.setJugador(jugador); // Establecer la relación inversa
                jugadores.add(jugador);
                break;
            }
            else {
                consolaNormal.imprimir("Tipo no válido. Por favor, introduce coche o pelota.");
            }
        } while (true);
        for (Jugador ite : jugadores)
            ite.setFortuna(dineroInicial);
    }

    private char generaAvatar() {
        char miavatar;
        do {
            miavatar = generaCharRandom();
        } while (avataresRep.toString().contains(String.valueOf(miavatar)));
        avataresRep.append(miavatar);
        return miavatar;
    }

    //GETTERS

    public int getPSalida(){
        return pSalida;
    }

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
    private void avanzarCasillas(int avance, boolean movAvanzado) throws LeerException {
        if (!movAvanzado) {
            if (avance >= 0) { // Funcionamiento normal (realmente podríamos poner "if getposicion+avance >0" pero así queda más legible)
                if ((jugadorActual.getAvatar().getPosicion() + avance) > 39) {
                    addVuelta(jugadorActual);
                    consolaNormal.imprimir("Pasas por la casilla de salida y cobras " + pSalida + "$");
                    jugadorActual.setDinero(jugadorActual.getDinero() + pSalida);
                    jugadorActual.getAvatar().setPosicion((jugadorActual.getAvatar().getPosicion() + avance) - 40, tablero.getCasillas());
                } else {
                    jugadorActual.getAvatar().setPosicion(jugadorActual.getAvatar().getPosicion() + avance, tablero.getCasillas());
                }
            } else { // Avance negativo
                if ((jugadorActual.getAvatar().getPosicion() + avance) <= 0) { // Si se pasa por la casilla de salida
                    jugadorActual.setVueltas(jugadorActual.getVueltas() - 1); // Se resta una vuelta
                    consolaNormal.imprimir("Pasas por la casilla de salida en sentido contrario y pagas " + pSalida + "$");
                    pagarAv(jugadorActual, pSalida);
                    jugadorActual.getAvatar().setPosicion((jugadorActual.getAvatar().getPosicion() + avance) + 40, tablero.getCasillas());
                } else {
                    jugadorActual.getAvatar().setPosicion(jugadorActual.getAvatar().getPosicion() + avance, tablero.getCasillas());
                }
            }
        } else { // Movimiento avanzado activado
            if (jugadorActual.getAvatar() instanceof Pelota) { // PELOTA
                jugadorActual.getAvatar().setAuxMovAvanzado(1); // Indica que el turno aún está en curso
                if (avance > 4) {
                    avanzarCasillas(4, false);
                    int aux = jugadorActual.getAvatar().getPosicion(); // guardamos la posición tras avanzar 4 casillas para comprobar paridades
                    for (int i = 5; i <= avance; i++) {
                        avanzarCasillas(1, false); // avanzamos una casilla
                        if (jugadorActual.getAvatar().getPosicion() == 30) { // Si cae en la casilla de ir a la cárcel, se acaba la función. Recordemos que accionCasilla y menuAccion de la última parada/casilla se ejecutan fuera de aquí.
                            return;
                        }
                        if (i == avance) {
                            jugadorActual.getAvatar().setAuxMovAvanzado(0);
                            consolaNormal.imprimir("Ya has pasado por todas tus paradas.");
                            return;
                        }
                        if ((jugadorActual.getAvatar().getPosicion() - aux) % 2 != 0) { // Si es impar, se para en la casilla, se ejecuta la accion correspondiente y el jugador puede interactuar
                            consolaNormal.imprimir("¡Momento de pararse! Introduce \"acabar parada\" para avanzar a la siguiente casilla");
                            tablero.imprimirTablero();
                            accionCasilla();
                            if (jugadorActual.getAvatar().inCarcel()) {
                                break;
                            }
                            menuAccion(true);
                            // menuAccion(true);
                        }
                        // Si es par, no se hace nada.
                    }
                } else {
                    int aux = jugadorActual.getAvatar().getPosicion(); // guardamos la posición para comprobar paridades
                    for (int i = avance; i > 0; i--) {
                        avanzarCasillas(-1, false);
                        if (jugadorActual.getAvatar().getPosicion() == 30) { // Si cae en la casilla de ir a la cárcel, se acaba la función. Recordemos que accionCasilla y menuAccion de la última parada/casilla se ejecutan fuera de aquí.
                            return;
                        }
                        if (i == 1) { // Si es la última parada
                            jugadorActual.getAvatar().setAuxMovAvanzado(0);
                            consolaNormal.imprimir("Ya has pasado por todas tus paradas.");
                            return;
                        }
                        if ((jugadorActual.getAvatar().getPosicion() - aux) % 2 != 0) { // Si es impar, se para en la casilla, se ejecuta la accion correspondiente y el jugador puede interactuar
                            consolaNormal.imprimir("¡Momentito de pararse! Introduce \"acabar parada\" para ir a la próxima casilla");
                            tablero.imprimirTablero();
                            accionCasilla();
                            if (jugadorActual.getAvatar().inCarcel()) {
                                break;
                            }
                            menuAccion(true);
                        }
                        // Si es par, no se hace nada.
                    }
                }
            } else if (jugadorActual.getAvatar() instanceof Coche) { // COCHE
                if (avance >= 4) {
                    avanzarCasillas(avance, false);
                    consolaNormal.imprimir("Puedes tirar los dados hasta 3 veces más mientras saques más de un 3.");
                    jugadorActual.getAvatar().setAuxMovAvanzado(3);
                } else {
                    avanzarCasillas(-avance, false);
                    jugadorActual.getAvatar().setAuxMovAvanzado(-3); // Los números negativos sin el "-" indicarán los turnos restantes sin poder tirar (em el turno actual, por eso se inicializa en -3 y no en -2. al acabar el turno se suma 1, con lo que al acabar el turno en el que se estropea el motor tienes -3+1 = (-)2 turnos más sin tirar)
                    consolaNormal.imprimir("Se te ha estropeado el motor y deberás estar dos turnos sin tirar mientras se arregla.");
                }
            }
        }
    }

    private void nextJugador() {//Al final de cada turno, avanzamos jugador;
        if (jugadores.indexOf(jugadorActual) == (jugadores.size() - 1))
            jugadorActual = jugadores.get(0);
        else jugadorActual = jugadores.get(jugadores.indexOf(jugadorActual) + 1);
    }

    private void accionSuerte(int numCarta) throws LeerException {
        consolaNormal.imprimir(cartaSuerte.getCartasSuerte().get(numCarta));
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
                    consolaNormal.imprimir("Como aun no tienes edificios construidos, te libras");
                } else {
                    consolaNormal.imprimir("En total, tienes que pagar" + total + "$");
                    pagarAv(jugadorActual, total);
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
                jugadorActual.getAvatar().setPosicion((jugadorActual.getAvatar().getPosicion() - 3), tablero.getCasillas());
                tablero.imprimirTablero();
                accionCasilla();
                break;

            case 4:
                int pTCercano = 5;
                switch (jugadorActual.getAvatar().getPosicion()){
                    case 7:
                        pTCercano = 15;
                        break;
                    case 22:
                        pTCercano = 25;
                        break;
                    case 36:
                        //pTCercano a 5 por defecto
                        consolaNormal.imprimir("Has pasado por la casilla de salida, cobras" + pSalida);
                        jugadorActual.addDinero(pSalida);
                        jugadorActual.setPasarPorCasillaDeSalida(jugadorActual.getPasarPorCasillaDeSalida() + pSalida);
                        addVuelta(jugadorActual);
                        break;
                }
                jugadorActual.getAvatar().setPosicion(pTCercano, tablero.getCasillas());
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
                    consolaNormal.imprimir("Pagas " + alquilersuerte + "$ por caer en " + tCercano.getNombre());
                } else if (tCercano.getHipotecado()) {
                    consolaNormal.imprimir("La casilla de transporte esta hipotecada... No pagas alquiler :)");
                }
            case 5:
                jugadorActual.addDinero(2000);
                jugadorActual.setPremiosInversionesOBote(jugadorActual.getPremiosInversionesOBote() + 2000);
                break;
        }
    }

    private void accionCajaC(int numCarta) throws LeerException {
        consolaNormal.imprimir(cartaCajaC.getCartasCajaC().get(numCarta));
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
                jugadorActual.getAvatar().setPosicion(30, tablero.getCasillas());
                accionCasilla();
                break;

            case 2:
                jugadorActual.getAvatar().setPosicion(0, tablero.getCasillas());
                jugadorActual.addDinero(pSalida);
                jugadorActual.setPasarPorCasillaDeSalida(jugadorActual.getPasarPorCasillaDeSalida() + pSalida);
                addVuelta(jugadorActual);
                break;

            case 3:
                jugadorActual.getAvatar().setPosicion(34, tablero.getCasillas());
                accionCasilla(); //un poco cutre pq no hace falta hacer la comprobacion de salida
                break;

            case 4:
                pagarAv(jugadorActual,1000);
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
    public void accionCasilla() throws LeerException {
        try {
            contadorCasillas.get(jugadorActual).put(jugadorActual.getCasilla(tablero.getCasillas()), contadorCasillas.get(jugadorActual).get(jugadorActual.getCasilla(tablero.getCasillas())) + 1);
            Casilla casilla = jugadorActual.getCasilla(tablero.getCasillas());
            if (casilla instanceof Solar) ((Solar) casilla).accionCasilla(jugadorActual);
            else if (casilla instanceof Transporte) ((Transporte) casilla).accionCasilla(jugadorActual);
            else if (casilla instanceof Servicios) ((Servicios) casilla).accionCasilla(jugadorActual, dado);
            else if (casilla instanceof Impuesto) ((Impuesto) casilla).accionCasilla(jugadorActual);
            else if (casilla instanceof ParkingGratuito) ((ParkingGratuito) casilla).accionCasilla(jugadorActual);
            else if (casilla instanceof VasALaCarcel)
                ((VasALaCarcel) casilla).accionCasilla(jugadorActual, tablero.getCasillas(), dado);
            else if (casilla instanceof Salida) ((Salida) casilla).accionCasilla();
            else if (casilla instanceof Carcel) ((Carcel) casilla).accionCasilla(jugadorActual);

            else if (casilla instanceof Suerte) {
                cartaSuerte.barajar();
                int indice = consolaNormal.leerInt("Elige una carta (introduciendo un numero del 1 al 6)");
                int numCarta = (cartaSuerte.getCartas().get(indice - 1));
                accionSuerte(numCarta);
            } else if (casilla instanceof Comunidad) {
                cartaCajaC.barajar();
                int indice = consolaNormal.leerInt("Elige una carta (introduciendo un numero del 1 al 6)");
                int numCarta = (cartaCajaC.getCartas().get(indice - 1));
                accionCajaC(numCarta);
            }
        }
        catch(AlquilerDineroInsufException a){
            consolaNormal.imprimir("No tienes suficiente para pagar tu deuda, debes vender o hipotecar propiedades... ");
            cobradorPendiente = a.getCobrador();
            pagoPendiente = a.getDineroPendiente();
            gestInsuf();
        }
    }



    /**
     * Función de compra de la casilla, con comprobación de si el jugadorActual tienen suficiente dinero
     * Realiza la transacción correspondiente entre el jugador y la banca, y mueve la propiedad de la Procesos.Casilla, y del grupo en caso de que corresponda
     */
    public boolean comprarCasilla() throws LeerException { //todo garantizar que se añade a propie
        Propiedad casilla;
        Casilla casillac = jugadorActual.getCasilla(tablero.getCasillas());
        casilla = (Propiedad) casillac;

        try{
            if (!(casillac instanceof Propiedad))
                throw new ComprarExceptionCasillaNoPropiedad();
            if (!casilla.getPropietario().isBanca() || casilla.getPropietario() == null)
                throw new ComprarExceptionCasillaConDueno();
            if (jugadorActual.getDinero() < casilla.getPrecio())
                throw new ComprarExceptionDineroInsuficiente();
            if (!casillac.getOcupantes().contains(jugadorActual))
                throw new ComprarExceptionCasillaDistinta();


        } catch(ComprarException e) {return false;}

        casilla.getPropietario().addDinero(casilla.getPrecio());
        casilla.setPropietario(jugadorActual);

        pagarAv(jugadorActual,casilla.getPrecio());
        jugadorActual.setDineroInvertido(jugadorActual.getDineroInvertido() + casilla.getPrecio());
        jugadorActual.setFortuna(jugadorActual.getFortuna() + casilla.getPrecio());

        consolaNormal.imprimir("Propiedad comprada con éxito por " + ((Propiedad) jugadorActual.getCasilla(tablero.getCasillas())).getPrecio() + "$");

        return true;
    }


    //INTERFAZ COMANDO
    /**
     * ayuda copia los condicionales de menuAcción para enseñar al jugador las opciones que tiene disponibles.
     *
     * @param haTirado si el jugadorActual ha tirado ya en este turno
     */
    public void ayuda(boolean haTirado){
        consolaNormal.imprimir("Listado de acciones");
    }
    public void imprimirTablero(){
        tablero.imprimirTablero();
    }
    public void jugador(){
        consolaNormal.imprimir("Nombre: " + jugadorActual.getNombre());
        consolaNormal.imprimir("Avatar: " + jugadorActual.getAvatar().getClass().getSimpleName());
        consolaNormal.imprimir("Dinero: " + jugadorActual.getDinero());
        consolaNormal.imprimir("Posición: " + jugadorActual.getAvatar().getPosicion());
    }
    public boolean tirarDados(boolean haTirado, String[] entradaPartida) throws LeerException {
        try{
            if (jugadorActual.getBancarrota())
                throw new ComandoBancarrotaException();
        }catch (ComandoException ce){
            return false;
        }
// EN PRIMER LUGAR, COMPROBAMOS SI EL JUGADOR ESTÁ EN LA CÁRCEL Y EJECUTAMOS LAS ACCIONES CORRESPONDIENTES
        if (jugadorActual.getAvatar().inCarcel()) {
            if (jugadorActual.getAvatar().getTurnosCarcel() == 1) {
                //En el último turno de carcel, tiene que pagar, y no juega, no puede tirar más veces
                consolaNormal.imprimir("Ya no puedes tirar más veces los dados :(");
            }
            else if (jugadorActual.getAvatar().getTurnosCarcel() > 1) {
                consolaNormal.imprimir("Te quedan " + (jugadorActual.getAvatar().getTurnosCarcel() - 1) + " oportunidades para salir de la cárcel tirando los dados. ¡Adelante!");
                if (entradaPartida.length == 5 && entradaPartida[2].equals("trucados")) {
                    dado.tirarDados(Integer.parseInt(entradaPartida[3]), Integer.parseInt(entradaPartida[4]));
                    jugadorActual.addVecesDados();
                } else {
                    dado.tirarDados();
                    jugadorActual.addVecesDados();
                }
                if (dado.areEqual()) {
                    consolaNormal.imprimir("¡Dados dobles! Enhorabuena, sales de la cárcel :)");
                    return true;
                } else {
                    consolaNormal.imprimir("Oh no... No has sacado dados dobles. Te quedas en la cárcel.");
                    jugadorActual.getAvatar().setTurnosCarcel(jugadorActual.getAvatar().getTurnosCarcel() - 1);
                    return true;
                }
            }
        } else { // SI NO ESTÁ EN LA CÁRCEL...

            // 1. NO LE DEJAMOS TIRAR SI TIENE EL MOTOR ROTO
            if (jugadorActual.getAvatar() instanceof Coche && jugadorActual.getAvatar().getAuxMovAvanzado() < 0) { // importante no poner movAuxActivado para evitar exploit de que el usuario reinicie los turnos sin poder moverse mediante la introducción de cambiar movimiento
                consolaNormal.imprimir("Tu motor sigue roto. ¡No puedes moverte!");
                return true;
            }

            // 2. HACEMOS RETURN EN LOS CASOS EN LOS QUE *NO* PUEDE LANZAR DADOS

            if (!puedeTirarOtraVez(haTirado)) {
                consolaNormal.imprimirError("¡No puedes volver a tirar los dados!");
                return true;
            }

            // SI LLEGAMOS HASTA AQUÍ, EL JUGADOR PUEDE TIRAR. PROCEDEMOS A TIRAR:

            if (entradaPartida.length == 5 && entradaPartida[2].equals("trucados")) {
                dado.tirarDados(Integer.parseInt(entradaPartida[3]), Integer.parseInt(entradaPartida[4]));
                jugadorActual.addVecesDados();
            } else {
                dado.tirarDados();
                jugadorActual.addVecesDados();
            }
            consolaNormal.imprimir("Has sacado un " + dado.getDado1() + " y un " + dado.getDado2());

            // REALIZAMOS LA ACCIÓN CORRESPONDIENTE SI SE SACA DOBLES:

            if (dado.areEqual()) {
                if (jugadorActual.getAvatar().getMovAvanzadoActivado() && jugadorActual.getAvatar() instanceof Coche) { // Si es coche, solo importan los dobles en la última tirada.
                    if (jugadorActual.getAvatar().getAuxMovAvanzado() == 0)
                        consolaNormal.imprimir("Has sacado dobles! Puedes volver a tirar.");
                } else { // De ser de otro modo, siempre importan los dobles, así que imprimimos el mensaje correspondiente.
                    consolaNormal.imprimir("Has sacado dobles! Puedes volver a tirar.");
                }
                if (jugadorActual.getAvatar().getMovAvanzadoActivado() && jugadorActual.getAvatar() instanceof Pelota && jugadorActual.getAvatar().getAuxMovAvanzado() == 999) {
                    consolaNormal.imprimir("Como el movimiento avanzado está activado, la tirada extra de dados será cuando pases por todas las paradas.");
                }
            }

            // DESPUÉS DE TIRAR, AVANZAMOS EN MOVIMIENTO NORMAL O AVANZADO, SEGÚN CORRESPONDA:

            // Primero, mandamos a la cárcel si corresponde
            if (dado.getC() == 3) {
                consolaNormal.imprimir("Has sacado dados dobles 3 veces seguidas. ¡Vas a la cárcel! ");
                jugadorActual.getAvatar().enviarCarcel(tablero.getCasillas());
                if (!jugadorActual.getAvatar().getMovAvanzadoActivado() || (jugadorActual.getAvatar().getMovAvanzadoActivado() && jugadorActual.getAvatar() instanceof Coche))
                    tablero.imprimirTablero();
                return true;
            }
            // Si el Movimiento Avanzado está activado...
            if (jugadorActual.getAvatar().getMovAvanzadoActivado()) {
                if (jugadorActual.getAvatar() instanceof Pelota) { // MOVIMIENTO AVANZADO "PELOTA" ------------------------------------------------------------------------------
                    // Si es el primer turno
                    // Si no es el primer turno y puede tirar, significa que ha sacado dobles. No obstante, tira en movimiento simple.
                    avanzarCasillas(dado.getSuma(), jugadorActual.getAvatar().getAuxMovAvanzado() == 999);
                }
                if (jugadorActual.getAvatar() instanceof Coche) { // MOVIMIENTO AVANZADO "COCHE" ------------------------------------------------------------------------------
                    if (jugadorActual.getAvatar().getAuxMovAvanzado() == 999) { // Si es el primer turno
                        avanzarCasillas(dado.getSuma(), true);
                    } else { // Si no es el primer turno...
                        if (jugadorActual.getAvatar().getAuxMovAvanzado() == 0) { // Si ya acabó sus tiradas extra y puede tirar, significa que ha sacado dobles. Nos movemos en modo simple sin restar auxMovAvanzado.
                            avanzarCasillas(dado.getSuma(), false);
                        } else { // Si no acabó sus tiradas extra
                            if (dado.getSuma() > 3) {
                                jugadorActual.getAvatar().setAuxMovAvanzado(jugadorActual.getAvatar().getAuxMovAvanzado() - 1); // Restamos 1 a las tiradas extra
                                avanzarCasillas(dado.getSuma(), false);
                                // Avisamos de cuántas tiradas extra le quedan
                                if (jugadorActual.getAvatar().getAuxMovAvanzado() > 0) {
                                    Juego.getConsolaNormal().imprimir("\nLanzamientos extra restantes: %d. Cuando saques menos de un 4, no podrás tirar de nuevo.\n" + jugadorActual.getAvatar().getAuxMovAvanzado());
                                } else {
                                    Juego.getConsolaNormal().imprimir("\nNo te quedan más lanzamientos extra por el movimiento especial.\n");
                                    if (dado.areEqual()) {
                                        Juego.getConsolaNormal().imprimir("\nPero como has sacado dobles... ¡Tienes otra tirada extra!\n");
                                    }
                                }
                            } else { // Si saca menos de 4, no se mueve y además no tiene más tiradas extra.
                                jugadorActual.getAvatar().setAuxMovAvanzado(0);
                                consolaNormal.imprimir("\nHas sacado menos de un 4, por lo que no tienes más lanzamientos extra.");
                                if (dado.areEqual()) {
                                    Juego.getConsolaNormal().imprimir("\nPero como has sacado dobles... ¡Tienes otra tirada más!\n");
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
            return true;
        } // Finalización del "if" "¿Está en la cárcel?"
        return false;
    }


    public void comprarComando(String[] entradaPartida, boolean haTirado) { //todo comprobar si funciona; PQ FUERON MAZO DE CAMBIOS
        try{
            if (jugadorActual.getBancarrota())
                throw new ComandoBancarrotaException();
            else if (!haTirado)
                throw new ComandoNoHaTiradoException();
            else if (jugadorActual.getAvatar().getMovAvanzadoActivado() && jugadorActual.getAvatar() instanceof Coche)
                if (((Coche) jugadorActual.getAvatar()).getPuedeComprarPropiedades()) throw new ComandoNoMasComprasException();
        }catch (ComandoException ce){
            return;
        }
        try {
            if (entradaPartida.length > 1 && entradaPartida[1].equals("propiedad") || entradaPartida[1].equals(jugadorActual.getCasilla(tablero.getCasillas()).getNombre())) {
                if (jugadorActual.getAvatar().getMovAvanzadoActivado() && jugadorActual.getAvatar() instanceof Coche) { // Si es coche
                    if (((Coche)jugadorActual.getAvatar()).getPuedeComprarPropiedades()) {
                        if (comprarCasilla()) {
                            consolaNormal.imprimir("No podrás comprar más propiedades, casillas, servicios o transportes hasta que acabe tu turno.");
                            ((Coche)jugadorActual.getAvatar()).setPuedeComprarPropiedades(false);
                        }
                    } else {
                        consolaNormal.imprimir("No puedes comprar más propiedades, casillas, servicios o transportes hasta que acabe tu turno.");
                    }
                } else if (!jugadorActual.getCasilla(tablero.getCasillas()).isComprable()) {
                    consolaNormal.imprimir("No puedes comprar esta propiedad");
                    //break;
                } else {
                    comprarCasilla();
                }
            } else { //Si está intentando comprar otra propiedad le avisamos...
                if (tablero.getCasilla(entradaPartida[1]) == null) throw new CasillaInexistenteException();
                else consolaNormal.imprimir("No estás en esta casilla");
            }
        }catch(ArrayIndexOutOfBoundsException | CasillaInexistenteException | LeerException ignored){

        }
    }
    public void edificar(String[] entradaPartida){
        try{
            if (jugadorActual.getBancarrota())
                throw new ComandoBancarrotaException();
        }catch (ComandoException ce){
            return;
        }
        boolean caidas;
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
                        consolaNormal.imprimir("No existe este edificio. Prueba con casa, hotel, piscina o pista :)");
                }
            else
                consolaNormal.imprimir("No estás en un solar >: (");
    }

    public void venderEdificio(String[] entradaPartida){
        try{
            if (jugadorActual.getBancarrota())
                throw new ComandoBancarrotaException();
        }catch (ComandoException ce){
            return;
        }
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
            else consolaNormal.imprimir("Identificador inválido...");
        } else consolaNormal.imprimir("Identificador inválido...");
    }
    public void hipotecar(String[] entradaPartida) throws LeerIncorrectoException {
        try{
            if (jugadorActual.getBancarrota())
                throw new ComandoBancarrotaException();
        }catch (ComandoException ce){
            return;
        }
        Casilla casilla;
        if (entradaPartida.length > 1) {
            try{
                casilla = tablero.getCasilla(entradaPartida[1]);
                if (casilla == null)
                    throw new HipotecarCasillaInexistenteException();
                else if (!(casilla instanceof Propiedad))
                    throw new HipotecarCasillanNoHipotecableException();
                else if (!((Propiedad) casilla).getPropietario().equals(jugadorActual))
                    throw new HipotecarPropiedadDeOtro();

            }catch(HipotecarException he){
                return;
            }
            ((Propiedad) casilla).hipotecar();
        }
        else throw new LeerIncorrectoException();
    }
    public void deshipotecar(String[] entradaPartida) throws LeerIncorrectoException {
        try{
            if (jugadorActual.getBancarrota())
                throw new ComandoBancarrotaException();
        }catch (ComandoException ce){
            return;
        }
        Casilla casilla;
        if (entradaPartida.length > 1) {
            try{
                casilla = tablero.getCasilla(entradaPartida[1]);
                if (casilla == null)
                    throw new HipotecarCasillaInexistenteException();
                else if (!(casilla instanceof Propiedad))
                    throw new HipotecarCasillanNoHipotecableException();
                else if (!((Propiedad) casilla).getPropietario().equals(jugadorActual))
                    throw new desHipotecarPropiedadDeOtro();

            }catch(HipotecarException he){
                return;
            }
            ((Propiedad) casilla).deshipotecar();
        }
        else throw new LeerIncorrectoException();
    }
    public void bancarrota(){
        try{
            if (jugadorActual.getBancarrota())
                throw new ComandoBancarrotaException();
        }catch (ComandoException ce){
            return;
        }
        String respuesta = consolaNormal.leer("¿Estás seguro de que quieres declararte en bancarrota?");
        if (respuesta.equals("si") || respuesta.equals("Si") || respuesta.equals("SI"))
            jugadorActual.declararBancarrota(cobradorPendiente);
    }

    public void describirJugador(String[] entradaPartida){
        boolean c = true;
        for (Jugador ite : jugadores) {
            if (entradaPartida.length > 2 && entradaPartida[2].equals(ite.getNombre())) {
                Juego.getConsolaNormal().imprimir(ite.toString());
                c = false;
            }
        }
        if (c) consolaNormal.imprimir("Vaya! No existe este jugador...");
    }
    public void describirCasilla(String s){
        if (tablero.getCasilla(s) != null) {
            Juego.getConsolaNormal().imprimir(tablero.getCasilla(s).descripcionDetallada());
        } else consolaNormal.imprimir("No hay ninguna casilla que se llame así");
    }
    public void describirAvatar(String[] entradaPartida){
        boolean c = true;
        for (Jugador ite : jugadores) {
            if (entradaPartida.length > 2 && entradaPartida[2].equals(String.valueOf(ite.getAvatar()))) {
                Juego.getConsolaNormal().imprimir("{\n" +
                        "id: " + "A" + ",\n" + //El avatar que corresponda
                        "tipo: " + "N/A" + ",\n" +
                        "casilla: " + ite.getCasilla(tablero.getCasillas()).getNombre() + ",\n" +
                        "jugador: [" + ite.getNombre() + "]\n" +
                        "}");
                c = false;
            }
        }
        if (c) consolaNormal.imprimir("Vaya! No existe este avatar...");
    }
    public void listarJugadores(){
        for (Jugador jugadore : jugadores) {
            consolaNormal.imprimir(jugadore.toString());
        }
    }
    public void listarEnVenta(){
        for (Casilla casilla : tablero.getCasillas()) {
            if (casilla.isComprable()) consolaNormal.imprimir(" " + casilla.descripcion());
        }
    }
    public void listarAvatares(){
        for (Jugador ite : jugadores) {
            consolaNormal.imprimir("{\n" +
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
                        consolaNormal.imprimir(" " + eite.getIdentificador() + " - " + eite.getCasilla());
        }
    }
    public void listarEdificiosColor(String[] entradaPartida){
        for (Grupo ite : tablero.getGrupos()) {
            if (ite.getColor().equals(entradaPartida[2])) {
                for (Solar cite : ite.getCasillas()) {
                    if (cite != null)
                        for (Edificio eite : cite.getEdificios())
                            consolaNormal.imprimir(" " + eite.getIdentificador() + " - " + eite.getCasilla());
                }
            }
        }
        consolaNormal.imprimir("No se reconoce el grupo/color");
    }
    public void salirCarcel() throws LeerException {
        try{
            if (jugadorActual.getBancarrota())
                throw new ComandoBancarrotaException();
        }catch (ComandoException ce){
            return;
        }
        if (!jugadorActual.getAvatar().inCarcel()) {
            consolaNormal.imprimir("No estas en la cárcel...");
        } else {
            consolaNormal.imprimir("Pagas " + precioCarcel + " para salir de la cárcel.");
            pagarAv(jugadorActual, precioCarcel);
            jugadorActual.setPagoTasasEImpuestos(jugadorActual.getPagoTasasEImpuestos() + precioCarcel);
            jugadorActual.getAvatar().setTurnosCarcel(0);
        }
    }
     public void pagarDeuda() throws LeerException {
        try{
            if (jugadorActual.getBancarrota())
                throw new ComandoBancarrotaException();
        }catch (ComandoException ce){
            return;
        }
        if (pagando) {
            pagarAv(jugadorActual, pagoPendiente, cobradorPendiente);
        } else consolaNormal.imprimir("No tienes nada que pagar :D");
    }
    public boolean acabarTurno(boolean haTirado){
        boolean c = false;
        if (puedeTirarOtraVez(haTirado) && !jugadorActual.getBancarrota()) { // SI PUEDE TIRAR OTRA VEZ, DEBE HACERLO. AVISAMOS.
            consolaNormal.imprimir("Tienes que tirar antes de terminar el turno");
        }
        // Sin embargo, existe un caso en el que el jugador no puede tirar, pero tampoco puede acabar el turno. Lo tratamos:
        else if (jugadorActual.getAvatar().getMovAvanzadoActivado() && jugadorActual.getAvatar() instanceof Pelota && jugadorActual.getAvatar().getAuxMovAvanzado() == 1) {
            // (Si es tipo Pelota, el movimiento avanzado está activado y aún le quedan paradas por recorrer)
            consolaNormal.imprimir("¡Aún te quedan paradas por recorrer!");
        }
        else if (jugadorActual.getAvatar().getMovAvanzadoActivado() && jugadorActual.getAvatar() instanceof Coche) { // MOVIMIENTO AVANZADO "COCHE" ------------------------------------------------------------------------------
            if (jugadorActual.getAvatar().getAuxMovAvanzado() < 0) { // SI EL JUGADOR QUE ACABA EL TURNO ES UN COCHE CON MOTOR ROTO
                jugadorActual.getAvatar().setAuxMovAvanzado(jugadorActual.getAvatar().getAuxMovAvanzado() + 1);
                if (jugadorActual.getAvatar().getAuxMovAvanzado() == 0) {
                    jugadorActual.getAvatar().setAuxMovAvanzado(999);
                    consolaNormal.imprimir("¡Motor reparado! En el siguiente turno podrás moverte.");
                } else{
                    Juego.getConsolaNormal().imprimir("\nTurnos restantes para poder moverte: \n" + -jugadorActual.getAvatar().getAuxMovAvanzado());
                    ((Coche) jugadorActual.getAvatar()).setPuedeComprarPropiedades(true);
                    // jugadorActual.setAuxMovAvanzado(999);
                    if (jugadorActual.getAvatar() instanceof Coche) {
                        Coche cocheActual = (Coche) jugadorActual.getAvatar();
                        cocheActual.setPuedeComprarPropiedades(true);
                    }
                    nextJugador();
                    consolaNormal.imprimir("Turno de: " + jugadorActual.getNombre());
                    if(jugadorActual.getTratosRecibidos()!=null){
                        consolaNormal.imprimir("Tienes los siguientes tratos propuestos: ");
                        jugadorActual.imprimirTratosRecibidos();}
                    c = true;
                }
            }
        }
        else if (pagando && !jugadorActual.getBancarrota()) {
            consolaNormal.imprimir("Debes saldar tu deuda antes de acabar el turno, o declararte en bancarrota");
        } else {
            if (!(jugadorActual.getAvatar() instanceof Coche && jugadorActual.getAvatar().getMovAvanzadoActivado() && jugadorActual.getAvatar().getAuxMovAvanzado() < 0)) {
                jugadorActual.getAvatar().setAuxMovAvanzado(999);
            }
            if (jugadorActual.getAvatar() instanceof Coche) {
                Coche cocheActual = (Coche) jugadorActual.getAvatar();
                cocheActual.setPuedeComprarPropiedades(true);
            }
                        /* if(jugadorActual.getAvatar().getMovAvanzadoActivado() && jugadorActual.getTipoMov()==0){
                            hayBug=true;
                        } */
            nextJugador();
            consolaNormal.imprimir("Turno de: " + jugadorActual.getNombre());
            if(jugadorActual.getTratosRecibidos()!=null){ //
                consolaNormal.imprimir("Tienes los siguientes tratos propuestos: ");
                jugadorActual.imprimirTratosRecibidos();}
            c = true;
            pagando = false;
            cobradorPendiente = banca;
        }
        return c;
    }

    public void cambiarMovimiento(boolean haTirado) {
        if (haTirado) {
            consolaNormal.imprimir("¡Solo puedes cambiar de movimiento al inicio de tu turno!");
        } else {
            jugadorActual.getAvatar().setMovAvanzadoActivado(!jugadorActual.getAvatar().getMovAvanzadoActivado());
            if (jugadorActual.getAvatar().getMovAvanzadoActivado())
                consolaNormal.imprimir("El movimiento avanzado está ahora activado");
            else
                consolaNormal.imprimir("El movimiento avanzado está ahora desactivado");
        }
    }

    public void estadisticas(){
        consolaNormal.imprimir("Casilla(s) mas rentables:  ");
        imprimirCasillas(casillaMasRentable());
        consolaNormal.imprimir("Grupo(s) mas rentables:    ");
        imprimirGrupos(grupoMasRentable(tablero.getCasillas()));
        consolaNormal.imprimir("Casilla(s) mas frecuentadas:   ");
        imprimirCasillas(casillaMasFrecuentada(tablero.getCasillas()));
        consolaNormal.imprimir("Jugador(es) con mas vueltas:   ");
        imprimirJugadores(jugadorMasVueltas(jugadores));
        consolaNormal.imprimir("Jugador(es) con mas tiradas de dado:   ");
        imprimirJugadores(jugadorMasDados(jugadores));
        consolaNormal.imprimir("Jugador(es) en cabeza:    ");
        imprimirJugadores(jugadorEnCabeza(jugadores));
    }
    public void estadisticasJugador(String[] entradaPartida){
        for (Jugador ite : jugadores) {
            if (entradaPartida[1].equals(ite.getNombre())) {
                consolaNormal.imprimir("Dinero invertido: " + ite.getDineroInvertido());
                consolaNormal.imprimir("Pago Tasas e Impuestos: " + ite.getPagoTasasEImpuestos());
                consolaNormal.imprimir("Pago De Alquileres: " + ite.getPagoDeAlquileres());
                consolaNormal.imprimir("Cobro de Alquileres: " + ite.getCobroDeAlquileres());
                consolaNormal.imprimir("Dinero por pasar por la casilla de salida:" + ite.getPasarPorCasillaDeSalida());
                consolaNormal.imprimir("Premios por inversiones o bote: " + ite.getPremiosInversionesOBote());
                consolaNormal.imprimir("Veces en la carcel: " + ite.getVecesEnLaCarcel());
                break;
            }
        }
    }

    public boolean menuAccion(boolean haTirado){
        try{//Lee y llama a la acción indicada sobre el jugadorActual
            //Comprobaciones/actualizaciones en cada turno
            actualizarPropietarioCasillas();
            // System.out.printf("\nAuxiliar es %d\n", jugadorActual.getAvatar().getAuxMovAvanzado());

            String[] entradaPartida = consolaNormal.leerConsolaFragmentado("Introduce una acción. Puedes escribir \"ayuda\" para obtener un listado de acciones.\n $>");

            switch (entradaPartida[0]) { //Si no reconoce lo introducido, no llamamos a nada, se llama después del switch
                //case "numero": consolaNormal.imprimir(jugadores.size());break;
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
                            else throw new LeerIncorrectoException();
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
                        case "Tratos":
                        case "tratos":

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
                    comprarComando(entradaPartida, haTirado);
                    break;
                case "salir":
                    if (entradaPartida.length > 1)
                        if (entradaPartida[1].equals("carcel") || entradaPartida[1].equals("cárcel")) {
                            salirCarcel();
                        }
                        else throw new LeerIncorrectoException();
                    break;
                case "ver":
                    if (entradaPartida[1].equals("tablero")) {
                        imprimirTablero();
                    }
                    break;
                case "tirar":
                case "lanzar":
                    try {
                        if (entradaPartida[1].equals("dados")) {
                            haTirado = tirarDados(haTirado, entradaPartida);
                        } else throw new LeerIncorrectoException();
                    }catch(ArrayIndexOutOfBoundsException a){
                        throw new LeerIncorrectoException();
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
                    edificar(entradaPartida);
                    break;
                case "vender":
                case "Vender":
                    venderEdificio(entradaPartida);
                    break;
                case "trato": //crear propuesta
                    proponerTrato(jugadorActual,tablero);
                    break;
                case "tratos": //ver todas las propuestas que te han hecho
                    jugadorActual.imprimirTratosRecibidos();
                    break;
                case "aceptar": //aceptar una propuesta
                    if (entradaPartida[1].equals("trato")) {
                        int numTrato;
                        if(jugadorActual.getTratosRecibidos()!=null) {
                            do {
                                numTrato = consolaNormal.leerInt("Introduce el numero de trato que quieres aceptar: ");
                            } while (numTrato > 0 && numTrato < jugadorActual.getTratosRecibidos().size());
                            jugadorActual.aceptarTrato(numTrato);
                        }
                        else
                            consolaNormal.imprimir("No tienes tratos pendientes.");
                    }
                    break;
                case "eliminar": //eliminar una propuesta
                    if (entradaPartida[1].equals("trato")) {
                        int numTrato;
                        if (jugadorActual.getTratosPropuestos()!=null) {
                            do {
                                numTrato = consolaNormal.leerInt("Introduce el numero de trato que quieres eliminar: ");
                            } while (numTrato > 0 && numTrato < jugadorActual.getTratosPropuestos().size());
                            jugadorActual.eliminarTrato(numTrato);
                        }
                    }
                        else
                            consolaNormal.imprimir("No tienes tratos que eliminar.");
                    break;

                default:
                    throw new LeerIncorrectoException();
            }
        }
        catch(LeerException le){
            return menuAccion(haTirado);
        }
        return menuAccion(haTirado);
    }

    public void acabarParada(){
        if (jugadorActual.getAvatar().getMovAvanzadoActivado() && jugadorActual.getAvatar() instanceof Pelota && jugadorActual.getAvatar().getAuxMovAvanzado() == 0) {
            consolaNormal.imprimir("Ya has pasado por todas tus paradas.");
        } else if (!jugadorActual.getAvatar().getMovAvanzadoActivado() || (jugadorActual.getAvatar().getMovAvanzadoActivado() && jugadorActual.getAvatar() instanceof Coche)) {
            consolaNormal.imprimir("¿Paradas? Bro think he pelota con movAvanzadoActivado");
        } else if (!(jugadorActual.getAvatar().getMovAvanzadoActivado() && jugadorActual.getAvatar() instanceof Pelota))
            tablero.imprimirTablero();
    }
    private void pagarAv(Jugador pagador, int importe) throws LeerException {
        pagarAv(pagador, importe, banca);
    }

    private void pagarAv(Jugador pagador, int importe, Jugador cobrador) throws LeerException {
        while (!pagador.pagar(importe, cobrador) && !pagador.getBancarrota()) {
            consolaNormal.imprimir("No tienes suficiente para pagar tu deuda, debes vender o hipotecar propiedades... ");
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
    private void gestInsuf() throws LeerException {
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
        consolaNormal.imprimir("Bienvenidos. Empezaremos definiendo los jugadores.");
        String entradaString;
        boolean turnoPruebas = false;
        do { //J1
            entradaString = consolaNormal.leer("Introduce crear jugador para darte de alta: \n");
            if (entradaString.equals("Prueba2J")){
                darAlta("Mateo",0);
                darAlta("Cese",1);
                turnoPruebas = true;
                break;
            }
            else if (entradaString.equals("Prueba3J")){
                darAlta("Mateo",0);
                darAlta("Cese",1);
                darAlta("Mencia",1);
                turnoPruebas = true;
                break;
            }
        } while (!entradaString.contains("crear jugador"));
        if (!turnoPruebas) darAlta();
        consolaNormal.imprimir("¡Primer jugador registrado!");
        if (!turnoPruebas) {
            do { //J2
                entradaString = consolaNormal.leer("Introduce crear jugador para darte de alta: \n");
            } while (!entradaString.contains("crear jugador"));
            darAlta();
            consolaNormal.imprimir("¡Segundo jugador registrado!");

            do {
                do { //Jx
                    entradaString = consolaNormal.leer("Ahora puedes introducir 'crear jugador' para darte de alta o 'empezar juego' para comenzar a jugar: \n");
                } while (!entradaString.contains("crear jugador") && !entradaString.contains("empezar juego"));

                if (entradaString.contains("crear jugador")) {
                    darAlta();
                    cont++;
                    consolaNormal.imprimir("¡Jugador registrado!");
                }

            } while (!entradaString.contains("empezar juego") && cont < 4);
        }
        if (cont == 4) consolaNormal.imprimir("No se pueden agregar más jugadores");
        consolaNormal.imprimir("¡Jugadores registrados!");

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
            Juego.getConsolaNormal().imprimir(ite.getNombre() + ", ");
        consolaNormal.imprimir("\n");
    }

    private void imprimirGrupos(ArrayList<Grupo> gruposEstad) {
        for (Grupo ite : gruposEstad)
            Juego.getConsolaNormal().imprimir(ite.getColor() + ", ");
        consolaNormal.imprimir("\n");
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
            Juego.getConsolaNormal().imprimir(ite.getNombre() + ", ");
        consolaNormal.imprimir("\n");
    }

    private boolean puedeTirarOtraVez(boolean haTirado) {
        if (jugadorActual.getAvatar().getMovAvanzadoActivado()) { // SI ESTÁ EL MOVIMIENTO AVANZADO ACTIVADO, DISTINGUIMOS ENTRE "PELOTA" Y "COCHE"
            if (jugadorActual.getAvatar() instanceof Pelota) { // MOVIMIENTO AVANZADO "PELOTA" ------------------------------------------------------------------------------
                if (jugadorActual.getAvatar().getAuxMovAvanzado() == 1) { // Si le quedan paradas sin recorrer, no puede tirar
                    return false;
                }
                if (jugadorActual.getAvatar().getAuxMovAvanzado() == 0 && !dado.areEqual()) { // Si no le quedan paradas y tampoco sacó dobles, no puede tirar
                    return false;
                }
            }

            if (jugadorActual.getAvatar() instanceof Coche) { // MOVIMIENTO AVANZADO "COCHE" ------------------------------------------------------------------------------
                if (jugadorActual.getAvatar().getAuxMovAvanzado() == 0 && !dado.areEqual()) { // Si se le acabaron las tiradas extra y no sacó dobles en la anterior, no puede tirar
                    return false;
                }
                // Motor roto
                return jugadorActual.getAvatar() instanceof Pelota || jugadorActual.getAvatar().getAuxMovAvanzado() >= 0;
            }
        } else { // SI NO ESTÁ ACTIVADO EL MOV AVANZADO...
            return !haTirado || dado.areEqual();
        }
        return true; // En el resto de los casos, el jugador SÍ puede tirar.
    }


    public Jugador getJugador(String nombre) { // DADO UN NOMBRE, DEVUELVE EL JUGADOR CON ESE NOMBRE
        for (Jugador jugador : jugadores) {
            if (jugador.getNombre().equals(nombre)) {
                return jugador;
            }
        }
        return null;
    }

    public void proponerTrato(Jugador jugadorActual, Tablero tablero){

        // Pedimos el tipo de trato
        int tipoTrato;
        do {
            consolaNormal.imprimir("Los tipos de trato que puedes proponer son los siguientes:");
            consolaNormal.imprimir(" 0. Cambiar una propiedad por otra");
            consolaNormal.imprimir(" 1. Vender una propiedad");
            consolaNormal.imprimir(" 2. Comprar una propiedad");
            consolaNormal.imprimir(" 3. Cambiar una propiedad por otra y pedir dinero");
            consolaNormal.imprimir(" 4. Cambiar una propiedad por otra y ofrecer dinero");
            consolaNormal.imprimir(" 5. Cambiar una propiedad por otra, y no pagar alquiler en otra propiedad de tu elección durante un número de turnos");
            tipoTrato = consolaNormal.leerInt("Introduce el tipo de trato que quieres proponer (0-5) " + " : ");
            if(tipoTrato > -1 && tipoTrato <6){
                break;
            }
        } while (true);

        // Pedimos el nombre del jugador al que se le va a proponer el trato

        String nombreReceptor = Juego.getConsolaNormal().leer("Introduce el nombre del jugador a quien quieres proponer el trato" + " : ");
        if (getJugador(nombreReceptor) == null){
            Juego.getConsolaNormal().imprimir("De momento, ese tal " + nombreReceptor + " no está disfrutando de nuestro increíble Monopoly...");
            return;
        }

        // Pedimos los datos necesarios para cada tipo de trato

        String nombrePropOfrecida;
        String nombrePropSolicitada;
        String nombrePropExenta;
        int dineroOfrecido;
        int dineroSolicitado;
        int turnosExento;

        switch (tipoTrato){
            case 0:

                // Pedimos la propiedad ofrecida

                nombrePropOfrecida = Juego.getConsolaNormal().leer("Nombre de la propiedad que quieres ofrecer" + " : ");
                if (!(tablero.getCasilla(nombrePropOfrecida) instanceof Propiedad) || tablero.getCasilla(nombrePropOfrecida)==null){ // Si lo introducido NO es una propiedad o es una casilla inexistente...
                    Juego.getConsolaNormal().imprimir("Eso no es una propiedad.");
                    return;
                }
                if(!(((Propiedad) tablero.getCasilla(nombrePropOfrecida)).getPropietario().equals(jugadorActual))){ // Si el jugador que propone el trato no es propietario...
                    Juego.getConsolaNormal().imprimir("Okupa, ¡esa propiedad no es tuya!");
                    return;
                }

                // Pedimos la propiedad solicitada

                nombrePropSolicitada = Juego.getConsolaNormal().leer("Nombre de la propiedad que quieres solicitar" + " : ");
                if (!(tablero.getCasilla(nombrePropSolicitada) instanceof Propiedad) || tablero.getCasilla(nombrePropSolicitada)==null){ // Si lo introducido NO es una propiedad o es una casilla inexistente...
                    Juego.getConsolaNormal().imprimir("Eso no es una propiedad.");
                    return;
                }
                if(!(((Propiedad) tablero.getCasilla(nombrePropSolicitada)).getPropietario().equals(getJugador(nombreReceptor)))){ // Si el jugador al que se le propone el trato no es propietario de dicha propiedad
                    Juego.getConsolaNormal().imprimir(nombreReceptor + " no es el propietario de " + nombrePropSolicitada);
                    return;
                }

                // Ahora que ya tenemos lo necesario, llamamos al constructor

                jugadorActual.addTratoPropuesto(new Trato(jugadorActual, getJugador(nombreReceptor), (Propiedad) tablero.getCasilla(nombrePropOfrecida), (Propiedad) tablero.getCasilla(nombrePropSolicitada)));
                getJugador(nombreReceptor).addTratoRecibido(new Trato(jugadorActual, getJugador(nombreReceptor), (Propiedad) tablero.getCasilla(nombrePropOfrecida), (Propiedad) tablero.getCasilla(nombrePropSolicitada)));
                break;
            case 1:

                // Pedimos la propiedad ofrecida

                nombrePropOfrecida = Juego.getConsolaNormal().leer("Nombre de la propiedad que quieres vender" + " : ");
                if (!(tablero.getCasilla(nombrePropOfrecida) instanceof Propiedad) || tablero.getCasilla(nombrePropOfrecida)==null){ // Si lo introducido NO es una propiedad o es una casilla inexistente...
                    Juego.getConsolaNormal().imprimir("Eso no es una propiedad.");
                    return;
                }
                if(!(((Propiedad) tablero.getCasilla(nombrePropOfrecida)).getPropietario().equals(jugadorActual))){ // Si el jugador que propone el trato no es propietario...
                    Juego.getConsolaNormal().imprimir("Okupa, ¡esa propiedad no es tuya!");
                    return;
                }

                // Pedimos el dinero solicitado

                dineroSolicitado = Juego.getConsolaNormal().leerInt("Introduce el dinero que quieres solicitar" + " : ");
                // todo COMPROBACIÓN DE QUE ES MAYOR QUE 0 (Ya si tal...)

                // Ahora que ya tenemos lo necesario, llamamos al constructor

                jugadorActual.addTratoPropuesto(new Trato(jugadorActual, getJugador(nombreReceptor), (Propiedad) tablero.getCasilla(nombrePropOfrecida), dineroSolicitado));
                getJugador(nombreReceptor).addTratoRecibido(new Trato(jugadorActual, getJugador(nombreReceptor), (Propiedad) tablero.getCasilla(nombrePropOfrecida), dineroSolicitado));
                break;
            case 2:

                // Pedimos la propiedad solicitada

                nombrePropSolicitada = Juego.getConsolaNormal().leer("Nombre de la propiedad que quieres solicitar" + " : ");
                if (!(tablero.getCasilla(nombrePropSolicitada) instanceof Propiedad) || tablero.getCasilla(nombrePropSolicitada)==null){ // Si lo introducido NO es una propiedad o es una casilla inexistente...
                    Juego.getConsolaNormal().imprimir("Eso no es una propiedad.");
                    return;
                }
                if(!(((Propiedad) tablero.getCasilla(nombrePropSolicitada)).getPropietario().equals(getJugador(nombreReceptor)))){ // Si el jugador al que se le propone el trato no es propietario de dicha propiedad
                    Juego.getConsolaNormal().imprimir(nombreReceptor + " no es el propietario de " + nombrePropSolicitada);
                    return;
                }

                // Pedimos el dinero ofrecido

                dineroOfrecido = Juego.getConsolaNormal().leerInt("Introduce el dinero que quieres ofrecer" + " : ");

                jugadorActual.addTratoPropuesto(new Trato(jugadorActual, getJugador(nombreReceptor), dineroOfrecido, (Propiedad) tablero.getCasilla(nombrePropSolicitada)));
                getJugador(nombreReceptor).addTratoRecibido(new Trato(jugadorActual, getJugador(nombreReceptor), dineroOfrecido, (Propiedad) tablero.getCasilla(nombrePropSolicitada)));
                break;
            case 3:

                // Pedimos la propiedad ofrecida

                nombrePropOfrecida = Juego.getConsolaNormal().leer("Nombre de la propiedad que quieres ofrecer" + " : ");
                if (!(tablero.getCasilla(nombrePropOfrecida) instanceof Propiedad) || tablero.getCasilla(nombrePropOfrecida)==null){ // Si lo introducido NO es una propiedad o es una casilla inexistente...
                    Juego.getConsolaNormal().imprimir("Eso no es una propiedad.");
                    return;
                }
                if(!(((Propiedad) tablero.getCasilla(nombrePropOfrecida)).getPropietario().equals(jugadorActual))){ // Si el jugador que propone el trato no es propietario...
                    Juego.getConsolaNormal().imprimir("Okupa, ¡esa propiedad no es tuya!");
                    return;
                }

                // Pedimos la propiedad solicitada

                nombrePropSolicitada = Juego.getConsolaNormal().leer("Nombre de la propiedad que quieres solicitar" + " : ");
                if (!(tablero.getCasilla(nombrePropSolicitada) instanceof Propiedad) || tablero.getCasilla(nombrePropSolicitada)==null){ // Si lo introducido NO es una propiedad o es una casilla inexistente...
                    Juego.getConsolaNormal().imprimir("Eso no es una propiedad.");
                    return;
                }
                if(!(((Propiedad) tablero.getCasilla(nombrePropSolicitada)).getPropietario().equals(getJugador(nombreReceptor)))){ // Si el jugador al que se le propone el trato no es propietario de dicha propiedad
                    Juego.getConsolaNormal().imprimir(nombreReceptor + " no es el propietario de " + nombrePropSolicitada);
                    return;
                }

                // Pedimos el dinero solicitado

                dineroSolicitado = Juego.getConsolaNormal().leerInt("Introduce el dinero que quieres solicitar" + " : ");

                // Ahora que ya tenemos lo necesario, llamamos al constructor

                jugadorActual.addTratoPropuesto(new Trato(jugadorActual, getJugador(nombreReceptor), (Propiedad) tablero.getCasilla(nombrePropOfrecida), (Propiedad) tablero.getCasilla(nombrePropSolicitada), dineroSolicitado));
                getJugador(nombreReceptor).addTratoRecibido(new Trato(jugadorActual, getJugador(nombreReceptor), (Propiedad) tablero.getCasilla(nombrePropOfrecida), (Propiedad) tablero.getCasilla(nombrePropSolicitada), dineroSolicitado));
                break;
            case 4:

                // Pedimos la propiedad ofrecida

                nombrePropOfrecida = Juego.getConsolaNormal().leer("Nombre de la propiedad que quieres ofrecer" + " : ");
                if (!(tablero.getCasilla(nombrePropOfrecida) instanceof Propiedad) || tablero.getCasilla(nombrePropOfrecida)==null){ // Si lo introducido NO es una propiedad o es una casilla inexistente...
                    Juego.getConsolaNormal().imprimir("Eso no es una propiedad.");
                    return;
                }
                if(!(((Propiedad) tablero.getCasilla(nombrePropOfrecida)).getPropietario().equals(jugadorActual))){ // Si el jugador que propone el trato no es propietario...
                    Juego.getConsolaNormal().imprimir("Okupa, ¡esa propiedad no es tuya!");
                    return;
                }

                // Pedimos la propiedad solicitada

                nombrePropSolicitada = Juego.getConsolaNormal().leer("Nombre de la propiedad que quieres solicitar" + " : ");
                if (!(tablero.getCasilla(nombrePropSolicitada) instanceof Propiedad) || tablero.getCasilla(nombrePropSolicitada)==null){ // Si lo introducido NO es una propiedad o es una casilla inexistente...
                    Juego.getConsolaNormal().imprimir("Eso no es una propiedad.");
                    return;
                }
                if(!(((Propiedad) tablero.getCasilla(nombrePropSolicitada)).getPropietario().equals(getJugador(nombreReceptor)))){ // Si el jugador al que se le propone el trato no es propietario de dicha propiedad
                    Juego.getConsolaNormal().imprimir(nombreReceptor + " no es el propietario de " + nombrePropSolicitada);
                    return;
                }

                // Pedimos el dinero ofrecido

                dineroOfrecido = Juego.getConsolaNormal().leerInt("Introduce el dinero que quieres ofrecer" + " : ");

                // Ahora que ya tenemos lo necesario, llamamos al constructor

                jugadorActual.addTratoPropuesto(new Trato(jugadorActual, getJugador(nombreReceptor), (Propiedad) tablero.getCasilla(nombrePropOfrecida), dineroOfrecido, (Propiedad) tablero.getCasilla(nombrePropSolicitada)));
                getJugador(nombreReceptor).addTratoRecibido(new Trato(jugadorActual, getJugador(nombreReceptor), (Propiedad) tablero.getCasilla(nombrePropOfrecida), dineroOfrecido, (Propiedad) tablero.getCasilla(nombrePropSolicitada)));
                break;
            case 5:

                // Pedimos la propiedad ofrecida

                nombrePropOfrecida = Juego.getConsolaNormal().leer("Nombre de la propiedad que quieres ofrecer" + " : ");
                if (!(tablero.getCasilla(nombrePropOfrecida) instanceof Propiedad) || tablero.getCasilla(nombrePropOfrecida)==null){ // Si lo introducido NO es una propiedad o es una casilla inexistente...
                    Juego.getConsolaNormal().imprimir("Eso no es una propiedad.");
                    return;
                }
                if(!(((Propiedad) tablero.getCasilla(nombrePropOfrecida)).getPropietario().equals(jugadorActual))){ // Si el jugador que propone el trato no es propietario...
                    Juego.getConsolaNormal().imprimir("Okupa, ¡esa propiedad no es tuya!");
                    return;
                }

                // Pedimos la propiedad solicitada

                nombrePropSolicitada = Juego.getConsolaNormal().leer("Nombre de la propiedad que quieres solicitar" + " : ");
                if (!(tablero.getCasilla(nombrePropSolicitada) instanceof Propiedad) || tablero.getCasilla(nombrePropSolicitada)==null){ // Si lo introducido NO es una propiedad o es una casilla inexistente...
                    Juego.getConsolaNormal().imprimir("Eso no es una propiedad.");
                    return;
                }
                if(!(((Propiedad) tablero.getCasilla(nombrePropSolicitada)).getPropietario().equals(getJugador(nombreReceptor)))){ // Si el jugador al que se le propone el trato no es propietario de dicha propiedad
                    Juego.getConsolaNormal().imprimir(nombreReceptor + " no es el propietario de " + nombrePropSolicitada);
                    return;
                }

                //Pedimos la propiedad en la que desea no pagar alquiler el solicitante

                nombrePropExenta = Juego.getConsolaNormal().leer("Nombre de la propiedad en la que quieres no pagar alquiler durante cierto número de turnos" + " : ");
                if (!(tablero.getCasilla(nombrePropExenta) instanceof Propiedad) || tablero.getCasilla(nombrePropExenta)==null){ // Si lo introducido NO es una propiedad o es una casilla inexistente...
                    Juego.getConsolaNormal().imprimir("Eso no es una propiedad.");
                    return;
                }
                if(!(((Propiedad) tablero.getCasilla(nombrePropExenta)).getPropietario().equals(getJugador(nombreReceptor)))){ // Si el jugador al que se le propone el trato no es propietario de dicha propiedad
                    Juego.getConsolaNormal().imprimir(nombreReceptor + " no es el propietario de " + nombrePropExenta);
                    return;
                }

                // Pedimos el número de turnos de exención

                turnosExento = Juego.getConsolaNormal().leerInt("Introduce el número de turnos de exención que quieres solicitar" + " : ");


                // Ahora que ya tenemos lo necesario, llamamos al constructor

                jugadorActual.addTratoPropuesto(new Trato(jugadorActual, getJugador(nombreReceptor), (Propiedad) tablero.getCasilla(nombrePropOfrecida), (Propiedad) tablero.getCasilla(nombrePropSolicitada), (Propiedad) tablero.getCasilla(nombrePropExenta), turnosExento));
                getJugador(nombreReceptor).addTratoRecibido(new Trato(jugadorActual, getJugador(nombreReceptor), (Propiedad) tablero.getCasilla(nombrePropOfrecida), (Propiedad) tablero.getCasilla(nombrePropSolicitada), (Propiedad) tablero.getCasilla(nombrePropExenta), turnosExento));
        }
    }
}

