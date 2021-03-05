/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import Database.QueryBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jefferson
 */
public class UsersModel extends Observable {

    private QueryBuilder builder;
    private DefaultTableModel users;

    public UsersModel() {
        this.builder = new QueryBuilder();
        this.users = new DefaultTableModel();
    }

    public DefaultTableModel getUsers() {
        return users;
    }

    public void setUsers(DefaultTableModel users) {
        this.users = users;
    }


    public void loadUsers() {

        this.users = new DefaultTableModel();
        this.users.addColumn("ID");
        this.users.addColumn("Name");
        this.users.addColumn("Created");

        ResultSet result = this.builder.selectAsDBA("SELECT * FROM DBA_USERS WHERE ACCOUNT_STATUS = 'OPEN' AND USERNAME NOT IN ('SYS', 'SYSTEM')");

        try {

            Object[] row = new Object[3];

            while (result.next()) {
                row[0] = result.getString(2);
                row[1] = result.getString(1);
                row[2] = result.getString(3);

                this.users.addRow(row);
            }

        } catch (SQLException ex) {
            System.out.println("Error - LoadUsers: " + ex.getMessage());
        }

        setChanged();
        notifyObservers();

    }

    public boolean createUser(String name, String password) {

        String session = "ALTER SESSION SET \"_ORACLE_SCRIPT\" = TRUE";

        this.builder.executeAsDBA(session);

        String query = "CREATE USER " + name + " IDENTIFIED BY " + password;

        return this.builder.executeAsDBA(query);
    }

    public boolean deleteUser(String name) {

        String session = "ALTER SESSION SET \"_ORACLE_SCRIPT\" = TRUE";

        this.builder.executeAsDBA(session);

        String query = "DROP USER " + name + " CASCADE";

        return this.builder.executeAsDBA(query);

    }

}
