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
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class VentanaPrincipal extends JFrame {

    private Administrador admin;
    private JTextArea txtEstadisticas;
    private JTextField txtBuscar, txtExt, txtMinKB, txtMaxKB, txtDesde, txtHasta;
    private DefaultTableModel modeloTabla;

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
        tabs.addTab("Estadisticas", crearPanelEstadisticas());
        tabs.addTab("Busqueda", crearPanelBusqueda());
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
            txtEstadisticas.setText(admin.generarEstadisticas());
        } catch (RutaInvalidaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Ruta invalida", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JScrollPane crearPanelEstadisticas() {
        txtEstadisticas = new JTextArea();
        txtEstadisticas.setEditable(false);
        return new JScrollPane(txtEstadisticas);
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel filtros = new JPanel(new GridLayout(2, 1));
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBuscar = new JTextField(15);
        txtExt = new JTextField(5);
        txtMinKB = new JTextField(6);
        txtMaxKB = new JTextField(6);
        txtDesde = new JTextField(8);
        txtHasta = new JTextField(8);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscar());

        fila1.add(new JLabel("Nombre:"));
        fila1.add(txtBuscar);
        fila1.add(new JLabel("Extension:"));
        fila1.add(txtExt);
        fila1.add(new JLabel("Tamano min (KB):"));
        fila1.add(txtMinKB);
        fila1.add(new JLabel("max (KB):"));
        fila1.add(txtMaxKB);
        fila2.add(new JLabel("Modificado desde (yyyy-MM-dd):"));
        fila2.add(txtDesde);
        fila2.add(new JLabel("hasta:"));
        fila2.add(txtHasta);
        fila2.add(btnBuscar);
        filtros.add(fila1);
        filtros.add(fila2);

        String[] columnas = {"Nombre", "Ruta absoluta", "Tamano (bytes)", "Modificado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 2) {
                    return Long.class;
                }
                if (col == 3) {
                    return Date.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int fila, int col) {
                return false;
            }
        };
        JTable tabla = new JTable(modeloTabla);
        tabla.setAutoCreateRowSorter(true);

        panel.add(filtros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void buscar() {
        try {
            ArrayList<File> lista = admin.buscar(txtBuscar.getText(), txtExt.getText(), txtMinKB.getText(),
                    txtMaxKB.getText(), txtDesde.getText(), txtHasta.getText());
            modeloTabla.setRowCount(0);
            for (File f : lista) {
                modeloTabla.addRow(new Object[]{f.getName(), f.getAbsolutePath(), f.length(), new Date(f.lastModified())});
            }
            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron archivos que coincidan con los criterios.");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Datos invalidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    //Distinguir carpetas de archivos con el icono
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