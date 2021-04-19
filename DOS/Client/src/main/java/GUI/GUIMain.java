package GUI;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.IDOSService;

public class GUIMain extends Application {
    private static IDOSService service;

    private static final Logger _logger = LogManager.getLogger();

    public static void main(IDOSService server, String[] args) {
        _logger.info("Starting GUI.");

        service = server;
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: add start logic
    }
}
