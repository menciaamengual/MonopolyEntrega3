package Procesos;

import java.util.Random;
public class Dado {
    private int Dado1;
    private int Dado2;
    private int c; //Registra el número de tiradas iguales seguidas
    public Dado(){
        Dado1 = 0;
        Dado2 = 0;
        c = 0;
    }

    public Dado(int d1, int d2){ //Inicialización trucada del dado
        Dado1 = d1;
        Dado2 = d2;
        c = 0;
        if (d1==d2) c=1;
    }
    //Getters
    public int getDado1() {
        return Dado1;
    } //no se necesitan
    public int getDado2() {
        return Dado2;
    } //
    public int getSuma(){
        return Dado1+Dado2;
    }

    public Boolean areEqual(){
        return Dado1==Dado2;}

    //Setters
    public void tirarDados(){ //ESTÁ SUPER COMPROBADO (HE PERDIDO MEDIA HORA EN ELLO) QUE LOS DADOS FUNCIONAN Y DEVUELVEN VALORES ALEATORIOS.
        Random random = new Random();
        Dado1 = random.nextInt(6)+1;
        Dado2 = random.nextInt(6)+1;
        if (areEqual()) c++;
        else c=0;
    }

    public void tirarDados(int Dado1, int Dado2){ //Dados trucados, con valor predefinido
        this.Dado1 = Dado1;
        this.Dado2 = Dado2;
        if (areEqual()) c++;
        else c=0;
    }

    public int getC() {
        return c;
    }
}
