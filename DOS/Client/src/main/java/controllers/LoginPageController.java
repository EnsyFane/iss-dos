package controllers;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.IDOSService;

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
}
