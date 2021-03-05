package Database;

import Helpers.QueryLog;
import Principal.Config;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase encargada de realizar consultas cómunes a la Base de Datos. De esta
 * forma todos los modelos pueden consultar y ejecutar comandos a la Base de
 * Datos de una forma facil y rápida.
 */
public class QueryBuilder {

    private Connect connection;
    private String dbaUsername;
    private String dbaPassword;

    public QueryBuilder() {
        this.connection = new Connect();
        this.dbaUsername = Config.auth("username");
        this.dbaPassword = Config.auth("password");
    }

    public String getDbaUsername() {
        return this.dbaUsername;
    }

    public String getDbaPassword() {
        return this.dbaPassword;
    }

    /**
     * Método que retorna la conexión de SYSTEM.
     *
     * @return Connection
     */
    public Connection connectionAsDBA() {
        return this.connection.getConnect(dbaUsername, dbaPassword);
    }

    /**
     * Método que retorna otra conexión diferente de SYSTEM.
     *
     * @param username String
     * @param password String
     * @return Connection
     */
    public Connection connectionAsOther(String username, String password) {
        return this.connection.getConnect(username, password);
    }

    /**
     * Método que ejecuta una consulta de tipo select desde SYSTEM.
     *
     * @param query String
     * @return ResultSet
     */
    public ResultSet selectAsDBA(String query) {
        return select(connectionAsDBA(), query);
    }

    /**
     * Método que ejecuta una consulta de tipo select desde otro usuario
     * diferente de SYSTEM.
     *
     * @param username String
     * @param password String
     * @param query String
     * @return ResultSet
     */
    public ResultSet selectAsOther(String username, String password, String query) {
        return select(connectionAsOther(username, password), query);
    }

    /**
     * Método que ejecuta una consulta de tipo sentencia desde SYSTEM. El mismo
     * retorna si la consulta se ejecutó correctamente o no.
     *
     * @param query String
     * @return boolean
     */
    public boolean executeAsDBA(String query) {
        return execute(connectionAsDBA(), query);
    }

    /**
     * Método que ejecuta una consulta de tipo sentencia desde otro usuario
     * diferente de SYSTEM. El mismo retorna si la consulta se ejecutó
     * correctamente o no.
     *
     * @param username String
     * @param password String
     * @param query String
     * @return boolean
     */
    public boolean executeAsOther(String username, String password, String query) {
        return execute(connectionAsOther(username, password), query);
    }

    /**
     * Método que recibe una conexión y una consulta y la ejecuta en la Base de
     * Datos. El mismo retorna si fue ejecutada con éxito.
     *
     * @param connect Connection
     * @param statement String
     * @return boolean
     */
    private boolean execute(Connection connect, String statement) {

        QueryLog.addLine("Execute", statement);

        PreparedStatement query = null;
        try {
            query = connect.prepareStatement(statement);
            query.execute();
            query.close();
            QueryLog.addLine("Respuesta", "Correcto");
            QueryLog.endLine();
            return true;
        } catch (SQLException e) {
            QueryLog.addLine("Error", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
        QueryLog.endLine();
        return false;
    }

    /**
     * Método que realiza una consulta de tipo select en la Base de Datos. Y
     * retorna el resultado de la misma.
     *
     * @param connect Connection
     * @param statement String
     * @return ResultSet
     */
    private ResultSet select(Connection connect, String statement) {

        QueryLog.addLine("Select", statement);

        ResultSet datos = null;
        PreparedStatement query = null;

        try {
            query = connect.prepareStatement(statement);
            datos = query.executeQuery();
            QueryLog.addLine("Respuesta", "Correcto");
        } catch (SQLException e) {
            QueryLog.addLine("Error", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }

        QueryLog.endLine();
        return datos;
    }
}
