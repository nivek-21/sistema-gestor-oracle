/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Helpers.Emergent;
import Models.UsersModel;
import Views.AddUserView;
import Views.UsersView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Jefferson
 */
public class UsersController implements Controller, ActionListener, MouseListener {

    private UsersView view;
    private UsersModel model;
    private Emergent em;
    private AddUserView add;
    private String selectedNameUser;

    public UsersController() {
        this.em = new Emergent();
        this.model = new UsersModel();
        this.view = new UsersView(this, model);
        this.add = new AddUserView(this, model);
        this.model.loadUsers();
        this.selectedNameUser = "";
    }

    public void addUser() {

        String name = this.add.getNameField().getText();
        String password = this.add.getPasswordField().getText();

        if (name.equals("") || password.equals("")) {
            em.message("Los campos Nombre y Contraseña no pueden estar vacíos");
        } else {
            if (this.model.createUser(name, password)) {
                em.message("Usuario creado con éxito");
                this.model.loadUsers();
                em.close();
                this.add.cleanFields();
            } else {
                em.message("Error al crear el usuario, verifique que el nombre ingresado no exista");
            }
        }
    }

    public void delUser() {

        if (this.selectedNameUser.equals("")) {
            em.message("Debe seleccionar un usuario en la tabla para poder eliminarlo");
        } else {
            int res = JOptionPane.showConfirmDialog(null, "¿Desea eliminar al usuario " + this.selectedNameUser + "?");

            if (res == JOptionPane.YES_OPTION) {
                if (this.model.deleteUser(this.selectedNameUser)) {
                    em.message("Usuario " + this.selectedNameUser + " borrado con éxito");
                    this.selectedNameUser = "";
                    this.model.loadUsers();
                } else {
                    em.message("Error al eliminar el usuario");
                }
            }
        }

    }

    @Override
    public void setPanels(HashMap<String, JPanel> handle) {
        handle.put("Users::users", view);
    }

    @Override
    public String getLabelName() {
        return "Users";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.view.getRefresh() == e.getSource()) {
            this.model.loadUsers();
        }
        if (this.view.getAgregarBtn() == e.getSource()) {
            em.open(add, 490, 280);
        }
        if (this.add.getRegresarBtn() == e.getSource()) {
            em.close();
            this.add.cleanFields();
        }
        if (this.add.getAddBtn() == e.getSource()) {
            this.addUser();
            this.selectedNameUser = "";
        }
        if (this.view.getDeleteBtn() == e.getSource()) {
            this.delUser();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (this.view.getTableUsers() == e.getSource()) {
            int row = view.getTableUsers().getSelectedRow();
            this.selectedNameUser = view.getTableUsers().getValueAt(row, 1).toString();
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
