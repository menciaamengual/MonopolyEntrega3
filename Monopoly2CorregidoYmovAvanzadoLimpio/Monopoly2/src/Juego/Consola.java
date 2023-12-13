package Juego;

public interface Consola {
    void imprimir(String mensaje);
    void imprimirError(String mensaje);
    String leer(String descripcion);
    String[] leerConsolaFragmentado(String descripcion);
    int leerInt(String descripcion);
}
