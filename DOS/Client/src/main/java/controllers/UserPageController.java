package controllers;

import domain.models.User;
import domain.models.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.IClientObserver;
import service.IDOSService;
import utils.AlertMessage;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;

public class UserPageController extends UnicastRemoteObject implements IClientObserver, Serializable {
    public AnchorPane adminLayout;
        public AnchorPane addUserSidenav;
            public TextField tf_addUserName;
            public TextField tf_addFirstName;
            public TextField tf_addLastName;
            public PasswordField tf_addPassword;
            public TextField tf_addEmail;
            public ComboBox<String> cb_addUserType;
            public Rectangle btn_confirmAddUser;

    public AnchorPane pharmacyLayout;
    public AnchorPane hospitalLayout;

    private IDOSService service;
    private Stage stage;
    private User user;

    private boolean isConfirmAddUserButtonDisabled;

    private static final Logger _logger = LogManager.getLogger();

    public UserPageController() throws RemoteException {}

    public void init(IDOSService service, Stage stage, User user) {
        _logger.info("Initializing 'User page controller'.");

        this.service = service;
        this.stage = stage;
        this.user = user;
        initComponents();
    }

    private void initComponents() {
        stage.setResizable(false);

        if (user.getUserType() != UserType.Admin) {
            adminLayout.setVisible(false);
        }
        if (user.getUserType() != UserType.PharmacyStaff) {
            pharmacyLayout.setVisible(false);
        }
        if (user.getUserType() != UserType.HospitalStaff) {
            hospitalLayout.setVisible(false);
        }

        stage.setOnCloseRequest(tx -> {
            _logger.traceEntry("Logging user out.");

            service.logoutUser(user.getUserName());

            _logger.traceExit("User logged out.");
        });

        tf_addUserName.textProperty().addListener(tx -> updateConfirmAddUserButtonStatus());
        tf_addFirstName.textProperty().addListener(tx -> updateConfirmAddUserButtonStatus());
        tf_addLastName.textProperty().addListener(tx -> updateConfirmAddUserButtonStatus());
        tf_addPassword.textProperty().addListener(tx -> updateConfirmAddUserButtonStatus());
        tf_addEmail.textProperty().addListener(tx -> updateConfirmAddUserButtonStatus());
        updateConfirmAddUserButtonStatus();

        ObservableList<String> userTypes = FXCollections.observableArrayList();
        userTypes.add(UserType.Admin.toString());
        userTypes.add(UserType.PharmacyStaff.toString());
        userTypes.add(UserType.HospitalStaff.toString());
        cb_addUserType.setItems(userTypes);
    }

    private void updateConfirmAddUserButtonStatus() {
        var shouldDisableButton = tf_addUserName.getText().isEmpty();
        shouldDisableButton |= tf_addFirstName.getText().isEmpty();
        shouldDisableButton |= tf_addLastName.getText().isEmpty();
        shouldDisableButton |= tf_addPassword.getText().isEmpty();
        shouldDisableButton |= tf_addEmail.getText().isEmpty();

        isConfirmAddUserButtonDisabled = shouldDisableButton;
    }

    public void handleCancelAddUser() {
        tf_addUserName.setText("");
        tf_addFirstName.setText("");
        tf_addLastName.setText("");
        tf_addPassword.setText("");
        tf_addEmail.setText("");

        addUserSidenav.setVisible(false);
    }

    public void handleAddUser() {
        cb_addUserType.getSelectionModel().select(0);
        tf_addUserName.requestFocus();

        addUserSidenav.setVisible(true);
    }

    public void handleConfirmAddUser() {
        if (isConfirmAddUserButtonDisabled) {
            return;
        }

        var selectedUserType = cb_addUserType.getSelectionModel().getSelectedIndex();

        var userToAdd = new User(-1,
                tf_addUserName.getText(),
                tf_addFirstName.getText(),
                tf_addLastName.getText(),
                tf_addPassword.getText(),
                "",
                UserType.fromDatabaseRepresentation(selectedUserType),
                tf_addEmail.getText(),
                new Date(System.currentTimeMillis()));

        var response = service.addUser(userToAdd);

        if (response == null) {
            AlertMessage.showAlert(Alert.AlertType.INFORMATION, "User added.", "User added.", stage);
        } else {
            AlertMessage.showAlert(Alert.AlertType.WARNING, "User not added.", "User not added.", stage);
        }
    }
}
