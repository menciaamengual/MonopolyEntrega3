package Juego;

public interface Consola {
    void imprimir(String mensaje);
    String leer(String descripcion);
    String[] leerFragmentado(String descripcion);
    int leerInt(String descripcion);
}
