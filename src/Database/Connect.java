package Database;

import Helpers.Emergent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Clase encargada de realizar la conexión con la Base de Datos. La misma genera
 * una lista enlazada de User para recordar las conexiones realizadas en el
 * aplicativo. Esto para evitar tiempos prolongados de espera cuando se realizan
 * conexiones con ORACLE.
 */
public class Connect {

    /**
     * Url = Link por el cual nos vamos a conectar a la Base de Datos.
     */
    private String url;

    /**
     * Lista enlazada estática que guarda todas las conexiones realizadas
     * durante la ejecución de la aplicación.
     */
    private static LinkedList<User> users = new LinkedList<>();

    /**
     * Constructor para inicializar la URL.
     */
    public Connect() {
        this.url = "jdbc:oracle:thin:@localhost:1521:xe";
    }

    /**
     * Método que retorna una conexión a la Base de Datos. El mismo espera un
     * username y una password; en caso que la conexión ya se encuentra
     * registrada en la lista enlazada estática de users el mismo la retornará;
     * en caso contrario, crea la nueva conexión y la almacena en dicha lista
     * enlazada.
     *
     * @param username String
     * @param password String
     * @return Connection
     */
    public Connection getConnect(String username, String password) {
        Connection connect = checkConnection(username, password);
        if (connect == null) {
            return newConnection(username, password);
        }
        return connect;
    }

    /**
     * Método que crea una nueva conexión con la Base de Datos, la almacena en
     * la lista enlazada estática y por ultimo la retorna.
     *
     * @param username String
     * @param password String
     * @return Connection
     */
    private Connection newConnection(String username, String password) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection connect = DriverManager.getConnection(url, username, password);
            Emergent.showConsole("Conexíon Exitosa");

            User newUser = new User(username, password, connect);
            users.add(newUser);
            return connect;

        } catch (ClassNotFoundException | SQLException e) {
            Emergent.showConsole("Error de Conexión - DB: " + e.getMessage());
        }

        return null;
    }

    /**
     * Método que revisa en la lista enlazada estática si la conexión ya existe;
     * en dicho caso retorna esa conexión, en caso contrario, retorna null.
     *
     * @param username String
     * @param password String
     * @return Connection
     */
    private Connection checkConnection(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user.getConnection();
            }
        }
        return null;
    }
}

/**
 * Clase encargada de almacenar la información necesaria para identificar el
 * username, password y la conexión.
 */
class User {

    private String username;
    private String password;
    private Connection connection;

    public User(String username, String password, Connection connection) {
        this.username = username;
        this.password = password;
        this.connection = connection;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Connection getConnection() {
        return connection;
    }

}
