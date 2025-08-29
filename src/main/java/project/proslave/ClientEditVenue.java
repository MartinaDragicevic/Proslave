package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Celebration;
import SistemZaPlaniranjeProslava.Menu;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ClientEditVenue implements Initializable {
    @FXML private ChoiceBox<String> menuChoiceBox;
    @FXML private Button cancelReservation;
    @FXML private Button saveChanges;
    @FXML private Text venueName;

    private Celebration selectedCelebration;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (selectedCelebration != null && selectedCelebration.getObjekat() != null) {
            handleVenueMenu();
            venueName.setText(selectedCelebration.getObjekat().getNaziv());
        } else {
            venueName.setText("No venue data available");
        }
        cancelReservation.setOnAction(event -> handleCancelReservation());
        saveChanges.setOnAction(event -> handleSaveChanges());
    }

    public void setCelebration(Celebration celebration) {
        this.selectedCelebration = celebration;
        if (celebration != null && celebration.getObjekat() != null) {
            handleVenueMenu();
            venueName.setText(celebration.getObjekat().getNaziv());
        }
    }

    public void handleVenueMenu() {
        if (selectedCelebration == null || selectedCelebration.getObjekat() == null) {
            menuChoiceBox.getItems().add("No venue data available");
            menuChoiceBox.setDisable(true);
            return;
        }

        int venueId = selectedCelebration.getObjekat().getId();
        menuChoiceBox.getItems().clear();

        boolean menuFound = false;
        for (Menu menu : Database.menus) {
            if (menu.getObjekat() != null && menu.getObjekat().getId() == venueId) {
                menuFound = true;
                String menuEntry = "Description: " + menu.getOpis() + " | Price: " + menu.getCijenaPoOsobi() + " KM";
                menuChoiceBox.getItems().add(menuEntry);
                if (selectedCelebration.getMeni() != null && menu.getOpis().equals(selectedCelebration.getMeni().getOpis())
                        && menu.getCijenaPoOsobi() == selectedCelebration.getMeni().getCijenaPoOsobi()) {
                    menuChoiceBox.getSelectionModel().select(menuEntry);
                }
            }
        }

        if (!menuFound) {
            menuChoiceBox.getItems().add("No menu for this venue.");
            menuChoiceBox.setDisable(true);
        } else {
            menuChoiceBox.setDisable(false);
        }
    }

    public void handleCancelReservation() {
        if (selectedCelebration != null) {
            ClientReservedVenues.refreshCanceled(selectedCelebration);
            backToDashboard(null);
        }
    }

    public void handleSaveChanges() {
        if (selectedCelebration != null && menuChoiceBox.getValue() != null && !menuChoiceBox.getValue().equals("No menu for this venue.")
                && !menuChoiceBox.getValue().equals("No venue data available")) {
            String selectedMenuText = menuChoiceBox.getValue();
            Menu newMenu = Database.menus.stream()
                    .filter(m -> m.getObjekat() != null && m.getObjekat().getId() == selectedCelebration.getObjekat().getId()
                            && ("Description: " + m.getOpis() + " | Price: " + m.getCijenaPoOsobi() + " KM").equals(selectedMenuText))
                    .findFirst().orElse(null);
            if (newMenu != null) {
                try {
                    Database.updateCelebrationMenu(selectedCelebration.getId(), newMenu.getId());
                    selectedCelebration.setMeni(newMenu);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            backToDashboard(null);
        }
    }

    public void backToDashboard(MouseEvent event) {
        try {
            Stage stage = (Stage) (cancelReservation.getScene().getWindow());
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_reservedVenues.fxml"))));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}