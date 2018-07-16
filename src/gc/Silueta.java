package gc;

import gc.utils.Utils;

import processing.core.PImage;

import java.io.*;

public class Silueta {
    private String path;
    private String name;
    private double[] distribution;
    private double[] distribution_acumulada;
    private double[] distribution_smoothed;
    private double[] pendientes;

    private PImage halfImage;
    private int val_acumuado_maximo;

    //por ahora solo guardaré la siluetal
    public Silueta(String path) {
        analizarPath(path);
        cargarDistribucion();
        buscarPendientes();
        guardarDatos();
//        guardarSiluetaAcumulada();
        guardarImagenesConPendiente();

//        System.out.println(" Done!");
    }

    void guardarSiluetaAcumulada() {

        System.out.println(halfImage.width);
        System.out.println(Main.app.map(val_acumuado_maximo, 0, val_acumuado_maximo, 0, halfImage.width));

        PImage image = Main.app.createImage(halfImage.width, halfImage.height, Main.app.RGB);
        image.loadPixels();

        for (int i = 0; i < halfImage.height; i++) {
            image.set((int) Main.app.map((float) distribution_acumulada[i], 0, val_acumuado_maximo, 0, halfImage.width), i, Main.app.color(255, 255, 255));
        }

        image.updatePixels();
        image.save("results/image_with_lines/" + name + "_acumulada.jpg");
        System.out.println("Se guarda silueta acumulada");

    }

    private void guardarImagenesConPendiente() {
        PImage image = Main.app.createImage(halfImage.width, halfImage.height, Main.app.RGB);
        image.loadPixels();

        for (int i = 1; i < pendientes.length - 1; i++) {
// pinta la silueta de la botella en la nueva imagen.
            image.set((int) distribution_smoothed[i], i, Main.app.color(255, 255, 255));

            int vl = validarPunto(distribution_smoothed, i, 30, 12);

            if (vl != -1) {
                for (int j = 0; j < image.width; j++) {
                    switch (vl) {
                        case 0:
                            image.set(j, i, Main.app.color(255,0,0));
//                            image.set(j, i, Main.app.color(255, 0, 0));
                            break;
                        case 1:
                            image.set(j, i, Main.app.color(0,255,0));

                            break;
                    }
                }
            }

        }

        image.updatePixels();
        image.save("results/image_with_lines/" + name + ".jpg");
    }

    public int validarPunto(double[] dist, int position, float rango_max, float presicion) {
        /*
        Para iniciar el analisis del punto, primero, buscamos en la disttribución el valor en el que la pendiente tenga un signo diferente al punto anterior.
         */
        if (comprobarPendiente(position)) {
//            System.out.println("Cambio de pen");

            /*
            Prueba con la suma de los deltas.
             */
            return comprobarDelta_atras(dist, position, rango_max, 5);
        }
        return -1;
    }

    private int comprobarDelta_atras(double[] dist, int position, float rango_max, float presicion) {
        int _v = 0;
        double acumulado_delta = 0;

        if (rango_max > 0) {
            _v = +1;
        } else {
        }
        _v = -1;

//primero hacia atrás.

        int cantidad_validos = 0;
//        System.out.println();
        for (int i = position; i > position - rango_max; i += _v) {
            int p_a = i;
            int p_b = i + _v;

            if (p_b > 0) {


                cantidad_validos++;

//                System.out.println(dist[p_a] - dist);
                float valor_prueba = (float) (dist[p_a] - dist[p_b]);
//                System.out.println("pru " + valor_prueba);
                acumulado_delta += valor_prueba;
            }
        }

        acumulado_delta = Math.abs(acumulado_delta);


        if (acumulado_delta > presicion) {
            System.out.println("testing ");
            return 1;
        } else {
            return 0;
        }
//        System.out.println("cantidad validos: " + cantidad_validos + " acumulado delta: " + acumulado_delta + " valor_test: " + last_value + " valido: " + (last_value > presicion));
    }

