/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import Database.QueryBuilder;
import Helpers.Emergent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author carosales
 */
public class ExplainModel extends Observable {

    private QueryBuilder builder;
    private DefaultTableModel indexes;

    public ExplainModel() {
        this.builder = new QueryBuilder();
        this.indexes = new DefaultTableModel();

    }

    public DefaultTableModel getIndexes() {
        return indexes;
    }

    public boolean createExplainPlan(String text) {
        String delete = "DELETE PLAN_TABLE";
        String aux = "EXPLAIN PLAN FOR " + text;
        String query = aux.replace(';', ' ');
        return builder.executeAsDBA(delete) && builder.executeAsDBA(query);
    }

    public void updateExplainTable() {

        indexes = new DefaultTableModel();
        indexes.addColumn("OPERATION");
        indexes.addColumn("OBJECT_NAME");
        indexes.addColumn("OBJECT_OWNER");
        indexes.addColumn("COST");
        indexes.addColumn("TIMESTAMP");

        ResultSet result = this.builder.selectAsDBA("SELECT SUBSTR (LPAD(' ', LEVEL-1) || OPERATION ||' (' || OPTIONS || ')',1,30 ) \"OPERACION\", OBJECT_NAME \"OBJETO\", OBJECT_OWNER, COST, TIMESTAMP FROM PLAN_TABLE START WITH ID = 0 CONNECT BY PRIOR ID=PARENT_ID");

        try {
            Object[] row = new Object[5];

            while (result.next()) {
                row[0] = result.getString(1);
                row[1] = result.getString(2);
                row[2] = result.getString(3);
                row[3] = result.getString(4);
                row[4] = result.getString(5);
                indexes.addRow(row);
            }
        } catch (SQLException ex) {
            Emergent.showConsole("Error - UpdateExplainTable: " + ex.getMessage());
        }

        setChanged();
        notifyObservers();
    }

}
