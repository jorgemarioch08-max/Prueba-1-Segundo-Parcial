package Logica;

import java.io.File;
import java.util.ArrayList;

/**
 * Busca recursivamente archivos por nombre (sin distinguir mayusculas)
 * combinando filtros opcionales de extension, tamano y fecha de modificacion.
 * Un filtro con valor -1 (o texto vacio) no se aplica.
 */
public class Buscador {

    private String texto;
    private String extension;
    private long minBytes;
    private long maxBytes;
    private long desde;
    private long hasta;
    private ArrayList<File> resultados;

    public Buscador(String texto, String extension, long minBytes, long maxBytes, long desde, long hasta) {
        this.texto = texto.toLowerCase();
        this.extension = extension.toLowerCase();
        this.minBytes = minBytes;
        this.maxBytes = maxBytes;
        this.desde = desde;
        this.hasta = hasta;
        resultados = new ArrayList<>();
    }

    /**
     * Busca en todo el arbol a partir de la raiz.
     * @param raiz nodo raiz generado por el Explorador
     * @return archivos que cumplen todos los criterios
     */
    public ArrayList<File> buscar(NodoArchivo raiz) {
        recorrer(raiz);
        Bitacora.registrar("BUSCAR", raiz.getFile().getAbsolutePath() + " texto='" + texto + "'");
        return resultados;
    }

    private void recorrer(NodoArchivo nodo) {
        if (nodo.isCarpeta()) {
            for (NodoArchivo hijo : nodo.getHijos()) {
                recorrer(hijo);
            }
        } else if (cumple(nodo.getFile())) {
            resultados.add(nodo.getFile());
        }
    }

    private boolean cumple(File f) {
        String nombre = f.getName().toLowerCase();
        if (!nombre.contains(texto)) {
            return false;
        }
        if (!extension.isEmpty() && !nombre.endsWith("." + extension)) {
            return false;
        }
        if (minBytes != -1 && f.length() < minBytes) {
            return false;
        }
        if (maxBytes != -1 && f.length() > maxBytes) {
            return false;
        }
        if (desde != -1 && f.lastModified() < desde) {
            return false;
        }
        if (hasta != -1 && f.lastModified() > hasta) {
            return false;
        }
        return true;
    }
}