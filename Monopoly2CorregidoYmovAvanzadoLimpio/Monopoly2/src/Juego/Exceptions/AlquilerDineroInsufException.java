package Juego.Exceptions;

import Procesos.Jugador;

public class AlquilerDineroInsufException extends JuegoException {
    private Jugador cobrador;
    private int dineroPendiente;
    public AlquilerDineroInsufException(Jugador j, int valor) {
        super("DineroInsuf");
        cobrador = j;
        dineroPendiente = valor;
    }

    public int getDineroPendiente() {
        return dineroPendiente;
    }

    public Jugador getCobrador() {
        return cobrador;
    }
}
