package controllers;

import domain.dto.UserDTO;
import domain.models.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.IDOSService;
import utils.AlertMessage;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.rmi.server.UnicastRemoteObject;

public class LoginPageController {
    public TextField tf_userName;
    public PasswordField tf_password;
    public Button btn_logIn;

    private IDOSService service;
    private Stage stage;

    private static final Logger _logger = LogManager.getLogger();

    public void init(IDOSService service, Stage stage) {
        _logger.info("Initializing 'Login page'.");

        this.service = service;
        this.stage = stage;
        initComponents();
    }

    private void initComponents() {
        stage.setResizable(false);

        btn_logIn.setDefaultButton(true);
        btn_logIn.setDisable(true);

        tf_userName.textProperty().addListener(tx -> updateLoginButtonStatus());
        tf_password.textProperty().addListener(tx -> updateLoginButtonStatus());
    }

    private void updateLoginButtonStatus() {
        btn_logIn.setDisable(tf_password.getText().isEmpty() || tf_userName.getText().isEmpty());
    }

    public void handleLogin() {
        _logger.traceEntry("Trying to log user in.");

        var userPageLoader = new FXMLLoader();
        userPageLoader.setLocation(getClass().getResource("/views/userPage.fxml"));
        AnchorPane userPageLayout;
        try {
            userPageLayout = userPageLoader.load();
        } catch (IOException e) {
            _logger.error(e);

            AlertMessage.showAlert(Alert.AlertType.ERROR, "Internal Error", "There was a problem loading the page. Restarting the application might help.", stage);

            _logger.traceExit();

            return;
        }

        var userPage = new Stage();
        var newScene = new Scene(userPageLayout);
        userPage.setScene(newScene);
        userPage.initOwner(stage);
        userPage.initModality(Modality.WINDOW_MODAL);

        UserPageController userPageController = userPageLoader.getController();

        User user;
        try {
            user = service.loginUser(new UserDTO(tf_userName.getText(), tf_password.getText()), userPageController);
        } catch (Exception e) {
            _logger.error(e);

            AlertMessage.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage(), stage);

            _logger.traceExit();

            return;
        }

        if (user == null) {
            _logger.traceExit("Login failed.");

            AlertMessage.showAlert(Alert.AlertType.ERROR, "Login failed!", "No user with the given credentials is registered in the app!", stage);
            return;
        }

        userPage.setTitle("D.O.S. - " + user.getUserType().toString() + " Page");
        userPageController.init(service, userPage, user);

        stage.hide();
        tf_userName.setText("");
        tf_password.setText("");
        tf_userName.requestFocus();
        btn_logIn.setDisable(true);

        _logger.traceExit("Login successful.");

        userPage.showAndWait();
        try {
            UnicastRemoteObject.unexportObject(userPageController, true);
        } catch (NoSuchObjectException e) {
            e.printStackTrace();
        }
        stage.show();
    }
}
