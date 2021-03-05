package Principal;

import Controllers.*;

public class App {

    /*
    _______________ PROYECTO FINAL ________________
    |                                             |
    |   CURSO: ADMINISTRACIÓN DE BASES DE DATOS   |
    |                                             |
    |   PROFESOR: MAP. RODOLFO SANCHEZ SANCHEZ    |
    |                                             |
    |   ESTUDIANTES:                              |
    |        KEVIN GONZÁLEZ NAVARRO               |
    |        BRYAN SOLANO SABORÍO                 |
    |        JEFFERSON HERNANDEZ FLORES           |
    |        CARLOS ROSALES SALAS                 |
    |                                             |
    |    II CICLO 2020                            |
    |                                             |
    |_____________________________________________|

    _______________ CONSIDERACIONES _______________
    |                                             |
    |   1. El programa necesita la librería       |
    |   llamada ojdbc8.jar.                       |
    |                                             |
    |   2. El programa está creado bajo el        |
    |   funcionamiento de Oracle Database 18C,    |
    |   por ello podría no funcionar en           |
    |   versiones posteriores a ella.             |
    |                                             |
    |   3. El sistema se conecta con SYSTEM, y    |
    |   para configurarla, ingresar a la clase    |
    |   Principal/Config.java.                    |
    |                                             |
    |_____________________________________________|

     */
    public static void main(String[] args) {
        Config.loadDb();
        Config.loadEs();

        MainController mainController = new MainController();

        SchemaController schemaController = new SchemaController();
        mainController.addController(schemaController);

        BackUpController backUpController = new BackUpController();
        mainController.addController(backUpController);

        RecoveryController recoveryController = new RecoveryController();
        mainController.addController(recoveryController);

        TableSpaceController tableSpaceController = new TableSpaceController();
        mainController.addController(tableSpaceController);

        DirectoryController directoryController = new DirectoryController();
        mainController.addController(directoryController);

        StatsController statsController = new StatsController();
        mainController.addController(statsController);

        UsersController usersController = new UsersController();
        mainController.addController(usersController);

        IndexController indexController = new IndexController();
        mainController.addController(indexController);

        AuditController auditController = new AuditController();
        mainController.addController(auditController);

        ExplainController explainController = new ExplainController();
        mainController.addController(explainController);

        mainController.init();
    }

}
