package controllers;

import domain.dto.DrugDTO;
import domain.models.Order;
import domain.models.User;
import domain.models.UserType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import service.IClientObserver;
import service.IDOSService;
import utils.AlertMessage;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        public Label lbl_availableDrugs;
            public TableView<DrugDTO> tv_hospitalDrugs;
            public TableColumn<DrugDTO, Boolean> tc_hospitalDrugsCheckBox;
            public TableColumn<DrugDTO, String> tc_hospitalDrugsName;
            public TableColumn<DrugDTO, String> tc_hospitalDrugsDescription;
            public TableColumn<DrugDTO, Integer> tc_hospitalDrugsInStock;
            public TableColumn<DrugDTO, Integer> tc_hospitalDrugsToOrder;
            public Circle btn_placeOrderBG;
            public SVGPath btn_placeOrderSVG;
        public Label lbl_drugOrders;
    
    public AnchorPane changePasswordSidenav;
        public PasswordField tf_changeOldPassword;
        public PasswordField tf_changeNewPassword;
        public PasswordField tf_changeConfirmPassword;
        public Rectangle btn_confirmChangePassword;

    private IDOSService service;
    private Stage stage;
    private User user;

    private boolean isConfirmAddUserButtonDisabled;
    private boolean isConfirmChangePasswordButtonDisabled;

    private transient final ObservableList<DrugDTO> hospitalDrugs = FXCollections.observableArrayList();

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

        adminLayout.setVisible(false);
        pharmacyLayout.setVisible(false);
        hospitalLayout.setVisible(false);

        switch (user.getUserType()){
            case Admin -> adminLayout.setVisible(true);
            case PharmacyStaff -> pharmacyLayout.setVisible(true);
            case HospitalStaff -> hospitalLayout.setVisible(true);
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

        tf_changeOldPassword.textProperty().addListener(tx -> updateConfirmChangePasswordButtonStatus());
        tf_changeNewPassword.textProperty().addListener(tx -> updateConfirmChangePasswordButtonStatus());
        tf_changeConfirmPassword.textProperty().addListener(tx -> updateConfirmChangePasswordButtonStatus());
        updateConfirmChangePasswordButtonStatus();

        ObservableList<String> userTypes = FXCollections.observableArrayList();
        userTypes.add(UserType.Admin.toString());
        userTypes.add(UserType.PharmacyStaff.toString());
        userTypes.add(UserType.HospitalStaff.toString());
        cb_addUserType.setItems(userTypes);

        btn_placeOrderBG.setVisible(false);
        btn_placeOrderSVG.setVisible(false);

        setupHospitalDrugsTable();
        updateTables();
    }

    private void updateTables() {
        _logger.traceEntry("Updating tables.");

        var drugs = service.getAvailableDrugs();

        _logger.info("Received available drugs.");

        hospitalDrugs.setAll(drugs);

        _logger.traceExit("Updated tables.");
    }

    private void setupHospitalDrugsTable() {
        tc_hospitalDrugsCheckBox.setCellValueFactory(c -> {
            var checkBox = new CheckBox();
            if (c.getValue().getInStock() <= 0) {
                checkBox.selectedProperty().setValue(false);
                checkBox.setDisable(true);
            } else {
                checkBox.selectedProperty().setValue(c.getValue().getSelected());
                checkBox.selectedProperty()
                        .addListener((ov, old_val, new_val) -> {
                            c.getValue().setSelected(new_val);
                            if (!new_val) {
                                c.getValue().setToOrder(0);
                            }
                            updateSendOrderButtonState(0);
                            tv_hospitalDrugs.refresh();
                        });
            }
            //noinspection rawtypes,unchecked,unchecked
            return new SimpleObjectProperty(checkBox);
        });
        tc_hospitalDrugsName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tc_hospitalDrugsDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tc_hospitalDrugsInStock.setCellValueFactory(new PropertyValueFactory<>("inStock"));
        tc_hospitalDrugsToOrder.setCellValueFactory(c -> {
            var spinner = new Spinner<Integer>();
            if (!c.getValue().getSelected()) {
                spinner.setDisable(true);
                c.getValue().setToOrder(0);
            }

            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, c.getValue().getInStock(), c.getValue().getToOrder()));
            spinner.valueProperty()
                    .addListener((ov,old_val,new_val) -> c.getValue().setToOrder(new_val));
            spinner.valueProperty()
                    .addListener((ov, old_val, new_val) -> updateSendOrderButtonState(new_val));

            //noinspection rawtypes,unchecked,unchecked
            return new SimpleObjectProperty(spinner);
        });

        tv_hospitalDrugs.setItems(hospitalDrugs);
    }

    private void updateConfirmAddUserButtonStatus() {
        var shouldDisableButton = tf_addUserName.getText().isEmpty();
        shouldDisableButton |= tf_addFirstName.getText().isEmpty();
        shouldDisableButton |= tf_addLastName.getText().isEmpty();
        shouldDisableButton |= tf_addPassword.getText().isEmpty();
        shouldDisableButton |= tf_addEmail.getText().isEmpty();

        isConfirmAddUserButtonDisabled = shouldDisableButton;
    }

    private void updateConfirmChangePasswordButtonStatus() {
        var shouldDisableButton = tf_changeOldPassword.getText().isEmpty();
        shouldDisableButton |= tf_changeNewPassword.getText().isEmpty();
        shouldDisableButton |= tf_changeConfirmPassword.getText().isEmpty();
        shouldDisableButton |= !tf_changeConfirmPassword.getText().equals(tf_changeNewPassword.getText());
        shouldDisableButton |= tf_changeNewPassword.getText().equals(tf_changeOldPassword.getText());

        isConfirmChangePasswordButtonDisabled = shouldDisableButton;
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

        var userToAdd = new User(0,
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

        handleCancelAddUser();
    }

    public void handleCancelChangePassword() {
        tf_changeOldPassword.setText("");
        tf_changeNewPassword.setText("");
        tf_changeConfirmPassword.setText("");

        changePasswordSidenav.setVisible(false);
    }

    public void handleChangePassword() {
        tf_changeOldPassword.requestFocus();

        changePasswordSidenav.setVisible(true);
    }

    public void handleConfirmChangePassword() {
        if (!isConfirmChangePasswordButtonDisabled) {
            return;
        }

        var result = service.changePassword(user.getId(), tf_changeOldPassword.getText(), tf_changeNewPassword.getText());

        if (result) {
            AlertMessage.showAlert(Alert.AlertType.INFORMATION, "Password changed.", "Password changed.", stage);
        } else {
            AlertMessage.showAlert(Alert.AlertType.WARNING, "Password not changed.", "Password not changed.", stage);
        }

        handleCancelChangePassword();
    }

    private void updateSendOrderButtonState(int spinner_val) {
        if (spinner_val > 0) {
            btn_placeOrderBG.setVisible(true);
            btn_placeOrderSVG.setVisible(true);
        } else {
            var toOrderDrugs = hospitalDrugs
                    .stream()
                    .filter(DrugDTO::getSelected)
                    .filter(d -> d.getToOrder() > 0)
                    .count();

            btn_placeOrderBG.setVisible(toOrderDrugs > 0);
            btn_placeOrderSVG.setVisible(toOrderDrugs > 0);
        }
    }

    public void handleSendOrder() {
        var response = AlertMessage.showAlert(Alert.AlertType.INFORMATION, "Place order.", "Are you sure you want to place this order?", stage, ButtonType.YES, ButtonType.NO);

        if (response.isEmpty()) {
            return;
        } else {
            if (response.get().getButtonData().isCancelButton()) {
                return;
            }
        }

        var order = new Order(1, user.getId(), false, new Date(System.currentTimeMillis()), null);
        hospitalDrugs.stream()
                .filter(DrugDTO::getSelected)
                .forEach(d -> order.addDrug(d.getId(), d.getToOrder()));

        var result = service.placeOrder(order);

        if (result) {
            AlertMessage.showAlert(Alert.AlertType.INFORMATION, "Order placed.", "Order placed.", stage);
        } else {
            AlertMessage.showAlert(Alert.AlertType.ERROR, "Order not placed.", "Order not placed.", stage);
        }
    }
}
