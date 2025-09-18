package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Celebration;
import SistemZaPlaniranjeProslava.Client;
import SistemZaPlaniranjeProslava.Menu;
import SistemZaPlaniranjeProslava.Venue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ClientEditVenue implements Initializable {
    @FXML private ChoiceBox<String> menuChoiceBox;
    @FXML private Button cancelReservation;
    @FXML private Button saveChanges;
    @FXML private Text venueName;

    private Celebration selectedCelebration;
    private Client currentClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (selectedCelebration != null && selectedCelebration.getObjekat() != null) {
            handleVenueMenu();
            venueName.setText(selectedCelebration.getObjekat().getNaziv());
        } else {
            venueName.setText("Nema dostupnih podataka o objektu");
        }

        cancelReservation.setOnAction(event -> cancelVenueReservation());
        saveChanges.setOnAction(event -> saveVenueChanges());
    }

    public void setCelebration(Celebration celebration) {
        this.selectedCelebration = celebration;
        if (celebration != null && celebration.getObjekat() != null) {
            handleVenueMenu();
            venueName.setText(celebration.getObjekat().getNaziv());
        }
    }

    public void setCurrentClient(Client client) {
        this.currentClient = client;
    }

    public void handleVenueMenu() {
        menuChoiceBox.getItems().clear();

        if (selectedCelebration == null || selectedCelebration.getObjekat() == null) {
            menuChoiceBox.getItems().add("Nema dostupnih podataka o objektu");
            menuChoiceBox.setDisable(true);
            return;
        }

        int venueId = selectedCelebration.getObjekat().getId();
        boolean menuFound = false;

        for (Menu menu : Database.menus) {
            if (menu.getObjekat() != null && menu.getObjekat().getId() == venueId) {
                menuFound = true;
                String menuEntry = "Description: " + menu.getOpis() + " | Price: " + menu.getCijenaPoOsobi() + " KM";
                menuChoiceBox.getItems().add(menuEntry);

                if (selectedCelebration.getMeni() != null
                        && menu.getOpis().equals(selectedCelebration.getMeni().getOpis())
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

    public void cancelVenueReservation() {
        if (selectedCelebration == null) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Nema odabrane proslave za otkazivanje.");
            return;
        }

        if (currentClient == null) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Klijent nije ispravno postavljen.");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate celebrationDate = selectedCelebration.getDatum();

        try {
            double clientBalance = Database.getAccountBalance(currentClient.getBrojRacuna());
            double ownerBalance = Database.getAccountBalance(selectedCelebration.getObjekat().getVlasnik().getBrojRacuna());
            double uplacenIznos = selectedCelebration.getUplacenIznos();
            String message;

            if (uplacenIznos <= 0) {
                showAlert(Alert.AlertType.ERROR, "Greška", "Uplaćeni iznos je nevažeći: " + uplacenIznos + " KM");
                return;
            }

            if (celebrationDate.isBefore(today.plusDays(3))) {
                message = "Rezervacija otkazana, ali je proslava za manje od 3 dana. Vlasnik zadržava sav uplaćeni novac (" + uplacenIznos + " KM). Stanje na računu: " + clientBalance + " KM";
            } else {
                if (ownerBalance < uplacenIznos) {
                    showAlert(Alert.AlertType.ERROR, "Greška", "Vlasnik nema dovoljno sredstava za povrat novca: " + ownerBalance + " KM dostupno, potrebno: " + uplacenIznos + " KM");
                    return;
                }
                Database.transferMoney(selectedCelebration.getObjekat().getVlasnik().getBrojRacuna(),
                        currentClient.getBrojRacuna(),
                        uplacenIznos);
                clientBalance = Database.getAccountBalance(currentClient.getBrojRacuna());
                message = "Rezervacija otkazana. Vraćeno vam je " + uplacenIznos + " KM. Stanje na računu: " + clientBalance + " KM";
            }

            Venue venue = selectedCelebration.getObjekat();
            if (venue != null && venue.getDatumi() != null) {
                List<LocalDate> datumi = new ArrayList<>(venue.getDatumi());
                datumi.remove(selectedCelebration.getDatum());
                venue.setDatumi(datumi);
                String datumiString = datumi.isEmpty() ? "" : String.join(",", datumi.stream().map(LocalDate::toString).toArray(String[]::new));
                Database.updateVenueDates(venue.getId(), datumiString);
            }

            Database.updateCelebrationProslavacol(selectedCelebration.getId(), "OTKAZANA");
            selectedCelebration.setProslavacol("OTKAZANA");

            showAlert(Alert.AlertType.INFORMATION, "Otkazivanje rezervacije", message);

            ClientReservedVenues.refreshCanceled(selectedCelebration);
            navigateToReservedVenues(null);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška prilikom otkazivanja rezervacije: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveVenueChanges() {
        if (selectedCelebration == null || menuChoiceBox.getValue() == null ||
                menuChoiceBox.getValue().equals("No menu for this venue.") ||
                menuChoiceBox.getValue().equals("Nema dostupnih podataka o objektu")) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Odaberite validan meni.");
            return;
        }

        String selectedMenuText = menuChoiceBox.getValue();
        Menu newMenu = Database.menus.stream()
                .filter(m -> m.getObjekat() != null && m.getObjekat().getId() == selectedCelebration.getObjekat().getId()
                        && ("Description: " + m.getOpis() + " | Price: " + m.getCijenaPoOsobi() + " KM").equals(selectedMenuText))
                .findFirst().orElse(null);

        if (newMenu != null) {
            try {
                Database.updateCelebrationMenu(selectedCelebration.getId(), newMenu.getId());
                selectedCelebration.setMeni(newMenu);
                showAlert(Alert.AlertType.INFORMATION, "Uspjeh", "Promjene uspješno sačuvane.");
                navigateToReservedVenues(null);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Greška", "Greška prilikom čuvanja promjena: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška: Odabrani meni nije validan.");
        }
    }

    public void navigateToReservedVenues(MouseEvent event) {
        try {
            Stage stage = (Stage) cancelReservation.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_reservedVenues.fxml"))));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška prilikom povratka na dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
