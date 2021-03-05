package Controllers;

import Helpers.Emergent;
import Models.RecoveryModel;
import Principal.Config;
import Views.RecoveryView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class RecoveryController implements Controller, ActionListener, MouseListener {

    private RecoveryView view;
    private RecoveryModel model;

    private Emergent em = new Emergent();

    public RecoveryController() {
        this.model = new RecoveryModel();
        this.view = new RecoveryView(this, model);

        this.model.setSchemas();
    }

    private void resetForm() {
        view.getPath().setText("");
        view.getTableName().setText("");
        view.getSchemaName().setText("");
        view.getPassword().setText("");
        view.getYesTable().setSelected(true);
        view.getYesSchema().setSelected(true);
        view.getMainGroup().clearSelection();
        model.resetDirectories();
        model.setSchemas();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == view.getSchemaTable()) {
            if (view.getSchemaTable().getSelectedItem() != null) {
                String schemaName = view.getSchemaTable().getSelectedItem().toString();
                view.updateTables(model.getTablesBySchema(schemaName));
            }
        }

        if (e.getSource() == view.getSelect()) {
            this.model.setSchemas();

            JFileChooser chooser = new JFileChooser("home");
            chooser.setDialogTitle(Config.es("chooser_title"));
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("*.dump", "dump"));

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                view.getPath().setText(chooser.getSelectedFile().toString());
                model.setDirectories(chooser.getSelectedFile().toString());
            } else {
                Emergent.message(Config.es("dir_path_empty"));
            }
        }

        if (e.getSource() == view.getNext()) {

            if (view.getPath().getText().isEmpty()) {
                Emergent.message("Por favor seleccione un respaldo.");
                return;
            }

            if (view.getMainGroup().getSelection() != null) {
                if (view.getTableRadio().isSelected()) {

                    String scheme = view.getSchemaTable().getSelectedItem().toString();
                    String directory = view.getDirectories().getSelectedItem().toString();
                    String path = view.getPath().getText();

                    if (view.getYesTable().isSelected()) {
                        if (view.getTable().getSelectedItem() == null) {
                            Emergent.message("Deberá agregar el nombre de la tabla.");
                        } else {
                            String table = view.getTable().getSelectedItem().toString();
                            Emergent.message(Config.es("time"));
                            model.recoverBackupByTable(scheme, table, directory, path, true);
                            resetForm();
                        }
                    } else {
                        String value = view.getTableName().getText();
                        String table = value.replaceAll(" ", "");
                        if (table.isEmpty()) {
                            Emergent.message("Tiene que agregar el nombre de la tabla.");
                        } else {
                            Emergent.message(Config.es("time"));
                            model.recoverBackupByTable(scheme, table, directory, path, false);
                            resetForm();
                        }
                    }

                } else if (view.getSchemaRadio().isSelected()) {

                    if (view.getSchemaGroup().getSelection() != null) {

                        String value = view.getPassword().getText();
                        String password = value.replaceAll(" ", "");

                        if (password.isEmpty()) {
                            Emergent.message("Por favor escriba una contraseña");
                            return;
                        }

                        if (view.getYesSchema().isSelected()) {
                            if (view.getSchema().getSelectedItem() == null) {
                                Emergent.message("Deberá escoger un esquema.");
                            } else {
                                Emergent.message(Config.es("time"));

                                String schema = view.getSchema().getSelectedItem().toString();
                                String directory = view.getDirectories().getSelectedItem().toString();
                                String path = view.getPath().getText();

                                model.recoverBackupBySchema(schema, directory, path, password, true);

                                resetForm();
                            }
                        } else if (view.getNoSchema().isSelected()) {
                            String aux = view.getSchemaName().getText();
                            String schemaName = aux.replaceAll(" ", "");
                            if (schemaName.isEmpty()) {
                                Emergent.message("Por favor digite el nombre del esquema");
                            } else {
                                Emergent.message(Config.es("time"));

                                String directory = view.getDirectories().getSelectedItem().toString();
                                String path = view.getPath().getText();
                                model.recoverBackupBySchema(schemaName, directory, path, password, false);

                                resetForm();
                            }
                        }
                    }
                }
            } else {
                Emergent.message("Por favor seleccione un tipo de respaldo.");
            }

        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

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
        handle.put("Recuperar Respaldo::recover_back_up", view);
    }

    @Override
    public String getLabelName() {
        return "Recuperación";
    }

}
