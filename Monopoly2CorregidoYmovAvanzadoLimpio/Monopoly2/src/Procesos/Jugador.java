package Procesos;

import Juego.Juego;
import Procesos.Casillas.*;

import java.util.*;


public class Jugador {
    //ATRIBUTOS
    private char avatar;
    private String nombre;
    private int tipo; //0-4. Ninguno, Pingüino,submarino, maletín y vaso canopo
    private int posicion;
    private int dinero;
    private ArrayList<Propiedad> propiedades;
    private ArrayList<Carta> cartasSuerte;
    private int turnosCarcel; //informa de la situación del jugador respecto de la cárcel:
    // 0: No está en la cárcel / está en la casilla de cárcel pero si tira dados mueve
    // 1: Está en la cárcel, y no tiene oportunidad para salir sacando dobles
    // 2: Está en la cárcel, y tiene UNA oportunidad para tirar dados, sacar dobles y salir
    // 3: Está en la cárcel, y tiene DOS oportunidades para tirar dados, sacar dobles y salir
    // 4: Está en la cárcel, y tiene TRES oportunidades para tirar dados, sacar dobles y salir
    private int vueltas;
    private boolean bancarrota;

    private int tipoMov; // Tipo de movimiento del jugador (se aplicará si lo ha activado)
    // 0: Pelota
    // 1: Coche
    private boolean movAvanzadoActivado; // Indica si el movimiento asociado a tipoMov esta activado en el jugador
    private int auxMovAvanzado;

    /* auxMovAvanzado es un atributo auxiliar que informa al programa de la situación actual del jugador respecto a su movimiento avanzado correspondiente. Se detalla el dominio de auxMovAvanzado:

        MOVIMIENTO AVANZADO TIPO 0 - PELOTA: (tipoMov == 0)

                auxMovAvanzado == 1: el jugador aún no ha llegado a la casilla final
                auxMovAvanzado == 0: el jugador ya está en la casilla final


        MOVIMIENTO AVANZADO TIPO 1 - COCHE:

                auxMovAvanzado == 3, 2, 1: el jugador ha tirado en la jugada anterior y ha obtenido más de un 3, con lo que tiene auxMovAvanzado tiradas extra restantes.
                auxMovAvanzado == 0: el jugador no puede volver a tirar, bien sea porque ha agotado sus 3 tiradas extra o porque ha fallado una de ellas. Se usa también cuando el jugador saca menos de un 3 y pasan los turnos correspondientes, para indicar que ya puede volver a moverse.
                                  Un 0 indica que el jugador no puede moverse más en ese turno (si ha sacado dobles en la última tirada, sí puede).
                auxMovAvanzado = -3, -2, -1: el jugador ha sacado menos de un 3, por lo que "se le "rompe el motor" y debe esperar dos turnos más sin tirar. El valor de auxMovAvanzado sin el signo negativo indica cuántos turnos de espera le quedan al jugador, incluido el actual (por eso se settea a -3 y no a -2, para que al acabar el turno en el que se le rompe el motor se establezca a -2 y por tanto le queden 2 turnos sin tirar).

        VALOR POR DEFECTO: 999

                Indica que el jugador puede tirar al inicio de su turno. Al acabar el turno de un jugador, se settea su auxMovAvanzado a 999 (la única excepción es el motor roto, cuando auxMovAvanzado es negativo. En tal caso, se aumenta en 1 cada vez que finaliza el turno).

    */

    private  boolean puedeComprarPropiedades; // Solo se hacen comprobaciones con él en el caso de que movAvanzado esté activado y sea tipo coche. Es como un segundo auxMovAvanzado
    private int dineroInvertido; //en tus propiedades. Hecho menos edificios
    private int pagoTasasEImpuestos; //suerte, carcel, impuestos. Hecho
    private int pagoDeAlquileres; //Hecho
    private int cobroDeAlquileres; //Hecho
    private int pasarPorCasillaDeSalida; //Hecho
    private int premiosInversionesOBote; //suerte, parking. Hecho
    private int vecesEnLaCarcel; //

    private int vecesDados;
    private int fortuna; //falta edificios


    //CONSTRUCTORES
    public Jugador(int dinero, String nombre, char avatar, int tipo) {
        this.dinero = dinero; //inicial: un tercio del valor total de los solares
        this.nombre = nombre;
        this.avatar = avatar; //Generacion del avatar random
        this.tipoMov = tipo;
        posicion = 0;
        propiedades = new ArrayList<>(20);
        cartasSuerte = null;
        turnosCarcel = 0;
        bancarrota = false;
        dineroInvertido = 0;
        pagoTasasEImpuestos = 0;
        pagoDeAlquileres = 0;
        cobroDeAlquileres = 0;
        pasarPorCasillaDeSalida = 0;
        premiosInversionesOBote = 0;
        vecesEnLaCarcel = 0;
        vecesDados=0;
        fortuna=0;
        movAvanzadoActivado = false;
        auxMovAvanzado=999; // con negativos o del 0 al 3 daba problemas con funciones ya implementadas
        puedeComprarPropiedades=true;
    }

