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
    private int selectedVenueId = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Database.loadNotifications();
        refreshVenueTextArea();
    }

    private void refreshVenueTextArea() {
        StringBuilder venuesText = new StringBuilder();

        List<Venue> sortedVenues = new ArrayList<>();
        for (Venue venue : Database.venues) {
            if (venue.getStatus().equals("ODOBREN")) {
                sortedVenues.add(venue);
            }
        }
        sortedVenues.sort(Comparator.comparing(Venue::getName, String.CASE_INSENSITIVE_ORDER));

        if (sortedVenues.isEmpty()) {
            venuesText.append("Nema dostupnih objekata.");
        } else {
            for (Venue venue : sortedVenues) {
                venuesText.append("ID: ").append(venue.getId())
                        .append(", Naziv: ").append(venue.getName())
                        .append(", Grad: ").append(venue.getPlace())
                        .append(", Adresa: ").append(venue.getAddress())
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
                alert.setTitle("Greška");
                alert.setHeaderText(null);
                alert.setContentText("Broj mesta mora biti ceo broj.");
                alert.showAndWait();
                return;
            }
        }

        StringBuilder venuesText = new StringBuilder();
        boolean anyMatch = false;

        for (Venue venue : Database.venues) {
            if (!venue.getStatus().equals("ODOBREN"))
                continue;

            boolean cityMatch = city.isEmpty() || venue.getPlace().toLowerCase().contains(city);
            boolean seatsMatch = (seats == -1) || (venue.getCapacity() >= seats);

            boolean dateMatch = true;
            if (date != null) {
                String venueDates = venue.getDatumi();
                if (venueDates == null || !venueDates.contains(date.toString())) {
                    dateMatch = false;
                }
            }

            if (cityMatch && seatsMatch && dateMatch) {
                venuesText.append("ID: ").append(venue.getId())
                        .append(", Naziv: ").append(venue.getName())
                        .append(", Grad: ").append(venue.getPlace())
                        .append(", Adresa: ").append(venue.getAddress())
                        .append(", Kapacitet: ").append(venue.getCapacity())
                        .append(", Dostupni datumi: ").append(venue.getDatumi())
                        .append("\n");
                anyMatch = true;
            }
        }

        if (!anyMatch) {
            venuesText.append("Nema objekata koji odgovaraju zadatim filterima.");
        }

        venueTextArea.setText(venuesText.toString());
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