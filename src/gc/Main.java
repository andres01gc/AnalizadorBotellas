package gc;

import processing.core.PApplet;

public class Main extends PApplet {
    public static PApplet app;

    public static void main(String[] args) {
        // write your code here
/*
TODO    Cargar las siluetas
TODO    Dividir la silueta en dos
TODO    Girarla 90 grados
TODO    Convertir la mitad de la silueta en una distribuciòn en dos ejes.

    Cada imagen tendrà una altura diferente. por lo tanto la distribuciòn, solo serà del tamño de la altura!., se debe conoocer el alto.

TODO Dividir la botella en 3 partes, Tapa. Cuello, Cuerpo, Base.
La mejor soluciòn que se me ha ocurrido es  buscar algun cambio en la pendiente
 */
        PApplet.main("gc.Main");
        new AnalizadorSiluetas();
    }

    @Override
    public void setup() {
        super.setup();
        app = this;
    }
}

