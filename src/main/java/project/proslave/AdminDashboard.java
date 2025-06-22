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

    /**
     *
     * Get the currently logged-in admin's username from the database, and use it for verification the password before updating.
     *
     * Display information (name, last name and username) for currently logged-in admin.
     *
     **/

    public void initialize() {
        String loggedInUser = Login.getAdminUsername();
        if (loggedInUser != null) {
            username.setText(loggedInUser);
            for (Admin admin : Database.admins) {
                if (admin.getKorisnickoIme().equals(loggedInUser)) {
                    fullName.setText(admin.getIme() + " " + admin.getPrezime()); break;
                }
            }
        }
    }

    @FXML
    private void changePassword(){
        String current = currentPassword.getText().trim();
        String newPass = newPassword.getText().trim();
        String confirm = confirmPassword.getText().trim();
        String username = Login.getAdminUsername();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()){
            showMessage("Please enter all fields.", Color.RED, 4); return;
        }else if (!newPass.equals(confirm)){
            showMessage("Passwords do not match.", Color.RED, 4); return;
        }else if (!Database.checkCurrentPassword(username, current)) {
            showMessage("Incorrect current password.", Color.RED, 4); return;
        }

        try {
            Database.changePassword(newPass, username, "admin");
            currentPassword.clear();
            newPassword.clear();
            confirmPassword.clear();
            showMessage("Password changed successfully!", Color.GREEN, 4);
        } catch (Exception e) {
            showMessage("Error changing password: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"), Color.RED, 4);
            e.printStackTrace();
        }

    }

    private void showMessage(String message, Color color, double seconds) {
        errorLabel.setText(message);
        errorLabel.setTextFill(color);

        PauseTransition delay = new PauseTransition(Duration.seconds(seconds));
        delay.setOnFinished(event -> errorLabel.setText(""));
        delay.play();
    }

    public void logout(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("login.fxml"))));
        stage.show();
    }

    public void venueRequests(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("admin_venueList.fxml"))));
        stage.show();
    }
}