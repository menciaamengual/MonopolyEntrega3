package Procesos.Casillas;

import Procesos.Jugador;

import java.util.ArrayList;

public abstract class Casilla{
    private int posicion; //0-43
    private String nombre;
    private int visitas;
    private ArrayList<Jugador> ocupantes;
    public static final int lonMaxNombre = 11; //Longitud del nombre en impresión. Función que mira long del nombre + largo

    public Casilla(int posicion, String nombre){
        this.posicion = posicion;
        this.nombre = nombre;

        visitas = 0;
        ocupantes = new ArrayList<>(6);
    }

    //Impresión por pantalla
    String StringAvatares(){
        StringBuilder avs = new StringBuilder();
        for (Jugador jugador:ocupantes){
            avs.append("&").append(jugador.getAvatar().getIdentificador());
        }
        return avs.toString();
    }
    public String toString (){
        String formato = "|\u001B[4m%"+lonMaxNombre+"s %"+6+"s\u001B[0m|";
        /*if (grupo!=null) formato = grupo.colorFormato() + formato + "\u001B[0m";*/
        return String.format(formato,nombre,StringAvatares());
    }

    //Trabajar con ocupantes
    public ArrayList<Jugador> getOcupantes() {
        return ocupantes;
    }

    /**
     * No hay ningún motivo para tener que usar esta función más que en la inicialización de la partida (si acaso)
     * @param ocupantes lista de jugadores a la que settear la lista de ocupantes de la casilla
     */
    public void setOcupantes(ArrayList<Jugador> ocupantes) {
        this.ocupantes = ocupantes;
    }

    /**
     * Esta función itera en la lista de ocupantes de la casilla, y crea una lista nueva dejando fuera al jugador específicado (en caso de que esté; en caso contrario no pasa nada).
     * @param jugador jugador que va a ser quitado de esa casilla
     */
    public void removeOcupante(Jugador jugador){
        ArrayList<Jugador> aux = new ArrayList<>(6);
        for (Jugador ite: ocupantes){
            if (!ite.equals(jugador)) aux.add(ite);
        }
        ocupantes = aux;
    }

    /**
     * Añade el jugador especificado a la lista de ocupantes de la casilla
     * @param jugador jugador a añadir a la lista de ocupantes de la casilla
     */
    public void addOcupante(Jugador jugador) {
        ocupantes.add(jugador);
    }
    public boolean isOcupante(Jugador jugador){
        return (ocupantes.contains(jugador));
    }
    public Boolean isComprable(){ //Suponemos que no se puede comprar una casilla que ya sea de otro jugador
        return false; //En todos los casos que no este overrideado (hijos comprables), devuelve falso
    }

    //Nombre
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getNombre() {
        return nombre;
    }

    //Visitas
    public void setVisitas(int visitas) {
        this.visitas = visitas;
    }

    public int getVisitas() {
        return visitas;
    }

    public String descripcionDetallada() {
        return "Descripción genérica";
    }

    public String descripcion(){
        return "{\nnombre: " + getNombre() +"\n"+
                "tipo: "+ getClass() +" \n" +
                "}\n";
    }

