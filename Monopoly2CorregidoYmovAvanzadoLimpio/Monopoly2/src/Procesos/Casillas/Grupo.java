package Procesos.Casillas;

import java.util.ArrayList;

import Procesos.Casillas.Casilla;
import Procesos.Jugador;

public class Grupo{
    private String color;
    private ArrayList<Solar> casillas;
    private int tam;

    private int precio; //Precio por casilla

    private Jugador propietario;

    private static final int pBase = 100;
    private int numero; //Color/grupo 0-7.

    private int rentabilidad;

    private static final String[] listaInicioFormatoColor = {"\u001B[33m", "\u001B[36m", "\u001B[35m", "\u001B[38;5;208m", "\u001B[31m", "\u001B[33m", "\u001B[32m", "\u001B[34m"};
    //Métodos internos
    private static int powP(int exponente,double multiplicador){ //Cálculo de precios con multiplicador de un grupo a otro
        /*double dpBase = pBase;*/
        return (int) (pBase * Math.pow(multiplicador,exponente));
    }
    private static int powP(int exponente){ //Cálculo de precios con multiplicador 1.3
        /*double dpBase = pBase;*/
        double multiplicador = 1.3;
        return (int) (pBase * Math.pow(multiplicador,exponente));
    }

    private static int[] listaPrecios = {pBase,powP(1),powP(2),powP(3),powP(4),powP(5),powP(6),powP(7)};
//Constructores

    public Grupo(int numero) {
        color = new String[]{"Marrón", "Cián", "Rosa","Naranja","Rojo","Amarillo","Verde","Azul"}[numero];

        this.numero = numero;
        if (numero == 0 || numero == 7) tam = 2;
        else tam = 3;
        casillas = new ArrayList<>(tam);

        precio = listaPrecios[numero];

        propietario = null;
        rentabilidad=0;
    }

    //Getters
    public ArrayList<Solar> getCasillas() {
        return casillas;
    }

    public Jugador getPropietario() {
        return propietario;
    }

    public int getPrecio() {
        return precio;
    }

    public int getTam() {
        return tam;
    }

    public int getNumero() {
        return numero;
    }

    public String getColor() {
        return color;
    }
    public int getRentabilidad() {return rentabilidad;}

    public String colorFormato(){
        return listaInicioFormatoColor[numero];
    }

//Setters


    public void setCasillas(ArrayList<Solar> casillas) {
        this.casillas = casillas;
    }

    public void addCasilla (Solar casilla){
        this.casillas.add(casilla);
    }

    public void setPropietario(Jugador propietario) { //Se llama desde addCasilla en caso de sea la última casilla a comprar en el grupo
        this.propietario = propietario;
    }
    public void setRentabilidad(int rentabilidad) {this.rentabilidad = rentabilidad;}
    public static int mediaSolares(){
        return (listaPrecios[0]*2+listaPrecios[1]*3+listaPrecios[2]*3+listaPrecios[3]*3+listaPrecios[4]*3+listaPrecios[5]*3+listaPrecios[6]*3+listaPrecios[7]*2)/22;
    }

}
