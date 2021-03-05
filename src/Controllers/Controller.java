package Controllers;

import java.util.HashMap;
import javax.swing.JPanel;

/**
 * Esta interfaz esta creada para que todos los controladores sean iguales y
 * puedan entrar en el template.
 *
 * @author Kevin González
 */
public interface Controller {

    /**
     * Método que recibe un hashmap tipo clave-valor con el nombre del botón y
     * el panel correspondiente a él.
     *
     * @param handle
     */
    public void setPanels(HashMap<String, JPanel> handle);

    /**
     * Retorna el nombre general del controlador que estamos utiliando.
     *
     * @return
     */
    public String getLabelName();
}
