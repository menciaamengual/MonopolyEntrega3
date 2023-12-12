package Juego;

import Juego.Exceptions.Lectura.LeerException;
import Juego.Exceptions.Lectura.LeerIncorrectoException;

public interface Comando {
    void ayuda(boolean haTirado);
    void imprimirTablero();
    void jugador();
    boolean tirarDados(boolean haTirado, String[] entradaPartida) throws LeerException;
    void comprarComando(String[] entradaPartida, boolean haTirado) throws LeerIncorrectoException;
    void edificar(String[] entradaPartida);
    void venderEdificio(String[] entradaPartida);
    void hipotecar(String[] entradaPartida) throws LeerIncorrectoException;
    void deshipotecar(String[] entradaPartida) throws LeerIncorrectoException;
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
