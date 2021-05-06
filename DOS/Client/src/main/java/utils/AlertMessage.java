package utils;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class AlertMessage {
    /**
     * Show and wait for an alert with the specified details.
     * @param type the type on the alert.
     * @param header the header of the alert.
     * @param content the text of the alert.
     * @param owner the owner of the alert.
     * @return the response of the alert.
     */
    @SuppressWarnings("UnusedReturnValue")
    public static Optional<ButtonType> showAlert(Alert.AlertType type, String header, String content, Stage owner) {
        var alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(owner);
        return alert.showAndWait();
    }

    /**
     * Show and wait for an alert with the specified details.
     * @param type the type on the alert.
     * @param header the header of the alert.
     * @param content the text of the alert.
     * @param owner the owner of the alert.
     * @param buttonTypes the buttons to be displayed on the alert.
     * @return the response of the alert.
     */
    public static Optional<ButtonType> showAlert(Alert.AlertType type, String header, String content, Stage owner, ButtonType... buttonTypes) {
        var alert = new Alert(type,content, buttonTypes);
        alert.setHeaderText(header);
        alert.initOwner(owner);
        return alert.showAndWait();
    }

    /**
     * Show and wait for an input dialog with the specified details.
     * @param title the title of the dialog.
     * @param header the header of the dialog.
     * @param content the content of the dialog.
     */
    public static Optional<String> showInputDialog(String title, String header, String content) {
        var dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }
}
