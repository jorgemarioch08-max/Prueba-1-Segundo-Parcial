package GUI;

import Administrador.Administrador;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import Logica.NodoArchivo;
import Logica.RutaInvalidaException;

public class VentanaPrincipal extends JFrame {

    private Administrador admin;

    //GUI
    private JTextField txtRuta;
    private JSpinner spnProfundidad;
    private JButton btnExaminar;
    private JButton btnAnalizar;
    private JTree arbol;
    private JTabbedPane tabs;

    public VentanaPrincipal() {
        admin = new Administrador();
        setTitle("Explorador de Archivos");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(crearPanelSuperior(), BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.addTab("Arbol", crearPanelArbol());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtRuta = new JTextField(25);
        btnExaminar = new JButton("Examinar...");
        spnProfundidad = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        btnAnalizar = new JButton("Analizar");

        btnExaminar.addActionListener(e -> seleccionarCarpeta());
        btnAnalizar.addActionListener(e -> analizar());

        panel.add(new JLabel("Directorio raiz:"));
        panel.add(txtRuta);
        panel.add(btnExaminar);
        panel.add(new JLabel("Profundidad (0 = ilimitada):"));
        panel.add(spnProfundidad);
        panel.add(btnAnalizar);
        return panel;
    }

    private JScrollPane crearPanelArbol() {
        arbol = new JTree(new DefaultMutableTreeNode("Seleccione un directorio"));
        arbol.setCellRenderer(new RendererArchivo());
        return new JScrollPane(arbol);
    }

    private void seleccionarCarpeta() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtRuta.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void analizar() {
        try {
            int prof = (Integer) spnProfundidad.getValue();
            DefaultMutableTreeNode raiz = admin.generarArbol(txtRuta.getText(), prof);
            arbol.setModel(new DefaultTreeModel(raiz));
        } catch (RutaInvalidaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Ruta invalida", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Distingue carpetas de archivos con el icono
    private class RendererArchivo extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            Object obj = ((DefaultMutableTreeNode) value).getUserObject();
            if (obj instanceof NodoArchivo) {
                NodoArchivo nodo = (NodoArchivo) obj;
                if (nodo.isCarpeta()) {
                    setIcon(UIManager.getIcon("FileView.directoryIcon"));
                } else {
                    setIcon(UIManager.getIcon("FileView.fileIcon"));
                }
            }
            return this;
        }
    }
}