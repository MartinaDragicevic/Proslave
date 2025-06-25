package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Menu;
import SistemZaPlaniranjeProslava.Venue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientCelebrationReservation implements Initializable {
    @FXML private TextArea venueTextArea;
    @FXML private ChoiceBox<String> menuChoiceBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshVenueTextArea();
        handleVenueMenu();
    }

    private void refreshVenueTextArea() {
        int selectedId = ClientVenueList.selectedVenueId;

        for (Venue venue : Database.venues) {
            if (venue.getId() == selectedId) {
                StringBuilder venuesText = new StringBuilder();
                venuesText.append("Naziv: ").append(venue.getName()).append("\n")
                        .append("Grad: ").append(venue.getPlace()).append("\n")
                        .append("Adresa: ").append(venue.getAddress()).append("\n")
                        .append("Kapacitet: ").append(venue.getCapacity()).append("\n")
                        .append("Broj stolova: ").append(venue.getBrojStolova()).append("\n")
                        .append("Cijena: ").append(venue.getReservationPrice()).append("\n")
                        .append("Dostupni datumi: ").append(venue.getDatumi());
                venueTextArea.setText(venuesText.toString());
                return;
            }
        }

        venueTextArea.setText("Greška: objekat nije pronađen.");
    }

    private void handleVenueMenu() {
        int selectedId = ClientVenueList.selectedVenueId;
        menuChoiceBox.getItems().clear();

        for (Menu menu : Database.menus) {
            if (menu.getObjekatId() == selectedId) {
                String menuEntry = "Opis: " + menu.getDescription() + " | Cijena: " + menu.getPrice() + " KM";
                menuChoiceBox.getItems().add(menuEntry);
            }
        }

        if (menuChoiceBox.getItems().isEmpty()) {
            menuChoiceBox.getItems().add("Nema menija za ovaj objekat.");
            menuChoiceBox.setDisable(true);
        } else {
            menuChoiceBox.setDisable(false);
            menuChoiceBox.getSelectionModel().selectFirst();
        }
    }

    public void backToVenueList(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_venueList.fxml"))));
        stage.show();
    }
}