package Controllers;

import Helpers.Emergent;
import Models.ExplainModel;
import Principal.Config;
import Views.ExplainView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JPanel;

public class ExplainController implements Controller, ActionListener {

    private ExplainModel model;
    private ExplainView view;

    public ExplainController() {
        this.model = new ExplainModel();
        this.view = new ExplainView(this, model);
        model.updateExplainTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == view.getBotton_explainplan()) {
            if (view.getConsulta_explainplan().getText().isEmpty()) {
                Emergent.message(Config.es("insert_consult"));
            } else {
                if (model.createExplainPlan(view.getConsulta_explainplan().getText())) {
                    Emergent.message(Config.es("add_explain_success"));
                    model.updateExplainTable();
                } else {
                    Emergent.message(Config.es("add_explain_error"));
                }
            }
        }

    }

    @Override
    public void setPanels(HashMap<String, JPanel> handle) {
        handle.put("Plan de Ejecución::pros-and-cons", view);
    }

    @Override
    public String getLabelName() {

        return "Plan de Ejecución";
    }

}
