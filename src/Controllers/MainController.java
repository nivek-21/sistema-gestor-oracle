package Controllers;

import Helpers.Emergent;
import Helpers.QueryLog;
import Models.MainModel;
import Views.MainView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.JButton;

public class MainController implements ActionListener {

    /**
     * Modelo y Vista estandar de cada controlador.
     */
    private MainModel model;
    private MainView view;

    Emergent em = new Emergent();

    public MainController() {
        this.model = new MainModel();
        this.view = new MainView(this, model);
    }

    public void addController(Controller controller) {
        this.model.addController(controller);
    }

    /**
     * Inicia la aplicación.
     */
    public void init() {
        this.model.create();
        this.view.init();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == view.getQueryLog()) {
            QueryLog.show();
            return;
        }

        if (e.getSource() == view.getInfo()) {
            InfoController info = new InfoController();
            info.init();
        }

        if (e.getSource() == view.getSalir()) {
            System.exit(0);
        }

        Iterator<JButton> buttons = this.view.getButtons().keySet().iterator();

        while (buttons.hasNext()) {

            JButton currentBtn = buttons.next();

            if (e.getSource() == currentBtn) {

                String currentButton = currentBtn.getAccessibleContext().getAccessibleDescription();
                String currentLabel = view.getButtons().get(currentBtn);
                view.setRightPanel(model.getRightPanel(currentLabel, currentButton));

            }

        }
    }

}
