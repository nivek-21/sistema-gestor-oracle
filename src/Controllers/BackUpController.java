package Controllers;

import Helpers.Emergent;
import Models.BackUpModel;
import Principal.Config;
import Views.BackUpDirectoriesView;
import Views.BackUpView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 * Clase encargada de gestionar todo lo referente a la creación de respaldos.
 */
public class BackUpController implements Controller, ActionListener, MouseListener {

    private BackUpView view;
    private BackUpDirectoriesView directories;
    private BackUpModel model;

    private String tableSelected;
    private String schemeSelected;
    private String directorySelected;
    private String userSelected;
    private String handle;

    private Emergent em = new Emergent();

    public BackUpController() {

        this.model = new BackUpModel();
        this.view = new BackUpView(this, model);
        this.directories = new BackUpDirectoriesView(this, model);

        this.tableSelected = "";
        this.schemeSelected = "";
        this.directorySelected = "";
        this.userSelected = "";
        this.handle = "";

        this.model.modelByScheme();
        this.model.modelByTable();
    }

    /**
     * Método para borrar la selección de la sección de tablas.
     */
    private void resetTable() {
        resetDirectories();
        userSelected = "";
        tableSelected = "";
        view.getTables().clearSelection();
    }

    /**
     * Método para eliminar la selección de la sección de directorios.
     */
    private void resetDirectories() {
        directorySelected = "";
        directories.getDirectories().clearSelection();
        handle = "";
    }

    /**
     * Método para eliminar la selección de la sección de esquemas.
     */
    private void resetSchemas() {
        resetDirectories();
        schemeSelected = "";
        this.view.getSchemes().clearSelection();
    }

    /**
     * Método para mostrar la pantalla emergente de directorios.
     */
    private void showDirectories() {
        directories.getDirectories().setModel(model.getAllDirectories());
        em.open(directories, 700, 500);
    }

    /**
     * Método para iniciar el proceso de respaldo.
     */
    private void startBackUpProcess() {
        em.close();
        em.message(Config.es("time"));
    }

    /**
     * Método que retorna true si el string insertado esta vacio.
     *
     * @param value
     * @return
     */
    private boolean check(String value) {
        if (value.isEmpty()) {
            em.message(Config.es("error_row"));
            return false;
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        /**
         * Método que escucha el botón de respaldar una tabla.
         */
        if (e.getSource() == view.getTableBackUp()) {
            if (check(tableSelected)) {
                showDirectories();
            }
        }

        /**
         * Método que escucha el botón de respaldar un esquema.
         */
        if (e.getSource() == view.getSchemeBackUp()) {
            if (check(schemeSelected)) {
                showDirectories();
            }
        }

        /**
         * Método que esucha el botón de respaldar full.
         */
        if (e.getSource() == this.view.getFullBackUp()) {
            showDirectories();
            handle = "full";
        }

        /**
         * Método que escucha el botón de refrescar directorios.
         */
        if (e.getSource() == directories.getRefresh()) {
            directories.getDirectories().setModel(model.getAllDirectories());
        }

        /**
         * Método que escucha el botón de refrescar tablas.
         */
        if (e.getSource() == view.getRefresh()) {
            model.modelByScheme();
            model.modelByTable();
            resetTable();
            resetSchemas();
            resetDirectories();
        }

        /**
         * Método que esucha el botón de cancelar de la vista de directorios.
         */
        if (e.getSource() == directories.getCancel()) {
            if ("tables".equals(handle)) {
                em.close();
                resetTable();
            } else if ("schemas".equals(handle)) {
                em.close();
                resetSchemas();
            } else if ("full".equals(handle)) {
                em.close();
                resetDirectories();
            } else {
                em.message(Config.es("system_error"));
            }
        }

        /**
         * Método que escucha el botón de continuar de la vista de directorios.
         */
        if (e.getSource() == directories.getNext()) {

            if ("tables".equals(handle)) {

                if (check(directorySelected)) {

                    String backUpName = Emergent.getString(Config.es("back_up_name"));

                    if ("empty".equals(backUpName)) {
                        Emergent.message(Config.es("back_up_name_error"));
                        resetTable();
                        em.close();
                        return;
                    }

                    startBackUpProcess();
                    model.createBackUpByTable(userSelected, tableSelected, directorySelected, backUpName);
                    resetTable();
                }

            } else if ("schemas".equals(handle)) {

                if (check(directorySelected)) {

                    String backUpName = Emergent.getString(Config.es("back_up_name"));

                    if ("empty".equals(backUpName)) {
                        Emergent.message(Config.es("back_up_name_error"));
                        resetSchemas();
                        em.close();
                        return;
                    }

                    startBackUpProcess();
                    model.createBackUpByScheme(schemeSelected, directorySelected, backUpName);
                    resetSchemas();
                }

            } else if ("full".equals(handle)) {

                if (check(directorySelected)) {

                    String backUpName = Emergent.getString(Config.es("back_up_name"));

                    if ("empty".equals(backUpName)) {
                        Emergent.message(Config.es("back_up_name_error"));
                        resetDirectories();
                        em.close();
                        return;
                    }

                    startBackUpProcess();
                    model.createBackUpFull(directorySelected, backUpName);
                    resetDirectories();
                }

            } else {
                em.message(Config.es("system_error"));
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        /**
         * Método que escucha la tabla de esquemas.
         */
        if (e.getSource() == view.getSchemes()) {
            int row = view.getSchemes().getSelectedRow();
            schemeSelected = view.getSchemes().getValueAt(row, 0).toString();
            handle = "schemas";
        }

        /**
         * Método que escucha la tabla de directorios.
         */
        if (e.getSource() == directories.getDirectories()) {
            int row = directories.getDirectories().getSelectedRow();
            directorySelected = directories.getDirectories().getValueAt(row, 1).toString();
        }

        /**
         * Método que escucha la tabla de tablas.
         */
        if (e.getSource() == view.getTables()) {
            int row = view.getTables().getSelectedRow();
            userSelected = view.getTables().getValueAt(row, 0).toString();
            tableSelected = view.getTables().getValueAt(row, 1).toString();
            handle = "tables";
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
        handle.put("Crear Respaldo::create_back_up", view);
    }

    @Override
    public String getLabelName() {
        return "Respaldos";
    }

}
