package Procesos.Casillas;

import Procesos.Jugador;
import Procesos.Tablero;
import Juego.Juego;

import java.util.ArrayList;

public final class Solar extends Propiedad {
    private final Grupo grupo;
    private int alquilerBase;
    private final ArrayList<Edificio> edificios;

    public Solar(int pBase, int pSalida, int posicion, String nombre, ArrayList<Grupo> grupos, Jugador propietario) {
        super(posicion, nombre, propietario);
        Grupo[] ArrayAux_grupo = {null, grupos.get(1 - 1), null, grupos.get(1 - 1), null, null, grupos.get(2 - 1), null, grupos.get(2 - 1), grupos.get(2 - 1), null, grupos.get(3 - 1), null, grupos.get(3 - 1), grupos.get(3 - 1), null, grupos.get(4 - 1), null, grupos.get(4 - 1), grupos.get(4 - 1), null, grupos.get(5 - 1), null, grupos.get(5 - 1), grupos.get(5 - 1), null, grupos.get(6 - 1), grupos.get(6 - 1), null, grupos.get(6 - 1), null, grupos.get(7 - 1), grupos.get(7 - 1), null, grupos.get(7 - 1), null, null, grupos.get(8 - 1), null, grupos.get(8 - 1)};
        //int[] ArrayAux_tipoCasilla = {7, 0, 3, 0, 6, 1, 0, 3, 0, 0, 4, 0, 2, 0, 0, 1, 0, 3, 0, 0, 5, 0, 3, 0, 0, 1, 0, 0, 2, 0, 8, 0, 0, 3, 0, 1, 3, 0, 6, 0};
        this.setOcupantes(new ArrayList<>(6));
        grupo = ArrayAux_grupo[posicion];
        this.alquilerBase = 0;
        grupo.addCasilla(this);
        setPrecio(grupo.getPrecio());
        alquilerBase = (int) (getPrecio() * 0.1);
        edificios = new ArrayList<>();
    }

    //Funciones generales
    public final int calcularAlquiler() {
        int alq = alquilerBase;
        int ncasas = 0;
        if (!edificios.isEmpty()) { //Calculo de número de edificios
            for (Edificio ite : edificios) {
                if (ite instanceof Casa)
                        ncasas++;
                else if (ite instanceof Hotel)
                        alq += alquilerBase * 70;
                else if (ite instanceof Piscina || ite instanceof Pista)
                        alq += alquilerBase * 25;
                }
            switch (ncasas) { //Considerams la adición de los edificios
                case 1:
                    alq += 5 * alquilerBase;
                    break;
                case 2:
                    alq += 15 * alquilerBase;
                    break;
                case 3:
                    alq += 35 * alquilerBase;
                    break;
                case 4:
                    alq += 50 * alquilerBase;
                    break;
            }
            if (grupo.getPropietario() != null) {
                return alq + alquilerBase;
            } else {
                return alq;
            }

            //Considerar el resto de edificios
        } else return alquilerBase;
    }

    //toString
    @Override
    public final String toString() {
        String formato = "|\u001B[4m%" + lonMaxNombre + "s %" + 6 + "s\u001B[0m|";
        if (grupo != null) formato = grupo.colorFormato() + formato + "\u001B[0m";
        return String.format(formato, getNombre(), StringAvatares());
    }

    //Hipotecas - Hacemos override de hipotecar para tener en cuenta los edificios

    /**
     * Comprueba si el solar tiene edificios, y si no, hipoteca:
     * Añadimos dinero al propietario
     * Ponemos hipotecado a true.
     */
    public final void hipotecar() {
        if (edificios != null && !edificios.isEmpty()) {//Overrideamos esto en solar
            Juego.getConsolaNormal().imprimir("Debes vender todos los edificios antes de hipotecar.");
            return;
        } else if (getHipotecado()) {
            Juego.getConsolaNormal().imprimir("No puedes hipotecar una propiedad ya hipotecada.");
            return;
        }
        setHipotecado(true);
        getPropietario().addDinero(getPrecio() / 2);
        Juego.getConsolaNormal().imprimir("Has hipotecado " + this + " por " + getPrecio() / 2 + "$");
    }

