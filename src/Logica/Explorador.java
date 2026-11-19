package Logica;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Recorre recursivamente un directorio y construye su arbol de nodos.
 */
public class Explorador {

    /**
     * Valida que la ruta no este vacia, exista y sea un directorio.
     * @param ruta texto ingresado por el usuario
     * @return el directorio validado
     * @throws RutaInvalidaException si la ruta no es valida
     */

    public File validarRuta(String ruta) throws RutaInvalidaException {
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new RutaInvalidaException("La ruta no puede estar vacia.");
        }
        File dir = new File(ruta.trim());
        if (!dir.exists()) {
            throw new RutaInvalidaException("La ruta no existe: " + dir.getAbsolutePath());
        }
        if (!dir.isDirectory()) {
            throw new RutaInvalidaException("La ruta no es un directorio: " + dir.getAbsolutePath());
        }
        return dir;
    }

    /**
     * Construye el arbol completo del directorio raiz.
     * @param raiz directorio inicial
     * @param profMax profundidad maxima (0 = ilimitada)
     * @return nodo raiz del arbol
     */

    public NodoArchivo construirArbol(File raiz, int profMax) {
        NodoArchivo nodo = new NodoArchivo(raiz);
        llenarNodo(nodo, 1, profMax);
        Bitacora.registrar("ANALIZAR", raiz.getAbsolutePath());
        return nodo;
    }

    public void llenarNodo(NodoArchivo nodo, int nivel, int profMax) {
        if (profMax != 0 && nivel > profMax) {
            return;
        }
        File[] contenido = nodo.getFile().listFiles();
        if (contenido == null) {
            Bitacora.registrar("SIN PERMISO", nodo.getFile().getAbsolutePath());
            return;
        }
        for (File f : contenido) {
            if (Files.isSymbolicLink(f.toPath())) {
                continue;
            }
            NodoArchivo hijo = new NodoArchivo(f);
            nodo.agregarHijo(hijo);
            if (f.isDirectory()) {
                llenarNodo(hijo, nivel + 1, profMax);
            }
        }
    }
}
