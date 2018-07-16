package gc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnalizadorSiluetas {
    List<Silueta> siluetas = new ArrayList<>();

    AnalizadorSiluetas() {
        cargarCarpetaConSiluetas("E:\\Gc\\Trabajo\\Arce\\Botellas\\Imagenes_Finales\\Todas\\bin");
    }

    public void analizarSiluetas() {

    }

    void cargarCarpetaConSiluetas(String pathCarpetaImagenes) {
        File principalFile = new File(pathCarpetaImagenes);

        for (File f : principalFile.listFiles()) {
            //compruebo si el nombre es jpg

            if (f.getName().toUpperCase().contains(".JPG")) {
                new Silueta(f.getAbsolutePath());
                System.out.println("fin tras la primera imagen");
                break;
            }
        }

        Main.app.exit();
        System.out.println("Se Cargan todas la siluetas");

    }
}