    public Jugador() {
        dinero = 100000000;
        fortuna= 100000000;
        nombre = "Banca";
        avatar = ' ';

        posicion = 0;
        cartasSuerte = null;
        propiedades = new ArrayList<>(40); //Se le meten cuando se crean las propiedades
    }

    //GETTERS
    public String getNombre() {
        return nombre;
    }

    public ArrayList<Propiedad> getPropiedades() {
        return propiedades;
    }

    public int getDinero() {
        return dinero;
    }

    public int getPosicion() {
        return posicion;
    }

    public int getTurnosCarcel() {
        return turnosCarcel;
    }

    public int getVueltas() {
        return vueltas;
    }

    public char getAvatar() {
        return avatar;
    }

    public Boolean getBancarrota() {return bancarrota;}

    public int getDineroInvertido() {return dineroInvertido;}

    public int getPagoDeAlquileres() {return pagoDeAlquileres;}

    public int getCobroDeAlquileres() {return cobroDeAlquileres;}

    public int getPagoTasasEImpuestos() {return pagoTasasEImpuestos;}

    public int getPremiosInversionesOBote() {return premiosInversionesOBote;}

    public int getPasarPorCasillaDeSalida() {return pasarPorCasillaDeSalida;}

    public int getVecesEnLaCarcel() {return vecesEnLaCarcel;}

    public int getVecesDados() {return vecesDados;}
    public int getFortuna() {return fortuna;}

    public int getAuxMovAvanzado(){
        return auxMovAvanzado;
    }

    public int getTipoMov(){
        return tipoMov;
    }

    public boolean getPuedeComprarPropiedades(){
        return puedeComprarPropiedades;
    }

    public boolean getMovAvanzadoActivado() {
        return movAvanzadoActivado;
    }



    /**
     * @param casillas
     * @return
     */
    public Casilla getCasilla(ArrayList<Casilla> casillas) {
        return casillas.get(posicion);
    }

    //SETTERS
    public void addDinero(int dinero) {
        fortuna += dinero;
        this.dinero += dinero;
    }

    public void setTipoMov(int tipo){
        tipoMov=tipo;
    }

    public void setAuxMovAvanzado(int aux){
        auxMovAvanzado = aux;
    }

    public void setMovAvanzadoActivado(boolean movEspecialActivado){
        this.movAvanzadoActivado = movEspecialActivado;
    }

    public void setPuedeComprarPropiedades(boolean puedeComprarPropiedades) {
        this.puedeComprarPropiedades = puedeComprarPropiedades;
    }

    public void setDinero(int dinero) {
        this.dinero = dinero;
    }

    //METODOS PUBLICOS
    public Boolean inCarcel() {
        return this.turnosCarcel != 0;
    }

    public Boolean isBanca() {
        return nombre.equals("Banca");
    }

    public void declararBancarrota(Jugador acreedor) { //acabar función
            ArrayList<Edificio> vacio = new ArrayList<>();
            if (acreedor.isBanca()){
                for (Propiedad ite: propiedades) if (ite instanceof Solar) {
                    int n = ((Solar)ite).getEdificios().size();
                    for (int i = 0; i<n; i++)
                        ((Solar)ite).venderEdificio(((Solar)ite).getEdificios().get(0),this);
                    ite.setPropietario(acreedor);
                }
                propiedades.clear();
            }
            else
                for (Casilla ite: propiedades){
                    ((Solar)ite).setPropietario(acreedor);
                }
        pagar(dinero,acreedor);
        Juego.getConsolaNormal().imprimir("Ahora estás en banca rota");
            bancarrota = true;
            dinero = 0; //Solo por si acaso
    }

    /**
     * Función para pagar alquiler, impuestos... considerando la posibilidad de bancarrota
     *
     * @param alquiler calculado (en principio) con la función calcularAlquiler(), cantidad a pagar
     * @return si puede o no pagar
     */
    public boolean pagar(int alquiler, Jugador cobrador) { //Todo actualizar esto para que sea como pagarAv
        if (dinero<alquiler)
            return false;
            //gestAcreedores(alquiler,cobrador);
        if (!bancarrota){
            dinero -= alquiler;
            fortuna -= alquiler;
            cobrador.addDinero(alquiler);
            return true;
        }
        return false;
    }


