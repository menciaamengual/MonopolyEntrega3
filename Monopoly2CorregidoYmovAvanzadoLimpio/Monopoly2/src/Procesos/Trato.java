package Procesos;
import Procesos.Casillas.*;
import Juego.Juego;

public class Trato {

    // ATRIBUTOS

    private Jugador ofertante;
    private Jugador receptor;
    int estado; // (0-2)
    // 0: El trato es nuevo. Le aparecerá al jugador receptor al comienzo de su turno.
    // 1: El trato no es nuevo y aún no ha sido aceptado
    // 2: El trato está eliminado. Es posible que este estado no haga falta, ya que al eliminar un trato podemos borrarlo del array de TratosRecibidos y TratosPropuestos de los jugadores correspondientes, y listo.
    private Propiedad propOfrecida; // Propiedad ofrecida por el ofertante
    private int dineroOfrecido; // Dinero ofrecido por el ofertante
    private Propiedad propSolicitada; // Propiedad que el receptor deberá dar
    private int dineroSolicitado; // Dinero que el receptor deberá dar
    private Propiedad propExenta; // Propiedad exenta de pago de alquiler por parte del receptor
    private int turnosExento; // Turnos que dura la exención del pago del alquiler en propExenta
    private int tipoTrato; // Puede que sea omisible, pero creo que puede facilitar mucho la impresión de un trato.

    // CONSTRUCTORES

        /* Identificamos 6 tipos de tratos:
            0. Cambiar propiedades
            1. Vender propiedad
            2. Comprar propiedad
            3. Cambiar propiedades y recibir dinero
            4. Cambiar propiedades y dar dinero
            5. Cambiar propiedades y alquiler exento

           Aplicaremos sobrecarga de métodos: cada constructor generará un tipo de trato
        */

    // 0. Cambiar propiedades
    public Trato(Jugador ofertante, Jugador receptor, Propiedad propOfrecida, Propiedad propSolicitada) {
        this.ofertante = ofertante;
        this.receptor = receptor;
        this.estado = 0;
        this.propOfrecida = propOfrecida;
        this.dineroOfrecido = 0;
        this.propSolicitada = propSolicitada;
        this.dineroSolicitado = 0;
        this.propExenta = null;
        this.turnosExento = 0;

    }

    // 1. Vender propiedad
    public Trato(Jugador ofertante, Jugador receptor, Propiedad propOfrecida, int dineroSolicitado) {
        this.ofertante = ofertante;
        this.receptor = receptor;
        this.estado = 0;
        this.propOfrecida = propOfrecida;
        this.dineroOfrecido = 0;
        this.propSolicitada = null;
        this.dineroSolicitado = dineroSolicitado;
        this.propExenta = null;
        this.turnosExento = 0;
        this.tipoTrato = 1;
    }

    // 2. Comprar propiedad
    public Trato(Jugador ofertante, Jugador receptor, int dineroOfrecido, Propiedad propSolicitada) {
        this.ofertante = ofertante;
        this.receptor = receptor;
        this.estado = 0;
        this.propOfrecida = null;
        this.dineroOfrecido = dineroOfrecido;
        this.propSolicitada = propSolicitada;
        this.dineroSolicitado = 0;
        this.propExenta = null;
        this.turnosExento = 0;
        this.tipoTrato = 2;
    }

    // 3. Cambiar propiedades y recibir dinero
    public Trato(Jugador ofertante, Jugador receptor, Propiedad propOfrecida, Propiedad propSolicitada, int dineroSolicitado) {
        this.ofertante = ofertante;
        this.receptor = receptor;
        this.estado = 0;
        this.propOfrecida = propOfrecida;
        this.dineroOfrecido = 0;
        this.propSolicitada = propSolicitada;
        this.dineroSolicitado = dineroSolicitado;
        this.propExenta = null;
        this.turnosExento = 0;
        this.tipoTrato = 3;
    }

    // 4. Cambiar propiedades y dar dinero
    public Trato(Jugador ofertante, Jugador receptor, Propiedad propOfrecida, int dineroOfrecido, Propiedad propSolicitada) {
        this.ofertante = ofertante;
        this.receptor = receptor;
        this.estado = 0;
        this.propOfrecida = propOfrecida;
        this.dineroOfrecido = dineroOfrecido;
        this.propSolicitada = propSolicitada;
        this.dineroSolicitado = 0;
        this.propExenta = null;
        this.turnosExento = 0;
        this.tipoTrato = 4;
    }

