package Models;

import Database.QueryBuilder;
import Helpers.Cmd;
import Helpers.Emergent;
import Helpers.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;

public class RecoveryModel extends Observable {

    private QueryBuilder builder;
    private LinkedList<String> schemas;
    private LinkedList<String> directories;

    public RecoveryModel() {
        this.builder = new QueryBuilder();
        this.schemas = new LinkedList<>();
        this.directories = new LinkedList<>();
    }

    public LinkedList<String> getSchemas() {
        return schemas;
    }

    public LinkedList<String> getDirectories() {
        return directories;
    }

    public void setSchemas() {
        schemas.clear();

        ResultSet result = this.builder.selectAsDBA("SELECT USERNAME FROM DBA_USERS WHERE ACCOUNT_STATUS = 'OPEN' AND USERNAME NOT IN ('SYS', 'SYSTEM')");

        try {
            while (result.next()) {
                this.schemas.add(result.getString(1));
            }
        } catch (SQLException ex) {
            Emergent.showConsole("Error - SetSchemas: " + ex.getMessage());
        }

        setChanged();
        notifyObservers();
    }

    public LinkedList<String> getTablesBySchema(String schemeName) {
        LinkedList<String> tables = new LinkedList<>();

        ResultSet result = this.builder.selectAsDBA("SELECT TABLE_NAME FROM ALL_TABLES WHERE OWNER = '" + schemeName + "'");

        try {
            while (result.next()) {
                tables.add(result.getString(1));
            }
        } catch (SQLException ex) {
            Emergent.showConsole("Error - SetTables: " + ex.getMessage());
        }

        return tables;
    }

    public void setDirectories(String path) {
        directories.clear();

        ResultSet result = this.builder.selectAsDBA("SELECT DIRECTORY_NAME FROM ALL_DIRECTORIES WHERE DIRECTORY_PATH = '" + Reader.divider(path).get("path") + "'");

        try {
            while (result.next()) {
                directories.add(result.getString(1));
            }
        } catch (SQLException ex) {
            Emergent.showConsole("Error - SetTables: " + ex.getMessage());
        }

        setChanged();
        notifyObservers();
    }

    public void resetDirectories() {
        directories.clear();
        setChanged();
        notifyObservers();
    }

    public void recoverBackupByTable(String schema, String tableName, String directory, String dumpPath, boolean needDrop) {
        String dropTable = "DROP TABLE " + schema + "." + tableName;
        String command = "IMPDP " + builder.getDbaUsername() + "/" + builder.getDbaPassword() + "@XE TABLES=" + schema + "." + tableName + " DIRECTORY=" + directory + " DUMPFILE=" + Reader.divider(dumpPath).get("name") + " LOGFILE=IMPDP_" + tableName + ".LOG";
        String logPath = Reader.divider(dumpPath).get("path") + "\\" + "IMPDP_" + tableName + ".LOG";
        Emergent.showConsole(dropTable, command, logPath);

        if (needDrop) {
            builder.executeAsDBA(dropTable);
        }

        Cmd.execute(command, logPath);
    }

    public void recoverBackupBySchema(String schema, String directory, String dumpPath, String newPassword, boolean needDrop) {

        String oracleScript = "ALTER SESSION SET \"_ORACLE_SCRIPT\" = TRUE";
        String dropScheme = "DROP USER " + schema + " CASCADE";
        String permissions = "GRANT CONNECT, RESOURCE TO " + schema;
        String privileges = "GRANT ALL PRIVILEGES TO " + schema;
        String command = "IMPDP " + builder.getDbaUsername() + "/" + builder.getDbaPassword() + "@XE SCHEMAS=" + schema + " DIRECTORY=" + directory + " DUMPFILE=" + Reader.divider(dumpPath).get("name") + " LOGFILE=IMPDP_" + schema + ".LOG";
        String logPath = Reader.divider(dumpPath).get("path") + "\\" + "IMPDP_" + schema + ".LOG";
        String createSchema = "";

        builder.executeAsDBA(oracleScript);

        if (needDrop) {
            createSchema = "CREATE USER " + schema + " IDENTIFIED BY " + newPassword + " DEFAULT TABLESPACE " + getTableSpaces(schema).get("DEFAULT_TABLESPACE") + " TEMPORARY TABLESPACE " + getTableSpaces(schema).get("TEMPORARY_TABLESPACE");
            builder.executeAsDBA(dropScheme);
        } else {
            createSchema = "CREATE USER " + schema + " IDENTIFIED BY " + newPassword;
        }

        builder.executeAsDBA(createSchema);
        builder.executeAsDBA(permissions);
        builder.executeAsDBA(privileges);
        Cmd.execute(command, logPath);

        Emergent.showConsole(dropScheme, createSchema, permissions, privileges, command, newPassword, logPath);
    }

    public HashMap<String, String> getTableSpaces(String schema) {

        HashMap<String, String> response = new HashMap<>();
        ResultSet result = this.builder.selectAsDBA("SELECT DEFAULT_TABLESPACE, TEMPORARY_TABLESPACE FROM DBA_USERS WHERE USERNAME = '" + schema + "'");

        try {
            while (result.next()) {
                response.put("DEFAULT_TABLESPACE", result.getString(1));
                response.put("TEMPORARY_TABLESPACE", result.getString(2));
            }
        } catch (SQLException ex) {
            Emergent.showConsole("Error - GetTableSpaces: " + ex.getMessage());
        }

        return response;
    }

}
