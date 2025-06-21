package project.proslave;

import Database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Login {

    @FXML private Label errorLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private static String adminUsername;
    private static String clientUsername;
    private static String ownerUsername;


    public void loginButton(ActionEvent event){
        String username = usernameField.getText();
        String password = passwordField.getText();

        errorLabel.setTextFill(Color.RED);

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            errorLabel.setTextFill(Color.RED);
        } else {
            if (authenticateUser(username, password)) {
                try {
                    FXMLLoader loader;
                    if (isUserAdmin(username)) {
                        adminUsername = username;
                        loader = new FXMLLoader(getClass().getResource("admin_dashboard.fxml"));
                    } else if (isUserClient(username)){
                        clientUsername = username;
                        loader = new FXMLLoader(getClass().getResource("client_dashboard.fxml"));
                    } else {
                        ownerUsername = username;
                        loader = new FXMLLoader(getClass().getResource("owner_dashboard.fxml"));
                    }
                    Parent root = loader.load();
                    Scene newScene = new Scene(root);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(newScene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                errorLabel.setText("Invalid username or password.");
                errorLabel.setTextFill(Color.RED);
            }
        }
    }

    private boolean authenticateUser(String username, String password) {
        if (isUserAdmin(username)) {
            return Database.admins.stream().anyMatch(admin -> admin.getKorisnickoIme().equals(username) && admin.getLozinka().equals(password));
        }else if (isUserClient(username)){
            return Database.clients.stream().anyMatch(client -> client.getKorisnickoIme().equals(username) && client.getLozinka().equals(password));
        }else{
            return Database.owners.stream().anyMatch(owner -> owner.getKorisnickoIme().equals(username) && owner.getLozinka().equals(password));
        }
    }

    private boolean isUserAdmin(String username) {
        return Database.admins.stream().anyMatch(admin -> admin.getKorisnickoIme().equals(username));
    }
    private boolean isUserClient(String username) {
        return Database.clients.stream().anyMatch(client -> client.getKorisnickoIme().equals(username));
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
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("register.fxml"))));
        stage.show();
    }

    public void registerText(MouseEvent event) throws IOException{
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("register.fxml"))));
        stage.show();
    }

}
