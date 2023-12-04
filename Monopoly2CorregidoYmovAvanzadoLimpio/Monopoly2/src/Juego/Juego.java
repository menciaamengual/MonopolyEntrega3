package Juego;

import java.util.*;

import Procesos.*;

public class Juego {
    //ATRIBUTOS
    private static Tablero tablero;
    private int dineroInicial;
    private int pSalida; //Premio salida
    private int pBase; //Precio casilla primer grupo
    private Jugador jugadorActual;
    private ArrayList<Jugador> jugadores;
    // private boolean hayBug; //Encontramos un error por el cual, tras la finalización del turno de un coche con movimiento avanzado, el siguiente jugador no puede tirar. Este booleano lo detectará y nos permitirá solucionarlo en menuAccion.

    private Jugador banca;
    private Dado dado;
    private Carta carta;
    private int precioCarcel;
    private boolean pagando; //Un booleano que se activa si el usuario está en trámites de pagar una deuda (para darle opciones de vender, hipotecar, etc)...
    private int pagoPendiente;
    private Jugador cobradorPendiente;
    private StringBuilder avataresrep = new StringBuilder("!");

    Map<Jugador, Map<Casilla, Integer>> contadorCasillas = new HashMap<>();

    private char generaCharRandom() {
        Random randomc = new Random();
        return (char) (randomc.nextInt(26) + 'A');
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
        // hayBug=false;
    }

