/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import Database.QueryBuilder;
import Helpers.Emergent;
import java.sql.Connection;
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
public class SchemaModel extends Observable {

    private QueryBuilder builder;
    private Connection conn;
    DefaultTableModel tableSchema;
    String selectedSchema;
    String schemaName;
    String schemaPassword;
    String defaultTBS;
    String tempTBS;

    public SchemaModel() {
        this.selectedSchema = "";
        this.schemaName = "";
        this.schemaPassword = "";
        builder = new QueryBuilder();
        conn = this.builder.connectionAsDBA();
    }
    
    public ArrayList<String> getSchemas(){
        ResultSet result = builder.selectAsDBA("select username as schema_name from sys.dba_users order by username");
        ArrayList<String> schemas  = new ArrayList<>();
        
        
        try {
            while (result.next()) {
                schemas.add(result.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SchemaModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return schemas;
    }
    
        public ArrayList<String> getAllDefaultTBS(){
        ResultSet result = builder.selectAsDBA("SELECT tablespace_name FROM dba_tablespaces WHERE contents = 'PERMANENT' AND TABLESPACE_NAME NOT IN ('SYSAUX', 'SYSTEM')");
        ArrayList<String> defaultTBS = new ArrayList<String>();

        try {
            while (result.next()) {
                defaultTBS.add(result.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SchemaModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return defaultTBS;
        
        

    }
        
    
    public ArrayList<String> getAllTempTBS() {
        ResultSet result = builder.selectAsDBA("SELECT tablespace_name FROM dba_tablespaces WHERE contents = 'TEMPORARY' AND TABLESPACE_NAME NOT IN ('TEMP')");
        ArrayList<String> tempTBS = new ArrayList<String>();

        try {
            while (result.next()) {
                tempTBS.add(result.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SchemaModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tempTBS;
    }

    public String getDefaultTBS() {
        return defaultTBS;
    }

    public void setDefaultTBS(String defaultTBS) {
        this.defaultTBS = defaultTBS;
    }

    public String getTempTBS() {
        return tempTBS;
    }

    public void setTempTBS(String tempTBS) {
        this.tempTBS = tempTBS;
    }
    
    
    public String getSelectedSchema() {
        return selectedSchema;
    }

    public void setSelectedSchema(String selectedSchema) {
        this.selectedSchema = selectedSchema;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSchemaPassword() {
        return schemaPassword;
    }

    public void setSchemaPassword(String schemaPassword) {
        this.schemaPassword = schemaPassword;
    }

    public DefaultTableModel getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(DefaultTableModel tableSchema) {
        this.tableSchema = tableSchema;
    }
    
    
    
     
    
    public void modelByScheme() {
        this.tableSchema = new DefaultTableModel();
        this.tableSchema.addColumn("USERNAME");
        this.tableSchema.addColumn("EXPIRY_DATE");
        this.tableSchema.addColumn("DEFAULT_TABLESPACE");
        this.tableSchema.addColumn("TEMPORARY_TABLESPACE");
        this.tableSchema.addColumn("LAST_LOGIN");

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

                this.tableSchema.addRow(row);
            }

        } catch (SQLException ex) {
            Emergent.showConsole("Error - LoadSchemes: " + ex.getMessage());
        }

        setChanged();
        notifyObservers();
    }
    

    public boolean createSchema() {
        System.out.println(this.getSchemaName() + " " + this.getSchemaPassword() + " " +
                this.getDefaultTBS() + " " + this.getTempTBS());
        if (this.getSchemaName().isEmpty() || this.getSchemaPassword().isEmpty()) {
            return false;
        } else {
            this.builder.executeAsDBA("alter session set \"_ORACLE_SCRIPT\" = true");
            this.builder.executeAsDBA("CREATE USER " + this.getSchemaName() + " IDENTIFIED BY " + this.getSchemaPassword() + " DEFAULT TABLESPACE " + this.getDefaultTBS() + " TEMPORARY TABLESPACE " + this.getTempTBS());
            this.builder.executeAsDBA("GRANT RESOURCE TO " + this.getSchemaName());
            this.builder.executeAsDBA("GRANT CONNECT TO " + this.getSchemaName());
            this.builder.executeAsDBA("GRANT ALL PRIVILEGES TO " + this.getSchemaName());
            return this.builder.executeAsDBA("GRANT RESOURCE TO " + this.getSchemaName());
        }
    }

    public boolean dropSchema() {
        System.out.println(this.getSelectedSchema());
       this.builder.executeAsDBA("alter session set \"_ORACLE_SCRIPT\" =true");
       return this.builder.executeAsDBA("DROP USER "+ this.getSelectedSchema() +" CASCADE");
        

    }

}
