package Juego;

public abstract interface Comando {
    void ayuda(boolean haTirado);
    void imprimirTablero();
    void jugador();
    void tirarDados();
    void tirarDadosTrucados();
    void comprar(String[] entradaPartida, boolean haTirado);
    void edificar();
    void venderEdificio();
    void hipotecar(String[] entradaPartida);
    void deshipotecar(String[] entradaPartida);
    void bancarrota();
    void describirJugador();
    void describirCasilla();
    void describirAvatar();
    void listarJugadores();
    void listarAvatares();
    void listarEnVenta();
    void listarEdificiosColor(String[] entradaPartida);
    void listarEdificios();
    void salirCarcel();
    void pagarDeuda();
    void acabarTurno();
    void acabarPartida();
    void cambiarMovimiento();
    void estadisticas();
    void estadisticasJugador();

}
