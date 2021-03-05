package Helpers;

import static Helpers.Emergent.message;
import Principal.Config;
import Views.ShowText;

/**
 * Clase encargada de almacenar todas las consultar SQL realizadas a la Base de
 * Datos. La misma funciona como una clase Singleton puesto que su constructor
 * es privado y cuanta con un Objeto instancia de su misma clase.
 */
public class QueryLog {

    public static QueryLog instance = null;
    public String log;
    private int cont;

    private ShowText view;
    Emergent em;

    /**
     * Constructor que inicializa la primera vez el log.
     */
    private QueryLog() {
        log = "";
        cont = 1;

        em = new Emergent();
        view = new ShowText();
        view.getClose().addActionListener((e) -> {
            em.close();
        });
    }

    /**
     * Método que retorna la instancia.
     */
    private static QueryLog getInstance() {
        if (instance == null) {
            instance = new QueryLog();
        }
        return instance;
    }

    /**
     * Método estático que agrega una nueva línea al registro de consultas.
     *
     * @param header String
     * @param newLine String
     */
    public static void addLine(String header, String newLine) {
        getInstance().insertNewLine(header, newLine);
    }

    /**
     * Método estático que ejecuta un salto de línea para notificar que ya se
     * terminó el comando.
     */
    public static void endLine() {
        getInstance().insertEndLine();
    }

    /**
     * Método estático que muestra el log.
     */
    public static void show() {
        getInstance().showLog();
    }

    /**
     * Método que inserta una nueva línea al String log.
     *
     * @param header String
     * @param newLine String
     */
    private void insertNewLine(String header, String newLine) {
        log += cont + ": " + header + ": " + newLine + "\n";
        cont++;

        view.getText().setText(log);
    }

    /**
     * Método que inserta un final de línea en el String log.
     */
    private void insertEndLine() {
        this.log += "\n";
        cont = 1;
    }

    /**
     * Método que muestra todo el log en una ventana emergente.
     */
    private void showLog() {
        if (log == null || log.isEmpty()) {
            message(Config.es("error_file"));
            return;
        }
        view.getTitle().setText("REGISTRO DE CONSULTAS SQL");
        em.open(view, 1200, 500);
    }

}
