package Administrador;

import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;
import Logica.Explorador;
import Logica.NodoArchivo;
import Logica.RutaInvalidaException;
import Logica.Estadisticas;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import Logica.Buscador;

public class Administrador {

    private Explorador explorador;
    private NodoArchivo ultimoArbol = null;

    public Administrador() {
        explorador = new Explorador();
    }

    //Valida la ruta, recorre y convierte el resultado a nodos de JTree
    public DefaultMutableTreeNode generarArbol(String ruta, int profMax) throws RutaInvalidaException {
        File raiz = explorador.validarRuta(ruta);
        ultimoArbol = explorador.construirArbol(raiz, profMax);
        return convertir(ultimoArbol);
    }

    //Valida los filtros y busca en el ultimo directorio analizado
    public ArrayList<File> buscar(String texto, String ext, String minKB, String maxKB,
                                  String desde, String hasta) {
        if (ultimoArbol == null) {
            throw new IllegalArgumentException("Primero analice un directorio.");
        }
        long min = leerKB(minKB, "Tamano minimo");
        long max = leerKB(maxKB, "Tamano maximo");
        if (min != -1 && max != -1 && min > max) {
            throw new IllegalArgumentException("El tamano minimo no puede ser mayor que el maximo.");
        }
        long fDesde = leerFecha(desde, false);
        long fHasta = leerFecha(hasta, true);
        if (fDesde != -1 && fHasta != -1 && fDesde > fHasta) {
            throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        }
        String extLimpia = ext.trim().startsWith(".") ? ext.trim().substring(1) : ext.trim();
        Buscador b = new Buscador(texto.trim(), extLimpia, min, max, fDesde, fHasta);
        return b.buscar(ultimoArbol);
    }

    //Convierte KB a bytes; vacio = -1 (sin filtro)
    private long leerKB(String valor, String campo) {
        if (valor.trim().isEmpty()) {
            return -1;
        }
        try {
            long kb = Long.parseLong(valor.trim());
            if (kb < 0) {
                throw new IllegalArgumentException(campo + " no puede ser negativo.");
            }
            return kb * 1024;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un numero entero.");
        }
    }

    //Convierte yyyy-MM-dd a milisegundos; vacio = -1 (sin filtro)
    private long leerFecha(String valor, boolean finDelDia) {
        if (valor.trim().isEmpty()) {
            return -1;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            long ms = sdf.parse(valor.trim()).getTime();
            return finDelDia ? ms + 86399999 : ms;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Fecha invalida (use yyyy-MM-dd): " + valor);
        }
    }

    private DefaultMutableTreeNode convertir(NodoArchivo nodo) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(nodo);
        for (NodoArchivo hijo : nodo.getHijos()) {
            treeNode.add(convertir(hijo));
        }
        return treeNode;
    }

    public String generarEstadisticas() {
        if (ultimoArbol == null) {
            return "Primero analice un directorio.";
        }
        Estadisticas est = new Estadisticas();
        est.calcular(ultimoArbol);
        return est.generarReporte();
    }

}