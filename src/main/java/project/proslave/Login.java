package project.proslave;

import Database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Login {

    @FXML private Label errorLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private static String adminUsername;
    private static String clientUsername;
    private static String ownerUsername;

    public void loginButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        errorLabel.setTextFill(Color.RED);

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }else if (!authenticateUser(username, password)) {
            showError("Invalid username or password.");
            return;
        }

        try {
            loadDashboard(event, username);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading dashboard.");
        }
    }

    private boolean authenticateUser(String username, String password) {
        if (isUserAdmin(username)) {
            return Database.admins.stream().anyMatch(a -> a.getKorisnickoIme().equals(username) && a.getLozinka().equals(password));
        }else if (isUserClient(username)) {
            return Database.clients.stream().anyMatch(c -> c.getKorisnickoIme().equals(username) && c.getLozinka().equals(password));
        }else{
            return Database.owners.stream().anyMatch(o -> o.getKorisnickoIme().equals(username) && o.getLozinka().equals(password));
        }
    }

    private void loadDashboard(ActionEvent event, String username) throws IOException {
        FXMLLoader loader;
        if (isUserAdmin(username)) {
            adminUsername = username;
            loader = new FXMLLoader(getClass().getResource("admin_dashboard.fxml"));
        } else if (isUserClient(username)) {
            clientUsername = username;
            loader = new FXMLLoader(getClass().getResource("client_dashboard.fxml"));
        } else {
            ownerUsername = username;
            loader = new FXMLLoader(getClass().getResource("owner_dashboard.fxml"));
        }
        changeScene(event, loader.load());
    }

    private void changeScene(ActionEvent event, Parent root) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showError(String message) {
        errorLabel.setTextFill(Color.RED);
        errorLabel.setText(message);
    }

    private boolean isUserAdmin(String username) {
        return Database.admins.stream().anyMatch(a -> a.getKorisnickoIme().equals(username));
    }
    private boolean isUserClient(String username) {
        return Database.clients.stream().anyMatch(c -> c.getKorisnickoIme().equals(username));
    }

    public static String getAdminUsername() {
        return adminUsername;
    }
    public static String getClientUsername() {
        return clientUsername;
    }
    public static String getOwnerUsername() {
        return ownerUsername;
    }

    public void registerButton(ActionEvent event) throws IOException {
        switchToRegister(event);
    }
    public void registerText(MouseEvent event) throws IOException {
        switchToRegister(event);
    }

    private void switchToRegister(Object eventSource) throws IOException {
        Stage stage = (Stage) ((Node) eventSource).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("register.fxml"))));
        stage.show();
    }
}