    //Edificios
    public final void construir(int tipo, Jugador jugador, boolean caida) {
        if (grupo.getPropietario() != getPropietario() && !caida || !jugador.equals(getPropietario())) { //Comprobación de propiedad
            Juego.getConsolaNormal().imprimir("No puedes construir si no tienes todo el grupo on no has caído 2 veces en esta casilla...");
            return;
        }

        int countc = 0, counth = 0, countp = 0, countd = 0, countgh = 0, countgc = 0; //Contador de casas por casilla, hoteles por grupo y casas por grupo
        Edificio edificio;
        switch (tipo){
            case 1: edificio = new Hotel (getPrecio()); break;
            case 2: edificio = new Piscina (getPrecio()); break;
            case 3: edificio = new Pista (getPrecio()); break;
            default: edificio = new Casa (getPrecio());
        }

        if (jugador.getDinero() < edificio.getPrecio()) { //Comprobación dinero
            Juego.getConsolaNormal().imprimir("Dinero insuficiente, inténtalo otra vez cuando tengas más dinero");
            return;
        }

        for (Edificio ite : edificios) {
            if (ite instanceof Casa) countc++;
            if (ite instanceof Hotel) counth++;
            if (ite instanceof Piscina) countp++;
            if (ite instanceof Pista) countd++;
        }
        for (Solar cite : grupo.getCasillas()) { //A solucionar...
            for (Edificio ite : cite.getEdificios()) {
                if (ite instanceof Hotel) countgh++;
                if (ite instanceof Casa) countgc++;
            }
        }
        switch (tipo) {
            case 0: //Mover para aquí el Tablero.contador...++
                if (countc >= 4) { //Comprobación de casas/solar //HAY QUE MIRAR QUE DEJA CONSTRUIR MÁS CASAS DE LAS DEBIDAS CUANDO ESTÁ LLENO DE HOTELES
                    Juego.getConsolaNormal().imprimir("Pedro Sánchez ha aprobado una ley de urbanismo que impide tener más de 4 casas por casilla ¡Vaya con estos socialistas!");
                } else if (countgh >= grupo.getTam() && countgc >= grupo.getTam()) {
                    Juego.getConsolaNormal().imprimir("El ministerio de igualdad prohibe que un solo grupo tenga tantos hoteles y casas a la vez, si quieres construir otra casa, debes vender algo antes o haber votado mejor...");
                } else {
                    Juego.getConsolaNormal().imprimir("¡OLE! Enhorabuena por la construcción de tu casa, ahora eres aún más especulador");
                    Juego.getConsolaNormal().imprimir("Pagas " + edificio.getPrecio() + " por la construccion");
                    edificios.add(edificio);
                    jugador.pagar(edificio.getPrecio());
                    jugador.setFortuna(jugador.getFortuna() + 2 * edificio.getPrecio()); //en pagar lo quitamos y ahora tenemos q sumarlo
                    jugador.setDineroInvertido(jugador.getDineroInvertido() + getPrecio());
                    edificio.setCasilla(this);
                    Tablero.addCcasa();
                }
                break;
            case 1: //Se cobra aunq no se construya el hotel Y NO SE COMPRUEBA QUE HAYA CASAS SUFICIENTES y se crea el edificio aunq no corresponda
                if (countc < 4) Juego.getConsolaNormal().imprimir("Debes tener 4 casas en esta propiedad para construir un hotel...");
                else if (countgh >= grupo.getTam())
                    Juego.getConsolaNormal().imprimir("No puedes construir más hoteles en esta propiedad");
                else {
                    Juego.getConsolaNormal().imprimir("Enhorabuena, estás colaborando con la gentrificación de tu ciudad");
                    construiccionInter(jugador, edificio);
                    Tablero.addChotel();
                    edificios.removeIf(ite -> ite instanceof Casa);
                }
                break;
            case 2:
                if (countp >= 1) Juego.getConsolaNormal().imprimir("No puedes construir más piscinas en esta propiedad");
                else if (countc < 2 || counth < 1)
                    Juego.getConsolaNormal().imprimir("Necesitas tener al menos 2 casas y 1 hotel para construir una piscina en esta propiedad");
                else {
                    Juego.getConsolaNormal().imprimir("Enhorabuena, estás colaborando con la gentrificación de tu ciudad");
                    construiccionInter(jugador, edificio);
                    Tablero.addCpiscina();
                }
                break;
            case 3:
                if (countd >= 1) Juego.getConsolaNormal().imprimir("No puedes construir más pistas en esta propiedad");
                else if (counth < 2)
                    Juego.getConsolaNormal().imprimir("Necesitas tener al menos 2 hoteles para construir una piscina en esta propiedad");
                else {
                    Juego.getConsolaNormal().imprimir("Enhorabuena, estás colaborando mucho con la gentrificación de tu ciudad");
                    construiccionInter(jugador, edificio);
                    Tablero.addCdeporte();
                }
                break;
        }
    }

