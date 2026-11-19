package Logica;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Recorre recursivamente un directorio y construye su arbol de nodos.
 */
public class Bitacora {

    public static final String ARCHIVO = "bitacora.log";
    public static final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Valida que la ruta no este vacia, exista y sea un directorio.
     * @param ruta texto ingresado por el usuario
     * @return el directorio validado
     * @throws RutaInvalidaException si la ruta no es valida
     */

    public static void registrar(String accion, String ruta) {
        try (FileWriter fw = new FileWriter(ARCHIVO, true)) {
            fw.write(formato.format(new Date()) + " | " + accion + " | " + ruta + "\n");
        } catch (IOException e) {
            System.out.println("No se pudo escribir en la bitacora: " + e.getMessage());
        }
    }


}
