package Procesos;

import Juego.Juego;
import Procesos.Casillas.*;
import Procesos.Avatares.*;

import java.util.*;


public class Jugador {
    //ATRIBUTOS
    private Avatar avatar; // Avatar del jugador. Almacena to' lo relativo a posiciones, movimiento y cárcel
    private String nombre;
    private int dinero;
    private ArrayList<Propiedad> propiedades;
    private ArrayList<Carta> cartasSuerte;
    private int vueltas;
    private boolean bancarrota;
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
    public Jugador(int dinero, String nombre, Avatar avatar) {
        this.dinero = dinero; //inicial: un tercio del valor total de los solares
        this.nombre = nombre;
        this.avatar = avatar; //Generacion del avatar random
        propiedades = new ArrayList<>(20);
        cartasSuerte = null;
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
    }

    public Jugador() {
        dinero = 100000000;
        fortuna= 100000000;
        nombre = "Banca";
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

    public int getVueltas() {
        return vueltas;
    }

    public Avatar getAvatar() {
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




    /**
     * @param casillas
     * @return
     */
    public Casilla getCasilla(ArrayList<Casilla> casillas) {
        return casillas.get(avatar.getPosicion());
    }

    //SETTERS
    public void addDinero(int dinero) {
        fortuna += dinero;
        this.dinero += dinero;
    }

    public void setAvatar(Avatar avatar){
        this.avatar = avatar;
    }

    public void setDinero(int dinero) {
        this.dinero = dinero;
    }

    //METODOS PUBLICOS

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

    public void setPropiedades(ArrayList<Propiedad> propiedades) {
        this.propiedades = propiedades;
    }

    public void setBancarrota(boolean bancarrota) {
        this.bancarrota = bancarrota;
    }

    public void setVueltas(int vueltas) {
        this.vueltas = vueltas;
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
                "avatar: " + avatar.getIdentificador() + ",\n" +
                "fortuna: " + dinero + ",\n" +
                "propiedades: "+ propiedades +"\n" +
                "hipotecas: [" + sHip + "]\n" +
                "edificios: "+edificios+"\n" +
                "}";
    }
}

