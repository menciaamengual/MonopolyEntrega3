package Juego;

public abstract interface Comando {
    void ayuda(boolean haTirado);
    void imprimirTablero();
    void jugador();
    void tirarDados();
    void tirarDadosTrucados();
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
    void salirCarcel();
    void pagarDeuda();
    boolean acabarTurno(boolean haTirado);
    void acabarParada();
    void cambiarMovimiento(boolean haTirado);
    void estadisticas();
    void estadisticasJugador(String[] entradaPartida);

}
