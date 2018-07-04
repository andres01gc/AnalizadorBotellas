package gc;

import gc.utils.Utils;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import processing.core.PImage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Silueta {
    private String path;
    private String name;
    private double[] distribution;
    private double[] distribution_1;

    //por ahora solo guardaré la siluetal
    public Silueta(String path) {
        analizarPath(path);
        cargarDistribucion();
        guardarDatos();
    }

    public void analizarPath(String path) {
        this.path = path;
        this.name = new File(this.path).getName().split("_crop")[0];
        System.out.println(this.name);
    }

    /**
     * TODO reemplazar processing por openCV en caso necesario
     * Al ser binaria solo me importa la distribución, procesing solo lo usarié por la facilidad que tiene para
     * gestionar imagenes.
     */
    public void cargarDistribucion() {
        PImage image = Main.app.loadImage(path);

        // le hago un crop :D
        image = Utils.cropBinarizedImage(image);
        // obtengo la parte de la derecha
        PImage halfImage = image.get(image.width / 2, 0, image.width / 2, image.height);

        int valorMaximo = halfImage.width;

        distribution = new double[halfImage.height];

        // recorro la imagen para encontrar los valores.

        for (int fil = 0; fil < halfImage.height; fil++) {
            for (int y = halfImage.width; y > 0; y--) {
                if (Main.app.brightness(halfImage.get(y, fil)) > 50) {
                    distribution[fil] = y;
//                    System.out.println(y);
                    break;
                }

            }
        }

        this.distribution_1 = lowPass1D();

    }

    // from github :D https://stackoverflow.com/questions/4026648/how-to-implement-low-pass-filter-using-java
    public double[] lowPass1D() {
        // por ahora el tamaño de la matriz será de 5

//        double[] valoresGauss = {1, 6, 15, 20, 15, 6, 1};
        double[] valoresGauss = {1, 8, 28, 56, 70, 56, 28, 8, 1};
//        double[] valoresGauss = {1, 6, 15, 20, 15, 6, 1};
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
            result[i] = (int) _r;

//            result[i] = distribution[i];
        }

        System.out.println("Se finaliza el filtro lowpass");

        return result;
    }

    private void guardarDatos() {
        // The name of the file to open.
        String fileName = "distribuciones.csv";

        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            //agrego primero los titulos
            bufferedWriter.write("original,low_pass");
            bufferedWriter.newLine();

            for (int i = 0; i < distribution.length - 1; i++) {
                bufferedWriter.write(distribution[i] + "," + distribution_1[i]);
                bufferedWriter.newLine();
            }
            // Always close files.
            bufferedWriter.close();
        } catch (IOException ex) {
            System.out.println(
                    "Error writing to file '" + fileName + "'");
        }
        System.out.println("se guardan los datos");
    }

}

