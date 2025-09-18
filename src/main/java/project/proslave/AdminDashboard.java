package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Admin;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class AdminDashboard {

    @FXML private TextField confirmPassword;
    @FXML private TextField currentPassword;
    @FXML private TextField newPassword;
    @FXML private Label errorLabel;
    @FXML private Label fullName;
    @FXML private Label username;

    public void initialize() {
        String loggedInUser = Login.getAdminUsername();

        username.setText(loggedInUser);
        Admin matchedAdmin = Database.admins.stream()
                .filter(a -> a.getKorisnickoIme().equals(loggedInUser))
                .findFirst()
                .orElse(null);

        if (matchedAdmin != null) {
            fullName.setText(matchedAdmin.getIme() + " " + matchedAdmin.getPrezime());
        }
    }

    @FXML
    private void changePassword() {
        String current = currentPassword.getText().trim();
        String newPass = newPassword.getText().trim();
        String confirm = confirmPassword.getText().trim();
        String username = Login.getAdminUsername();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showMessage("Please enter all fields.", Color.RED, 4); return;
        }else if (!newPass.equals(confirm)) {
            showMessage("Passwords do not match.", Color.RED, 4); return;
        }else if (!Database.checkCurrentPassword(username, current)) {
            showMessage("Incorrect current password.", Color.RED, 4); return;
        }

        try {
            Database.changePassword(newPass, username, "admin");
            clearPasswordFields();
            showMessage("Password changed successfully!", Color.GREEN, 4);
        } catch (Exception e) {
            String error = (e.getMessage() != null) ? e.getMessage() : "Unknown error";
            showMessage("Error changing password: " + error, Color.RED, 4);
            e.printStackTrace();
        }
    }

    private void clearPasswordFields() {
        currentPassword.clear();
        newPassword.clear();
        confirmPassword.clear();
    }

    private void showMessage(String message, Color color, double seconds) {
        errorLabel.setText(message);
        errorLabel.setTextFill(color);

        PauseTransition delay = new PauseTransition(Duration.seconds(seconds));
        delay.setOnFinished(e -> errorLabel.setText(""));
        delay.play();
    }

    public void logout(ActionEvent event) throws IOException {
        switchScene(event, "login.fxml");
    }

    public void venueRequests(ActionEvent event) throws IOException {
        switchScene(event, "admin_venueList.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        Scene newScene = new Scene(FXMLLoader.load(getClass().getResource(fxmlFile)));
        stage.setScene(newScene);
        stage.show();
    }
}