    // 5. Cambiar propiedades y alquiler exento
    public Trato(Jugador ofertante, Jugador receptor, Propiedad propOfrecida, Propiedad propSolicitada, Propiedad propExenta, int turnosExento) {
        this.ofertante = ofertante;
        this.receptor = receptor;
        this.estado = 0; // Valor predeterminado para un trato nuevo
        this.propOfrecida = propOfrecida;
        this.dineroOfrecido = 0; // No se ofrece dinero en este tipo de trato
        this.propSolicitada = propSolicitada;
        this.dineroSolicitado = 0; // No se solicita dinero en este tipo de trato
        this.propExenta = propExenta;
        this.turnosExento = turnosExento;
        this.tipoTrato = 5;
    }



    // GETTERS

    public Jugador getOfertante() {
        return ofertante;
    }

    public Jugador getReceptor() {
        return receptor;
    }

    public int getEstado() {
        return estado;
    }

    public Propiedad getPropOfrecida() {
        return propOfrecida;
    }

    public int getDineroOfrecido() {
        return dineroOfrecido;
    }

    public Propiedad getPropSolicitada() {
        return propSolicitada;
    }

    public int getDineroSolicitado() {
        return dineroSolicitado;
    }

    public Propiedad getPropExenta() {
        return propExenta;
    }

    public int getTurnosExento() {
        return turnosExento;
    }




    // SETTERS

    public void setOfertante(Jugador ofertante) {
        this.ofertante = ofertante;
    }

