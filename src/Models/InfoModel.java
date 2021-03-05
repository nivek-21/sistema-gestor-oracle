package Models;

import Database.QueryBuilder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Observable;
import javax.swing.table.DefaultTableModel;

public class InfoModel extends Observable {

    private QueryBuilder builder;
    private DefaultTableModel table;

    public InfoModel() {
        builder = new QueryBuilder();
        table = new DefaultTableModel();
    }

    public DefaultTableModel getTable() {
        return table;
    }

    public void makeQuery(String type) {
        String query = "";
        switch (type) {
            case "InfoInstance":
                query = "SELECT * FROM V$INSTANCE";
                break;
            case "DbName":
                query = "SELECT VALUE FROM V$SYSTEM_PARAMETER WHERE NAME = 'db_name'";
                break;
            case "DbParameters":
                query = "SELECT * FROM V$SYSTEM_PARAMETER";
                break;
            case "DbVersion":
                query = "SELECT VALUE FROM V$SYSTEM_PARAMETER WHERE NAME = 'compatible'";
                break;
            case "SpPath":
                query = "SELECT VALUE FROM V$SYSTEM_PARAMETER WHERE NAME = 'spfile'";
                break;
            case "CurrentConnections":
                query = "SELECT OSUSER, USERNAME, MACHINE, PROGRAM FROM V$SESSION ORDER BY OSUSER";
                break;
            case "TempFiles":
                query = "SELECT * FROM V$TEMPFILE";
                break;
        }
        executeQuery(query);
    }

    private void executeQuery(String query) {

        table = new DefaultTableModel();

        ResultSet resultado = builder.selectAsDBA(query);

        try {

            ResultSetMetaData metaData = resultado.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            for (int i = 0; i < numberOfColumns; i++) {
                table.addColumn(metaData.getColumnName(i + 1));
            }

            Object[] row = new Object[numberOfColumns];

            while (resultado.next()) {

                for (int i = 0; i < numberOfColumns; i++) {
                    row[i] = resultado.getString(i + 1);
                }

                table.addRow(row);

            }

        } catch (SQLException ex) {

        }

        setChanged();
        notifyObservers();
    }

}