    public int getPosicion() {
        return posicion;
    }
}
//
//public class Casilla {
//    //ATRIBUTOS
//    private int tipo; //0-8. 0.Solar; 1.Transporte; 2.Servicios 3. Suerte o Caja de comunidad. 4.Cárcel. 5.Parkineos. 6. Impuesto. 7.Salida 8.Ir cárcel
//    private int posicion; //0-43
//    private String nombre;
//    private Jugador propietario;
//    private boolean hipotecado;
//    private int precio;
//    private int alquilerBase;
//    private int vHipoteca;
//    private Grupo grupo; //Color/grupo
//    private ArrayList<Edificio> edificios;
//    private int rentabilidad; //dinero generado
//    private int visitas;
//
//    //0 - Marrón 1 - Cián 2 - Fucsia 3 - Tangerina 4 - Escarlateta 5 - Amarillo 6 - Verde 7 - Azul
//
//    private ArrayList<Jugador> ocupantes;
//    private static int lonMaxNombre = 11; //Longitud del nombre en impresión. Función que mira long del nombre + largo
//
//
//    //CONSTRUCTORES
//    public Casilla (int pBase,int pSalida, int posicion, String nombre, ArrayList<Grupo> grupos) {
//        Grupo[] ArrayAux_grupo= {null, grupos.get(1-1), null, grupos.get(1-1), null, null, grupos.get(2-1), null, grupos.get(2-1), grupos.get(2-1), null, grupos.get(3-1), null, grupos.get(3-1), grupos.get(3-1), null, grupos.get(4-1), null, grupos.get(4-1), grupos.get(4-1), null, grupos.get(5-1), null, grupos.get(5-1), grupos.get(5-1), null, grupos.get(6-1), grupos.get(6-1), null, grupos.get(6-1), null, grupos.get(7-1), grupos.get(7-1), null, grupos.get(7-1), null, null, grupos.get(8-1), null, grupos.get(8-1)};
//        int[] ArrayAux_tipoCasilla = {7, 0, 3, 0, 6, 1, 0, 3, 0, 0, 4, 0, 2, 0, 0, 1, 0, 3, 0, 0, 5, 0, 3, 0, 0, 1, 0, 0, 2, 0, 8, 0, 0, 3, 0, 1, 3, 0, 6, 0};
//
//        ocupantes = new ArrayList<>(6);
//        grupo = ArrayAux_grupo[posicion];
//        this.tipo = ArrayAux_tipoCasilla[posicion];
//        this.posicion = posicion;
//        this.nombre = nombre;
//        this.alquilerBase = 0;
//        this.hipotecado = false;
//        this.propietario = null;
//        this.precio = 0;
//        rentabilidad=0;
//        visitas=0;
//
//        switch (tipo) {
//            case 0: //Solar
//                grupo.addCasilla(this);
//                precio = grupo.getPrecio();
//                alquilerBase =(int)(precio * 0.1);
//                edificios = new ArrayList<>();
//                break;
//            case 1: //Transporte
//                precio = pSalida;
//                alquilerBase =(int)(0.25*pSalida);
//                break;
//
//            case 2: //Servicios
//                precio = (int) (pSalida * 0.75);
//                alquilerBase =(int)(pSalida/200); //POR EL VALOR DE LOS DADOS
//                //factorServicio=200 veces menos que pSalida
//                break;
//
//            case 3: //Suerte / Caja Comunidad
//                break;
//
//            case 4: // Carcel
//                break;
//
//            case 5: // Parking
//                alquilerBase = 0;
//                break;
//
//            case 6: //Impuesto
//                if (posicion<20) alquilerBase = pSalida/2;
//                else alquilerBase = pSalida;
//                break;
//
//            case 7: //Salida
//                break;
//
//            case 8: // Ir Carcel
//                break;
//        }
//        vHipoteca = (int) precio / 2;
//    }
//
//    //SETTERS
//
//    /**
//     * Añade a una casilla (solar) edificio del tipo que corresponda, realizando las comprobaciones pertinentes, y cobra su precio al jugador
//     * @param tipo 0-3 tipo de edificio a construir (C,H,P,D)
//     */
//    public void construir(int tipo,Jugador jugador, boolean caida) {
//        if (this.tipo!=0){
//            System.out.println("No puedes costruir en una casilla que no sea un solar");
//            return;
//        }
//
//        if (grupo.getPropietario()!=propietario && !caida || !jugador.equals(propietario)) { //Comprobación de propiedad
//            System.out.println("No puedes construir si no tienes tod el grupo on no has caído 2 veces en esta casilla...");
//            return;
//        }
//
//        int countc = 0, counth = 0, countp = 0, countd = 0, countgh = 0, countgc=0; //Contador de casas por casilla, hoteles por grupo y casas por grupo
//        Edificio edificio = new Edificio(tipo,precio);
//
//        if(jugador.getDinero()<edificio.getPrecio()){ //Comprobación dinero
//            System.out.println("Dinero insuficiente, inténtalo otra vez cuando tengas más dinero");
//            return;
//        }
//
//        for (Edificio ite: edificios){
//            if (ite.getTipo()==0) countc++;
//            if (ite.getTipo()==1) counth++;
//            if (ite.getTipo()==2) countp++;
//            if (ite.getTipo()==3) countd++;
//        }
//        for (Casilla cite: grupo.getCasillas()){
//            for (Edificio ite: cite.getEdificios()){
//                if (ite.getTipo()==1) countgh++;
//                if (ite.getTipo()==0) countgc++;
//            }
//        }
//        switch (tipo) {
//            case 0: //Mover para aquí el Tablero.contador...++
//                if (countc>=4){ //Comprobación de casas/solar //HAY QUE MIRAR QUE DEJA CONSTRUIR MÁS CASAS DE LAS DEBIDAS CUANDO ESTÁ LLENO DE HOTELES
//                    System.out.println("Pedro Sánchez ha aprobado una ley de urbanismo que impide tener más de 4 casas por casilla ¡Vaya con estos socialistas!");
//                }
//                else if(countgh >= grupo.getTam() && countgc>=grupo.getTam()){
//                        System.out.println("El ministerio de igualdad prohibe que un solo grupo tenga tantos hoteles y casas a la vez, si quieres construir otra casa, debes vender algo antes o haber votado mejor...");
//                }
//                else{
//                    System.out.println("¡OLE! Enhorabuena por la construcción de tu casa, ahora eres aún más especulador");
//                    System.out.println("Pagas "+edificio.getPrecio()+" por la construccion");
//                    edificios.add(edificio);
//                    jugador.pagar(edificio.getPrecio());
//                    jugador.setFortuna(jugador.getFortuna()+ 2*edificio.getPrecio()); //en pagar lo quitamos y ahora tenemos q sumarlo
//                    jugador.setDineroInvertido(jugador.getDineroInvertido()+precio);
//                    edificio.setCasilla(this);
//                    Tablero.addCcasa();
//                }
//                break;
//            case 1: //Se cobra aunq no se construya el hotel Y NO SE COMPRUEBA QUE HAYA CASAS SUFICIENTES y se crea el edificio aunq no corresponda
//                if (countc<4) System.out.println("Debes tener 4 casas en esta propiedad para construir un hotel...");
//                else if (countgh>=grupo.getTam()) System.out.println("No puedes construir más hoteles en esta propiedad");
//                else{
//                    System.out.println("Enhorabuena, estás colaborando con la gentrificación de tu ciudad");
//                    System.out.println("Pagas "+edificio.getPrecio()+" por construir");
//                    edificios.add(edificio);
//                    jugador.pagar(edificio.getPrecio());
//                    jugador.setFortuna(jugador.getFortuna()+ 2*edificio.getPrecio()); //en pagar lo quitamos y ahora tenemos q sumarlo
//                    jugador.setDineroInvertido(jugador.getDineroInvertido()+precio);
//                    edificio.setCasilla(this);
//                    Tablero.addChotel();
//                    edificios.removeIf(ite -> ite.getTipo() == 0);
//                }
//                break;
//            case 2:
//                if (countp>=1) System.out.println("No puedes construir más piscinas en esta propiedad");
//                else if (countc<2 || counth<1) System.out.println("Necesitas tener al menos 2 casas y 1 hotel para construir una piscina en esta propiedad");
//                else{
//                    System.out.println("Enhorabuena, estás colaborando con la gentrificación de tu ciudad");
//                    System.out.println("Pagas "+edificio.getPrecio()+" por construir");
//                    edificios.add(edificio);
//                    jugador.pagar(edificio.getPrecio());
//                    jugador.setFortuna(jugador.getFortuna()+ 2*edificio.getPrecio()); //en pagar lo quitamos y ahora tenemos q sumarlo
//                    jugador.setDineroInvertido(jugador.getDineroInvertido()+precio);
//                    edificio.setCasilla(this);
//                    Tablero.addCpiscina();
//                }
//                break;
//            case 3:
//                if (countd>=1) System.out.println("No puedes construir más pistas en esta propiedad");
//                else if (counth<2) System.out.println("Necesitas tener al menos 2 hoteles para construir una piscina en esta propiedad");
//                else{
//                    System.out.println("Enhorabuena, estás colaborando con la gentrificación de tu ciudad");
//                    System.out.println("Pagas "+edificio.getPrecio()+" por construir");
//                    edificios.add(edificio);
//                    jugador.pagar(edificio.getPrecio());
//                    jugador.setFortuna(jugador.getFortuna()+ 2*edificio.getPrecio()); //en pagar lo quitamos y ahora tenemos q sumarlo
//                    jugador.setDineroInvertido(jugador.getDineroInvertido()+precio);
//                    edificio.setCasilla(this);
//                    Tablero.addCdeporte();
//                }
//                break;
//        }
//    }
//
//    /**
//     * Realiza las comprobaciones correspondientes, destruye un edificio y devuelve su coste al jugador
//     * @param edificio
//     * @param jugador
//     */
//    public void venderEdificio(Edificio edificio, Jugador jugador){
//        if (propietario.equals(jugador)&&!hipotecado&&edificio!=null){
//            jugador.addDinero(edificio.getPrecio()/2);
//            System.out.println("Cobras "+edificio.getPrecio()+" por "+edificio.getIdentificador());
//            removeEdificio(edificio);
//        }
//        else System.out.println("No se puede vender este edificio");
//    }
//    public void venderEdificio(String sedificio, Jugador jugador){
//        Edificio edificio = getEdificio(sedificio);
//        if (propietario.equals(jugador)&&!hipotecado&&edificio!=null){
//            jugador.addDinero(edificio.getPrecio()/2);
//            removeEdificio(edificio);
//        }
//        else System.out.println("No se puede vender este edificio");
//    }
//    public Edificio getEdificio(String identificador){
//        for(Edificio ite: edificios){
//            if (ite.getIdentificador().equals(identificador)) return ite;
//        }
//        return null; //Comprobar en la función desde la que se llame
//    }
//    public void removeEdificio(Edificio edificio){
//        edificios.remove(edificio);
//    }
//
//    public ArrayList<Edificio> getEdificios() {
//        return edificios;
//    }
//
//    public void hipotecar(){
//        if (edificios!=null && !edificios.isEmpty()){
//            System.out.println("Debes vender todos los edificios antes de hipotecar.");
//            return;
//        }
//        else if (hipotecado){
//            System.out.println("No puedes hipotecar una propiedad ya hipotecada.");
//            return;
//        }
//        hipotecado = true;
//        propietario.addDinero(precio/2);
//        System.out.println("Has hipotecado "+ this+" por "+precio/2+"$");
//    }
//    public void deshipotecar(){
//        if (!hipotecado){
//            System.out.println("No puedes deshipotecar una propiedad no hipotecada.");
//            return;
//        }
//        if (propietario.getDinero()<(int) ((-precio/2)*1.1)){
//            System.out.println("Dinero insuficiente para deshipotecar, necesitas "+(int) ((-precio/2)*1.1));
//            return;
//        }
//        hipotecado = false;
//        propietario.addDinero((int) ((-precio/2)*1.1)); //Add dinero negativo...
//        System.out.println("Has deshipotecado "+nombre+" por "+(int) ((precio/2)*1.1)+"$ ¡Qué bien!");
//    }
//
//
//    public void removeOcupante(Jugador jugador){
//        ArrayList<Jugador> aux = new ArrayList<>(6);
//        ArrayList<Jugador> aux = new ArrayList<>(6);
//        for (Jugador ite: ocupantes){
//            if (!ite.equals(jugador)) aux.add(ite);
//        }
//        ocupantes = aux;
//    }
//
//    public void addOcupante(Jugador jugador) {
//        ocupantes.add(jugador);
//    }
//
//    public void setOcupantes(ArrayList<Jugador> ocupantes) {this.ocupantes = ocupantes;}
//    public void setPrecio(int precio) {this.precio = precio;}
//    public void setRentabilidad(int rentabilidad) {this.rentabilidad = rentabilidad;}
//    public void setVisitas(int visitas) {this.visitas = visitas;}
//    /**
//     * Settea el propietario, añadiendo además la casilla a su lista de propiedades
//     * Al llamar a addPropiedad, también hace la comprobación de los grupos
//     * @param propietario
//     */
//    public void setPropietario(Jugador propietario){
//        if (tipo>2) return; //Si no es un solar, servicio o transporte, no se le puede asignar propietario
//        this.propietario = propietario;
//        propietario.addPropiedad(this);}
//
//    public void setAlquilerBase(int alquilerBase) { //Pensada para trabajar con parking gratuito
//        this.alquilerBase = alquilerBase;
//    }
//
//    //GETTERS
//    public int getAlquilerBase() {
//        return alquilerBase;
//    }
//
//    public String getTipoString(){
//        String aux = new String[]{"Solar", "Transporte", "Servicios","Suerte/Comunidad","Cárcel","Parking","Impuestos","Salida","Ir Cárcel"}[tipo];
//        return aux;
//    }
//    public String getGrupoString(){
//        return grupo.getColor();
//    }
//    public Boolean getHipotecado(){
//        return hipotecado;
//    }
//    public ArrayList<Jugador> getOcupantes() {return ocupantes;}
//    public int getPosicion() {return posicion;}
//    public static int getLonMaxNombre() {return lonMaxNombre;}
//    public int getTipo() { return tipo;}
//    public Grupo getGrupo() {return grupo;}
//    public Jugador getPropietario() {return propietario;}
//    public String getNombre() {return nombre;}
//    public int getPrecio(){
//        return precio;
//    }
//    public int getvHipoteca(){
//        return vHipoteca;
//    }
//    public int getRentabilidad() {return rentabilidad;}
//    public int getVisitas() {return visitas;}
//
//    //METODOS PUBLICOS


