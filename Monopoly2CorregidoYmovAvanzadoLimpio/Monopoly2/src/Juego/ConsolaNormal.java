package Juego;

import Juego.Exceptions.Lectura.LeerException;

import java.util.Scanner;

public class ConsolaNormal implements Consola{

    /**
     * Impresión de string con \n
     */
    @Override
    public void imprimir(String mensaje) {
        System.out.println(mensaje);
    }

    /**
     * Impresión de objetos con \n
     */
    @Override
    public void imprimir(Object mensaje) { //Cogemos un object
        System.out.println(mensaje);
    }
    @Override
    public String leer(String descripcion){
        System.out.print(descripcion);
        Scanner entrada = new Scanner(System.in);
        return entrada.nextLine();
    }

    @Override
    public String[] leerConsolaFragmentado(String descripcion) {
        System.out.print(descripcion);
        Scanner entrada = new Scanner(System.in);
        String entradaString = entrada.nextLine();
        return entradaString.split(" ");
    }
    @Override
    public int leerInt (String descripcion){
        try {
            try {
                System.out.println(descripcion);
                Scanner entrada = new Scanner(System.in);
                return entrada.nextInt();
            } catch (Exception e) {
                throw new LeerException("Solo se aceptan números enteros.");
            }
        }catch(LeerException le){
            return leerInt(descripcion);
        }
    }
}
