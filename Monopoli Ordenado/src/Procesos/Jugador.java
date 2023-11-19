package Procesos;

import java.util.*;


public class Jugador {
    //ATRIBUTOS
    private char avatar;
    private String nombre;
    private int tipo; //0-4. Ninguno, Pingüino,submarino, maletín y vaso canopo
    private int posicion;
    private int dinero;
    private ArrayList<Casilla> propiedades;
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

    public ArrayList<Casilla> getPropiedades() {
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
                for (Casilla ite: propiedades){
                    int n = ite.getEdificios().size();
                    for (int i = 0; i<n; i++)
                        ite.venderEdificio(ite.getEdificios().get(0),this);
                    ite.setPropietario(acreedor);
                }
                propiedades.clear();
            }
            else
                for (Casilla ite: propiedades){
                    ite.setPropietario(acreedor);
                }
        pagar(dinero,acreedor);
            System.out.println("Ahora estás en banca rota");
            bancarrota = true;
            dinero = 0; //Solo por si acaso
    }

    /**
     * Función para pagar alquiler, impuestos... considerando la posibilidad de bancarrota
     *
     * @param alquiler calculado (en principio) con la función calcularAlquiler(), cantidad a pagar
     * @return si puede o no pagar
     */
    public boolean pagar(int alquiler, Jugador cobrador) {
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
            if (!ite.getHipotecado()) return true;
        }
        return false;
    }

    public void setPosicion(int posicion, ArrayList<Casilla> casillas) {
        casillas.get(this.posicion).removeOcupante(this);
        this.posicion = posicion;
        casillas.get(posicion).addOcupante(this);
        casillas.get(posicion).setVisitas(casillas.get(posicion).getVisitas()+1);
    }

    public void setPropiedades(ArrayList<Casilla> propiedades) {
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

    public void setVecesDados(int vecesDados) {
        this.vecesDados = vecesDados;
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
    public void addPropiedad(Casilla propiedad) {
        propiedades.add(propiedad);
        if (propiedad.getTipo() == 0) { //Solo trabajamos con grupos si es un solar
            for (Casilla ite : propiedad.getGrupo().getCasillas()) {
                if (!ite.getPropietario().equals(this) || ite.getPropietario().getNombre().equals("Banca")) return;
            }
            propiedad.getGrupo().setPropietario(this);
        }
    }

    public void removePropiedad(Casilla propiedad) {
        propiedades.remove(propiedad);
    }

    public int getNTrans() {
        int i = 0;
        for (Casilla casilla : propiedades) {
            if (casilla.getTipo() == 1) i++;
        }
        return i;
    }

    public int getNServicios() {
        int i = 0;
        for (Casilla casilla : propiedades) {
            if (casilla.getTipo() == 2) i++;
        }
        return i;
    }


    public boolean equals(Jugador obj) {
        return nombre.equals(obj.getNombre());
    }

    @Override
    public String toString() {
        ArrayList<Edificio> edificios = new ArrayList<>();
        StringBuilder sProp = new StringBuilder();
        StringBuilder sHip = new StringBuilder();
        for (Casilla prop : propiedades) {
            if (prop.getEdificios()!=null) edificios.addAll(prop.getEdificios());
            if (!prop.getHipotecado()) sProp.append(prop.getNombre()).append(" ");
            else sHip.append(prop.getNombre()).append(" ");
        }
        return "{\n" +
                "nombre: " + nombre + ",\n" +
                "avatar: " + avatar + ",\n" +
                "fortuna: " + dinero + ",\n" +
                "propiedades: [" + sProp + "]\n" +
                "hipotecas: [" + sHip + "]\n" +
                "edificios: "+edificios+"\n" +
                "}";
    }


}

