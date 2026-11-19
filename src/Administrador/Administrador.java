package Administrador;

import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;
import Logica.Explorador;
import Logica.NodoArchivo;
import Logica.RutaInvalidaException;
import Logica.Estadisticas;

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