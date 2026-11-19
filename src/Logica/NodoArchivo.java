package Logica;

import java.io.File;
import java.util.ArrayList;

/**
 * Nodo del arbol de directorios. Representa un archivo o carpeta
 * y gurada solo las carpetas.
 */

public class NodoArchivo {

    private File file;
    private ArrayList<NodoArchivo> hijos;

    public NodoArchivo(File file) {
        this.file = file;
        hijos = new ArrayList<>();
    }

    public void agregarHijo(NodoArchivo hijo) {
        hijos.add(hijo);
    }

    public boolean isCarpeta() {
        return file.isDirectory();
    }

    public File getFile() {
        return file;
    }

    public ArrayList<NodoArchivo> getHijos() {
        return hijos;
    }

    @Override
    public String toString() {
        return file.getName().isEmpty() ? file.getAbsolutePath() : file.getName();
    }


}
