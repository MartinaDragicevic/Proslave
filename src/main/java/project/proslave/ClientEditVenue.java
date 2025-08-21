package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Menu;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientEditVenue implements Initializable {
    @FXML private ChoiceBox<String> menuChoiceBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handleVenueMenu();
    }

    private void handleVenueMenu() {
        int selectedId = ClientVenueList.selectedVenueId;
        menuChoiceBox.getItems().clear();

        for (Menu menu : Database.menus) {
            if (menu.getObjekat() != null && menu.getObjekat().getId() == selectedId) {
                String menuEntry = "Description: " + menu.getOpis() + " | Price: " + menu.getCijenaPoOsobi() + " KM";
                menuChoiceBox.getItems().add(menuEntry);
            }
        }

        if (menuChoiceBox.getItems().isEmpty()) {
            menuChoiceBox.getItems().add("No menu for this venue.");
            menuChoiceBox.setDisable(true);
        } else {
            menuChoiceBox.setDisable(false);
            menuChoiceBox.getSelectionModel().selectFirst();
        }
    }

    public void backToDashboard(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_reservedVenues.fxml"))));
        stage.show();
    }
}