    public void setReceptor(Jugador receptor) {
        this.receptor = receptor;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public void setPropOfrecida(Propiedad propOfrecida) {
        this.propOfrecida = propOfrecida;
    }

    public void setDineroOfrecido(int dineroOfrecido) {
        this.dineroOfrecido = dineroOfrecido;
    }

    public void setPropSolicitada(Propiedad propSolicitada) {
        this.propSolicitada = propSolicitada;
    }

    public void setDineroSolicitado(int dineroSolicitado) {
        this.dineroSolicitado = dineroSolicitado;
    }

    public void setPropExenta(Propiedad propExenta) {
        this.propExenta = propExenta;
    }

    public void setTurnosExento(int turnosExento) {
        this.turnosExento = turnosExento;
    }


    // MÉTODOS Y UTILIDADES
    public void imprimirTratoParaProponer() {
        switch (tipoTrato) {
            case 0:
                Juego.getConsolaNormal().imprimir(ofertante.getNombre() + ": " + receptor.getNombre() + ", ¿te doy " +
                        propOfrecida.getNombre() + " y tú me das " + propSolicitada.getNombre() + "?");
                break;
            case 1:
                Juego.getConsolaNormal().imprimir(ofertante.getNombre() + ": " + receptor.getNombre() + ", ¿me compras " +
                        propOfrecida.getNombre() + " por " + dineroSolicitado + "?");
                break;
            case 2:
                Juego.getConsolaNormal().imprimir(ofertante.getNombre() + ": " + receptor.getNombre() + ", ¿me vendes " +
                        propSolicitada.getNombre() + " por " + dineroOfrecido + "?");
                break;
            case 3:
                Juego.getConsolaNormal().imprimir(ofertante.getNombre() + ": " + receptor.getNombre() + ", ¿me das " +
                        propSolicitada.getNombre() + " y " + dineroSolicitado + " a cambio de " +
                        propOfrecida.getNombre() + "?");
                break;
            case 4:
                Juego.getConsolaNormal().imprimir(ofertante.getNombre() + ": " + receptor.getNombre() + ", ¿me das " +
                        propSolicitada.getNombre() + " a cambio de " + propOfrecida.getNombre() +
                        " y " + dineroOfrecido + "?");
                break;
            case 5:
                Juego.getConsolaNormal().imprimir(ofertante.getNombre() + ": " + receptor.getNombre() + ", ¿me das " +
                        propSolicitada.getNombre() + " a cambio de " + propOfrecida.getNombre() +
                        " y que no pague alquiler en " + propExenta.getNombre() + " durante " + turnosExento +
                        " turnos?");
                break;
        }
    }

    public void imprimirTratoAceptado() {
        switch (tipoTrato) {
            case 0:
                Juego.getConsolaNormal().imprimir("Se ha aceptado el siguiente trato con " + ofertante.getNombre() +
                        ": le das " + propSolicitada.getNombre() + " y él te da " + propOfrecida.getNombre());
                break;
            case 1:
                Juego.getConsolaNormal().imprimir("Se ha aceptado el siguiente trato con " + ofertante.getNombre() +
                        ": le compras " + propOfrecida.getNombre() + " por " + dineroSolicitado);
                break;
            case 2:
                Juego.getConsolaNormal().imprimir("Se ha aceptado el siguiente trato con " + ofertante.getNombre() +
                        ": le vendes " + propSolicitada.getNombre() + " por " + dineroOfrecido);
                break;
            case 3:
                Juego.getConsolaNormal().imprimir("Se ha aceptado el siguiente trato con " + ofertante.getNombre() +
                        ": le das " + propSolicitada.getNombre() + " y le pagas " + dineroSolicitado + " a cambio de " +
                        propOfrecida.getNombre());
                break;
            case 4:
                Juego.getConsolaNormal().imprimir("Se ha aceptado el siguiente trato con " + ofertante.getNombre() +
                        ": le das " + propSolicitada.getNombre() + " a cambio de " + propOfrecida.getNombre() +
                        " y él te da " + dineroOfrecido);
                break;
            case 5:
                Juego.getConsolaNormal().imprimir("Se ha aceptado el siguiente trato con " + ofertante.getNombre() +
                        ": le das " + propSolicitada.getNombre() + " a cambio de " + propOfrecida.getNombre() +
                        " y él no pagará alquiler en " + propExenta.getNombre() + " durante " + turnosExento +
                        " turnos");
                break;
        }
    }


    public void proponerTrato(Jugador jugadorActual, Tablero tablero, Juego juego){ // ESTA FUNCION NO VA AQUI, IRÍA EN JUEGO CREO

        // Pedimos el tipo de trato

        do {
            Juego.getConsolaNormal().imprimir("Los tipos de trato que puedes proponer son los siguientes:");
            Juego.getConsolaNormal().imprimir(" 0. Cambiar una propiedad por otra");
            Juego.getConsolaNormal().imprimir(" 1. Vender una propiedad");
            Juego.getConsolaNormal().imprimir(" 2. Comprar una propiedad");
            Juego.getConsolaNormal().imprimir(" 3. Cambiar una propiedad por otra y pedir dinero");
            Juego.getConsolaNormal().imprimir(" 4. Cambiar una propiedad por otra y ofrecer dinero");
            Juego.getConsolaNormal().imprimir(" 5. Cambiar una propiedad por otra, y no pagar alquiler en otra propiedad de tu elección durante un número de turnos");
            int tipoTrato = Juego.getConsolaNormal().leerInt("Introduce el tipo de trato que quieres proponer (0-5) " + " : ");
            if(tipoTrato > -1 && tipoTrato <6){
                break;
            }
        } while (true);

        // Pedimos el nombre del jugador al que se le va a proponer el trato

        String nombreReceptor = Juego.getConsolaNormal().leer("Introduce el nombre del jugador a quien quieres proponer el trato" + " : ");
        if (juego.getJugador(nombreReceptor) == null){
            Juego.getConsolaNormal().imprimir("De momento, ese tal " + nombreReceptor + " no está disfrutando de nuestro increíble Monopoly...");
            return;
        }

        // Pedimos los datos necesarios para cada tipo de trato

        switch (tipoTrato){
            case 0:

                // Pedimos la propiedad ofrecida

                String nombrePropOfrecida = Juego.getConsolaNormal().leer("Nombre de la propiedad que quieres ofrecer" + " : ");
                if (!(tablero.getCasilla(nombrePropOfrecida) instanceof Propiedad) || tablero.getCasilla(nombrePropOfrecida)==null){ // Si lo introducido NO es una propiedad o es una casilla inexistente...
                    Juego.getConsolaNormal().imprimir("Eso no es una propiedad.");
                    return;
                }
                if(!(((Propiedad) tablero.getCasilla(nombrePropOfrecida)).getPropietario().equals(jugadorActual))){ // Si el jugador que propone el trato no es propietario...
                    Juego.getConsolaNormal().imprimir("Okupa cabrón, ¡esa propiedad no es tuya!");
                    return;
                }

                // Pedimos la propiedad solicitada

                String nombrePropSolicitada = Juego.getConsolaNormal().leer("Nombre de la propiedad que quieres solicitar" + " : ");
                if (!(tablero.getCasilla(nombrePropSolicitada) instanceof Propiedad) || tablero.getCasilla(nombrePropSolicitada)==null){ // Si lo introducido NO es una propiedad o es una casilla inexistente...
                    Juego.getConsolaNormal().imprimir("Eso no es una propiedad.");
                    return;
                }
                if(!(((Propiedad) tablero.getCasilla(nombrePropSolicitada)).getPropietario().equals(juego.getJugador(nombreReceptor)))){ // Si el jugador al que se le propone el trato no es propietario de dicha propiedad
                    Juego.getConsolaNormal().imprimir(nombreReceptor + " no es el propietario de " + nombrePropSolicitada);
                    return;
                }

                // Ahora que ya tenemos lo necesario, llamamos al constructor



            case 1:






            case 2:
            case 3:
            case 4:
            case 5:
        }
    }





}
