package Procesos.Casillas;

import Procesos.Jugador;
import Procesos.Tablero;

import java.util.ArrayList;

public class Solar extends Propiedad {
    private Grupo grupo;
    private int alquilerBase;
    private ArrayList<Edificio> edificios;

    public Solar(int pBase, int pSalida, int posicion, String nombre, ArrayList<Grupo> grupos, Jugador propietario) {
        super(posicion,nombre,propietario);
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
    public int calcularAlquiler(){
        int alq = alquilerBase;
                if (!edificios.isEmpty()){ //Calculo de número de edificios
                    int ncasas = 0;
                    for(Edificio ite: edificios){
                       switch (ite.getTipo()){
                           case 0:
                               ncasas++;
                               break;
                           case 1:
                               alq+=alquilerBase*70;
                               break;
                           case 2:
                           case 3:
                               alq+=alquilerBase*25;
                               break;
                       }
                    }
                    switch (ncasas){ //Considerams la adición de los edificios
                        case 1: alq+=5*alquilerBase;  break;
                        case 2: alq+=15*alquilerBase; break;
                        case 3: alq+=35*alquilerBase; break;
                        case 4: alq+=50*alquilerBase; break;
                    }
                    if(grupo.getPropietario()!=null){
                        return alq+alquilerBase;
                    }else{
                        return alq;
                    }

                    //Considerar el resto de edificios
                }
                else return alquilerBase;
    }

    //toString
    @Override
    public String toString (){
        String formato = "|\u001B[4m%"+lonMaxNombre+"s %"+6+"s\u001B[0m|";
        if (grupo!=null) formato = grupo.colorFormato() + formato + "\u001B[0m";
        return String.format(formato, getNombre(),StringAvatares());
    }

    //Hipotecas - Hacemos override de hipotecar para tener en cuenta los edificios
    /**
     * Comprueba si el solar tiene edificios, y si no, hipoteca:
     *  Añadimos dinero al propietario
     *  Ponemos hipotecado a true.
      */
    public void hipotecar(){
        if (edificios!=null && !edificios.isEmpty()){//Overrideamos esto en solar
            System.out.println("Debes vender todos los edificios antes de hipotecar.");
            return;
        }
        else if (getHipotecado()){
            System.out.println("No puedes hipotecar una propiedad ya hipotecada.");
            return;
        }
        setHipotecado(true);
        getPropietario().addDinero(getPrecio() /2);
        System.out.println("Has hipotecado "+ this+" por "+ getPrecio() /2+"$");
    }

    public void construir(int tipo,Jugador jugador, boolean caida) {
        if (grupo.getPropietario()!= getPropietario() && !caida || !jugador.equals(getPropietario())) { //Comprobación de propiedad
            System.out.println("No puedes construir si no tienes todo el grupo on no has caído 2 veces en esta casilla...");
            return;
        }

        int countc = 0, counth = 0, countp = 0, countd = 0, countgh = 0, countgc=0; //Contador de casas por casilla, hoteles por grupo y casas por grupo
        Edificio edificio = new Edificio(tipo, getPrecio());

        if(jugador.getDinero()<edificio.getPrecio()){ //Comprobación dinero
            System.out.println("Dinero insuficiente, inténtalo otra vez cuando tengas más dinero");
            return;
        }

        for (Edificio ite: edificios){
            if (ite.getTipo()==0) countc++;
            if (ite.getTipo()==1) counth++;
            if (ite.getTipo()==2) countp++;
            if (ite.getTipo()==3) countd++;
        }
        for (Solar cite: grupo.getCasillas()){ //A solucionar...
            for (Edificio ite: cite.getEdificios()){
                if (ite.getTipo()==1) countgh++;
                if (ite.getTipo()==0) countgc++;
            }
        }
        switch (tipo) {
            case 0: //Mover para aquí el Tablero.contador...++
                if (countc>=4){ //Comprobación de casas/solar //HAY QUE MIRAR QUE DEJA CONSTRUIR MÁS CASAS DE LAS DEBIDAS CUANDO ESTÁ LLENO DE HOTELES
                    System.out.println("Pedro Sánchez ha aprobado una ley de urbanismo que impide tener más de 4 casas por casilla ¡Vaya con estos socialistas!");
                }
                else if(countgh >= grupo.getTam() && countgc>=grupo.getTam()){
                        System.out.println("El ministerio de igualdad prohibe que un solo grupo tenga tantos hoteles y casas a la vez, si quieres construir otra casa, debes vender algo antes o haber votado mejor...");
                }
                else{
                    System.out.println("¡OLE! Enhorabuena por la construcción de tu casa, ahora eres aún más especulador");
                    System.out.println("Pagas "+edificio.getPrecio()+" por la construccion");
                    edificios.add(edificio);
                    jugador.pagar(edificio.getPrecio());
                    jugador.setFortuna(jugador.getFortuna()+ 2*edificio.getPrecio()); //en pagar lo quitamos y ahora tenemos q sumarlo
                    jugador.setDineroInvertido(jugador.getDineroInvertido()+ getPrecio());
                    edificio.setCasilla(this);
                    Tablero.addCcasa();
                }
                break;
            case 1: //Se cobra aunq no se construya el hotel Y NO SE COMPRUEBA QUE HAYA CASAS SUFICIENTES y se crea el edificio aunq no corresponda
                if (countc<4) System.out.println("Debes tener 4 casas en esta propiedad para construir un hotel...");
                else if (countgh>=grupo.getTam()) System.out.println("No puedes construir más hoteles en esta propiedad");
                else{
                    System.out.println("Enhorabuena, estás colaborando con la gentrificación de tu ciudad");
                    System.out.println("Pagas "+edificio.getPrecio()+" por construir");
                    edificios.add(edificio);
                    jugador.pagar(edificio.getPrecio());
                    jugador.setFortuna(jugador.getFortuna()+ 2*edificio.getPrecio()); //en pagar lo quitamos y ahora tenemos q sumarlo
                    jugador.setDineroInvertido(jugador.getDineroInvertido()+ getPrecio());
                    edificio.setCasilla(this);
                    Tablero.addChotel();
                    edificios.removeIf(ite -> ite.getTipo() == 0);
                }
                break;
            case 2:
                if (countp>=1) System.out.println("No puedes construir más piscinas en esta propiedad");
                else if (countc<2 || counth<1) System.out.println("Necesitas tener al menos 2 casas y 1 hotel para construir una piscina en esta propiedad");
                else{
                    System.out.println("Enhorabuena, estás colaborando con la gentrificación de tu ciudad");
                    System.out.println("Pagas "+edificio.getPrecio()+" por construir");
                    edificios.add(edificio);
                    jugador.pagar(edificio.getPrecio());
                    jugador.setFortuna(jugador.getFortuna()+ 2*edificio.getPrecio()); //en pagar lo quitamos y ahora tenemos q sumarlo
                    jugador.setDineroInvertido(jugador.getDineroInvertido()+ getPrecio());
                    edificio.setCasilla(this);
                    Tablero.addCpiscina();
                }
                break;
            case 3:
                if (countd>=1) System.out.println("No puedes construir más pistas en esta propiedad");
                else if (counth<2) System.out.println("Necesitas tener al menos 2 hoteles para construir una piscina en esta propiedad");
                else{
                    System.out.println("Enhorabuena, estás colaborando con la gentrificación de tu ciudad");
                    System.out.println("Pagas "+edificio.getPrecio()+" por construir");
                    edificios.add(edificio);
                    jugador.pagar(edificio.getPrecio());
                    jugador.setFortuna(jugador.getFortuna()+ 2*edificio.getPrecio()); //en pagar lo quitamos y ahora tenemos q sumarlo
                    jugador.setDineroInvertido(jugador.getDineroInvertido()+ getPrecio());
                    edificio.setCasilla(this);
                    Tablero.addCdeporte();
                }
                break;
        }
    }

    /**
     * Realiza las comprobaciones correspondientes, destruye un edificio y devuelve su coste al jugador
     * @param edificio Edificio a vender
     * @param jugador Jugador que lo vende (comprobación interna)
     */
    public void venderEdificio(Edificio edificio, Jugador jugador){
        if (getPropietario().equals(jugador)&&!getHipotecado()&&edificio!=null){
            jugador.addDinero(edificio.getPrecio()/2);
            System.out.println("Cobras "+edificio.getPrecio()+" por "+edificio.getIdentificador());
            removeEdificio(edificio);
        }
        else System.out.println("No se puede vender este edificio");
    }
    public void venderEdificio(String sedificio, Jugador jugador){
        Edificio edificio = getEdificio(sedificio);
        if (getPropietario().equals(jugador)&&!getHipotecado()&&edificio!=null){
            jugador.addDinero(edificio.getPrecio()/2);
            removeEdificio(edificio);
        }
        else System.out.println("No se puede vender este edificio");
    }
    public Edificio getEdificio(String identificador){
        for(Edificio ite: edificios){
            if (ite.getIdentificador().equals(identificador)) return ite;
        }
        return null; //Comprobar en la función desde la que se llame
    }
    public void removeEdificio(Edificio edificio){
        edificios.remove(edificio);
    }

    public ArrayList<Edificio> getEdificios() {
        return edificios;
    }

    public Grupo getGrupo() {
        return grupo;
    }
}
