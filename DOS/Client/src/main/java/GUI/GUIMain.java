package GUI;

import controllers.LoginPageController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.IDOSService;

import java.io.IOException;

public class GUIMain extends Application {
    private static IDOSService service;

    private static final Logger _logger = LogManager.getLogger();

    public static void main(IDOSService server, String[] args) {
        _logger.info("Starting GUI.");

        service = server;
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            initView(primaryStage);
            primaryStage.show();
        } catch (IOException e) {
            _logger.fatal(e);

            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void initView(Stage primaryStage) throws IOException{
        _logger.traceEntry("Initializing primary view.", primaryStage);

        var loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getClassLoader().getResource("views/loginPage.fxml"));
        AnchorPane loginLayout = loginLoader.load();
        var scene = new Scene(loginLayout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("D.O.S. - Login");

        LoginPageController loginPageController = loginLoader.getController();
        loginPageController.init(service, primaryStage);

        _logger.traceExit("Primary view initialized");
    }
}