    public boolean comprobarPendiente(int position) {
        double p_a = pendientes[position];
        double p_b = p_a;

        if (position > 0) {
            p_b = pendientes[position - 1];
        }

        boolean signo1 = (p_a >= 0);
        boolean signo2 = (p_b >= 0);

        if (signo1 != signo2) {
//            System.out.println("Cambio de pendiente detectado");
            return true;
        }
        return false;
    }


    public void analizarPath(String path) {
        this.path = path;
        this.name = new File(this.path).getName().split("_crop")[0];
        System.out.print("analizando: " + this.name + "...");
    }

    /**
     * TODO reemplazar processing por openCV en caso necesario
     * Al ser binaria solo me importa la distribución, procesing solo lo usarié por la facilidad que tiene para
     * gestionar imagenes.
     */
    public void cargarDistribucion() {
//        System.out.println(path);
        PImage image = Main.app.loadImage(path);

        // le hago un crop :D
        image = Utils.cropBinarizedImage(image);
        // obtengo la parte de la derecha
        halfImage = image.get(image.width / 2, 0, image.width / 2, image.height);

        int valorMaximo = halfImage.width;

        distribution = new double[halfImage.height];
        distribution_acumulada = new double[halfImage.height];

        // recorro la imagen para encontrar los valores.
        int val = 0;
        for (int fil = 0; fil < halfImage.height; fil++) {
            for (int col = halfImage.width; col > 0; col--) {

                if (Main.app.brightness(halfImage.get(col, fil)) > 100) {
                    distribution[fil] = col;
                    val += col;
                    distribution_acumulada[fil] = val;
                    break;
                }

            }
        }

        val_acumuado_maximo = val;
        this.distribution_smoothed = lowPass1D(distribution);

        for (int i = 0; i < 50; i++) {
            this.distribution_smoothed = lowPass1D(distribution_smoothed);
        }

    }

    // from github :D https://stackoverflow.com/questions/4026648/how-to-implement-low-pass-filter-using-java
    public double[] lowPass1D(double[] distribution) {
        // por ahora el tamaño de la matriz será de 5

//        double[] valoresGauss = {1, 6, 15, 20, 15, 6, 1};
//        double[] valoresGauss = {1, 8, 28, 56, 70, 56, 28, 8, 1};

        double[] valoresGauss = {1, 9, 36, 84, 126, 126, 84, 36, 9, 1};

        int tam_matrix = valoresGauss.length;
        double[] result = new double[distribution.length];

        for (int i = tam_matrix; i < distribution.length - 1; i++) {
            double _r = 0;
            int _pos = i - (tam_matrix / 2);
            double _div = 0;

            for (double vg : valoresGauss) {
                if (_pos > distribution.length - 1) {
                    break;
                }
                _r += (distribution[_pos] * vg);
                _pos++;
                _div += vg;
            }

            _r = _r / _div;
            result[i] = _r;

//            result[i] = distribution[i];
        }

//        System.out.println("Se finaliza el filtro lowpass");
        return result;
    }

    public void buscarPendientes() {

        pendientes = new double[distribution_smoothed.length];

        for (int x_b = 1; x_b < distribution_smoothed.length - 1; x_b++) {
            int x_a = x_b - 1;

            double y_b = distribution_smoothed[x_b];
            double y_a = distribution_smoothed[x_a];


            double m = (y_b - y_a) / (x_b - x_a);
            pendientes[x_b] = m;

        }
//        System.out.println("se encuentran las pendientes");

    }

    private void guardarDatos() {
        // The name of the file to open.
        String fileName = "results/datas/" + name + "_distributions.csv";

        try {
            // Assume default encoding.
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), "utf-8"))) {
//                writer.write("something");
            }
            FileWriter fileWriter = new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            //agrego primero los titulos
            bufferedWriter.write("original,low_pass, pendiente");
            bufferedWriter.newLine();

            for (int i = 0; i < distribution.length - 1; i++) {
                bufferedWriter.write(distribution[i] + "," + distribution_smoothed[i] + "," + pendientes[i]);
                bufferedWriter.newLine();
            }
            // Always close files.
            bufferedWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error writing to file '" + fileName);
        }
//        System.out.println("se guardan los datos");
    }

}

