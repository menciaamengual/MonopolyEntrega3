# MonopolyV2.2

He cambiado un poco la estructura del main branch de git, como veis. Básicamente pq había 3 carpetas y 2 zips en cada versión. Ahora hay solo 1 carpeta con el código. Ánimo!

Observaciones:
  A partir de ahora, debeis reemplazar los System.out.println() por consolaNormal.imprimir(). No hace automáticamente el toString así que puede que a veces tengáis que meterselo en los argumetos rollo imprimir(casilla.toString()).
  Lo mismo con los scanners. Ahora existe el método consolaNormal.leer(descripción). Le meteis el texto rollo "Introduce un número del 1 al 6", en descripción, y os devuelve el String de la entrada por teclado. Hice dos versiones más: leerFragmentado() y leerInt(), bastante autoexplicativas. 
  - Para usarlo fuera de Juego teneis que incluir un import Juego.Juego arriba de todo (realmente solo pasa en algunas Clases, pero como no se cual es el criterio que las distingue...), y llamarlas como Juego.consolaNormal.leer()/imprimir(), en lugar de solo consolaNormal.leer().


Tareas pendientes: 
  Implementar descripción en la nueva implementación de casillas.
  @Cese revisa la tirarDados en la carcel y entrar con dobles 3 veces y esas cosas...
