package Helpers;

import Principal.Config;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * Clase encargada de generar ventanas emergentes, la misma es de tipo JFrame.
 */
public class Emergent extends JFrame {

    /**
     * Único panel que agregamos al JFrame.
     */
    private JPanel panel;

    /**
     * Constructor que inicializa los componentes y el JPanel.
     */
    public Emergent() {
        this.panel = new JPanel();
        initComponents();
    }

    /**
     * Método que inicializa los componentes del JFrame y agrega el JPanel al
     * JFrame.
     */
    private void initComponents() {
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.add(panel);
    }

    /**
     * Método principal que setea el JPanel del JFrame al nuevo que ingresa.
     * Esto para que el panel cambie.
     *
     * @param newPanel JPanel
     * @param width int
     * @param height int
     */
    public void open(JPanel newPanel, int width, int height) {

        if (this.isVisible()) {
            close();
        }

        this.remove(this.panel);
        this.panel = newPanel;
        this.add(this.panel);

        this.setSize(width, height);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Método que cierra el JFrame en caso de ser necesario.
     */
    public void close() {
        if (this.isVisible()) {
            this.dispose();
        }
    }

    /**
     * Método que levanta una ventana emergente con un string dentro, el mismo
     * es recibido por parámetro.
     *
     * @param log String
     */
    public void showFile(String log) {

        if (log == null || log.isEmpty()) {
            message(Config.es("error_file"));
            return;
        }

        JTextArea textArea = new JTextArea(37, 82);
        textArea.setEditable(false);
        textArea.setSize(700, 800);
        textArea.setText(log);

        JScrollPane scroll = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        JButton btnClose = new JButton("CERRAR");
        btnClose.addActionListener((e) -> {
            close();
        });

        JPanel mainPanel = new JPanel();
        mainPanel.add(new JLabel(Config.es("end_process")));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(scroll);
        mainPanel.add(btnClose);

        open(mainPanel, 1000, 717);
    }

    /**
     * Método estático que escribe en la consola de forma ordenada. El mismo
     * recibe un array indefinido de tipo String.
     *
     * @param msjs String[]
     */
    public static void showConsole(String... msjs) {
        int i = 1;
        for (String msj : msjs) {
            System.out.println(i + ". " + msj);
            i++;
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    /**
     * Método estático que levanta un mensaje simple en pantalla.
     *
     * @param key String
     */
    public static void message(String key) {
        JOptionPane.showMessageDialog(null, key);
    }

    /**
     * Método que retorna un string ingresado por un usuario, el mismo realiza
     * validaciones básicas.
     *
     * @param msj String
     * @return String
     */
    public static String getString(String msj) {
        String response = JOptionPane.showInputDialog(msj);
        if (response == null || response.isEmpty()) {
            return "empty";
        } else {
            return response;
        }
    }
}
