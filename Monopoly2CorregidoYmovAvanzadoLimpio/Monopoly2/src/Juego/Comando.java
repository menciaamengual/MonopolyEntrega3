package Juego;

import Juego.Exceptions.LeerException;

public interface Comando {
    void ayuda(boolean haTirado);
    void imprimirTablero();
    void jugador();
    boolean tirarDados(boolean haTirado, String[] entradaPartida) throws LeerException;
    void comprar(String[] entradaPartida, boolean haTirado);
    void edificar(String[] entradaPartida);
    void venderEdificio(String[] entradaPartida);
    void hipotecar(String[] entradaPartida);
    void deshipotecar(String[] entradaPartida);
    void bancarrota();
    void describirJugador(String[] entradaPartida);
    void describirCasilla(String s);
    void describirAvatar(String[] entradaPartida);
    void listarJugadores();
    void listarAvatares();
    void listarEnVenta();
    void listarEdificiosColor(String[] entradaPartida);
    void listarEdificios();
    void salirCarcel() throws LeerException;
    void pagarDeuda() throws LeerException;
    boolean acabarTurno(boolean haTirado);
    void acabarParada();
    void cambiarMovimiento(boolean haTirado);
    void estadisticas();
    void estadisticasJugador(String[] entradaPartida);

}