//    public Boolean pagasAlquiler(Jugador jugador){ //Puede que tenga 0 usos esta funciíon
//        //return propietario!=jugador && ;
//        //return !Propietario().equals(jugador)&&!casilla.isComprable() && !casilla.getHipotecado();
//        return true;
//    }

//    /**
//     * Calcula el alquiler que debe ser pagado al caer en una casilla de tipo solar, transporte, servicio o impuesto
//     * @return int con el valor que el jugador que caiga en esa casilla debe pagar.
//     */
//    public int calcularAlquiler(Dado dado){
//        int alq = alquilerBase;
//        switch (tipo){
//            case 0:
//                if (!edificios.isEmpty()){
//                    int ncasas = 0;
//                    for(Edificio ite: edificios){
//                       switch (ite.getTipo()){
//                           case 0:
//                               ncasas++;
//                               break;
//                           case 1:
//                               alq+=alquilerBase*70;
//                               break;
//                           case 2:
//                           case 3:
//                               alq+=alquilerBase*25;
//                               break;
//                       }
//                    }
//                    switch (ncasas){
//                        case 1: alq+=5*alquilerBase;  break;
//                        case 2: alq+=15*alquilerBase; break;
//                        case 3: alq+=35*alquilerBase; break;
//                        case 4: alq+=50*alquilerBase; break;
//                    }
//                    if(grupo.getPropietario()!=null){
//                        return alq+alquilerBase;
//                    }else{
//                        return alq;
//                    }
//                }
//                else return alquilerBase;
//            case 1:
//                return propietario.getNTrans()*alquilerBase;
//            case 2:
//                if (propietario.getNServicios() == 1) return dado.getSuma()*alquilerBase*4;
//                else if (propietario.getNServicios() == 2) return dado.getSuma()*alquilerBase*10;
//                return 0;
//            case 6: return alquilerBase;
//            default: return 0; //En los casos especiales, usamos otras funciones
//        }
//    }
//
//    //toString
//    private String StringAvatares(){
//        StringBuilder avs = new StringBuilder();
//        for (Jugador jugador:ocupantes){
//            avs.append("&").append(jugador.getAvatar());
//        }
//        return avs.toString();
//    }
//
//    public String toString (){
//        String formato = "|\u001B[4m%"+lonMaxNombre+"s %"+6+"s\u001B[0m|";
//        if (grupo!=null) formato = grupo.colorFormato() + formato + "\u001B[0m";
//        return String.format(formato,this.nombre,StringAvatares());
//    }
//
//    public String descripcion() {
//        if (tipo==0) return "{\n" +
//                "nombre: " + nombre +"\n"+
//                "color: " + grupo.getColor() + "\n"+
//                "tipo: "+getTipoString()+" \n" +
//                "valor: "+precio+" \n" +
//                "edificios: "+edificios+" \n" +
//                "}\n";
//        else return "{\n" +
//                "nombre: " + nombre +"\n"+
//                "tipo: "+getTipoString()+" \n" +
//                "valor: "+precio+" \n" +
//                "}\n";
//    }
//    public String descripcionDetallada() {
//        switch (tipo){
//            case 0:
//                return "{\n" +
//                        "nombre: " + nombre +"\n"+
//                        "color: " + grupo.getColor() + "\n"+ //Estaría chulo meterle el formateo para que se vea del color que es
//                        "tipo: "+getTipoString()+" \n" +
//                        "Propietario: "+propietario.getNombre()+" \n" +
//                        "Precio: "+ precio +" \n" +
//                        "Edificios: " + edificios +"\n"+
//                        "ALQUILER ACTUAL: "+ calcularAlquiler(null) + "$\n" +
//                        "   -Alquiler Básico: "+alquilerBase+"$ \n" +
//                        "   -Alquiler con tod el grupo: "+alquilerBase*2+"$ \n" +
//                        //Podemos meter aquí alquiler con construcones (igual no todas las combinaciones, pero un poquito).
//                        "Puedes hipotecar esta casilla por "+precio/2+"$\n"+
//
//                        //"Ocupantes: "+ ocupantes +" \n" +
//
//                        "}\n";
//            case 1:
//                return "{\n" +
//                        "nombre: " + nombre +"\n"+
//                        "tipo: "+getTipoString()+" \n" +
//                        "Propietario: "+propietario.getNombre() +" \n" +
//                        "Precio: "+ precio +" \n" +
//                        " Alquiler Básico: "+alquilerBase+" \n" +
//                        " El alquiler de esta propiedad escala según la cantidad de transportes que tenga el dueño \n" +
//                        "}\n";
//            case 2:
//            return "{\n" +
//                        "nombre: " + nombre +"\n"+
//                        "tipo: "+getTipoString()+" \n" +
//                        "Propietario: "+propietario.getNombre()+" \n" +
//                        "Precio: "+ precio +" \n" +
//                        " Alquiler Básico: "+alquilerBase+" \n" +
//                        " El alquiler de esta propiedad escala según la cantidad de servicios que tenga el dueño \n" +
//                        "}\n";
//            case 3:
//                return "{\n"+
//                        "nombre: " + nombre+ "\n"+
//                        "descripción: sacas la carta que corresponda al caer en esta casilla.\n" +
//                        "}\n";
//            case 4:
//            case 5:
//                return "{\n"+
//                        "nombre: " + nombre+
//                        "}\n";
//            case 6:
//                return "{\n"+
//                        "nombre: " + nombre+
//                        "descripción: al caer en esta casilla, pagas un impuesto a la banca.\n" +
//                        "}\n";
//            case 7:
//                return "{\n"+
//                        "nombre: " + nombre+
//                        "descripción: al pasar por esta casilla recibes un salario\n" +
//                        "}\n";
//            case 8:
//                return "{\n"+
//                        "nombre: " + nombre+
//                        "descripción: al caer en esta casilla eres enviado a la cárcel\n" +
//                        "}\n";
//
//        }
//        return "";
//        /*return "{\n" +
//                "nombre: " + nombre +"\n"+
//                "color: " + grupo.getColor() + "\n"+ //Estaría chulo meterle el formateo para que se vea del color que es
//                "tipo: "+getTipoString()+" \n" +
//                " Alquiler Básico: "+alquilerBase+" \n" +
//                " Alquiler con tod el grupo: "+alquilerBase*2+" \n" +
//                "valor: "+precio+" \n" +
//                "valor: "+precio+" \n" +
//
//                "}\n";*/
//    }
//
//    public void setEdificios(ArrayList<Edificio> edificios) {
//        this.edificios = edificios;
//    }
//}