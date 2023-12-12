package Juego;

import Juego.Exceptions.Lectura.LeerException;

public interface Consola {
    void imprimir(String mensaje);

    void imprimir(Object mensaje);

    String leer(String descripcion);
    String[] leerConsolaFragmentado(String descripcion);
    int leerInt(String descripcion) throws LeerException;
}