    //todo modificar con excepciones.
    /**
     *
     * @param alquiler
     * @return si puede o no pagar
     */
    public boolean pagar(int alquiler) { // Para pagos que no recibe el jugador, como pagar para salir de la cárcel
        if (dinero<alquiler)
            return false;
            //gestAcreedores(alquiler,null);
        if (!bancarrota){
            dinero -= alquiler;
            fortuna -=alquiler;
            return true;
        }
        return false;
    }

    public boolean tienePropiedadesSinHipotecar() {
        for (Casilla ite : propiedades) {
            if (!((Propiedad)ite).getHipotecado()) return true;
        }
        return false;
    }

    public void setPosicion(int posicion, ArrayList<Casilla> casillas) {
        casillas.get(this.posicion).removeOcupante(this);
        this.posicion = posicion;
        casillas.get(posicion).addOcupante(this);
        casillas.get(posicion).setVisitas(casillas.get(posicion).getVisitas()+1);
    }

    public void setPropiedades(ArrayList<Propiedad> propiedades) {
        this.propiedades = propiedades;
    }

    public void setBancarrota(boolean bancarrota) {
        this.bancarrota = bancarrota;
    }

    public void setVueltas(int vueltas) {
        this.vueltas = vueltas;
    }

    public void setTurnosCarcel(int turnosCarcel) {
        this.turnosCarcel = turnosCarcel;
    }

    public void setCobroDeAlquileres(int cobroDeAlquileres) {
        this.cobroDeAlquileres = cobroDeAlquileres;
    }

    public void setDineroInvertido(int dineroInvertido) {
        this.dineroInvertido = dineroInvertido;
    }

    public void setVecesEnLaCarcel(int vecesEnLaCarcel) {
        this.vecesEnLaCarcel = vecesEnLaCarcel;
    }

    public void setPremiosInversionesOBote(int premiosInversionesOBote) {
        this.premiosInversionesOBote = premiosInversionesOBote;
    }

    public void setPasarPorCasillaDeSalida(int pasarPorCasillaDeSalida) {
        this.pasarPorCasillaDeSalida = pasarPorCasillaDeSalida;
    }

    public void setPagoDeAlquileres(int pagoDeAlquileres) {
        this.pagoDeAlquileres = pagoDeAlquileres;
    }

    public void setPagoTasasEImpuestos(int pagoTasasEImpuestos) {
        this.pagoTasasEImpuestos = pagoTasasEImpuestos;
    }

    public void addVecesDados() {
        this.vecesDados++;
    }
    public void setFortuna(int fortuna) {this.fortuna = fortuna;}

    //Utilidad
    public void enviarCarcel(ArrayList<Casilla> casillas) {
        turnosCarcel = 4;
        setPosicion(10, casillas);
    }

    /**
     * Añade una propiedad a un jugador, marcandolo también como propietario del grupo en caso de que corresponda
     *
     * @param propiedad
     */
    public void addPropiedad(Propiedad propiedad) {
        propiedades.add(propiedad);
        if (propiedad instanceof Solar) { //Solo trabajamos con grupos si es un solar
            for (Solar ite : ((Solar)propiedad).getGrupo().getCasillas()) {
                if (!ite.getPropietario().equals(this) || ite.getPropietario().getNombre().equals("Banca")) return;
            }
            ((Solar)propiedad).getGrupo().setPropietario(this);
        }
    }

    public void removePropiedad(Casilla propiedad) {
        propiedades.remove(propiedad);
    }

    public int getNTrans() {
        int i = 0;
        for (Casilla casilla : propiedades) {
            if (casilla instanceof Transporte) i++;
        }
        return i;
    }

    public int getNServicios() {
        int i = 0;
        for (Casilla casilla : propiedades) {
            if (casilla instanceof Servicios) i++;
        }
        return i;
    }


    public boolean equals(Jugador obj) {
        return nombre.equals(obj.getNombre());
    }

    @Override
    public String toString() {
        ArrayList<Edificio> edificios = new ArrayList<>();
        StringBuilder sHip = new StringBuilder();
        for (Propiedad prop : propiedades) {
            if (prop instanceof Solar && ((Solar)prop).getEdificios()!=null) edificios.addAll(((Solar)prop).getEdificios());
            else sHip.append(prop.getNombre()).append(" ");
        }
        return "{\n" +
                "nombre: " + nombre + ",\n" +
                "avatar: " + avatar + ",\n" +
                "fortuna: " + dinero + ",\n" +
                "propiedades: "+ propiedades +"\n" +
                "hipotecas: [" + sHip + "]\n" +
                "edificios: "+edificios+"\n" +
                "}";
    }
}

