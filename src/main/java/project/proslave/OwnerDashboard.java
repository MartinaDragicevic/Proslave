package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Owner;
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

public class OwnerDashboard {
    @FXML private TextField confirmPassword;
    @FXML private TextField currentPassword;
    @FXML private Label errorLabel;
    @FXML private TextField newPassword;
    @FXML private Label fullName;
    @FXML private Label username;
    @FXML private Label accountBalance;

    public void initialize() {
        String loggedInUser = Login.getOwnerUsername();
        if (loggedInUser != null) {
            username.setText(loggedInUser);
            for (Owner owner : Database.owners) {
                if (owner.getKorisnickoIme().equals(loggedInUser)) {
                    fullName.setText(owner.getIme() + " " + owner.getPrezime());

                    double balance = Database.getAccountBalance(owner.getBrojRacuna());
                    accountBalance.setText(String.format("$%.2f", balance));
                }
            }
        }
    }

    @FXML
    private void changePassword(){
        String current = currentPassword.getText().trim();
        String newPass = newPassword.getText().trim();
        String confirm = confirmPassword.getText().trim();
        String username = Login.getOwnerUsername();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()){
            showMessage("Please enter all fields.", Color.RED, 4); return;
        }else if (!newPass.equals(confirm)){
            showMessage("Passwords do not match.", Color.RED, 4); return;
        }else if (!Database.checkCurrentPassword(username, current)) {
            showMessage("Incorrect current password.", Color.RED, 4); return;
        }

        try {
            Database.changePassword(newPass, username, "vlasnik");
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
    public void addNewVenue(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("owner_newVenue.fxml"))));
        stage.show();
    }
    public void venueList(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("owner_venueList.fxml"))));
        stage.show();
    }
}