    //METODOS PUBLICOS
    public void darAlta() {
        Scanner entrada = new Scanner(System.in);
        System.out.println("Introduce tu nombre, Jugador " + (jugadores.size() + 1) + " : ");
        String miNombre = entrada.nextLine();
        char miavatar;
        do {
            miavatar = generaCharRandom();
        } while (avataresrep.toString().contains(String.valueOf(miavatar)));
        avataresrep.append(miavatar);
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
        for (Jugador ite:jugadores)
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
     * @param avance, movAvanzado
     */
    private void avanzarCasillas(int avance, boolean movAvanzado) {
        if(!movAvanzado){
            if(avance>=0){ // Funcionamiento normal (realmente podríamos poner "if getposicion+avance >0" pero así queda más legible)
                if ((jugadorActual.getPosicion() + avance) > 39){
                    addVuelta(jugadorActual);
                    System.out.println("Pasas por la casilla de salida y cobras " + pSalida+"$");
                    jugadorActual.setDinero(jugadorActual.getDinero()+pSalida);
                    jugadorActual.setPosicion((jugadorActual.getPosicion() + avance)-40, tablero.getCasillas());
                }
                else {
                    jugadorActual.setPosicion(jugadorActual.getPosicion()+avance , tablero.getCasillas());
                }
            }
            else{ // Avance negativo
                if ((jugadorActual.getPosicion() + avance) <= 0) { // Si se pasa por la casilla de salida
                    jugadorActual.setVueltas(jugadorActual.getVueltas()-1); // Se resta una vuelta
                    System.out.println("Pasas por la casilla de salida en sentido contrario y pagas " + pSalida+"$");
                    jugadorActual.pagar(pSalida, banca);
                    jugadorActual.setPosicion((jugadorActual.getPosicion() + avance)+40, tablero.getCasillas());
                }
                else {
                    jugadorActual.setPosicion(jugadorActual.getPosicion() + avance , tablero.getCasillas());
                }
            }
        }
        else{ // Movimiento avanzado activado
            if(jugadorActual.getTipoMov()==0){ // PELOTA
                jugadorActual.setAuxMovAvanzado(1); // Indica que el turno aún está en curso
                if(avance>4){
                    avanzarCasillas(4, false);
                    int aux = jugadorActual.getPosicion(); // guardamos la posición tras avanzar 4 casillas para comprobar paridades
                    for (int i=5; i<=avance; i++){
                        avanzarCasillas(1, false); // avanzamos una casilla
                        if(jugadorActual.getPosicion()==30){ // Si cae en la casilla de ir a la cárcel, se acaba la función. Recordemos que accionCasilla y menuAccion de la última parada/casilla se ejecutan fuera de aquí.
                            return;
                        }
                        if (i==avance){
                            jugadorActual.setAuxMovAvanzado(0);
                            System.out.println("Ya has pasado por todas tus paradas.");
                            return;
                        }
                        if((jugadorActual.getPosicion()-aux)%2!=0){ // Si es impar, se para en la casilla, se ejecuta la accion correspondiente y el jugador puede interactuar
                            System.out.println("¡Momento de pararse! Introduce \"acabar parada\" para avanzar a la siguiente casilla");
                            tablero.imprimirTablero();
                            accionCasilla();
                            if(jugadorActual.inCarcel()){
                                break;
                            }
                            menuAccion(true);
                            // menuAccion(true);
                        }
                        // Si es par, no se hace nada.
                    }
                    return;
                }
                if(avance<=4){
                    int aux = jugadorActual.getPosicion(); // guardamos la posición para comprobar paridades
                    for(int i=avance; i>0; i--){
                        avanzarCasillas(-1, false);
                        if(jugadorActual.getPosicion()==30){ // Si cae en la casilla de ir a la cárcel, se acaba la función. Recordemos que accionCasilla y menuAccion de la última parada/casilla se ejecutan fuera de aquí.
                            return;
                        }
                        if (i==1){ // Si es la última parada
                            jugadorActual.setAuxMovAvanzado(0);
                            System.out.println("Ya has pasado por todas tus paradas.");
                            return;
                        }
                        if((jugadorActual.getPosicion()-aux)%2!=0){ // Si es impar, se para en la casilla, se ejecuta la accion correspondiente y el jugador puede interactuar
                            System.out.println("¡Momentito de pararse! Introduce \"acabar parada\" para ir a la próxima casilla");
                            tablero.imprimirTablero();
                            accionCasilla();
                            if(jugadorActual.inCarcel()){
                                break;
                            }
                            menuAccion(true);
                        }
                        // Si es par, no se hace nada.
                    }
                }
            }
            else if(jugadorActual.getTipoMov()==1) { // COCHE
                if(avance>=4){
                    avanzarCasillas(avance, false);
                    System.out.println("Puedes tirar los dados hasta 3 veces más mientras saques más de un 3.");
                    jugadorActual.setAuxMovAvanzado(3);
                }
                else{
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
                int contC=0, contH=0, contP=0, contD=0;
                for(Casilla ite: jugadorActual.getPropiedades()){
                        if (ite.getTipo()==0) for(Edificio cite: ite.getEdificios()){
                            if(cite.getTipo()==0) contC++;
                            if(cite.getTipo()==1) contH++;
                            if(cite.getTipo()==2) contP++;
                            if(cite.getTipo()==3) contD++;
                        }
                    }

                int total=(100*contC)+(200*contH)+(300*contP)+(400*contD);
                if(total==0) {
                    System.out.println("Como aun no tienes edificios construidos, te libras");
                }
                else {
                    System.out.println("En total, tienes que pagar" + total + "$");
                    pagarAv(jugadorActual,total);
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
                    pagarAv(jugadorActual,500, ite);
                    jugadorActual.setPagoTasasEImpuestos(jugadorActual.getPagoTasasEImpuestos() + 500);
                    ite.setPremiosInversionesOBote(ite.getPremiosInversionesOBote()+500);
                }
                break;

            case 2:
                jugadorActual.addDinero(1000);
                jugadorActual.setPremiosInversionesOBote(jugadorActual.getPremiosInversionesOBote() + 1000);
                break;

            case 3:
                jugadorActual.setPosicion((jugadorActual.getPosicion() - 3), tablero.getCasillas());
                accionCasilla();
                break;

            case 4:
                int alquilersuerte=tablero.getCasilla(jugadorActual.getPosicion()).calcularAlquiler(dado)*2;
                if (jugadorActual.getPosicion() == 7) {
                    jugadorActual.setPosicion(15, tablero.getCasillas());
                    if(!tablero.getCasilla(15).getPropietario().equals(jugadorActual) && !tablero.getCasilla(15).getPropietario().equals(banca) && !tablero.getCasilla(15).getHipotecado()){
                        pagarAv(jugadorActual,alquilersuerte, tablero.getCasilla(15).getPropietario());
                        tablero.getCasilla(15).setRentabilidad(tablero.getCasilla(15).getRentabilidad() + alquilersuerte);
                        jugadorActual.setPagoDeAlquileres(jugadorActual.getPagoDeAlquileres() + alquilersuerte);
                        tablero.getCasilla(15).getPropietario().setCobroDeAlquileres(tablero.getCasilla(15).getPropietario().getCobroDeAlquileres() + alquilersuerte);
                        System.out.println("Pagas " + alquilersuerte + "$ por caer en " + tablero.getCasilla(15).getNombre());
                    }
                    else if (tablero.getCasilla(15).getHipotecado()) {
                        System.out.println("La casilla de transporte esta hipotecada... No pagas alquiler :)");
                    }
                }
                if (jugadorActual.getPosicion() == 22) {
                    jugadorActual.setPosicion(25, tablero.getCasillas());
                    if(!tablero.getCasilla(25).getPropietario().equals(jugadorActual) && !tablero.getCasilla(25).getPropietario().equals(banca) && !tablero.getCasilla(25).getHipotecado()){
                        pagarAv(jugadorActual,alquilersuerte, tablero.getCasilla(25).getPropietario());
                        tablero.getCasilla(25).setRentabilidad(tablero.getCasilla(25).getRentabilidad() + alquilersuerte);
                        jugadorActual.setPagoDeAlquileres(jugadorActual.getPagoDeAlquileres() + alquilersuerte);
                        tablero.getCasilla(25).getPropietario().setCobroDeAlquileres(tablero.getCasilla(25).getPropietario().getCobroDeAlquileres() + alquilersuerte);
                        System.out.println("Pagas " + alquilersuerte + "$ por caer en " + tablero.getCasilla(25).getNombre());
                    }
                    else if (tablero.getCasilla(25).getHipotecado()) {
                        System.out.println("La casilla de transporte esta hipotecada... No pagas alquiler :)");
                    }

                }
                if (jugadorActual.getPosicion() == 36) {
                    jugadorActual.setPosicion(5, tablero.getCasillas());
                    System.out.println("Has pasado por la casilla de salida, cobras" + pSalida);
                    jugadorActual.addDinero(pSalida);
                    jugadorActual.setPasarPorCasillaDeSalida(jugadorActual.getPasarPorCasillaDeSalida() + pSalida);
                    addVuelta(jugadorActual);
                    if(!tablero.getCasilla(5).getPropietario().equals(jugadorActual) && !tablero.getCasilla(5).getPropietario().equals(banca) && !tablero.getCasilla(5).getHipotecado()){
                        pagarAv(jugadorActual,alquilersuerte, tablero.getCasilla(5).getPropietario());
                        tablero.getCasilla(5).setRentabilidad(tablero.getCasilla(5).getRentabilidad() + alquilersuerte);
                        jugadorActual.setPagoDeAlquileres(jugadorActual.getPagoDeAlquileres() + alquilersuerte);
                        tablero.getCasilla(5).getPropietario().setCobroDeAlquileres(tablero.getCasilla(5).getPropietario().getCobroDeAlquileres() + alquilersuerte);
                        System.out.println("Pagas " + alquilersuerte + "$ por caer en " + tablero.getCasilla(5).getNombre());
                    }
                    else if (tablero.getCasilla(5).getHipotecado()) {
                        System.out.println("La casilla de transporte esta hipotecada... No pagas alquiler :)");
                    }
                }
                break;

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
                    pagarAv(jugadorActual,200, ite);
                    jugadorActual.setPagoTasasEImpuestos(jugadorActual.getPagoTasasEImpuestos() + 200);
                    ite.setPremiosInversionesOBote(ite.getPremiosInversionesOBote()+200);
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
     * Ejecuta la acción de la casilla. Cobra alquileres, impuestos, etc. y te manda a la carcel si corresponde
     * Y lo avisa por pantalla
     *
     * @param
     */
    public void accionCasilla() {
        contadorCasillas.get(jugadorActual).put(jugadorActual.getCasilla(tablero.getCasillas()),contadorCasillas.get(jugadorActual).get(jugadorActual.getCasilla(tablero.getCasillas()))+1);
        //System.out.println("Caída "+contadorCasillas.get(jugadorActual).get(jugadorActual.getCasilla(tablero.getCasillas())));
        int t = jugadorActual.getCasilla(tablero.getCasillas()).getTipo();
        Casilla casilla = jugadorActual.getCasilla(tablero.getCasillas());
        Grupo grupo=casilla.getGrupo();
        carta = new Carta(jugadorActual.getPosicion());
        if (t != 4 && t != 7) { //La casilla de carcel, parking gratuido y salida no hacen nada. El "salario" de salida está implementado con el movimiento
            switch (t) {
                case 0:
                case 1:
                case 2:
                    if (!casilla.getPropietario().equals(jugadorActual)&&!casilla.getPropietario().equals(banca) && !casilla.getHipotecado()) {
                        pagarAv(jugadorActual,casilla.calcularAlquiler(dado), casilla.getPropietario());
                        casilla.setRentabilidad(casilla.getRentabilidad()+casilla.calcularAlquiler(dado)); //edificios??
                        jugadorActual.setPagoDeAlquileres(jugadorActual.getPagoDeAlquileres() + casilla.calcularAlquiler(dado));
                        casilla.getPropietario().setCobroDeAlquileres(casilla.getPropietario().getCobroDeAlquileres() + casilla.calcularAlquiler(dado));
                        if(t==0)
                            grupo.setRentabilidad(grupo.getRentabilidad()+casilla.calcularAlquiler(dado));
                        System.out.println("Pagas " + casilla.calcularAlquiler(dado) + "$ por caer en " + casilla.getNombre());
                    } else if (casilla.getHipotecado()) {
                        System.out.println("Casilla hipotecada... No pagas alquiler :)");
                    }
                    else if(casilla.getPropietario().equals(banca)){
                        System.out.println("Esta propiedad aun no tiene dueño, la puedes comprar.");
                    }
                    else if(casilla.getPropietario().equals(jugadorActual)){
                    System.out.println("Has caido en una casilla de tu propiedad, disfruta de tu estancia");
                }
                    break;
                case 3:
                    carta.barajar();
                    int indice;
                    String entradaString;
                    do{
                        System.out.println("Elige una carta (introduciendo un numero del 1 al 6)");
                        Scanner entrada = new Scanner(System.in);
                        entradaString = entrada.nextLine();
                    }while(!entradaString.equals("1")&&!entradaString.equals("2")&&!entradaString.equals("3")&&!entradaString.equals("4")&&!entradaString.equals("5")&&!entradaString.equals("6"));

                    indice = Integer.parseInt(entradaString);

                    int numCarta = (carta.getcartas().get(indice - 1));
                    if (carta.getTipo() == 0)
                        accionSuerte(numCarta);
                    if (carta.getTipo() == 1)
                        accionCajaC(numCarta);
                    break;

                case 5: //parking"
                    jugadorActual.addDinero(casilla.getAlquilerBase());
                    System.out.println("Has caído en el Parking! cobras " + casilla.getAlquilerBase() + "$.");
                    jugadorActual.setPremiosInversionesOBote(jugadorActual.getPremiosInversionesOBote() + casilla.getAlquilerBase());
                    casilla.setAlquilerBase(0);
                    break;
                case 6: //Impuesto
                    pagarAv(jugadorActual,casilla.calcularAlquiler(dado), banca);
                    tablero.getCasilla(20).setAlquilerBase(tablero.getCasilla(20).getAlquilerBase() + casilla.calcularAlquiler(dado));
                    System.out.println("Has caído en la casilla impuestos, debes pagar " + casilla.calcularAlquiler(dado)+"$");
                    jugadorActual.setPagoTasasEImpuestos(jugadorActual.getPagoTasasEImpuestos() + casilla.calcularAlquiler(dado));
                    break;
                case 8:
                    jugadorActual.enviarCarcel(tablero.getCasillas()); //Podemos probar a cambiar esto por las funciones correspondientes y ya
                    dado.tirarDados(1, 2); //Ponemos valores arbitrarios (pero distintos), así NUNCA entras en la cárcel con dobles y se acaba el turno
                    System.out.println("A la cárcel!");
                    jugadorActual.setVecesEnLaCarcel(jugadorActual.getVecesEnLaCarcel() + 1);
                    break;
            }


        }
    }

    /**
     * Listar copia los condicionales de menuAcción para enseñar al jugador las opciones que tiene disponibles.
     *
     * @param haTirado si el jugadorActual ha tirado ya en este turno
     */
    private void listar(boolean haTirado) {
        System.out.println("----MENU DE AYUDA----");
        System.out.println("  ayuda: imprime esta lista de opciones");
        System.out.println("  jugador: imprime el nombre y avatar del jugador actual");
        System.out.println("  listar jugadores: imprime una lista de los jugadores y sus características");
        if (!haTirado)
            if (jugadorActual.inCarcel())
                System.out.println("lanzar dados: lanzar dados para intentar salir de la cárcel");
            else System.out.println("  lanzar dados: lanzar los dados para avanzar");
        if (haTirado) {
            System.out.println("  lanzar dados: puedes volver a lanzar los dados si has sacado dados dobles.");
            System.out.println("  acabar turno: finaliza el turno y pasa al siguiente jugador");
        }
        if (jugadorActual.inCarcel())
            System.out.println("  salir carcel: pagas " + pSalida / 4 + " para salir de la carcel");
        System.out.println("  describir nombreCasilla: imprime una descripción de la casilla indicada");
        System.out.println("  describir jugador x: imprime una descripción del jugador indicado(x)");
        System.out.println("  describir avatar  x: imprime una descripción del avatar indicado (x)");
        if (haTirado && jugadorActual.getCasilla(tablero.getCasillas()).isComprable())
            System.out.println("  comprar propiedad: si tienes dinero, compra la casilla en la que se encuentra tu avatar");
        System.out.println("  listar en venta: lista las casillas(solares, servicios y transportes) disponibles para comprar");
        System.out.println("  ver tablero: imprime el tablero por pantalla");
        if (jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov()==0 && jugadorActual.getAuxMovAvanzado()==1) {// Si está en modo avanzado, no ha terminado de pararse en las casillas y es pelota
            System.out.println("  acabar parada: visita la próxima casilla disponible tras moverte con movimiento avanzado");
        }
        System.out.println("  fin partida: termina el juego");
    }


    /**
     * Función de compra de la casilla, con comprobación de si el jugadorActual tienen suficiente dinero
     * Realiza la transacción correspondiente entre el jugador y la banca, y mueve la propiedad de la Procesos.Casilla, y del grupo en caso de que corresponda
     */
    public void comprarCasilla() {
        Casilla casilla = jugadorActual.getCasilla(tablero.getCasillas());
//        if (casilla.getTipo()>2){
//            System.out.println("No puedes comprar esta casilla");
//            return;
//        }
        if (jugadorActual.getDinero() >= casilla.getPrecio()) {
            casilla.getPropietario().addDinero(casilla.getPrecio());
            casilla.setPropietario(jugadorActual);
            jugadorActual.setDinero(jugadorActual.getDinero() - casilla.getPrecio());
            jugadorActual.setDineroInvertido(jugadorActual.getDineroInvertido() + casilla.getPrecio());
            jugadorActual.setFortuna(jugadorActual.getFortuna()+ casilla.getPrecio());
        } else System.out.println("Cuidado... Ya no tienes dinero suficiente para comprar esta casilla.");
    }

    /**
     * Cada vez que se cambia de jugador se llama desde main a menuAcción(false). Después, se hacen llamadas recursivas de menuAcción(false/true, según corresponda) desde el propio menuAccion hasta el fin de turno
     * Llamamos desde la función menuAcción como método para mantener la información de haTirado.
     * Usar recursividad aquí no debería dar problemas pq contamos con que un jugador no haga 20 acciones distintas en su turno
     *
     * @param haTirado
     * @return devuelve un booleano para indicar si sigue la partida o no
     */
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
                listar(haTirado);
                return menuAccion(haTirado);

            case "jugador":
            case "Jugador":
                System.out.println("Nombre: " + jugadorActual.getNombre());
                System.out.println("Avatar: " + jugadorActual.getAvatar());
                System.out.println("Dinero: " + jugadorActual.getDinero());
                System.out.println("Posición: " + jugadorActual.getPosicion());
                return menuAccion(haTirado);
            case "listar":
                if (entradaPartida.length > 1) switch (entradaPartida[1]) {
                    case "Jugadores":
                    case "jugadores":
                        for (int i = 0; i < jugadores.size(); i++) {
                            System.out.println(jugadores.get(i));
                        }
                        return menuAccion(haTirado);
                    case "en":
                    case "En":
                        if (entradaPartida.length > 2 && (entradaPartida[2].equals("venta") || entradaPartida[2].equals("Venta"))) {
                            for (Casilla casilla : tablero.getCasillas()) {
                                if (casilla.isComprable()) System.out.println(" " + casilla.descripcion());
                            }

                            return menuAccion(haTirado);
                        }
                        break;
                    case "Avatares":
                    case "avatares":
                        for (Jugador ite : jugadores) {
                            System.out.println("{\n" +
                                    "id: " + ite.getAvatar() + "\n" +
                                    "tipo: " + "N/A" + ",\n" +
                                    "casilla: " + ite.getCasilla(tablero.getCasillas()) + "\n" +
                                    "jugador: " + ite.getNombre() + "\n" +
                                    "}");
                        }
                        return menuAccion(haTirado);
                    case "Edificios":
                    case "edificios":
                        if (entradaPartida.length>2){
                            for (Grupo ite: tablero.getGrupos()){
                                if (ite.getColor().equals(entradaPartida[2])){
                                    for (Casilla cite: ite.getCasillas()){
                                        for (Edificio eite: cite.getEdificios())
                                            System.out.println(" "+eite.getIdentificador()+" - "+eite.getCasilla());
                                    }
                                    return menuAccion(haTirado);
                                }
                            }
                            System.out.println("No se reconoce el grupo/color");
                        }
                        else for (Casilla cite: tablero.getCasillas()){
                            if (!(cite.getEdificios ()==null)) for(Edificio eite: cite.getEdificios())
                                System.out.println(" "+eite.getIdentificador()+" - "+eite.getCasilla());
                        }
                }
                break;
            case "hipotecar":
            case "Hipotecar":
                if(jugadorActual.getBancarrota()){
                    System.out.println("Ya no puedes hipotecar, desgraciadamente estas en bancarrota y la partida se ha acabado para ti.");
                    break;
                }
                if (entradaPartida.length > 1) {
                    Casilla casilla = tablero.getCasilla(entradaPartida[1]);
                    if (casilla == null) {
                        System.out.println("Esta casilla no existe");
                        break;
                    }
                    if (casilla.getTipo() > 2) {
                        System.out.println("Esta casilla no es hipotecable");
                        break;
                    }
                    if (!casilla.getPropietario().equals(jugadorActual)) {
                        System.out.println("No puedes hipotecar una casilla que no es tuya");
                        break;
                    }
                    casilla.hipotecar();
                }
                break;
            case "deshipotecar":
            case "Deshipotecar":
                if(jugadorActual.getBancarrota()){
                    System.out.println("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
                    break;
                }
                if (entradaPartida.length > 1) {
                    Casilla casilla = tablero.getCasilla(entradaPartida[1]);
                    if (casilla == null) {
                        System.out.println("Esta casilla no existe");
                        break;
                    }
                    if (casilla.getTipo() > 2) {
                        System.out.println("Esta casilla no es hipotecable");
                        break;
                    }
                    if (!casilla.getPropietario().equals(jugadorActual)) {
                        System.out.println("No puedes deshipotecar una casilla que no es tuya");
                        break;
                    }
                    casilla.deshipotecar();
                }
                break;
            case "comprar":
                if(jugadorActual.getBancarrota()){
                    System.out.println("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
                    break;
                }
                if (!haTirado) {
                    System.out.println("Aun no has tirado los dados. Tiralos para poder comprar propiedades");
                    break;
                }

                if (entradaPartida.length > 1 && entradaPartida[1].equals("propiedad") || entradaPartida[1].equals(jugadorActual.getCasilla(tablero.getCasillas()).getNombre())) {
                    if(jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov()==1){ // Si es coche
                        if(jugadorActual.getPuedeComprarPropiedades()){
                            if (!jugadorActual.getCasilla(tablero.getCasillas()).isComprable()) {
                                System.out.println("No puedes comprar esta propiedad");
                                return menuAccion(false);
                            } else{
                                comprarCasilla();
                                System.out.println("Propiedad comprada con éxito por " + jugadorActual.getCasilla(tablero.getCasillas()).getPrecio() + "$");
                                System.out.println("No podrás comprar más propiedades, casillas, servicios o transportes hasta que acabe tu turno.");
                                jugadorActual.setPuedeComprarPropiedades(false); //
                                return menuAccion(false);
                            }
                        }else{
                            System.out.println("No puedes comprar más propiedades, casillas, servicios o transportes hasta que acabe tu turno.");
                            return menuAccion(false);
                        }
                    }
                    if (!jugadorActual.getCasilla(tablero.getCasillas()).isComprable()) {
                        System.out.println("No puedes comprar esta propiedad");
                        //break;
                    }else {
                        comprarCasilla();
                        System.out.println("Propiedad comprada con éxito por " + jugadorActual.getCasilla(tablero.getCasillas()).getPrecio() + "$");
                        return menuAccion(true);
                    }
                } else { //Si está intentando comprar otra propiedad le avisamos...
                    for (Casilla ite : getTablero().getCasillas()) {
                        if (entradaPartida[1].equals(ite.getNombre())) {
                            System.out.println("Solo puedes comprar la casilla en la que caes...");
                            return menuAccion(haTirado);
                        }
                    }
                    System.out.println("Esta casilla no existe");
                }
                break;
            case "salir":
                if(jugadorActual.getBancarrota()){
                    System.out.println("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
                    break;
                }
                if (entradaPartida.length>1) if (entradaPartida[1].equals("carcel") || entradaPartida[1].equals("cárcel")) {
                    if (!jugadorActual.inCarcel()) {
                        System.out.println("No estas en la cárcel...");
                        return menuAccion(haTirado);
                    } else {
                        System.out.println("Pagas " + precioCarcel + " para salir de la cárcel.");
                        pagarAv(jugadorActual,precioCarcel);
                        jugadorActual.setPagoTasasEImpuestos(jugadorActual.getPagoTasasEImpuestos() + precioCarcel);
                        jugadorActual.setTurnosCarcel(0);
                        return menuAccion(true);
                    }
                }
                break;
            case "ver":
                if (entradaPartida[1].equals("tablero")) {
                    tablero.imprimirTablero();
                    return menuAccion(haTirado);
                }
                break;
            case "tirar":
            case "lanzar":
                if(jugadorActual.getBancarrota()){
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
                            if (entradaPartida.length==5 && entradaPartida[2].equals("trucados")){
                                dado.tirarDados(Integer.parseInt(entradaPartida[3]),Integer.parseInt(entradaPartida[4]));
                                jugadorActual.addVecesDados();
                            }
                            else {
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
                        if (jugadorActual.getTipoMov()==1 && jugadorActual.getAuxMovAvanzado()<0) { // importante no poner movAuxActivado para evitar exploit de que el usuario reinicie los turnos sin poder moverse mediante la introducción de cambiar movimiento
                            System.out.println("Tu motor sigue roto. ¡No puedes moverte!");
                            return menuAccion(true);
                        }

                        // 2. HACEMOS RETURN EN LOS CASOS EN LOS QUE *NO* PUEDE LANZAR DADOS

                        if (puedeTirarOtraVez(haTirado) == false){
                            System.out.println("¡No puedes volver a tirar los dados!");
                            return menuAccion(true);
                        }

                        // SI LLEGAMOS HASTA AQUÍ, EL JUGADOR PUEDE TIRAR. PROCEDEMOS A TIRAR:

                        if (entradaPartida.length==5 && entradaPartida[2].equals("trucados")){
                            dado.tirarDados(Integer.parseInt(entradaPartida[3]),Integer.parseInt(entradaPartida[4]));
                            jugadorActual.addVecesDados();
                        }
                        else {
                            dado.tirarDados();
                            jugadorActual.addVecesDados();
                        }
                        System.out.println("Has sacado un " + dado.getDado1() + " y un " + dado.getDado2());

                        // REALIZAMOS LA ACCIÓN CORRESPONDIENTE SI SE SACA DOBLES:

                        if(dado.areEqual()){
                            if (jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov()==1){ // Si es coche, solo importan los dobles en la última tirada.
                                if(jugadorActual.getAuxMovAvanzado()==0)
                                    System.out.println("Has sacado dobles! Puedes volver a tirar.");
                            }else{ // De ser de otro modo, siempre importan los dobles, así que imprimimos el mensaje correspondiente.
                                System.out.println("Has sacado dobles! Puedes volver a tirar.");
                            }
                            if (jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov()==0 && jugadorActual.getAuxMovAvanzado()==999){
                                System.out.println("Como el movimiento avanzado está activado, la tirada extra de dados será cuando pases por todas las paradas.");
                            }
                        }

                        // DESPUÉS DE TIRAR, AVANZAMOS EN MOVIMIENTO NORMAL O AVANZADO, SEGÚN CORRESPONDA:

                            // Primero, mandamos a la cárcel si corresponde
                        if (dado.getC() == 3) {
                            System.out.println("Has sacado dados dobles 3 veces seguidas. ¡Vas a la cárcel! ");
                            jugadorActual.enviarCarcel(tablero.getCasillas());
                            if (!jugadorActual.getMovAvanzadoActivado() || (jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov()==1)) tablero.imprimirTablero();
                            return menuAccion(true);
                        }
                            // Si el Movimiento Avanzado está activado...
                        if (jugadorActual.getMovAvanzadoActivado()){
                            if (jugadorActual.getTipoMov() == 0){ // MOVIMIENTO AVANZADO "PELOTA" ------------------------------------------------------------------------------
                                if (jugadorActual.getAuxMovAvanzado() == 999){ // Si es el primer turno
                                    avanzarCasillas(dado.getSuma(), true);
                                }else{ // Si no es el primer turno y puede tirar, significa que ha sacado dobles. No obstante, tira en movimiento simple.
                                    avanzarCasillas(dado.getSuma(), false);
                                }
                            }
                            if (jugadorActual.getTipoMov() == 1){ // MOVIMIENTO AVANZADO "COCHE" ------------------------------------------------------------------------------
                                if(jugadorActual.getAuxMovAvanzado()==999){ // Si es el primer turno
                                    avanzarCasillas(dado.getSuma(), true);
                                }else{ // Si no es el primer turno...
                                    if(jugadorActual.getAuxMovAvanzado() == 0){ // Si ya acabó sus tiradas extra y puede tirar, significa que ha sacado dobles. Nos movemos en modo simple sin restar auxMovAvanzado.
                                        avanzarCasillas(dado.getSuma(), false);
                                    }else{ // Si no acbaó sus tiradas extra
                                        if (dado.getSuma()>3){
                                            jugadorActual.setAuxMovAvanzado(jugadorActual.getAuxMovAvanzado()-1); // Restamos 1 a las tiradas extra
                                            avanzarCasillas(dado.getSuma(), false);
                                            // Avisamos de cuántas tiradas extra le quedan
                                            if(jugadorActual.getAuxMovAvanzado()>0){
                                                System.out.printf("\nLanzamientos extra restantes: %d. Cuando saques menos de un 4, no podrás tirar de nuevo.\n", jugadorActual.getAuxMovAvanzado());
                                            }else{
                                                System.out.printf("\nNo te quedan más lanzamientos extra por el movimiento especial.\n");
                                                if(dado.areEqual()){
                                                    System.out.printf("\nPero como has sacado dobles... ¡Tienes otra tirada extra!\n");
                                                }
                                            }
                                        }else{ // Si saca menos de 4, no se mueve y además no tiene más tiradas extra.
                                            jugadorActual.setAuxMovAvanzado(0);
                                            System.out.println("\nHas sacado menos de un 4, por lo que no tienes más lanzamientos extra.");
                                            if(dado.areEqual()){
                                                System.out.printf("\nPero como has sacado dobles... ¡Tienes otra tirada más!\n");
                                            }
                                        }
                                    }
                                }
                            }
                        }else{ // Si el movimiento avanzado no está activado
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
                    if(haTirado){
                        System.out.println("¡Solo puedes cambiar de movimiento al inicio de tu turno!");
                        break;
                    }
                    jugadorActual.setMovAvanzadoActivado(!jugadorActual.getMovAvanzadoActivado());
                    if (jugadorActual.getMovAvanzadoActivado())
                        System.out.println("El movimiento avanzado está ahora activado");
                    else
                        System.out.println("El movimiento avanzado está ahora desactivado");
                    break;
                }
                break;
            case "describir":
            case "descripción":
            case "descripcion":
                if (entradaPartida.length > 1) switch (entradaPartida[1]) {
                    case "jugador":
                    case "Jugador":
                        for (Jugador ite : jugadores) {
                            if (entradaPartida.length > 2 && entradaPartida[2].equals(ite.getNombre())) {
                                System.out.print(ite);
                                return menuAccion(haTirado);
                            }
                        }
                        System.out.println("Vaya! No existe este jugador...");
                        break;
                    case "avatar":
                    case "Avatar":
                        for (Jugador ite : jugadores) {
                            if (entradaPartida.length > 2 && entradaPartida[2].equals(String.valueOf(ite.getAvatar()))) {
                                System.out.print("{\n" +
                                        "id: " + "A" + ",\n" + //El avatar que corresponda
                                        "tipo: " + "N/A" + ",\n" +
                                        "casilla: " + ite.getCasilla(tablero.getCasillas()).getNombre() + ",\n" +
                                        "jugador: [" + ite.getNombre() + "]\n" +
                                        "}");
                                return menuAccion(haTirado);
                            }
                        }
                        System.out.println("Vaya! No existe este jugador...");
                        break;
                    default:
                        if (tablero.getCasilla(entradaPartida[1])!=null){
                                System.out.print(tablero.getCasilla(entradaPartida[1]).descripcionDetallada());
                                return menuAccion(haTirado);
                            }
                            else System.out.println("No hay ninguna casilla que se llame así");
                        }
            case "fin":
                if (entradaPartida.length > 1 && entradaPartida[1].equals("partida")) return false;
                break;
            case "pagar":
                if(jugadorActual.getBancarrota()){
                    System.out.println("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
                    break;
                }
                if (pagando){
                    pagarAv(jugadorActual,pagoPendiente,cobradorPendiente);
                }
                else System.out.println("No tienes nada que pagar :D");
                break;
            case "acabar":
                if (entradaPartida.length > 1 && entradaPartida[1].equals("turno")) {
                    //if (((!haTirado && !jugadorActual.getMovAvanzadoActivado()) || (!jugadorActual.getMovAvanzadoActivado() && dado.areEqual()))&&!jugadorActual.getBancarrota()){

                    if (puedeTirarOtraVez(haTirado) && !jugadorActual.getBancarrota()){ // SI PUEDE TIRAR OTRA VEZ, DEBE HACERLO. AVISAMOS.
                        System.out.println("Tienes que tirar antes de terminar el turno");
                        break;
                    }

                    // Sin embargo, existe un caso en el que el jugador no puede tirar, pero tampoco puede acabar el turno. Lo tratamos:
                    if(jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov()==0 && jugadorActual.getAuxMovAvanzado()==1){
                        // (Si es tipo Pelota, el movimiento avanzado está activado y aún le quedan paradas por recorrer)
                        System.out.println("¡Aún te quedan paradas por recorrer!");
                        break;
                    }

                    if (jugadorActual.getTipoMov()==1) { // MOVIMIENTO AVANZADO "COCHE" ------------------------------------------------------------------------------
                        if  (jugadorActual.getAuxMovAvanzado()<0){ // SI EL JUGADOR QUE ACABA EL TURNO ES UN COCHE CON MOTOR ROTO
                            jugadorActual.setAuxMovAvanzado(jugadorActual.getAuxMovAvanzado()+1);
                            if (jugadorActual.getAuxMovAvanzado()==0){
                                jugadorActual.setAuxMovAvanzado(999);
                                System.out.println("¡Motor reparado! En el siguiente turno podrás moverte.");
                            }else
                                System.out.printf("\nTurnos restantes para poder moverte: %d\n", -jugadorActual.getAuxMovAvanzado());
                            jugadorActual.setPuedeComprarPropiedades(true);
                            // jugadorActual.setAuxMovAvanzado(999);
                            jugadorActual.setPuedeComprarPropiedades(true);
                            /* if(jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov()==0){
                                hayBug=true;
                            } */
                            nextJugador();
                            System.out.println("Turno de: " + jugadorActual.getNombre());
                            return true;
                        }
                    }
                    if(pagando && !jugadorActual.getBancarrota()){
                        System.out.println("Debes saldar tu deuda antes de acabar el turno, o declararte en bancarrota");
                    } else {
                        if(!(jugadorActual.getTipoMov()==1 && jugadorActual.getMovAvanzadoActivado() && jugadorActual.getAuxMovAvanzado()<0)){
                            jugadorActual.setAuxMovAvanzado(999);
                        }
                        jugadorActual.setPuedeComprarPropiedades(true);
                        /* if(jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov()==0){
                            hayBug=true;
                        } */
                        nextJugador();
                        System.out.println("Turno de: " + jugadorActual.getNombre());
                        pagando = false;
                        cobradorPendiente = banca;
                        return true;
                    }
                    break;
                }
                if (entradaPartida.length > 1 && entradaPartida[1].equals("parada")) {
                    if(jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov()==0 && jugadorActual.getAuxMovAvanzado()==0){
                        System.out.println("Ya has pasado por todas tus paradas.");
                        break;
                    }
                    else if(!jugadorActual.getMovAvanzadoActivado() || (jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov()==1)){
                        System.out.println("¿Paradas? Bro think he pelota con movAvanzadoActivado");
                        break;
                    }
                    else if (!(jugadorActual.getMovAvanzadoActivado() && jugadorActual.getTipoMov()==0)) tablero.imprimirTablero();
                    //if (dado.getSuma()>4) return menuAccion(haTirado);
                    return haTirado;
                }
                break;
            case "bancarrota":
            case "Bancarrota":
                if(jugadorActual.getBancarrota()){
                    System.out.println("No puedes ejecutar esta acción estando en bancarrota, tristemente la partida se ha acabado para ti.");
                    break;
                }
                Scanner scanner = new Scanner(System.in);
                System.out.print("¿Estás seguro de que quieres declararte en bancarrota?");
                String respuesta = scanner.nextLine();
                if (respuesta.equals("si")||respuesta.equals("Si")||respuesta.equals("SI")) jugadorActual.declararBancarrota(cobradorPendiente);
                break;
            case "estadisticas":
            case "estadísticas":
                if (entradaPartida.length > 1) {
                    for (Jugador ite : jugadores) {
                        if (entradaPartida[1].equals(ite.getNombre())) {
                            System.out.println("Dinero invertido: " + ite.getDineroInvertido());
                            System.out.println("Pago Tasas e Impuestos: " + ite.getPagoTasasEImpuestos());
                            System.out.println("Pago De Alquileres: " + ite.getPagoDeAlquileres());
                            System.out.println("Cobro de Alquileres: " + ite.getCobroDeAlquileres());
                            System.out.println("Dinero por pasar por la casilla de salida:" + ite.getPasarPorCasillaDeSalida());
                            System.out.println("Premios por inversiones o bote: " + ite.getPremiosInversionesOBote());
                            System.out.println("Veces en la carcel: " + ite.getVecesEnLaCarcel());
                            return menuAccion(haTirado);
                        }
                    }
                }
                else{
                    System.out.println("Casilla(s) mas rentables:  ");
                    imprimirCasillas(casillaMasRentable(tablero.getCasillas()));
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

                    return menuAccion(haTirado);
                }
                break;
            case "edificar":
            case "construir":
                boolean caidas = false;
                if(jugadorActual.getBancarrota()){
                    System.out.println("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
                    break;
                }
                caidas = contadorCasillas.get(jugadorActual).get(jugadorActual.getCasilla(tablero.getCasillas()))==2;

                if (entradaPartida.length > 1) switch (entradaPartida[1]) {
                    case "casa":
                    case "Casa":
                        jugadorActual.getCasilla(tablero.getCasillas()).construir(0, jugadorActual,caidas);
                        break;
                    case "hotel":
                    case "Hotel":
                        jugadorActual.getCasilla(tablero.getCasillas()).construir(1, jugadorActual,caidas);
                        break;
                    case "piscina":
                    case "Piscina":
                        jugadorActual.getCasilla(tablero.getCasillas()).construir(2, jugadorActual,caidas);
                        break;
                    case "pista":
                    case "Pista":
                        jugadorActual.getCasilla(tablero.getCasillas()).construir(3, jugadorActual,caidas);
                        break;
                    default:
                        System.out.println("No existe este edificio. Prueba con casa, hotel, piscina o pista :)");
                }
                break;
            case "vender":
            case "Vender":
                if(jugadorActual.getBancarrota()){
                    System.out.println("No puedes ejecutar esta acción estando en bancarrota, la partida se ha acabado para ti.");
                    break;
                }
                Edificio aux = null;
                if (entradaPartida.length > 2) {
                    String identificador = entradaPartida [1]+" "+entradaPartida[2];
                    if (jugadorActual.getPropiedades()!=null) for (Casilla cite: jugadorActual.getPropiedades()) if (cite.getEdificios()!=null) for(Edificio eite: cite.getEdificios()){
                        if (eite.getIdentificador().equals(identificador)) aux = eite;
                    }
                    if (aux!=null) aux.getCasilla().venderEdificio(aux,jugadorActual);
                    else System.out.println("Identificador inválido...");
                } else System.out.println("Identificador inválido...");
                break;
            default:
                System.out.println("No se reconoce la acción... Introduce 'ayuda' para ver tus opciones.");
        }
        return menuAccion(haTirado);
    }
    private void pagarAv(Jugador pagador, int importe){
        pagarAv(pagador,importe,banca);
    }
    private void pagarAv(Jugador pagador, int importe, Jugador cobrador){
        while(!pagador.pagar(importe,cobrador) && !pagador.getBancarrota()){
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
     *
     */
    private void gestInsuf(){
        pagando = true;
        menuAccion(true);
    }

    private void actualizarPropietarioCasillas() {
        for (Casilla ite: tablero.getCasillas()){
            if (ite.getPropietario()==null) ite.setPropietario(banca);
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
        for (Jugador jugador: jugadores) {
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
            if (ite.getPropietario().isBanca()) {
                ite.setPrecio((int) (ite.getPrecio() * 1.05));
            }
    }

    private ArrayList<Casilla> casillaMasRentable(ArrayList<Casilla> tablero){
        int maximo=tablero.get(0).getRentabilidad();
        for(Casilla ite:tablero){
            if (ite.getTipo() < 3) {
                if (ite.getRentabilidad() > maximo)
                    maximo = ite.getRentabilidad();
            }
        }
        ArrayList<Casilla> casillasConMaximo = new ArrayList<>();
        for (Casilla ite : tablero) {
            if(ite.getTipo() < 3){
                if (ite.getRentabilidad() == maximo)
                    casillasConMaximo.add(ite);
            }
        }
        return casillasConMaximo;
    }

    private void imprimirCasillas(ArrayList<Casilla> casillasEstad){
        for(Casilla ite:casillasEstad)
            System.out.print(ite.getNombre() + ", ");
        System.out.println("\n");
    }

    private void imprimirGrupos(ArrayList<Grupo> gruposEstad){
        for(Grupo ite:gruposEstad)
            System.out.print(ite.getColor() + ", ");
        System.out.println("\n");
    }

    private ArrayList<Grupo> grupoMasRentable(ArrayList<Casilla> tablero){
        int maximo=tablero.get(1).getGrupo().getRentabilidad();
        for(Casilla ite:tablero) {
            if (ite.getTipo() == 0) {
                if (ite.getGrupo().getRentabilidad() > maximo)
                    maximo = ite.getGrupo().getRentabilidad();
            }
        }
        ArrayList<Grupo> gruposConMaximo = new ArrayList<>();
        for (Casilla ite : tablero) {
            if(ite.getTipo()==0){
                if(ite.getGrupo().getRentabilidad()==maximo && !gruposConMaximo.contains(ite.getGrupo()))
                    gruposConMaximo.add(ite.getGrupo());
            }
        }
        return gruposConMaximo;
    }

    private ArrayList<Casilla> casillaMasFrecuentada(ArrayList<Casilla> tablero){
        int maximo=tablero.get(0).getVisitas();
        for(Casilla ite:tablero){
            if (ite.getVisitas() > maximo) {
                maximo = ite.getVisitas();
            }}
        ArrayList<Casilla> casillasConMaximo = new ArrayList<>();
        for (Casilla ite : tablero) {
            if (ite.getVisitas() == maximo) {
                casillasConMaximo.add(ite);
            }
        }
        return casillasConMaximo;
    }

    private ArrayList<Jugador> jugadorMasVueltas(ArrayList<Jugador> jugadores){
        int maximo=jugadores.get(0).getVueltas();
        for(Jugador ite:jugadores){
            if (ite.getVueltas() > maximo) {
                maximo = ite.getVueltas();
            }}
        ArrayList<Jugador> jugadoresConMaximo = new ArrayList<>();
        for (Jugador ite : jugadores) {
            if (ite.getVueltas() == maximo) {
                jugadoresConMaximo.add(ite);
            }
        }
        return jugadoresConMaximo;
    }

    private ArrayList<Jugador>  jugadorMasDados(ArrayList<Jugador> jugadores){
        int maximo=jugadores.get(0).getVecesDados();
        for(Jugador ite:jugadores){
            if (ite.getVecesDados() > maximo) {
                maximo = ite.getVecesDados();
            }}
        ArrayList<Jugador> jugadoresConMaximo = new ArrayList<>();
        for (Jugador ite : jugadores) {
            if (ite.getVecesDados() == maximo) {
                jugadoresConMaximo.add(ite);
            }
        }
        return jugadoresConMaximo;
    }

    private ArrayList<Jugador> jugadorEnCabeza(ArrayList<Jugador> jugadores){
        int maximo=jugadores.get(0).getFortuna();
        for(Jugador ite:jugadores){
            if (ite.getFortuna() > maximo) {
                maximo = ite.getFortuna();
            }}
        ArrayList<Jugador> jugadoresConMaximo = new ArrayList<>();
        for (Jugador ite : jugadores) {
            if (ite.getFortuna() == maximo) {
                jugadoresConMaximo.add(ite);
            }
        }
        return jugadoresConMaximo;
    }
    private void imprimirJugadores(ArrayList<Jugador> jugadoresEstad){
        for(Jugador ite:jugadoresEstad)
            System.out.print(ite.getNombre() + ", ");
        System.out.println("\n");
    }

    private boolean puedeTirarOtraVez(boolean haTirado){
        if(jugadorActual.getMovAvanzadoActivado()){ // SI ESTÁ EL MOVIMIENTO AVANZADO ACTIVADO, DISTINGUIMOS ENTRE "PELOTA" Y "COCHE"
            if(jugadorActual.getTipoMov() == 0){ // MOVIMIENTO AVANZADO "PELOTA" ------------------------------------------------------------------------------
                if(jugadorActual.getAuxMovAvanzado()==1){ // Si le quedan paradas sin recorrer, no puede tirar
                    return false;
                }
                if(jugadorActual.getAuxMovAvanzado() == 0 && !dado.areEqual()){ // Si no le quedan paradas y tampoco sacó dobles, no puede tirar
                    return false;
                }
            }

            if(jugadorActual.getTipoMov() == 1) { // MOVIMIENTO AVANZADO "COCHE" ------------------------------------------------------------------------------
                if(jugadorActual.getAuxMovAvanzado() == 0 && !dado.areEqual()){ // Si se le acabaron las tiradas extra y no sacó dobles en la anterior, no puede tirar
                    return false;
                }
                if (jugadorActual.getTipoMov()==1 && jugadorActual.getAuxMovAvanzado()<0){ // Motor roto
                    return false;
                }
            }
        }else{ // SI NO ESTÁ ACTIVADO EL MOV AVANZADO...
            if(haTirado && !dado.areEqual()){
                return false;
            }
        }
        return true; // En el resto de los casos, el jugador SÍ puede tirar.
    }
}
