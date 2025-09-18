package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Client;
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

public class ClientDashboard {

    @FXML private TextField confirmPassword;
    @FXML private TextField currentPassword;
    @FXML private Label errorLabel;
    @FXML private TextField newPassword;
    @FXML private Label fullName;
    @FXML private Label username;
    @FXML private Label accountBalance;

    public void initialize() {
        String loggedInUser = Login.getClientUsername();
        if (loggedInUser != null && Database.clients != null) {
            username.setText(loggedInUser);
            for (Client client : Database.clients) {
                if (client != null && client.getKorisnickoIme().equals(loggedInUser)) {
                    fullName.setText(client.getIme() + " " + client.getPrezime());

                    try {
                        double balance = Database.getAccountBalance(client.getBrojRacuna());
                        accountBalance.setText(String.format("$%.2f", balance));
                    } catch (Exception e) {
                        accountBalance.setText("$0.00");
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } else {
            fullName.setText("Nepoznat korisnik");
            accountBalance.setText("$0.00");
        }
    }

    @FXML
    private void changePassword() {
        String current = currentPassword.getText().trim();
        String newPass = newPassword.getText().trim();
        String confirm = confirmPassword.getText().trim();
        String username = Login.getClientUsername();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showMessage("Please enter all fields.", Color.RED, 4);
            return;
        }else if (!newPass.equals(confirm)) {
            showMessage("Passwords do not match.", Color.RED, 4);
            return;
        }else if (!Database.checkCurrentPassword(username, current)) {
            showMessage("Incorrect current password.", Color.RED, 4);
            return;
        }

        try {
            Database.changePassword(newPass, username, "klijent");
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
    public void venueList(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_venueList.fxml"))));
        stage.show();
    }
    public void editVenue(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_reservedVenues.fxml"))));
        stage.show();
    }
}
