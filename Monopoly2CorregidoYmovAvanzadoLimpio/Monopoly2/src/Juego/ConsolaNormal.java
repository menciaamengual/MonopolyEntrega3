package Juego;


import Juego.Exceptions.Lectura.LeerException;
import Juego.Exceptions.Lectura.LeerIntException;

import java.util.Scanner;

public final class ConsolaNormal implements Consola{

    @Override
    public void imprimir(String mensaje) {
        System.out.println(mensaje);
    }

    @Override
    public String leer(String descripcion) {
        System.out.print(descripcion);
        Scanner entrada = new Scanner(System.in);
        return entrada.nextLine();
    }
    public void imprimirError(String mensaje) {
        System.out.println(mensaje);
    }

    @Override
    public String[] leerConsolaFragmentado(String descripcion) {
        System.out.print(descripcion);
        Scanner entrada = new Scanner(System.in);
        String entradaString = entrada.nextLine();
        return entradaString.split(" ");
    }
    public int leerInt (String descripcion){
        try {
            try {
                System.out.println(descripcion);
                Scanner entrada = new Scanner(System.in);
                return entrada.nextInt();
            } catch (Exception e) {
                throw new LeerIntException();
            }
        }catch(LeerException le){
            return leerInt(descripcion);
        }
    }
}
