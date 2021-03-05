package Views;

import Controllers.MainController;
import Models.MainModel;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class MainView extends JFrame implements Observer {

    private MainController controller;
    private MainModel model;
    private Home homepage;

    private Container container;
    private JSplitPane splitPane;
    private HashMap<JButton, String> buttons;

    /**
     * Opciones Principales.
     */
    JMenuBar mb;
    JMenu menu;
    JMenuItem queryLog;
    JMenuItem info;
    JMenuItem salir;

    public MainView(MainController controller, MainModel model) {

        /**
         * Configuración General del JFrame
         */
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1150, 700);
        this.setTitle("KBJC - System");

        /**
         * Configuración del munu desplegable.
         */
        mb = new JMenuBar();
        menu = new JMenu("Archivo");

        queryLog = new JMenuItem("Registro de Consultas");
        queryLog.addActionListener(controller);
        menu.add(queryLog);

        info = new JMenuItem("Información del sistema");
        info.addActionListener(controller);
        menu.add(info);

        salir = new JMenuItem("Salir");
        salir.addActionListener(controller);
        menu.add(salir);

        mb.add(menu);
        this.setJMenuBar(mb);

        /**
         * Configuración del controlador y el modelo.
         */
        this.controller = controller;
        this.model = model;
        this.model.addObserver(this);

        /**
         * Configuración de los paneles del JSplitPane. Originalmente el panel
         * derecho tiene un panel por defecto pero el izquierdo no.
         */
        this.container = getContentPane();
        this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        this.splitPane.setEnabled(false);
        this.homepage = new Home();
        this.splitPane.setRightComponent(homepage);
        this.container.add(splitPane);

        /**
         * Otras declaraciones.
         */
        this.buttons = new HashMap<>();
    }

    public HashMap<JButton, String> getButtons() {
        return buttons;
    }

    public JMenuItem getQueryLog() {
        return queryLog;
    }

    public JMenuItem getInfo() {
        return info;
    }

    public JMenuItem getSalir() {
        return salir;
    }

    /**
     * Método que inicia la ventana.
     */
    public void init() {
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Método que actualiza el panel actual izquierdo en caso de ser necesario.
     */
    private void updatePanel() {
        JPanel leftPanel = new JPanel();

        leftPanel.setBackground(Color.LIGHT_GRAY);

        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        BoxLayout boxlayout = new BoxLayout(leftPanel, BoxLayout.Y_AXIS);
        leftPanel.setLayout(boxlayout);

        JButton home = new JButton("INICIO");
        home.addActionListener(this.controller);
        home.setMaximumSize(new Dimension(500, 40));
        home.addActionListener((e) -> {
            setRightPanel(homepage);
        });

        leftPanel.add(home);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10))); //

        Iterator<String> labels = this.model.getLabels().iterator();

        while (labels.hasNext()) {

            String currentLabel = labels.next();
            Iterator<String> btns = this.model.getButtons(currentLabel).iterator();

            if (model.getButtons(currentLabel).size() > 1) {
                JLabel label = new JLabel(currentLabel);
                leftPanel.add(label);
            }

            leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            while (btns.hasNext()) {
                String currentBtn = btns.next();

                String[] values = currentBtn.split("::");

                JButton button = new JButton(values[0]);
                button.getAccessibleContext().setAccessibleDescription(currentBtn);

                if (values.length > 1) {
                    String iconRoute = "/Assets/" + values[1] + ".png";
                    button.setIcon(new ImageIcon(Class.class.getResource(iconRoute)));
                }
                button.setHorizontalAlignment(SwingConstants.LEFT);

                button.addActionListener(this.controller);
                button.setMaximumSize(new Dimension(500, 40));
                this.buttons.put(button, currentLabel);
                leftPanel.add(button);
                leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }

            leftPanel.add(Box.createRigidArea(new Dimension(0, 5))); //

        }

        splitPane.setLeftComponent(leftPanel);
    }

    /**
     * Método que setea el panel derecho con la nueva vista. El mismo recibe el
     * panel que va a ser insertado.
     *
     * @param rightPanel
     */
    public void setRightPanel(JPanel rightPanel) {
        if (rightPanel != null && rightPanel != homepage) {

            JScrollPane scrollPane = new JScrollPane(rightPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.getVerticalScrollBar().setUnitIncrement(20);

            splitPane.setRightComponent(scrollPane);
        } else {
            splitPane.setRightComponent(homepage);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        updatePanel();
    }
}
