package Models;

import Database.QueryBuilder;
import Helpers.Emergent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Observable;
import javax.swing.table.DefaultTableModel;

public class IndexModel extends Observable {

    private QueryBuilder builder;
    private DefaultTableModel indexes;

    public IndexModel() {
        this.builder = new QueryBuilder();
        this.indexes = new DefaultTableModel();
    }

    public DefaultTableModel getIndexes() {
        return indexes;
    }

    public void updateIndexes() {
        this.indexes = new DefaultTableModel();

        indexes.addColumn("OWNER");
        indexes.addColumn("INDEX_NAME");
        indexes.addColumn("TABLE_OWNER");
        indexes.addColumn("TABLE_NAME");
        indexes.addColumn("INDEX_TYPE");

        loadIndexes();
    }

    private void loadIndexes() {

        ResultSet result = this.builder.selectAsDBA("SELECT OWNER, INDEX_NAME, TABLE_OWNER, TABLE_NAME, INDEX_TYPE FROM ALL_INDEXES JOIN DBA_USERS ON ALL_INDEXES.OWNER = DBA_USERS.USERNAME WHERE DBA_USERS.ACCOUNT_STATUS = 'OPEN' AND OWNER NOT IN ('SYS', 'SYSTEM')");

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
            Emergent.showConsole("Error - LoadDirectories: " + ex.getMessage());
        }

        setChanged();
        notifyObservers();

    }

    public LinkedList<String> getSchemas() {
        LinkedList<String> schemas = new LinkedList<>();

        ResultSet result = this.builder.selectAsDBA("SELECT * FROM DBA_USERS WHERE DBA_USERS.ACCOUNT_STATUS = 'OPEN' AND DBA_USERS.USERNAME NOT IN ('SYS', 'SYSTEM')");

        try {
            while (result.next()) {
                schemas.add(result.getString(1));
            }
        } catch (SQLException ex) {
            Emergent.showConsole("Error - GetSchemas: " + ex.getMessage());
        }

        return schemas;
    }

    public LinkedList<String> getTablesBySchema(String schema) {
        LinkedList<String> tables = new LinkedList<>();

        ResultSet result = this.builder.selectAsDBA("SELECT DISTINCT TABLE_NAME FROM ALL_TABLES WHERE OWNER =  '" + schema + "'");

        try {
            while (result.next()) {
                tables.add(result.getString(1));
            }
        } catch (SQLException ex) {
            Emergent.showConsole("Error - GetTablesBySchema: " + ex.getMessage());
        }

        return tables;
    }

    public LinkedList<String> getColumnsByTable(String schema, String table) {
        LinkedList<String> columns = new LinkedList<>();

        ResultSet result = this.builder.selectAsDBA("SELECT COLUMN_NAME FROM ALL_TAB_COLUMNS WHERE OWNER = '" + schema + "' AND TABLE_NAME = '" + table + "'");

        try {
            while (result.next()) {
                columns.add(result.getString(1));
            }
        } catch (SQLException ex) {
            Emergent.showConsole("Error - GetColumnsByTable: " + ex.getMessage());
        }

        return columns;
    }

    public boolean createIndex(String schemaName, String password, String tableName, String columnName, String indexName, String indexType) {
        boolean response = false;
        switch (indexType) {
            case "Arbol B+":
                response = createIndexNormal(schemaName, password, tableName, columnName, indexName);
                break;

            case "Bitmap":
                response = createIndexBitmap(schemaName, password, tableName, columnName, indexName);
                break;
        }
        Emergent.showConsole(schemaName, password, tableName, columnName, indexName, indexType);
        return response;
    }

    private boolean createIndexNormal(String schemaName, String password, String tableName, String columnName, String indexName) {
        String query = "CREATE INDEX " + indexName + " ON " + schemaName + "." + tableName + " (" + columnName + ")";
        Emergent.showConsole(query);
        return builder.executeAsOther(schemaName, password, query);
    }

    private boolean createIndexBitmap(String schemaName, String password, String tableName, String columnName, String indexName) {
        String query = "CREATE BITMAP INDEX " + indexName + " ON " + schemaName + "." + tableName + " (" + columnName + ")";
        Emergent.showConsole(query);
        return builder.executeAsOther(schemaName, password, query);
    }

    public boolean deleteIndex(String username, String password, String indexName) {
        String query = "DROP INDEX " + indexName;
        Emergent.showConsole(query);
        return builder.executeAsOther(username, password, query);
    }

    public boolean rebuildIndex(String username, String password, String indexName) {
        String query = "ALTER INDEX " + indexName + " REBUILD";
        Emergent.showConsole(query);
        return builder.executeAsOther(username, password, query);
    }

    public boolean login(String username, String password) {
        return builder.connectionAsOther(username, password) != null;
    }

}
