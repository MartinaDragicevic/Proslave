package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Venue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class ClientVenueList implements Initializable {
    @FXML private TextArea venueTextArea;
    @FXML private TextField cityFilter;
    @FXML private DatePicker dateFilter;
    @FXML private TextField seatsFilter;
    public static int selectedVenueId = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshVenueTextArea();
        venueTextArea.setOnMouseClicked(this::handleVenueSelection);
    }

    private void refreshVenueTextArea() {
        StringBuilder venuesText = new StringBuilder();

        List<Venue> sortedVenues = new ArrayList<>();
        for (Venue venue : Database.venues) {
            if (venue.getStatus().equals("ODOBREN")) {
                sortedVenues.add(venue);
            }
        }
        sortedVenues.sort(Comparator.comparing(Venue::getNaziv, String.CASE_INSENSITIVE_ORDER));

        if (sortedVenues.isEmpty()) {
            venuesText.append("No available venues.");
        } else {
            for (Venue venue : sortedVenues) {
                venuesText.append("ID: ").append(venue.getId())
                        .append(", Name: ").append(venue.getNaziv())
                        .append(", City: ").append(venue.getGrad())
                        .append("\n");
            }
        }
        venueTextArea.setText(venuesText.toString());
    }

    public void searchFilters() {
        String city = cityFilter.getText().trim().toLowerCase();
        LocalDate date = dateFilter.getValue();
        String seatsText = seatsFilter.getText().trim();
        int seats = -1;

        if (!seatsText.isEmpty()) {
            try {
                seats = Integer.parseInt(seatsText);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Number of seats must be digit.");
                alert.showAndWait();
                return;
            }
        }

        StringBuilder venuesText = new StringBuilder();
        boolean anyMatch = false;

        for (Venue venue : Database.venues) {
            if (!venue.getStatus().equals("ODOBREN"))
                continue;

            boolean cityMatch = city.isEmpty() || venue.getGrad().toLowerCase().contains(city);
            boolean seatsMatch = (seats == -1) || (venue.getBrojMjesta() >= seats);

            boolean dateMatch = true;
            if (date != null) {
                dateMatch = venue.getDatumi() != null && venue.getDatumi().contains(date);
            }

            if (cityMatch && seatsMatch && dateMatch) {
                venuesText.append("ID: ").append(venue.getId())
                        .append(", Name: ").append(venue.getNaziv())
                        .append(", City: ").append(venue.getGrad())
                        .append(", Max Seats: ").append(venue.getBrojMjesta())
                        .append("\n");
                anyMatch = true;
            }
        }

        if (!anyMatch) {
            venuesText.append("There is no venue that matches these filters.");
        }

        venueTextArea.setText(venuesText.toString());
    }

    private void handleVenueSelection(MouseEvent event) {
        String selectedText = venueTextArea.getSelectedText();

        if (selectedText != null && selectedText.contains("ID:")) {
            try {
                int idStart = selectedText.indexOf("ID:") + 4;
                int idEnd = selectedText.indexOf(",", idStart);
                selectedVenueId = Integer.parseInt(selectedText.substring(idStart, idEnd));

                FXMLLoader loader = new FXMLLoader(getClass().getResource("client_celebrationReservation.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                stage.show();
            } catch (Exception e) {
                System.out.println("Greška pri parsiranju ID-a.");
            }
        }
    }

    public void deleteFilters() {
        cityFilter.clear();
        dateFilter.setValue(null);
        seatsFilter.clear();
        refreshVenueTextArea();
    }

    public void backToDashboard(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_dashboard.fxml"))));
        stage.show();
    }
}