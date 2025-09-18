package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Celebration;
import SistemZaPlaniranjeProslava.Client;
import SistemZaPlaniranjeProslava.Menu;
import SistemZaPlaniranjeProslava.Venue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientCelebrationReservation implements Initializable {
    @FXML private TextArea venueTextArea;
    @FXML private Button confirmReservation;
    @FXML private ChoiceBox<String> menuChoiceBox;
    @FXML private DatePicker celebrationDatePicker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displaySelectedVenueDetails();
        handleVenueMenu();
        confirmReservation.setOnAction(event -> confirmCelebrationReservation());
    }

    private void displaySelectedVenueDetails() {
        int selectedId = ClientVenueList.selectedVenueId;

        Venue selectedVenue = Database.venues.stream()
                .filter(v -> v.getId() == selectedId)
                .findFirst().orElse(null);

        if (selectedVenue != null) {
            String availableDates = selectedVenue.getDatumi() != null && !selectedVenue.getDatumi().isEmpty()
                    ? selectedVenue.getDatumi().stream().map(LocalDate::toString).collect(Collectors.joining(", "))
                    : "All dates available";

            String venueInfo = String.format(
                    "Name: %s\nCity: %s\nAddress: %s\nMax Seats: %d\nTable number: %d\nPrice: %.2f\nAvailable dates: %s",
                    selectedVenue.getNaziv(),
                    selectedVenue.getGrad(),
                    selectedVenue.getAdresa(),
                    selectedVenue.getBrojMjesta(),
                    selectedVenue.getBrojStolova(),
                    selectedVenue.getCijenaRezervacije(),
                    availableDates
            );

            venueTextArea.setText(venueInfo);
        } else {
            venueTextArea.setText("Error: can't find venue.");
        }
    }

    private void handleVenueMenu() {
        int selectedId = ClientVenueList.selectedVenueId;
        menuChoiceBox.getItems().clear();

        Venue selectedVenue = Database.venues.stream()
                .filter(v -> v.getId() == selectedId)
                .findFirst().orElse(null);

        if (selectedVenue != null) {
            Database.menus.stream()
                    .filter(m -> m.getObjekat() != null && m.getObjekat().getId() == selectedId)
                    .forEach(m -> menuChoiceBox.getItems().add(
                            "Description: " + m.getOpis() + " | Price: " + m.getCijenaPoOsobi() + " KM"
                    ));

            if (menuChoiceBox.getItems().isEmpty()) {
                menuChoiceBox.setDisable(true);
                menuChoiceBox.getItems().add("Error: No menus found for this venue.");
            } else {
                menuChoiceBox.setDisable(false);
                menuChoiceBox.getSelectionModel().selectFirst();
            }
        } else {
            menuChoiceBox.setDisable(true);
            menuChoiceBox.getItems().add("Error: Venue not found.");
        }
    }

    public void navigateToVenueList(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_venueList.fxml"))));
        stage.show();
    }

    public void confirmCelebrationReservation() {
        LocalDate selectedDate = celebrationDatePicker.getValue();
        int selectedVenueId = ClientVenueList.selectedVenueId;
        String selectedMenu = menuChoiceBox.getSelectionModel().getSelectedItem();

        if (selectedDate == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a celebration date.");
            return;
        }

        Venue venue = Database.venues.stream()
                .filter(v -> v.getId() == selectedVenueId)
                .findFirst().orElse(null);

        if (venue == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Venue not found.");
            return;
        }

        if (venue.getDatumi() != null && venue.getDatumi().contains(selectedDate)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Selected date is already booked.");
            return;
        }

        if (selectedMenu == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a menu.");
            return;
        }

        Client client = Database.clients.stream()
                .filter(c -> c.getKorisnickoIme().equals(Login.getClientUsername()))
                .findFirst().orElse(null);

        if (client == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No client data available for the logged-in user.");
            return;
        }

        Menu menu = Database.menus.stream()
                .filter(m -> m.getObjekat() != null && m.getObjekat().getId() == selectedVenueId
                        && ("Description: " + m.getOpis() + " | Price: " + m.getCijenaPoOsobi() + " KM").equals(selectedMenu))
                .findFirst().orElse(null);

        if (menu == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Selected menu not found.");
            return;
        }

        double reservationPrice = venue.getCijenaRezervacije();
        double clientBalance = Database.getAccountBalance(client.getBrojRacuna());
        if (clientBalance < reservationPrice) {
            showAlert(Alert.AlertType.ERROR, "Error", "Insufficient funds. Required: $" + reservationPrice);
            return;
        }

        try {
            double ownerBalance = Database.getAccountBalance(venue.getVlasnik().getBrojRacuna());

            Database.updateClientBalance(client.getBrojRacuna(), clientBalance - reservationPrice);
            Database.updateClientBalance(venue.getVlasnik().getBrojRacuna(), ownerBalance + reservationPrice);

            Celebration celebration = new Celebration(
                    0,
                    venue,
                    client,
                    menu,
                    "Celebration",
                    selectedDate,
                    10,
                    reservationPrice + (menu.getCijenaPoOsobi() * 10),
                    reservationPrice
            );

            Database.addCelebration(celebration);
            updateVenueDates(selectedVenueId, selectedDate, venue);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Reservation confirmed. New balance: $" + (clientBalance - reservationPrice));

            Stage stage = (Stage) confirmReservation.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_venueList.fxml"))));
            stage.show();
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to confirm reservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateVenueDates(int venueId, LocalDate date, Venue venue) throws SQLException {
        if (venue != null) {
            List<LocalDate> datumi = venue.getDatumi() != null ? new ArrayList<>(venue.getDatumi()) : new ArrayList<>();
            datumi.add(date);
            venue.setDatumi(datumi);
            String datumiString = String.join(",", datumi.stream().map(LocalDate::toString).toArray(String[]::new));
            Database.updateVenueDates(venueId, datumiString);
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
