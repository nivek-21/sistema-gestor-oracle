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
import java.util.ArrayList;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Pc
 */
public class StatsModel extends Observable{
    private QueryBuilder builder;
    private DefaultTableModel tableStats;
    private DefaultTableModel consultStats;
    private String selectedSchema;
    private String selectedTable;
    private boolean existStats;
    
    public StatsModel(){
        this.existStats = true;
        this.selectedSchema = "";
        this.selectedTable = "";
        this.builder = new QueryBuilder();
    }

    public DefaultTableModel getTableStats() {
        return tableStats;
    }

    public void setTableStats(DefaultTableModel tableStats) {
        this.tableStats = tableStats;
    }

    public DefaultTableModel getConsultStats() {
        return consultStats;
    }

    public void setConsultStats(DefaultTableModel consultStats) {
        this.consultStats = consultStats;
    }
    
    

    public String getSelectedSchema() {
        return selectedSchema;
    }

    public void setSelectedSchema(String selectedSchema) {
        this.selectedSchema = selectedSchema;
    }

    public String getSelectedTable() {
        return selectedTable;
    }

    public void setSelectedTable(String selectedTable) {
        this.selectedTable = selectedTable;
    }

    public boolean isExistStats() {
        return existStats;
    }

    public void setExistStats(boolean existStats) {
        this.existStats = existStats;
    }
    
    
    
    public ArrayList<String> getAllTables() {
        ResultSet result = builder.selectAsDBA("SELECT TABLE_NAME FROM ALL_TABLES WHERE OWNER='"+ this.getSelectedSchema() +"'");
        ArrayList<String> tables = new ArrayList<String>();

        try {
            while (result.next()) {
                tables.add(result.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SchemaModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tables;

    }
    
    
    public void modelByScheme() {

        this.tableStats = new DefaultTableModel();
        this.tableStats.addColumn("USERNAME");
        this.tableStats.addColumn("EXPIRY_DATE");
        this.tableStats.addColumn("DEFAULT_TABLESPACE");
        this.tableStats.addColumn("TEMPORARY_TABLESPACE");
        this.tableStats.addColumn("STATUS");

        loadSchemes();
    }

    private void loadSchemes() {
        ResultSet result = this.builder.selectAsDBA("SELECT USERNAME, EXPIRY_DATE, DEFAULT_TABLESPACE, TEMPORARY_TABLESPACE, ACCOUNT_STATUS FROM DBA_USERS WHERE ACCOUNT_STATUS = 'OPEN' AND USERNAME NOT IN('SYS','SYSTEM')");

        try {

            Object[] row = new Object[5];

            while (result.next()) {
                row[0] = result.getString(1);
                row[1] = result.getString(2);
                row[2] = result.getString(3);
                row[3] = result.getString(4);
                row[4] = result.getString(5);

                this.tableStats.addRow(row);
            }

        } catch (SQLException ex) {
            Emergent.showConsole("Error - LoadSchemes: " + ex.getMessage());
        }

        setChanged();
        notifyObservers();
    }

    public void modelByStats() {
        this.consultStats = new DefaultTableModel();
        this.consultStats.addColumn("TABLE_NAME");
        this.consultStats.addColumn("LAST_ANALYZED");

        loadStats();
    }

    private void loadStats() {
        ResultSet result = this.builder.selectAsDBA("SELECT TABLE_NAME, LAST_ANALYZED from dba_tables WHERE OWNER = '"+ this.getSelectedSchema() +"'");

        try {

            Object[] row = new Object[2];

            while (result.next()) {
                row[0] = result.getString(1);
                row[1] = result.getString(2);

                this.consultStats.addRow(row);
            }

        } catch (SQLException ex) {
            Emergent.showConsole("Error - LoadSchemes: " + ex.getMessage());
        }
    }

    
    //Verifica los stats para una tabla en especifico
    public boolean checkStatsInTable() {
        ResultSet result = this.builder.selectAsDBA("SELECT TABLE_NAME, LAST_ANALYZED from dba_tables WHERE OWNER = '" + this.getSelectedSchema() + "' AND TABLE_NAME = '"+ this.getSelectedTable() +"'");
        try {
            while (result.next()) {
               if(null == result.getString(2)){
                   return false;
               }
            }
        } catch (SQLException ex) {
            Emergent.showConsole("Error - LoadSchemes: " + ex.getMessage());
        }
        return true;
    }

    
    public boolean generateSchemaStats(){
         return this.builder.executeAsDBA("BEGIN DBMS_STATS.GATHER_SCHEMA_STATS('" + this.getSelectedSchema() + "',cascade=>TRUE); END;");
    }
    
    public boolean generateTableStats() {
        if (this.checkStatsInTable()) {
            return this.builder.executeAsDBA("ANALYZE TABLE " + this.getSelectedSchema() + "." + this.getSelectedTable() + " ESTIMATE STATISTICS");

        } else{
            this.existStats = false;
            return this.builder.executeAsDBA("BEGIN DBMS_STATS.GATHER_TABLE_STATS('" + this.getSelectedSchema() + "','" + this.getSelectedTable() + "',cascade=>TRUE); END;");            
        }

    }

    public boolean deleteTableStats() {
        if (this.checkStatsInTable()) {
            String query = "ANALYZE TABLE " + this.getSelectedSchema() + "." + this.getSelectedTable() + " DELETE STATISTICS";
            return this.builder.executeAsDBA(query);
        } else {
            this.existStats = false;
            return false;
        }
    }

    
    
}
