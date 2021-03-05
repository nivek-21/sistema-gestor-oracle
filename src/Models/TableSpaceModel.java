/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import Database.QueryBuilder;
import static java.lang.Integer.parseInt;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jefferson
 */
public class TableSpaceModel extends Observable {

    private QueryBuilder builder;
    private DefaultTableModel tableSpaces;

    public TableSpaceModel() {
        this.builder = new QueryBuilder();
        this.tableSpaces = new DefaultTableModel();
        this.tableSpaces.addColumn("TS#");
        this.tableSpaces.addColumn("Name");
        this.tableSpaces.addColumn("Path");
    }

    public DefaultTableModel getTableSpaces() {
        return tableSpaces;
    }

    public void setTableSpaces(DefaultTableModel tableSpaces) {
        this.tableSpaces = tableSpaces;
    }

    public void loadTableSpaces() {

        this.tableSpaces = new DefaultTableModel();
        this.tableSpaces.addColumn("TS#");
        this.tableSpaces.addColumn("Name");
        this.tableSpaces.addColumn("Path");

        ResultSet result = this.builder.selectAsDBA("select * from dba_data_files");

        try {

            Object[] row = new Object[5];

            while (result.next()) {
                row[0] = result.getString(2);
                row[1] = result.getString(3);
                row[2] = result.getString(1);

                this.tableSpaces.addRow(row);
            }

        } catch (SQLException ex) {
            System.out.println("Error - LoadTableSpaces: " + ex.getMessage());
        }

        setChanged();
        notifyObservers();

    }

    public boolean createTableSpace(String name, String size, String typeSize, String path) {

        String query = "CREATE TABLESPACE " + name + " DATAFILE '" + path + "\\" + name + ".dbf' " + "SIZE " + size + typeSize + " ONLINE";

        boolean aux = this.builder.executeAsDBA(query);

        int res = parseInt(size) / 2;

        String query2 = "CREATE TEMPORARY TABLESPACE " + name + "Temp TEMPFILE '" + path + "\\" + name + "Temp.dbf' " + "SIZE " + res + typeSize;

        this.builder.executeAsDBA(query2);

        return aux; 

    }

    public boolean resizeTS(String path, String size, String typeSize) {

        String query = "ALTER DATABASE DATAFILE '" + path + "' RESIZE " + size + typeSize;

        return this.builder.executeAsDBA(query);

    }

    public boolean deleteTS(String name) {
        
        String putOffline = "ALTER TABLESPACE "+name+" OFFLINE";
        
        this.builder.executeAsDBA(putOffline);
        
        String query = "DROP TABLESPACE " + name + " INCLUDING CONTENTS AND DATAFILES";
        
        String queryTemp = "DROP TABLESPACE " + name + "TEMP INCLUDING CONTENTS AND DATAFILES";

        boolean aux = this.builder.executeAsDBA(query);
        boolean aux2 = this.builder.executeAsDBA(queryTemp);

        if(!aux || !aux2){
            return false;
        }
        
        return true;

    }

}
