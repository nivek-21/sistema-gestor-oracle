package Models;

import Database.QueryBuilder;
import Helpers.Cmd;
import Helpers.Emergent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;
import javax.swing.table.DefaultTableModel;

public class BackUpModel extends Observable {

    private QueryBuilder builder;
    private DefaultTableModel tables;
    private DefaultTableModel schemes;

    public BackUpModel() {
        this.builder = new QueryBuilder();
        this.tables = new DefaultTableModel();
        this.schemes = new DefaultTableModel();
    }

    public DefaultTableModel getTables() {
        return this.tables;
    }

    public DefaultTableModel getSchemes() {
        return schemes;
    }

    public void modelByScheme() {
        this.schemes = new DefaultTableModel();
        this.schemes.addColumn("USERNAME");
        this.schemes.addColumn("EXPIRY_DATE");
        this.schemes.addColumn("DEFAULT_TABLESPACE");
        this.schemes.addColumn("TEMPORARY_TABLESPACE");
        this.schemes.addColumn("LAST_LOGIN");

        loadSchemes();
    }

    private void loadSchemes() {
        ResultSet result = this.builder.selectAsDBA("SELECT USERNAME, EXPIRY_DATE, DEFAULT_TABLESPACE, TEMPORARY_TABLESPACE, LAST_LOGIN FROM DBA_USERS WHERE ACCOUNT_STATUS = 'OPEN' AND USERNAME NOT IN ('SYS', 'SYSTEM')");

        try {

            Object[] row = new Object[5];

            while (result.next()) {
                row[0] = result.getString(1);
                row[1] = result.getString(2);
                row[2] = result.getString(3);
                row[3] = result.getString(4);
                row[4] = result.getString(5);

                this.schemes.addRow(row);
            }

        } catch (SQLException ex) {
            Emergent.showConsole("Error - LoadSchemes: " + ex.getMessage());
        }

        setChanged();
        notifyObservers();
    }

    public void modelByTable() {
        this.tables = new DefaultTableModel();
        this.tables.addColumn("OWNER");
        this.tables.addColumn("TABLE_NAME");

        loadTables();
    }

    private void loadTables() {
        ResultSet result = this.builder.selectAsDBA("SELECT OWNER, TABLE_NAME FROM ALL_TABLES JOIN DBA_USERS ON ALL_TABLES.OWNER = DBA_USERS.USERNAME WHERE DBA_USERS.ACCOUNT_STATUS = 'OPEN' AND DBA_USERS.USERNAME NOT IN ('SYS', 'SYSTEM')");

        try {

            Object[] row = new Object[2];

            while (result.next()) {
                row[0] = result.getString(1);
                row[1] = result.getString(2);

                this.tables.addRow(row);
            }

        } catch (SQLException ex) {
            Emergent.showConsole("Error - LoadTables: " + ex.getMessage());
        }

        setChanged();
        notifyObservers();
    }

    public void searchTable(String key) {
        this.tables = new DefaultTableModel();
        this.tables.addColumn("OWNER");
        this.tables.addColumn("TABLE_NAME");

        loadSearchTable(key);
    }

    private void loadSearchTable(String key) {
        ResultSet result = this.builder.selectAsDBA("SELECT owner, table_name FROM all_tables where table_name like '%" + key + "%' OR owner like '%" + key + "%' ");

        try {

            Object[] row = new Object[2];

            while (result.next()) {
                row[0] = result.getString(1);
                row[1] = result.getString(2);

                this.tables.addRow(row);
            }

        } catch (SQLException ex) {
            Emergent.showConsole("Error - LoadSearchTable: " + ex.getMessage());
        }

        setChanged();
        notifyObservers();
    }

    public DefaultTableModel getAllDirectories() {
        DefaultTableModel directories = new DefaultTableModel();

        directories.addColumn("OWNER");
        directories.addColumn("DIRECTORY_NAME");
        directories.addColumn("DIRECTORY_PATH");
        directories.addColumn("ORIGIN_CON_ID");

        loadDirectories(directories);

        return directories;
    }

    private void loadDirectories(DefaultTableModel directories) {

        ResultSet result = this.builder.selectAsDBA("SELECT * FROM ALL_DIRECTORIES");

        try {
            Object[] row = new Object[4];

            while (result.next()) {
                row[0] = result.getString(1);
                row[1] = result.getString(2);
                row[2] = result.getString(3);
                row[3] = result.getString(4);

                directories.addRow(row);
            }
        } catch (SQLException ex) {
            Emergent.showConsole("Error - LoadDirectories: " + ex.getMessage());
        }
    }

    public void createBackUpByScheme(String scheme, String directory, String backUpName) {
        String permissions = "GRANT READ, WRITE ON DIRECTORY " + directory + " TO " + scheme;
        String command = "EXPDP " + builder.getDbaUsername() + "/" + builder.getDbaPassword() + "@XE SCHEMAS=" + scheme + " DIRECTORY=" + directory + " DUMPFILE=" + backUpName + ".DUMP LOGFILE=" + backUpName + ".LOG";
        String logPath = getDirectoryPath(directory) + "\\" + backUpName + ".LOG";

        Emergent.showConsole(permissions, command, logPath);
        builder.executeAsDBA(permissions);
        Cmd.execute(command, logPath);
    }

    public void createBackUpFull(String directory, String backUpName) {
        String command = "EXPDP " + builder.getDbaUsername() + "/" + builder.getDbaPassword() + "@XE FULL=Y DIRECTORY=" + directory + " DUMPFILE=" + backUpName + ".DUMP LOGFILE=" + backUpName + ".LOG";
        String logPath = getDirectoryPath(directory) + "\\" + backUpName + ".LOG";

        Emergent.showConsole(command, logPath);
        Cmd.execute(command, logPath);
    }

    public void createBackUpByTable(String scheme, String table, String directory, String backUpName) {
        String command = "EXPDP " + builder.getDbaUsername() + "/" + builder.getDbaPassword() + "@XE TABLES=" + scheme + "." + table + " DIRECTORY=" + directory + " DUMPFILE=" + backUpName + ".DUMP LOGFILE=" + backUpName + ".LOG";
        String logPath = getDirectoryPath(directory) + "\\" + backUpName + ".LOG";

        Emergent.showConsole(command, logPath);
        Cmd.execute(command, logPath);
    }

    private String getDirectoryPath(String directory) {
        ResultSet result = this.builder.selectAsDBA("SELECT DIRECTORY_PATH FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME = '" + directory + "'");
        try {
            String path = "";

            while (result.next()) {
                path = result.getString(1);
            }

            return path;
        } catch (SQLException ex) {
            Emergent.showConsole("Error - GetDirectoryPath: " + ex.getMessage());
            return null;
        }
    }

}
