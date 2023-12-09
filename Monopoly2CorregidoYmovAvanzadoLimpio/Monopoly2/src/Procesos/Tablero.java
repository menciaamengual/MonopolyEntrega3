package Procesos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import Juego.Juego;
import Procesos.Casillas.*;

public class Tablero {
    static int ccasa = 0;
    static int chotel = 0;
    static int cpiscina = 0;
    static int cdeporte = 0;
    private ArrayList<Casilla> casillas;
    private ArrayList<Grupo> grupos;
    
    Map<String, Integer> casillasMap;
    // Función Constructor de tablero
    public Tablero(int pBase, int pSalida, Jugador banca){
        inicializarGrupos();
        inicializarCasillas(pBase,pSalida, banca);
    }
    private void inicializarGrupos(){
        grupos = new ArrayList<>(8);
        for (int i = 0; i<8; i++){
            grupos.add(new Grupo(i));
        }
    }



    private void inicializarCasillas(int pBase, int pSalida, Jugador banca) {
        casillasMap = new LinkedHashMap<>();
        casillasMap.put("Salida", 0);
        casillasMap.put("Soria", 1);
        casillasMap.put("Caja1", 2);
        casillasMap.put("Carrilana", 3);
        casillasMap.put("Impuesto1", 4);
        casillasMap.put("Transporte1", 5);
        casillasMap.put("PlazaRoja", 6);
        casillasMap.put("Suerte1", 7);
        casillasMap.put("Krystal", 8);
        casillasMap.put("Tropical", 9);
        casillasMap.put("Carcel", 10);
        casillasMap.put("Auditorio", 11);
        casillasMap.put("Servicio1", 12);
        casillasMap.put("Obradoiro", 13);
        casillasMap.put("Pisopa", 14);
        casillasMap.put("Transporte2", 15);
        casillasMap.put("Parla", 16);
        casillasMap.put("Caja2", 17);
        casillasMap.put("Composdata", 18);
        casillasMap.put("Barroso", 19);
        casillasMap.put("Parking", 20);
        casillasMap.put("Conchi", 21);
        casillasMap.put("Suerte2", 22);
        casillasMap.put("SushiFafa", 23);
        casillasMap.put("Makumba", 24);
        casillasMap.put("Transporte3", 25);
        casillasMap.put("ETSE", 26);
        casillasMap.put("CiTIUS", 27);
        casillasMap.put("Servicio2", 28);
        casillasMap.put("Emprendia", 29);
        casillasMap.put("IrCarcel", 30);
        casillasMap.put("Vanitas", 31);
        casillasMap.put("Malatesta", 32);
        casillasMap.put("Caja3", 33);
        casillasMap.put("Circus", 34);
        casillasMap.put("Transporte4", 35);
        casillasMap.put("Suerte3", 36);
        casillasMap.put("Tarasca", 37);
        casillasMap.put("Impuesto2", 38);
        casillasMap.put("Porron", 39);


        /* Tomando ciertas libertades artísticas:
    0 - Marrón
    1 - Cián
    2 - Fuxia
    3 - Tangerina
    4 - Escarlatata
    5 - Amarillo
    6 - Verde
    7 - Azul
     */
        //(int posicion, String nombre, int color)
        //precioInicial = listaPrecios[color];
        //Procesos.Casilla (int pBase,int pSalida,int tipo, int posicion, String nombre, int color){

        //casillasMap.put("PISOPA",41);

    ArrayList<String> nombres = new ArrayList<>(casillasMap.keySet());

        int[] ArrayAux_tipoCasilla = {7, 0, 3, 0, 6, 1, 0, 3, 0, 0, 4, 0, 2, 0, 0, 1, 0, 3, 0, 0, 5, 0, 3, 0, 0, 1, 0, 0, 2, 0, 8, 0, 0, 3, 0, 1, 3, 0, 6, 0};
        /*
            0. Solar
            1. Transporte
            2. Servicios
            3. Comunidad
             9. Suerte
            4. Cárcel
            5. Párking gratuito
            6.
         */

        casillas = new ArrayList<>(40);
        for (int i = 0; i < 40; i++) { //Inicialización de casillas
            switch(ArrayAux_tipoCasilla[i]){
                case 0:
                    casillas.add(new Solar(pBase, pSalida, i, nombres.get(i), grupos, banca));
                    break;
                case 1:
                    casillas.add(new Transporte(i,nombres.get(i),banca,pSalida));
                    break;
                case 2:
                    casillas.add(new Servicios(i,nombres.get(i),banca,pSalida));
                    break;
                case 3:
                    casillas.add(new Comunidad(i,nombres.get(i)));
                    break;
                case 4:
                    casillas.add(new Carcel(i,nombres.get(i)));
                    break;
                case 5:
                    casillas.add(new ParkingGratuito(i,nombres.get(i)));
                    break;
                case 6:
                    if (i<20) casillas.add(new Impuesto(i,nombres.get(i),pSalida/2));
                    else casillas.add(new Impuesto(i,nombres.get(i),pSalida));
                    break;
                case 7:
                    casillas.add(new Salida(i,nombres.get(i)));
                    break;
                case 8:
                    casillas.add(new VasALaCarcel(i,nombres.get(i)));
                    break;
            }
        }

    }

    public static void addCcasa(){ ccasa++;}
    public static void addChotel(){ chotel++;}
    public static void addCpiscina(){ cpiscina++;}
    public static void addCdeporte(){ cdeporte++;}

    public String toString() { //Usamos casilla.toString
        StringBuilder stab = new StringBuilder();
        stab.append(String.format("\u001B[4m%"+(Casilla.lonMaxNombre+6+3)*11+"s\u001B[0m\n"," "));


        for (int i = 20; i < (19+12); i++) {
            stab.append(casillas.get(i).toString());
        }
        stab.append("\n");
        int j;
        int lonMaxNombre = Casilla.lonMaxNombre;
        int n = (lonMaxNombre+9)*9;
        String formato = "%"+n+"s";



        for (int i = 19;i> 11; i--){
            j = 19-i;
            stab.append(casillas.get(i).toString());
            stab.append(String.format(formato," "));//
            stab.append(casillas. get(31+j).toString());
            stab.append("\n");
        }
        stab.append(casillas.get(11).toString());
        stab.append(String.format("\u001B[4m%"+(Casilla.lonMaxNombre+6+3)*9+"s\u001B[0m"," "));//
        stab.append(casillas.get(31+8).toString());
        stab.append("\n");

        for (int i = 10; i>=0; i--){//for (int i = (13 + 8*2); i < 40; i++) {
            stab.append(casillas.get(i).toString());
        }
        return stab.toString();
    }

    //Getters
    public ArrayList<Casilla> getCasillas() {
        return casillas;
    }

    public Casilla getCasilla(int index){
        return casillas.get(index);
    }

    public Casilla getCasilla (String nombre) {
        if (casillasMap.get(nombre)==null) return null;
        return casillas.get(casillasMap.get(nombre));
    }

    public static int getCcasa() {
        return ccasa;
    }

    public static int getCdeporte() {
        return cdeporte;
    }

    public static int getChotel() {
        return chotel;
    }

    public static int getCpiscina() {
        return cpiscina;
    }

    public ArrayList<Grupo> getGrupos() {
        return grupos;
    }

    public void imprimirTablero(){
        Juego.getConsolaNormal().imprimir(this.toString());
    }


    public int precioTotal() {
        int c = 0;
        for (Casilla casilla:casillas){
            if (casilla instanceof Propiedad) c+=((Propiedad)casilla).getPrecio();
        }
        return c;
    }
}
