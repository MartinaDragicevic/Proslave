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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceBox;
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
        refreshVenueTextArea();
        handleVenueMenu();
        confirmReservation.setOnAction(event -> handleConfirmReservation());
    }

    private void refreshVenueTextArea() {
        int selectedId = ClientVenueList.selectedVenueId;

        for (Venue venue : Database.venues) {
            if (venue.getId() == selectedId) {
                StringBuilder venuesText = new StringBuilder();
                venuesText.append("Name: ").append(venue.getNaziv()).append("\n")
                        .append("City: ").append(venue.getGrad()).append("\n")
                        .append("Address: ").append(venue.getAdresa()).append("\n")
                        .append("Max Seats: ").append(venue.getBrojMjesta()).append("\n")
                        .append("Table number: ").append(venue.getBrojStolova()).append("\n")
                        .append("Price: ").append(venue.getCijenaRezervacije()).append("\n")
                        .append("Available dates: ").append(venue.getDatumi() != null ?
                                "Booked dates: " + venue.getDatumi().stream().map(LocalDate::toString).collect(Collectors.joining(", ")) : "All dates available");
                venueTextArea.setText(venuesText.toString());
                return;
            }
        }

        venueTextArea.setText("Error: can't find venue.");
    }

    private void handleVenueMenu() {
        int selectedId = ClientVenueList.selectedVenueId;
        menuChoiceBox.getItems().clear();

        // Pronađi odabrani Venue
        Venue selectedVenue = Database.venues.stream()
                .filter(v -> v.getId() == selectedId)
                .findFirst().orElse(null);

        if (selectedVenue != null) {
            // Pronađi sve menije povezane s ovim Venue-om
            for (Menu menu : Database.menus) {
                if (menu.getObjekat() != null && menu.getObjekat().getId() == selectedId) {
                    String menuEntry = "Description: " + menu.getOpis() + " | Price: " + menu.getCijenaPoOsobi() + " KM";
                    menuChoiceBox.getItems().add(menuEntry);
                }
            }

            // Osiguraj da je ChoiceBox omogućen samo ako postoji barem jedan meni
            if (menuChoiceBox.getItems().isEmpty()) {
                menuChoiceBox.setDisable(true);
                menuChoiceBox.getItems().add("Error: No menus found for this venue."); // Sigurnosna mjera
            } else {
                menuChoiceBox.setDisable(false);
                menuChoiceBox.getSelectionModel().selectFirst(); // Automatski odaberi prvi meni
            }
        } else {
            menuChoiceBox.setDisable(true);
            menuChoiceBox.getItems().add("Error: Venue not found.");
        }
    }

    public void backToVenueList(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_venueList.fxml"))));
        stage.show();
    }

    public void handleConfirmReservation() {
        LocalDate selectedDate = celebrationDatePicker.getValue();
        int selectedVenueId = ClientVenueList.selectedVenueId;
        String selectedMenu = menuChoiceBox.getSelectionModel().getSelectedItem();

        if (selectedDate == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a celebration date.");
            return;
        }

        Venue venue = Database.venues.stream().filter(v -> v.getId() == selectedVenueId).findFirst().orElse(null);
        if (venue == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Venue not found.");
            return;
        }

        // Provjera dostupnosti datuma
        if (venue.getDatumi() != null && venue.getDatumi().contains(selectedDate)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Selected date is already booked.");
            return;
        }

        if (selectedMenu == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a menu.");
            return;
        }

        // Pronađi ulogovanog klijenta
        String loggedInUsername = Login.getClientUsername();
        Client client = Database.clients.stream()
                .filter(c -> c.getKorisnickoIme().equals(loggedInUsername))
                .findFirst().orElse(null);
        if (client == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No client data available for the logged-in user.");
            return;
        }

        // Pronađi meni
        Menu menu = Database.menus.stream()
                .filter(m -> m.getObjekat() != null && m.getObjekat().getId() == selectedVenueId
                        && ("Description: " + m.getOpis() + " | Price: " + m.getCijenaPoOsobi() + " KM").equals(selectedMenu))
                .findFirst().orElse(null);
        if (menu == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Selected menu not found.");
            return;
        }

        // Kreiraj novu rezervaciju
        Celebration celebration = new Celebration(
                0, // ID će biti generiran u bazi
                venue,
                client,
                menu,
                "Default Celebration", // Placeholder za proslavacol
                selectedDate,
                10, // Placeholder za broj gostiju
                venue.getCijenaRezervacije() + (menu.getCijenaPoOsobi() * 10), // Proračun ukupne cijene
                0.0 // Placeholder za uplaćeni iznos
        );

        try {
            // Spremi rezervaciju u bazu
            Database.addCelebration(celebration);
            // Ažuriraj datume u bazi i lokalni Venue objekt
            updateVenueDates(selectedVenueId, selectedDate, venue);
            // Dohvati trenutni Stage i vratiti se na listu lokacija
            Stage stage = (Stage) confirmReservation.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_venueList.fxml"))));
            stage.show();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to confirm reservation: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
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