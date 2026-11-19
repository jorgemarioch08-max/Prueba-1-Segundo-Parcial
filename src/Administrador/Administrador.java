package Administrador;

import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;
import Logica.Explorador;
import Logica.NodoArchivo;
import Logica.RutaInvalidaException;

public class Administrador {

    private Explorador explorador;

    public Administrador() {
        explorador = new Explorador();
    }

    //Valida la ruta, recorre y convierte el resultado a nodos de JTree
    public DefaultMutableTreeNode generarArbol(String ruta, int profMax) throws RutaInvalidaException {
        File raiz = explorador.validarRuta(ruta);
        NodoArchivo nodo = explorador.construirArbol(raiz, profMax);
        return convertir(nodo);
    }

    private DefaultMutableTreeNode convertir(NodoArchivo nodo) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(nodo);
        for (NodoArchivo hijo : nodo.getHijos()) {
            treeNode.add(convertir(hijo));
        }
        return treeNode;
    }
}