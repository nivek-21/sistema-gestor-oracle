package Controllers;

import Helpers.Emergent;
import Models.AuditModel;
import Views.AddAuditTableView;
import Views.AuditView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 * Clase encargada del gestionamiento de las Auditorías.
 */
public class AuditController implements Controller, ActionListener {

    private AuditView view;
    private AuditModel model;
    private AddAuditTableView add;
    private Emergent em;

    public AuditController() {
        this.model = new AuditModel();
        this.view = new AuditView(this, model);
        this.add = new AddAuditTableView(this, model);
        this.em = new Emergent();

        this.model.loadAudit();
    }

    /**
     * Método que carga el combo box de los esquemas.
     */
    public void loadSchemas() {
        ArrayList<String> schemas = this.model.getSchemas();
        this.add.getSchemaCombo().removeAllItems();
        for (int x = 0; x < schemas.size(); x++) {
            this.add.getSchemaCombo().addItem(schemas.get(x));
        }
        this.loadTables(this.add.getSchemaCombo().getSelectedItem().toString());
    }

    /**
     * Método que carga el combo box de las tablas.
     *
     * @param schema
     */
    public void loadTables(String schema) {
        ArrayList<String> tables = this.model.getTables(schema);
        for (int x = 0; x < tables.size(); x++) {
            this.add.getTableCombo().addItem(tables.get(x));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        /**
         * Procedimiento cuando agregamos un nuevo audit.
         */
        if (this.view.getAddBtn() == e.getSource()) {
            this.loadSchemas();
            this.add.getTableCombo().removeAllItems();
            this.loadTables(this.add.getSchemaCombo().getSelectedItem().toString());
            em.open(add, 500, 280);
        }

        /**
         * Procedimiento para saber que esquema seleccionó del combo box de
         * esquemas.
         */
        if (this.add.getSchemaCombo() == e.getSource()) {
            this.add.getTableCombo().removeAllItems();
            if (this.add.getSchemaCombo().getSelectedItem() != null) {
                this.loadTables(this.add.getSchemaCombo().getSelectedItem().toString());
            }
        }

        /**
         * Procedimiento para el botón de regresar.
         */
        if (this.add.getRegresarBtn() == e.getSource()) {
            this.em.close();
        }

        /**
         * Procedimiento para el botón de refrescar.
         */
        if (this.view.getRefresh() == e.getSource()) {
            this.model.loadAudit();
        }

        /**
         * Procedimiento para el botón de agregar auditoría del formulario de
         * agregación.
         */
        if (this.add.getAddBtn() == e.getSource()) {

            if (this.add.getTableCombo().getItemCount() == 0) {
                em.message("Debe seleccionar una tabla para auditarla");
            } else {
                if (this.model.createTableAudit(this.add.getSchemaCombo().getSelectedItem().toString(), this.add.getTableCombo().getSelectedItem().toString())) {
                    em.message("Auditoría para la tabla " + this.add.getTableCombo().getSelectedItem().toString() + " creada");
                    em.close();
                } else {
                    em.message("Error al crear la auditoría");
                }
            }
        }

    }

    @Override
    public void setPanels(HashMap<String, JPanel> handle) {
        handle.put("Auditoría::parameters", view);
    }

    @Override
    public String getLabelName() {
        return "Auditoría";
    }

}
