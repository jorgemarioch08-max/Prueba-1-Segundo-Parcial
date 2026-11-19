package Logica;

import java.io.File;
import java.util.Date;

/**
 * Calcula estadisticas recursivas sobre el arbol de un directorio:
 * conteo y tamano por extension (.txt, .java, .pdf, otros), archivo mas grande,
 * mas reciente, profundidad maxima, carpetas y carpetas vacias.
 * Los archivos sin extension y los ocultos se cuentan en "otros".
 */
public class Estadisticas {

    private String[] categorias = {"txt", "java", "pdf", "otros"};
    private int[] cantidades = new int[4];
    private long[] tamanos = new long[4];
    private File masGrande = null;
    private File masReciente = null;
    private int profundidadMax = 0;
    private int carpetas = 0;
    private int carpetasVacias = 0;

    /**
     * Recorre recursivamente el arbol y acumula las estadisticas.
     * @param raiz nodo raiz generado por el Explorador
     */
    public void calcular(NodoArchivo raiz) {
        recorrer(raiz, 0);
    }

    private void recorrer(NodoArchivo nodo, int nivel) {
        if (nivel > profundidadMax) {
            profundidadMax = nivel;
        }
        if (nodo.isCarpeta()) {
            carpetas++;
            if (nodo.getHijos().isEmpty()) {
                carpetasVacias++;
            }
            for (NodoArchivo hijo : nodo.getHijos()) {
                recorrer(hijo, nivel + 1);
            }
        } else {
            contarArchivo(nodo.getFile());
        }
    }

    private void contarArchivo(File f) {
        int i = indiceCategoria(f.getName());
        cantidades[i]++;
        tamanos[i]+=f.length();
        if (masGrande == null || f.length() > masGrande.length()) {
            masGrande = f;
        }
        if (masReciente == null || f.lastModified() > masReciente.lastModified()) {
            masReciente = f;
        }
    }

    //Devuelve la posicion de la categoria segun la extension (sin distinguir mayusculas)
    private int indiceCategoria(String nombre) {
        int punto = nombre.lastIndexOf('.');
        if (punto <= 0) {
            return 3;
        }
        String ext = nombre.substring(punto + 1).toLowerCase();
        for (int i = 0; i < 3; i++) {
            if (categorias[i].equals(ext)) {
                return i;
            }
        }
        return 3;
    }

    /**
     * Convierte bytes a un texto legible (B, KB, MB, GB).
     */
    public static String formatoTamano(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        double valor = bytes / 1024.0;
        String[] unidades = {"KB", "MB", "GB"};
        int u = 0;
        while (valor >= 1024 && u < 2) {
            valor/=1024;
            u++;
        }
        return String.format("%.2f %s", valor, unidades[u]);
    }

    /**
     * Genera el texto del reporte con todas las estadisticas.
     */
    public String generarReporte() {
        long total = 0;
        for (long t : tamanos) {
            total+=t;
        }
        String r = "ESTADISTICAS POR EXTENSION\n";
        for (int i = 0; i < 4; i++) {
            double porc = total == 0 ? 0 : tamanos[i] * 100.0 / total;
            r+="." + categorias[i] + ": " + cantidades[i] + " archivos, "
                    + formatoTamano(tamanos[i]) + ", " + String.format("%.2f", porc) + "%\n";
        }
        r+="\nESTADISTICAS GLOBALES\n";
        r+="Tamano total: " + formatoTamano(total) + "\n";
        r+="Archivo mas grande: " + (masGrande == null ? "-" : masGrande.getAbsolutePath()
                                                               + " (" + formatoTamano(masGrande.length()) + ")") + "\n";
        r+="Archivo mas reciente: " + (masReciente == null ? "-" : masReciente.getAbsolutePath()
                                                                   + " (" + new Date(masReciente.lastModified()) + ")") + "\n";
        r+="Profundidad maxima: " + profundidadMax + "\n";
        r+="Carpetas: " + carpetas + "\n";
        r+="Carpetas vacias: " + carpetasVacias + "\n";
        return r;
    }
}