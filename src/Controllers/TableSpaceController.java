/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Helpers.Emergent;
import Models.TableSpaceModel;
import Views.AddTableSpaceView;
import Views.ResizeTableSpaceView;
import Views.TableSpaceView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Jefferson
 */
public class TableSpaceController implements Controller, ActionListener, MouseListener {

    private TableSpaceModel model;
    private TableSpaceView view;
    private Emergent em;
    private AddTableSpaceView add;
    private JFileChooser choose;
    private String pathSelected;
    private ResizeTableSpaceView resize;
    private String nameToDelete;

    public TableSpaceController() {

        this.model = new TableSpaceModel();
        this.view = new TableSpaceView(this, model);
        this.model.loadTableSpaces();
        this.em = new Emergent();
        this.add = new AddTableSpaceView(this, model);
        this.choose = new JFileChooser();
        this.resize = new ResizeTableSpaceView(this, model);
        this.pathSelected = "";
        this.nameToDelete = "";

    }

    public String choosePath() {

        choose.setDialogTitle("Seleccione la ruta donde guardar los TableSpace");
        choose.setApproveButtonText("Seleccionar");
        choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = choose.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return choose.getSelectedFile().getAbsolutePath();
        }

        return "Nada";

    }

    public void addTableSpace() {

        String path = this.choosePath();

        if (path.equals("Nada")) {

            int res = JOptionPane.showConfirmDialog(choose, "No seleccionó ninguna ruta. ¿Desea intentarlo nuevamente o cancelar la operación?");

            if (res == JOptionPane.YES_OPTION) {
                this.addTableSpace();
            }

        } else {

            if (this.model.createTableSpace(this.add.getNameField().getText(), this.add.getSizeField().getText(), this.add.getSizeCombo().getSelectedItem().toString(), path)) {
                this.model.loadTableSpaces();
                this.em.message("TableSpace Creado con éxito");
            } else {
                this.em.message("Error al crear TableSpace, verifique que el nombre no exista y que la ruta de guardado sea válida");
            }
        }

    }

    public void deleteTableSpace() {

        int res = JOptionPane.showConfirmDialog(choose, "¿Desea eliminar el TableSpace " + this.nameToDelete + "?");

        if (res == JOptionPane.YES_OPTION) {
            if (this.model.deleteTS(this.nameToDelete)) {
                this.em.message("TableSpace y Temp Eliminados con éxito");
            } else {
                this.em.message("Error al eliminar el TableSpace o su Temp");
            }
        }

    }

    @Override
    public void setPanels(HashMap<String, JPanel> handle) {
        handle.put("TableSpace::tablespaces", view);
    }

    @Override
    public String getLabelName() {
        return "TableSpace";
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (view.getRefresh() == e.getSource()) {
            this.model.loadTableSpaces();
        }

        if (view.getAgregarBtn() == e.getSource()) {
            this.em.close();
            this.em.open(add, 580, 280);
        }
        if (add.getRegresarBtn() == e.getSource()) {
            this.em.close();
        }
        if (add.getAddBtn() == e.getSource()) {
            this.em.close();
            this.addTableSpace();
        }
        if (view.getResizeBtn() == e.getSource()) {
            if(!"".equals(this.pathSelected)){
                this.em.close();
                this.em.open(resize, 375, 280);
            }
            else{
                this.em.message("Debe seleccionar un TableSpace de la tabla para poder modificarlo");
            }
        }
        if (this.resize.getRegresarBtn() == e.getSource()) {
            this.em.close();
        }
        if (this.resize.getResizeBtn() == e.getSource()) {
            this.em.close();

            if (this.model.resizeTS(pathSelected, this.resize.getSizeField().getText(), this.resize.getSizeCombo().getSelectedItem().toString())) {
                this.em.message("TableSpace modificado con éxito");
                this.model.loadTableSpaces();
                this.pathSelected = "";
                this.nameToDelete = "";
            } else {
                this.em.message("Error al modificar el TableSpace, verifique que el nuevo tamaño supere el espacio requerido por el mismo");
            }
        }
        if (this.view.getDeleteBtn() == e.getSource()) {
            if(!"".equals(this.nameToDelete)){
                this.deleteTableSpace();
                this.nameToDelete = "";
                this.pathSelected = "";
            }
            else{
                this.em.message("Debe seleccionar un TableSpace de la tabla para poder eliminarlo");
            }
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (this.view.getTableSpaceTable() == e.getSource()) {
            int row = view.getTableSpaceTable().getSelectedRow();
            pathSelected = view.getTableSpaceTable().getValueAt(row, 2).toString();
            this.nameToDelete = view.getTableSpaceTable().getValueAt(row, 1).toString();
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
