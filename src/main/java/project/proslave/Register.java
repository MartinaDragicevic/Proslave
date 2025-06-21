package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.BankAccount;
import SistemZaPlaniranjeProslava.Client;
import SistemZaPlaniranjeProslava.Owner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Register {

    @FXML private TextField usernameField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField bankAccountField;
    @FXML private TextField jmbgField;
    @FXML private TextField phoneNumberField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private javafx.scene.control.Button clientButton;
    @FXML private javafx.scene.control.Button ownerButton;


    public void createAccount(ActionEvent event){
        String username = usernameField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String bankAccount = bankAccountField.getText();
        String jmbg = jmbgField.getText().trim();
        String phoneNumber = phoneNumberField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        errorLabel.setTextFill(Color.RED);

        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || bankAccount.isEmpty() || jmbg.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            errorLabel.setText("Please enter all fields."); return;
        }else if (isUsernameTaken(username)){
            errorLabel.setText("Username already exist. Please choose another one."); return;
        }else if (!password.equals(confirmPassword)){
            errorLabel.setText("Passwords do not match."); return;
        }

        boolean bankAccountExists = false;
        boolean jmbgMatches = false;
        for (BankAccount account : Database.bankAccounts) {
            if (account.getBrojRacuna().equals(bankAccount)) {
                bankAccountExists = true;
                if (account.getJmbg().equals(jmbg)) {
                    jmbgMatches = true;
                }
                break;
            }
        }
        if (!bankAccountExists) {
            errorLabel.setText("Bank account does not exist."); return;
        } else if (!jmbgMatches) {
            errorLabel.setText("JMBG does not match the bank account."); return;
        }

        String userType = (event.getSource() == clientButton) ? "klijent" : "vlasnik";
        createNewAccount(firstName, lastName, jmbg, bankAccount, username, password, userType);

        try{
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("login.fxml"))));
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private boolean isUsernameTaken(String username){
        for (Client client : Database.clients){
            if (client.getKorisnickoIme().equals(username)){
                return true;
            }
        }
        for (Owner owner : Database.owners){
            if (owner.getKorisnickoIme().equals(username)){
                return true;
            }
        }
        return false;
    }

    public void createNewAccount(String firstName, String lastName, String jmbg, String bankAccount, String username, String password, String userType) {
        int id;
        String tableName;
        if (userType.equals("klijent")) {
            id = Database.clients.size() + 1;
            tableName = "klijent";
            Database.addUserToDatabase(id, firstName, lastName, jmbg, bankAccount, username, password, tableName, userType);
        } else if (userType.equals("vlasnik")) {
            id = Database.owners.size() + 1;
            tableName = "vlasnik";
            Database.addUserToDatabase(id, firstName, lastName, jmbg, bankAccount, username, password, tableName, userType);
        }
    }

    public void backToLoginText(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("login.fxml"))));
        stage.show();
    }

}