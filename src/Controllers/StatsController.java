/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Helpers.Emergent;
import Models.StatsModel;
import Principal.Config;
import Views.AddTableStatsView;
import Views.ConsultStatsView;
import Views.StatsView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 *
 * @author Pc
 */
public class StatsController implements Controller, ActionListener, MouseListener {

    private StatsModel model;
    private StatsView view;
    private AddTableStatsView add;
    private ConsultStatsView consult;
    private Emergent em = new Emergent();

    public StatsController() {
        this.model = new StatsModel();
        this.view = new StatsView(this, model);
        this.add = new AddTableStatsView(this, model);
        this.consult = new ConsultStatsView(this, model);
        this.model.modelByScheme();
        this.model.modelByStats();
    }

    public void reset() {
        this.model.setSelectedSchema("");
        this.model.setSelectedTable("");
        this.model.setExistStats(true);
        this.view.getTableStats().clearSelection();
    }

    public void admGenerateSuccessMsg() {
        if (this.model.isExistStats()) {
            Emergent.message(Config.es("upd_stats_success"));
            em.close();
        } else {
            Emergent.message(Config.es("add_stats_success"));
            em.close();
        }
    }

    public void admGenerateErrorMsg() {
        if (this.model.isExistStats()) {
            Emergent.message(Config.es("upd_stats_error"));
            em.close();
        } else {
            Emergent.message(Config.es("add_stats_error"));
            em.close();
        }
    }

    public void admDelErrorMsg() {
        if (!this.model.isExistStats()) {
            Emergent.message(Config.es("non_stats"));
        } else {
            em.close();
            Emergent.message(Config.es("del_stats_error"));
            reset();
        }
    }

    public void callAction(String action) {
        if (action.equals("Generar")) {
            if (this.model.generateTableStats()) {
                this.admGenerateSuccessMsg();
            } else {
                this.admGenerateErrorMsg();
            }
            reset();
        }

        if (action.equals("Eliminar")) {
            if (this.model.deleteTableStats()) {
                em.close();
                Emergent.message(Config.es("del_stats_success"));
            } else {
                this.admDelErrorMsg();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //Eventos StatsView
        if (this.view.getConsultStatsBtn() == e.getSource()) {
            if (this.model.getSelectedSchema().isEmpty()) {
                Emergent.message(Config.es("error_row"));
                reset();
            } else {
                this.model.modelByStats();
                this.consult.getConsultStatsTable().setModel(this.model.getConsultStats());
                this.em.open(consult, 500, 350);

            }
        }

        if (this.view.getTableStatsBtn() == e.getSource()) {
            if (this.model.getSelectedSchema().isEmpty()) {
                Emergent.message(Config.es("error_row"));
                reset();
            } else {
                this.em.open(add, 500, 280);
                this.add.fillTablesJcbx();
            }
        }

        if (this.view.getSchemaStatsBtn() == e.getSource()) {
            if (this.model.generateSchemaStats()) {
                Emergent.message(Config.es("add_stats_success"));
            } else {
                Emergent.message(Config.es("add_stats_error"));
            }

            reset();
        }

        //Eventos de la ventana AddTableStatsView
        if (this.add.getCancelAddBtn() == e.getSource()) {
            this.em.close();
            reset();

        }

        if (this.add.getConfirmBtn() == e.getSource()) {
            String table = this.add.getJcbxTables().getSelectedItem().toString();
            String action = this.add.getJcbxActions().getSelectedItem().toString();
            this.model.setSelectedTable(table);
            this.callAction(action);

        }

        //Eventos de ConsultStatsView
        if (this.consult.getCloseConsultBtn() == e.getSource()) {
            em.close();
            reset();
        }

        if (this.view.getRefresh() == e.getSource()) {
            this.model.modelByScheme();
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == view.getTableStats()) {
            int row = view.getTableStats().getSelectedRow();
            this.model.setSelectedSchema(view.getTableStats().getValueAt(row, 0).toString());
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void setPanels(HashMap<String, JPanel> handle) {
        handle.put("Stats::statistics", view);
    }

    @Override
    public String getLabelName() {
        return "Stats";
    }

}
