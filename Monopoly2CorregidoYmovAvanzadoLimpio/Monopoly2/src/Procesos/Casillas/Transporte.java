package Procesos.Casillas;

import Juego.Exceptions.AlquilerDineroInsufException;
import Juego.Juego;
import Procesos.Jugador;

public class Transporte extends Propiedad {
    private static int factorTransporte;

    public Transporte(int posicion, String nombre, Jugador propietario, int pSalida) {
        super(posicion, nombre, propietario);
        Transporte.factorTransporte = pSalida / 4;
        setPrecio(pSalida);
    }

    @Override
    public String descripcionDetallada() {
        return "{\n" +
                "nombre: " + getNombre() + "\n" +
                "tipo: Transporte \n" +
                "Propietario: " + getPropietario().getNombre() + " \n" +
                "Precio: " + getPrecio() + " \n" +
                "ALQUILER ACTUAL: " + calcularAlquiler() + "$\n" +
                "Factor transporte: " + factorTransporte + "\n " +
                "Puedes hipotecar esta casilla por " + getPrecio() / 2 + "$\n" +

                //"Ocupantes: "+ ocupantes +" \n" +

                "}\n";
    }

    public int calcularAlquiler() {
        return getPropietario().getNTrans() * factorTransporte;
    }

    @Override
    public void accionCasilla(Jugador jugador) throws AlquilerDineroInsufException {
        if (!getPropietario().equals(jugador) && !getPropietario().isBanca() && !getHipotecado()) {
            if (!jugador.pagar(calcularAlquiler(), getPropietario())) throw new AlquilerDineroInsufException();
            setRentabilidad(getRentabilidad() + calcularAlquiler());
            jugador.setPagoDeAlquileres(jugador.getPagoDeAlquileres() + calcularAlquiler());
            getPropietario().setCobroDeAlquileres(getPropietario().getCobroDeAlquileres() + calcularAlquiler());
            Juego.getConsolaNormal().imprimir("Pagas " + calcularAlquiler() + "$ por caer en " + getNombre());
        } else if (getHipotecado()) {
            Juego.getConsolaNormal().imprimir("Casilla hipotecada... No pagas alquiler :)");
        } else if (getPropietario().isBanca()) {
            Juego.getConsolaNormal().imprimir("Esta propiedad aun no tiene dueño, la puedes comprar.");
        } else if (getPropietario().equals(jugador)) {
            Juego.getConsolaNormal().imprimir("Has caído en una casilla de tu propiedad, disfruta de tu estancia");
        }
    }
}