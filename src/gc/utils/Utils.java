package gc.utils;

import gc.Main;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Created by andre on 4/7/2017.
 * <p>
 * Contiene Filtros genericos que pueden ser aplicados a objectos PImage
 */
public class Utils {
    private static PApplet app = Main.app;

    //recorta y  enmarca un objeto que este en una imagen sin  fondo (png)
    public static PImage cropBackgroundImage(PImage source) {
        PImage image = source.get();

        int posX = 0, posY = 0;//esquina superior del recorte
        int ancho = 0, alto = 0;

        image.loadPixels();

        int umbral = 250;
        int salto = 1;

        boolean continuar = true;

        //recorres parte superior
        for (int y = 0; y < image.height; y += salto) {
            for (int x = 0; x < image.width; x += salto) {
                int posPx = x + (image.width * y);
                if (app.alpha(image.pixels[posPx]) > 200) {
                    posY = y;
                    continuar = false;
                    break;
                }
            }
            if (!continuar) break;
        }


        continuar = true;
        //recorrer parte izquierda
        for (int x = 0; x < image.width; x += salto) {
            for (int y = 0; y < image.height; y += salto) {
                int posPx = x + (image.width * y);
                if (app.alpha(image.pixels[posPx]) > umbral) {
                    posX = x;
                    continuar = false;
                    break;
                }
            }
            if (!continuar) break;
        }

        continuar = true;
        for (int x = image.width - 1; x > 0; x -= salto) {
            for (int y = image.height - 1; y > 0; y -= salto) {
                int posPx = x + (image.width * y);
                if (app.alpha(image.pixels[posPx]) > umbral) {
                    ancho = x - posX + 0;
                    continuar = false;
                    break;
                }
            }
            if (!continuar) break;

        }
        continuar = true;
        for (int y = image.height - 1; y > 0; y -= salto) {
            for (int x = image.width - 1; x > 0; x -= salto) {
                int posPx = x + (image.width * y);
                if (app.alpha(image.pixels[posPx]) > umbral) {
                    alto = y - posY;
                    continuar = false;
                    break;
                }
            }
            if (!continuar) break;
        }

        System.out.println("posX: " + posX + " " + "posY: " + posY + " " + "w: " + ancho + " " + "h: " + alto);
        PImage resultado = image.get(posX, posY, ancho, alto);
        image.updatePixels();

        return resultado;
    }

    public static PImage cropBinarizedImage(PImage source) {
        PImage image = source.get();

        int posX = 0, posY = 0;// esquina superior del recorte
        int ancho = 0, alto = 0;

        image.loadPixels();

        int umbral = 250;
        int salto = 1;

        boolean continuar = true;

        //recorres parte superior
        for (int y = 0; y < image.height; y += salto) {
            for (int x = 0; x < image.width; x += salto) {
                int posPx = x + (image.width * y);
                if (app.brightness(image.pixels[posPx]) > 200) {
                    posY = y;
                    continuar = false;
                    break;
                }
            }
            if (!continuar) break;
        }


        continuar = true;
        //recorrer parte izquierda
        for (int x = 0; x < image.width; x += salto) {
            for (int y = 0; y < image.height; y += salto) {
                int posPx = x + (image.width * y);
                if (app.brightness(image.pixels[posPx]) > umbral) {
                    posX = x;
                    continuar = false;
                    break;
                }
            }
            if (!continuar) break;
        }

        continuar = true;
        for (int x = image.width - 1; x > 0; x -= salto) {
            for (int y = image.height - 1; y > 0; y -= salto) {
                int posPx = x + (image.width * y);
                if (app.brightness(image.pixels[posPx]) > umbral) {
                    ancho = x - posX + 0;
                    continuar = false;
                    break;
                }
            }
            if (!continuar) break;

        }
        continuar = true;
        for (int y = image.height - 1; y > 0; y -= salto) {
            for (int x = image.width - 1; x > 0; x -= salto) {
                int posPx = x + (image.width * y);
                if (app.brightness(image.pixels[posPx]) > umbral) {
                    alto = y - posY;
                    continuar = false;
                    break;
                }
            }
            if (!continuar) break;
        }

//        System.out.println("posX: " + posX + " " + "posY: " + posY + " " + "w: " + ancho + " " + "h: " + alto);
        PImage resultado = image.get(posX, posY, ancho, alto);
        image.updatePixels();

        return resultado;
    }

}
