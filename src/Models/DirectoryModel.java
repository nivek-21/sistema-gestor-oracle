package Models;

import Database.QueryBuilder;
import Helpers.Emergent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;
import javax.swing.table.DefaultTableModel;

public class DirectoryModel extends Observable {

    private QueryBuilder builder;
    private DefaultTableModel directories;

    public DirectoryModel() {
        this.builder = new QueryBuilder();
        this.directories = new DefaultTableModel();
    }

    public DefaultTableModel getDirectories() {
        return directories;
    }

    public void modelDirectories() {
        this.directories = new DefaultTableModel();

        directories.addColumn("OWNER");
        directories.addColumn("DIRECTORY_NAME");
        directories.addColumn("DIRECTORY_PATH");
        directories.addColumn("ORIGIN_CON_ID");

        loadDirectories();
    }

    public void loadDirectories() {
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

        setChanged();
        notifyObservers();
    }

    public boolean deleteDirectory(String directoryName) {
        return builder.executeAsDBA("DROP DIRECTORY " + directoryName);
    }

    public boolean addDirectory(String path, String directoryName) {
        String query = "CREATE OR REPLACE DIRECTORY " + directoryName + " AS '" + path + "'";
        return builder.executeAsDBA(query);
    }
}