    private void construiccionInter(Jugador jugador, Edificio edificio) {
        Juego.getConsolaNormal().imprimir("Pagas " + edificio.getPrecio() + " por construir");
        edificios.add(edificio);
        jugador.pagar(edificio.getPrecio());
        jugador.setFortuna(jugador.getFortuna() + 2 * edificio.getPrecio()); //en pagar lo quitamos y ahora tenemos q sumarlo
        jugador.setDineroInvertido(jugador.getDineroInvertido() + getPrecio());
        edificio.setCasilla(this);
    }

    /**
     * Realiza las comprobaciones correspondientes, destruye un edificio y devuelve su coste al jugador
     *
     * @param edificio Edificio a vender
     * @param jugador  Jugador que lo vende (comprobación interna)
     */
    public final void venderEdificio(Edificio edificio, Jugador jugador) {
        if (getPropietario().equals(jugador) && !getHipotecado() && edificio != null) {
            jugador.addDinero(edificio.getPrecio() / 2);
            Juego.getConsolaNormal().imprimir("Cobras " + edificio.getPrecio() + " por " + edificio.getIdentificador());
            removeEdificio(edificio);
        } else Juego.getConsolaNormal().imprimir("No se puede vender este edificio");
    }

    public final void venderEdificio(String sedificio, Jugador jugador) {
        Edificio edificio = getEdificio(sedificio);
        if (getPropietario().equals(jugador) && !getHipotecado() && edificio != null) {
            jugador.addDinero(edificio.getPrecio() / 2);
            removeEdificio(edificio);
        } else Juego.getConsolaNormal().imprimir("No se puede vender este edificio");
    }

    public final Edificio getEdificio(String identificador) {
        for (Edificio ite : edificios) {
            if (ite.getIdentificador().equals(identificador)) return ite;
        }
        return null; //Comprobar en la función desde la que se llame
    }

    public final void removeEdificio(Edificio edificio) {
        edificios.remove(edificio);
    }

    public final ArrayList<Edificio> getEdificios() {
        return edificios;
    }

    //Grupos
    public final Grupo getGrupo() {
        return grupo;
    }
    //Acción

    @Override
    public void accionCasilla(Jugador jugador) {
        if (!getPropietario().equals(jugador) && !getPropietario().isBanca() && !getHipotecado()) {
            jugador.pagar(calcularAlquiler(), getPropietario());
            setRentabilidad(getRentabilidad() + calcularAlquiler());
            jugador.setPagoDeAlquileres(jugador.getPagoDeAlquileres() + calcularAlquiler());
            getPropietario().setCobroDeAlquileres(getPropietario().getCobroDeAlquileres() + calcularAlquiler());
            grupo.setRentabilidad(grupo.getRentabilidad() + calcularAlquiler());
            Juego.getConsolaNormal().imprimir("Pagas " + calcularAlquiler() + "$ por caer en " + getNombre());
        } else if (getHipotecado()) {
            Juego.getConsolaNormal().imprimir("Casilla hipotecada... No pagas alquiler :)");
        } else if (getPropietario().isBanca()) {
            Juego.getConsolaNormal().imprimir("Esta propiedad aun no tiene dueño, la puedes comprar.");
        } else if (getPropietario().equals(jugador)) {
            Juego.getConsolaNormal().imprimir("Has caído en una casilla de tu propiedad, disfruta de tu estancia");
        }
    }


    //Impresión
    public final String descripcionDetallada() {
        return "{\n" +
                "nombre: " + getNombre() + "\n" +
                "color: " + grupo.getColor() + "\n" + //Estaría chulo meterle el formateo para que se vea del color que es
                "tipo: Solar \n" +
                "Propietario: " + getPropietario().getNombre() + " \n" +
                "Precio: " + getPrecio() + " \n" +
                "Edificios: " + edificios + "\n" +
                "ALQUILER ACTUAL: " + calcularAlquiler() + "$\n" +
                "   -Alquiler Básico: " + alquilerBase + "$ \n" +
                "   -Alquiler con tod el grupo: " + alquilerBase * 2 + "$ \n" +
                //Podemos meter aquí alquiler con edificios (igual no todas las combinaciones, pero un poquito).
                "Puedes hipotecar esta casilla por " + getPrecio() / 2 + "$\n" +

                //"Ocupantes: "+ ocupantes +" \n" +

                "}\n";
    }
}
