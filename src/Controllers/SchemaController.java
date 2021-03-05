/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Helpers.Emergent;
import Models.SchemaModel;
import Principal.Config;
import Views.AddSchemaView;
import Views.SchemaView;
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
public class SchemaController implements Controller, ActionListener, MouseListener {

    private SchemaView view;
    private AddSchemaView add;
    private SchemaModel model;

    private Emergent em = new Emergent();

    public SchemaController() {
        this.model = new SchemaModel();
        this.view = new SchemaView(this, model);
        this.add = new AddSchemaView(this, model);
        this.model.modelByScheme();
    }

    public void resetTBS() {
        model.setDefaultTBS("");
        model.setTempTBS("");
    }

    public void resetAdd() {
        em.close();
        add.getSchemaName().setText("");
        add.getSchemaPassword().setText("");
        add.fillDefaultJcbx();
        add.fillTempJcbx();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (view.getRefresh() == e.getSource()) {
            this.model.modelByScheme();
        }

        if (this.view.getAddBtn() == e.getSource()) {
            this.add.fillDefaultJcbx();
            this.add.fillTempJcbx();
            em.open(add, 500, 350);
        }

        if (this.add.getConfirmAddBtn() == e.getSource()) {
            this.model.setSchemaName(add.getSchemaName().getText());
            this.model.setSchemaPassword(add.getSchemaPassword().getText());
            this.model.setDefaultTBS(add.getJcbxDefaultTBS().getSelectedItem().toString());
            this.model.setTempTBS((add.getJcbxTempTBS().getSelectedItem().toString()));
            if (this.model.createSchema()) {
                Emergent.message(Config.es("add_schema_success"));
            } else {
                Emergent.message(Config.es("add_schema_error"));
            }
            this.resetTBS();
            this.resetAdd();
            this.model.modelByScheme();
        }

        if (this.add.getCancelAddBtn() == e.getSource()) {
            this.resetAdd();
        }

        if (this.view.getDeleteBtn() == e.getSource()) {
            if (this.model.getSelectedSchema().isEmpty()) {
                Emergent.message(Config.es("error_row"));
            } else {
                if (this.model.dropSchema()) {
                    Emergent.message(Config.es("del_schema_success"));
                } else {
                    Emergent.message(Config.es("del_schema_error"));
                }
                this.model.modelByScheme();
                this.model.setSelectedSchema("");
            }
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == view.getSchemasTable()) {
            int row = view.getSchemasTable().getSelectedRow();
            this.model.setSelectedSchema(view.getSchemasTable().getValueAt(row, 0).toString());
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
        handle.put("Schemas::schema", view);
    }

    @Override
    public String getLabelName() {
        return "Schemas";
    }

}
