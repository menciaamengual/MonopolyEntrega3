package Juego;

import java.util.Scanner;

public class ConsolaNormal implements Consola{

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

    @Override
    public String[] leerFragmentado(String descripcion) {
        System.out.println(descripcion);
        Scanner entrada = new Scanner(System.in);
        String entradaString = entrada.nextLine();
        return entradaString.split(" ");
    }
    public int leerInt (String descripcion){
        System.out.println(descripcion);
        Scanner entrada = new Scanner(System.in);
        return entrada.nextInt();
    }
}
