package Controllers;

import Helpers.Emergent;
import Models.IndexModel;
import Principal.Config;
import Views.AddIndexView;
import Views.IndexView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 *
 * @author HOME
 */
public class IndexController implements Controller, ActionListener, ItemListener, MouseListener {

    private IndexView view;
    private AddIndexView add;
    private IndexModel model;

    private String indexSelected;
    private String ownerSelected;

    private Emergent em = new Emergent();

    public IndexController() {
        this.model = new IndexModel();
        this.view = new IndexView(this, model);
        this.add = new AddIndexView(this);

        this.model.updateIndexes();

        indexSelected = "";
        ownerSelected = "";
    }

    public void resetAdd() {
        em.close();
        add.getIndexName().setText("");
        refresh();
    }

    public void refresh() {
        model.updateIndexes();
        view.getIndexes().clearSelection();
        indexSelected = "";
        ownerSelected = "";
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == add.getSchemas()) {
            if (add.getSchemas().getSelectedItem() != null) {
                String schemaSelected = add.getSchemas().getSelectedItem().toString();
                add.setTables(model.getTablesBySchema(schemaSelected));
            }
        }

        if (e.getSource() == add.getTables()) {
            String schemaSelected = add.getSchemas().getSelectedItem().toString();

            if (add.getTables().getSelectedItem() != null) {
                String tableSelected = add.getTables().getSelectedItem().toString();
                add.setColumns(model.getColumnsByTable(schemaSelected, tableSelected));
            } else {
                add.setColumns(model.getColumnsByTable(schemaSelected, ""));
            }
        }

        if (e.getSource() == add.getNext()) {
            if (add.getTables().getSelectedItem() != null) {
                if (add.getColumns().getSelectedItem() != null) {
                    if (!add.getIndexName().getText().isEmpty()) {

                        String schemaName = add.getSchemas().getSelectedItem().toString();
                        String tableName = add.getTables().getSelectedItem().toString();
                        String columnName = add.getColumns().getSelectedItem().toString();
                        String indexName = add.getIndexName().getText();
                        String indexType = add.getTypes().getSelectedItem().toString();
                        String password = Emergent.getString("Digite la clave del usuario " + schemaName);

                        if (model.login(schemaName, password)) {
                            if (model.createIndex(schemaName, password, tableName, columnName, indexName, indexType)) {
                                Emergent.message(Config.es("add_index_success"));
                            } else {
                                Emergent.message(Config.es("add_index_error"));
                            }
                            resetAdd();
                        } else {
                            Emergent.message(Config.es("error_password"));
                        }

                    } else {
                        Emergent.message("Se debe agregar un nombre válido para el index.");
                    }
                } else {
                    Emergent.message("Se debe seleccionar una columna válida");
                }
            } else {
                Emergent.message("Se debe seleccionar una tabla válida");
            }
        }

        if (e.getSource() == add.getCancel()) {
            resetAdd();
        }

        if (e.getSource() == view.getAdd()) {
            add.setSchemas(model.getSchemas());
            em.open(add, 550, 360);
        }

        if (e.getSource() == view.getRefresh()) {
            refresh();
        }

        if (e.getSource() == view.getDelete()) {
            if (indexSelected.isEmpty() || ownerSelected.isEmpty()) {
                Emergent.message(Config.es("error_row"));
            } else {

                String password = Emergent.getString(Config.es("insert_password") + ownerSelected);

                if (model.login(ownerSelected, password)) {
                    if (model.deleteIndex(ownerSelected, password, indexSelected)) {
                        Emergent.message(Config.es("del_index_success"));
                    } else {
                        Emergent.message(Config.es("del_index_error"));
                    }
                } else {
                    Emergent.message(Config.es("error_password"));
                }

                refresh();

            }
        }

        if (e.getSource() == view.getRebuild()) {
            if (indexSelected.isEmpty() || ownerSelected.isEmpty()) {
                Emergent.message(Config.es("error_row"));
            } else {

                String password = Emergent.getString(Config.es("insert_password") + ownerSelected);

                if (model.login(ownerSelected, password)) {
                    if (model.rebuildIndex(ownerSelected, password, indexSelected)) {
                        Emergent.message(Config.es("rebuild_index_success"));
                    } else {
                        Emergent.message(Config.es("rebuild_index_error"));
                    }
                } else {
                    Emergent.message(Config.es("error_password"));

                }

                refresh();
            }

        }

    }

    @Override
    public void itemStateChanged(ItemEvent evt) {
    }

    @Override
    public void setPanels(HashMap<String, JPanel> handle) {
        handle.put("Índices::index", view);
    }

    @Override
    public String getLabelName() {
        return "Índices";
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        /**
         * Método que escucha la tabla de indices.
         */
        if (e.getSource() == view.getIndexes()) {
            int row = view.getIndexes().getSelectedRow();
            ownerSelected = view.getIndexes().getValueAt(row, 0).toString();
            indexSelected = view.getIndexes().getValueAt(row, 1).toString();
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

}
