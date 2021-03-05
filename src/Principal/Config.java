package Principal;

import java.util.HashMap;

/**
 * Clase encargada de almacenar configuraciones importantes del sistema. La
 * misma almacena dicha información en clases HashMap para su posterior
 * utilización.
 */
public class Config {

    /**
     * HashMaps estáticos que almacenan el clave valor de la configuración de la
     * Base de Datos y el lenguaje.
     */
    private static HashMap<String, String> es = new HashMap<String, String>();
    private static HashMap<String, String> auth = new HashMap<String, String>();

    /**
     * Método encargado de configurar la conexión a la Base de Datos.
     */
    public static void loadDb() {
        auth.put("username", "system");
        auth.put("password", "admin123");
    }

    /**
     * Método encargado de registrar los mensajes emergentes del sistema.
     */
    public static void loadEs() {

        /**
         * Mensajes de acciones generales.
         */
        es.put("error_row", "Por favor seleccione una fila de la tabla.");
        es.put("time", "El proceso podría tardar algún tiempo, le notificaremos cuando termine.");
        es.put("system_error", "La aplicación está presentando problemas.");
        es.put("error_cmd", "La operación en linea de comandos falló inesperadamente.");
        es.put("end_process", "PROCESO FINALIZADO");
        es.put("success_process", "Proceso Finalizado Exitosamente");
        es.put("error_process", "El proceso finalizó inesperadamente");

        /**
         * Mensajes del controlador BackUpController.
         */
        es.put("back_up_success", "Respaldo creado exitosamente.");
        es.put("back_up_name_error", "No se puede crear un respaldo sin nombre.");
        es.put("back_up_name", "Por favor digite el nombre del respaldo: ");
        es.put("error_file", "No es posible mostrar el archivo.");

        /**
         * Mensajes del controlador DirectoryController.
         */
        es.put("del_dir_success", "Directorio eliminado exitosamente.");
        es.put("del_dir_error", "No pudimos eliminar el directorio.");
        es.put("chooser_title", "Seleccione la ubicación del directorio");
        es.put("dir_name_empty", "Por favor digite el nombre del directorio.");
        es.put("dir_path_empty", "Por favor seleccione un directorio.");
        es.put("add_dir_success", "Directorio agregado exitosamente.");
        es.put("add_dir_error", "No pudimos agregar el directorio.");

        /**
         * Mensajes del controlador SchemaController.
         */
        es.put("add_schema_success", "Esquema creado exitosamente.");
        es.put("add_schema_error", "No pudimos crear el esquema.");
        es.put("del_schema_error", "No pudimos eliminar el esquema.");
        es.put("del_schema_success", "Esquema eliminado exitosamente.");

        /**
         * Mensajes del controlador StatsController.
         */
        es.put("add_stats_success", "Estadísticas creadas exitosamente.");
        es.put("add_stats_error", "No pudimos crear las estadísticas.");
        es.put("del_stats_success", "Estadisticas elimadas exitosamente.");
        es.put("del_stats_error", "No pudimos eliminar las estisticas.");
        es.put("upd_stats_success", "Estadisticas actualizadas exitosamente.");
        es.put("upd_stats_error", "No pudimos actualizar las estisticas.");
        es.put("non_stats", "No se encontraron estadisticas para esta tabla.");
        es.put("exist_stats", "Esta tabla ya cuenta con estadisticas generadas.");

        /**
         * Mensajes del controlador IndexController.
         */
        es.put("add_index_success", "Índice creado exitosamente");
        es.put("add_index_error", "Tuvimos algún problema en la creación del índice");
        es.put("del_index_success", "Índice eliminado exitosamente.");
        es.put("del_index_error", "Error al eliminar el índice");
        es.put("rebuild_index_success", "Índice reconstruido exitosamente.");
        es.put("rebuild_index_error", "Error al reconstruir el índice.");
        es.put("error_password", "La clave suministrada es incorrecta");
        es.put("insert_password", "Digite la clave del usuario ");

        /**
         * Mensajes del controlador ExplainController.
         */
        es.put("insert_consult", "Digite por una consulta.");
        es.put("add_explain_success", "Plan de ejecución creado exitosamente.");
        es.put("add_explain_error", "Tuvimos un error creando el plan de ejecución.");
    }

    /**
     * Retorna una configuración específica del auth de bases de datos.
     *
     * @return
     */
    public static String auth(String key) {
        return auth.get(key);
    }

    /**
     * Retorna un configuración específica de los mensajes de traducción.
     *
     * @return
     */
    public static String es(String key) {
        return es.get(key);
    }
}
