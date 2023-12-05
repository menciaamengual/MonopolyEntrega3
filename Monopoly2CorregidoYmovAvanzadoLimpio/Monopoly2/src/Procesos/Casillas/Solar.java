package Procesos.Casillas;

import java.util.ArrayList;

public class Solar extends Propiedad {
    private Grupo grupo;
    private int alquilerBase;
    private ArrayList<Edificio> edificios;

    public Solar(int pBase, int pSalida, int posicion, String nombre, ArrayList<Grupo> grupos) {
        Grupo[] ArrayAux_grupo = {null, grupos.get(1 - 1), null, grupos.get(1 - 1), null, null, grupos.get(2 - 1), null, grupos.get(2 - 1), grupos.get(2 - 1), null, grupos.get(3 - 1), null, grupos.get(3 - 1), grupos.get(3 - 1), null, grupos.get(4 - 1), null, grupos.get(4 - 1), grupos.get(4 - 1), null, grupos.get(5 - 1), null, grupos.get(5 - 1), grupos.get(5 - 1), null, grupos.get(6 - 1), grupos.get(6 - 1), null, grupos.get(6 - 1), null, grupos.get(7 - 1), grupos.get(7 - 1), null, grupos.get(7 - 1), null, null, grupos.get(8 - 1), null, grupos.get(8 - 1)};
        //int[] ArrayAux_tipoCasilla = {7, 0, 3, 0, 6, 1, 0, 3, 0, 0, 4, 0, 2, 0, 0, 1, 0, 3, 0, 0, 5, 0, 3, 0, 0, 1, 0, 0, 2, 0, 8, 0, 0, 3, 0, 1, 3, 0, 6, 0};

        this.setOcupantes(new ArrayList<>(6));
        grupo = ArrayAux_grupo[posicion];
        //this.tipo = ArrayAux_tipoCasilla[posicion];
        this.posicion = posicion;
        this.nombre = nombre;
        this.alquilerBase = 0;
        this.hipotecado = false;
        this.setPropietario(null);
        this.precio = 0;
        rentabilidad = 0;
        visitas = 0;

        grupo.addCasilla(this);
        precio = grupo.getPrecio();
        alquilerBase = (int) (precio * 0.1);
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
        return String.format(formato,nombre,StringAvatares());
    }
}
