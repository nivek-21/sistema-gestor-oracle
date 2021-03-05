package Models;

import Database.QueryBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 * Clase encargada de realizar las consultas a la base de datos para la sección
 * de autorías.
 */
public class AuditModel extends Observable {

    private QueryBuilder builder;
    private DefaultTableModel audits;

    public AuditModel() {
        this.builder = new QueryBuilder();
        this.audits = new DefaultTableModel();
    }

    public DefaultTableModel getAudits() {
        return audits;
    }

    public void setAudits(DefaultTableModel audits) {
        this.audits = audits;
    }

    /**
     * Método que retorna una lista con los nombres de todos los esquemas de la
     * base de datos.
     *
     * @return
     */
    public ArrayList<String> getSchemas() {
        ResultSet result = builder.selectAsDBA("select username as schema_name from sys.dba_users WHERE ACCOUNT_STATUS = 'OPEN' AND USERNAME NOT IN ('SYS', 'SYSTEM') order by username");
        ArrayList<String> schemas = new ArrayList<String>();

        try {
            while (result.next()) {
                schemas.add(result.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SchemaModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return schemas;
    }

    /**
     * Método que retorna una lista con los nombres de todas las tablas dado un
     * esquema específico.
     *
     * @param schema
     * @return
     */
    public ArrayList<String> getTables(String schema) {
        ResultSet result = builder.selectAsDBA("select object_name as table_name from all_objects t where object_type = 'TABLE' and owner = '" + schema + "' order by object_name");
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

    /**
     * Método que carga la tabla de Auditorías.
     */
    public void loadAudit() {

        this.audits = new DefaultTableModel();
        this.audits.addColumn("USERNAME");
        this.audits.addColumn("DATE/HOUR");
        this.audits.addColumn("TABLE NAME");
        this.audits.addColumn("OWNER");
        this.audits.addColumn("ACTION NAME");

        ResultSet result = this.builder.selectAsDBA("select distinct username, TO_CHAR(timestamp, 'DD-MON-YY HH24:MI'), obj_name, owner, action_name from dba_audit_object");

        try {
            Object[] row = new Object[5];
            while (result.next()) {
                row[0] = result.getString(1);
                row[1] = result.getString(2);
                row[2] = result.getString(3);
                row[3] = result.getString(4);
                row[4] = result.getString(5);
                this.audits.addRow(row);
            }

        } catch (SQLException ex) {
            System.out.println("Error - LoadUsers: " + ex.getMessage());
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Método que ejecuta el comando SQL para crear el audit en la tabla del
     * esquema especificado.
     *
     * @param schema
     * @param table
     * @return
     */
    public boolean createTableAudit(String schema, String table) {
        return this.builder.executeAsDBA("Audit insert, update, delete on " + schema + "." + table + " by access");
    }

}
