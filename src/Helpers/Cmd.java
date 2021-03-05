package Helpers;

import Principal.Config;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Clase encargada de ejecutar comandos en CMD, la misma almacena las consultas
 * en un cola, esto para que la información pueda ser accesada desde el método
 * init del hilo.
 */
public class Cmd {

    /**
     * Cola que almacena todas las request que ingresan al método execute.
     */
    private static Queue<Request> requests = new LinkedList<>();

    /**
     * Método que almacena la petición en la cola estática e inicia el hilo con
     * la ejecución de la petición. Sin embargo, este constructor agrega un path
     * para levantar el log file resultante de los comandos EXPDP e IMPDP.
     *
     * @param command String
     * @param path String
     */
    public static void execute(String command, String path) {
        requests.add(new Request(command, path, true));
        run();
    }

    /**
     * Método que almacena la petición en la cola estática e inicia el hilo con
     * la ejecución de la petición.
     *
     * @param command String
     */
    public static void execute(String command) {
        requests.add(new Request(command, null, false));
        run();
    }

    /**
     * Método que inicia el hilo de ejecución para que los comandos en CMD se
     * ejecuten en segundo plano.
     */
    private static void run() {
        new Thread(() -> {
            init(requests.remove());
        }).start();
    }

    /**
     * Método que ejecuta el comando en el CMD.
     *
     * @param request Request
     */
    private static void init(Request request) {
        Emergent em = new Emergent();

        try {
            Process process = Runtime.getRuntime().exec(request.command);

            process.waitFor(4, TimeUnit.MINUTES);
            process.destroy();

            Emergent.message(Config.es("success_process"));

            if (request.needLog) {
                em.showFile(Reader.readLog(request.logPath));
            }

            return;

        } catch (IOException | InterruptedException e) {
            Emergent.showConsole("Error - CMD: " + e.getMessage());
        }

        Emergent.message(Config.es("error_process"));

        if (request.needLog) {
            em.showFile(Reader.readLog(request.logPath));
        }
    }
}

/**
 * Clase encargada de almacenar la información de la request que ingresa, el
 * mismo almacena el comando a ejecutar, el log en caso de ser necesario y si
 * necesita realizar un levantamiento del mismo al finalizar la ejecución del
 * comando.
 */
class Request {

    public String command;
    public String logPath;
    public boolean needLog;

    public Request(String command, String logPath, boolean needLog) {
        this.command = command;
        this.logPath = logPath;
        this.needLog = needLog;
    }
}
