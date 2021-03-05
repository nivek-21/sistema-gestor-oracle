package Models;

import Controllers.Controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Set;
import javax.swing.JPanel;

public class MainModel extends Observable {

    private ArrayList<Controller> controllers;
    private HashMap<String, HashMap<String, JPanel>> core;

    public MainModel() {
        this.controllers = new ArrayList<>();
        this.core = new HashMap<>();
    }

    /**
     * Método que retorna todos los controladores.
     *
     * @return
     */
    public ArrayList<Controller> getControllers() {
        return controllers;
    }

    /**
     * Método que agrega un controlador al array de controladores.
     *
     * @param controller
     */
    public void addController(Controller controller) {
        HashMap<String, JPanel> panels = new HashMap<>();
        controller.setPanels(panels);
        core.put(controller.getLabelName(), panels);
        controllers.add(controller);
    }

    /**
     * Método que inicializa el panel izquierdo de la vista actualizando ese
     * observador.
     *
     */
    public void create() {
        setChanged();
        notifyObservers();
    }

    /**
     * Método que retorna el panel según el nombre del controlador y le nombre
     * del botón.
     *
     * @param labelname
     * @param panelname
     * @return
     */
    public JPanel getRightPanel(String labelname, String panelname) {
        return core.get(labelname).get(panelname);
    }

    /**
     * Retorna la lista de labels que se han agregado.
     *
     * @return
     */
    public Set<String> getLabels() {
        return core.keySet();
    }

    /**
     * Retorna la lista de los nombres de los botones segun el nombre de
     * controlador.
     *
     * @param label
     * @return
     */
    public Set<String> getButtons(String label) {
        return core.get(label).keySet();
    }
}
