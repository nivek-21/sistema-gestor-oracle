package Controllers;

import Helpers.Emergent;
import Helpers.QueryLog;
import Models.DirectoryModel;
import Principal.Config;
import Views.AddDirectoryView;
import Views.DirectoryView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class DirectoryController implements Controller, ActionListener, MouseListener {

    private DirectoryView view;
    private AddDirectoryView add;
    private DirectoryModel model;
    private String directoryName;

    private Emergent em = new Emergent();

    public DirectoryController() {
        this.model = new DirectoryModel();
        this.view = new DirectoryView(this, model);
        this.add = new AddDirectoryView(this, model);

        this.directoryName = "";

        this.model.modelDirectories();
    }

    /**
     * Método que resetea el formulario interno de delete.
     */
    private void resetDelete() {
        directoryName = "";
        view.getDirectories().clearSelection();
        model.modelDirectories();
    }

    /**
     * Método que resetea el formulario de add.
     */
    private void resetAdd() {
        add.getPath().setText("");
        add.getNameInput().setText("");
        em.close();
        model.modelDirectories();
    }

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
         * Método que escha el botón de agregar.
         */
        if (e.getSource() == view.getAdd()) {
            em.open(add, 500, 350);
        }

        /**
         * Método que escucha el botón de eliminar.
         */
        if (e.getSource() == view.getDelete()) {
            if (check(directoryName)) {
                if (model.deleteDirectory(directoryName)) {
                    Emergent.message(Config.es("del_dir_success"));
                } else {
                    Emergent.message(Config.es("del_dir_error"));
                }
                resetDelete();
            }
        }

        /**
         * Método que escucha el botón de refrescar.
         */
        if (e.getSource() == view.getRefresh()) {
            model.modelDirectories();
            resetDelete();
            resetAdd();
        }

        /**
         * Método que esucha el botón de continuar.
         */
        if (e.getSource() == add.getNext()) {
            if (add.getNameInput().getText().isEmpty()) {
                Emergent.message(Config.es("dir_name_empty"));
            } else if (add.getPath().getText().isEmpty()) {
                Emergent.message(Config.es("dir_path_empty"));
            } else {
                if (model.addDirectory(add.getPath().getText(), add.getNameInput().getText())) {
                    Emergent.message(Config.es("add_dir_success"));
                } else {
                    Emergent.message(Config.es("add_dir_error"));
                }
                resetAdd();
            }
        }

        /**
         * Método que escucha el botón de seleccionar directorio.
         */
        if (e.getSource() == add.getSelect()) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle(Config.es("chooser_title"));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                add.getPath().setText(chooser.getSelectedFile().toString());
            } else {
                Emergent.message(Config.es("dir_path_empty"));
            }
        }

        /**
         * Método que escucha el botón de cancelar.
         */
        if (e.getSource() == add.getCancel()) {
            resetAdd();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        /**
         * Método que escucha la tabla de directorios.
         */
        if (e.getSource() == view.getDirectories()) {
            int row = view.getDirectories().getSelectedRow();
            directoryName = view.getDirectories().getValueAt(row, 1).toString();
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
        handle.put("Directorios::directories", view);
    }

    @Override
    public String getLabelName() {
        return "Directorios";
    }

}
