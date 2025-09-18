package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Menu;
import SistemZaPlaniranjeProslava.Owner;
import SistemZaPlaniranjeProslava.Venue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OwnerNewVenue {

    @FXML private TextField venueName;
    @FXML private TextField venueAddress;
    @FXML private TextField venuePlace;
    @FXML private TextField maxSeatsNumber;
    @FXML private TextField peopleNumber;
    @FXML private TextField tablesNumber;
    @FXML private TextField reservationPrice;
    @FXML private TextArea textArea;

    private int currentVlasnikId = 1;

    public void saveNewVenueToDatabase() {
        String name = venueName.getText().trim();
        String address = venueAddress.getText().trim();
        String place = venuePlace.getText().trim();
        String maxSeats = maxSeatsNumber.getText().trim();
        String seatsPerTableStr = peopleNumber.getText().trim();
        String tables = tablesNumber.getText().trim();
        String price = reservationPrice.getText().trim();
        String menuInput = textArea.getText().trim();

        if (areAnyFieldsEmpty(name, address, place, maxSeats, seatsPerTableStr, tables, price, menuInput)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill out all fields!");
            return;
        }

        if (!validatePositiveInteger(maxSeats) || !validatePositiveInteger(seatsPerTableStr)
                || !validatePositiveInteger(tables) || !validatePositiveDecimal(price)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ensure numeric fields contain positive numbers.");
            return;
        }

        int maxSeatsInt = Integer.parseInt(maxSeats);
        int seatsPerTableInt = Integer.parseInt(seatsPerTableStr);
        int tablesInt = Integer.parseInt(tables);
        double priceDouble = Double.parseDouble(price);

        if (maxSeatsInt < seatsPerTableInt * tablesInt) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Total seats must be at least " + (seatsPerTableInt * tablesInt) +
                            " (seats per table × number of tables).");
            return;
        }

        List<Menu> menus = parseMenuInput(menuInput);
        if (menus.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Please provide at least one valid menu (format: description: price).");
            return;
        }

        try {
            Venue newVenue = new Venue(
                    0,
                    new Owner(currentVlasnikId, "", "", "", "", "", ""),
                    name, priceDouble, place, address,
                    maxSeatsInt, tablesInt, new ArrayList<>(), 0.0, "NA ČEKANJU"
            );

            Database.addVenue(newVenue, tablesInt, seatsPerTableInt, menus);
            Database.venues = Database.retrieveDataFromTable("objekat", Venue.class);
            Database.menus = Database.retrieveDataFromTable("meni", Menu.class);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Venue added successfully!");
            clearFields();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add venue: " + e.getMessage());
        }
    }

    private boolean areAnyFieldsEmpty(String... values) {
        for (String v : values) {
            if (v.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean validatePositiveInteger(String str) {
        try {
            return Integer.parseInt(str) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean validatePositiveDecimal(String str) {
        try {
            return Double.parseDouble(str) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private List<Menu> parseMenuInput(String input) {
        List<Menu> menus = new ArrayList<>();
        for (String line : input.replace("\r\n", "\n").split("\n")) {
            String[] parts = line.trim().split(": ", 2);
            if (parts.length == 2) {
                String description = parts[0].trim();
                String priceStr = parts[1].trim();
                try {
                    double price = Double.parseDouble(priceStr);
                    if (!description.isEmpty() && price > 0) {
                        menus.add(new Menu(0, new Venue(), description, price));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error! Wrong input: " + e.getMessage());
                }
            }
        }
        return menus;
    }

    private void clearFields() {
        venueName.clear();
        venueAddress.clear();
        venuePlace.clear();
        maxSeatsNumber.clear();
        peopleNumber.clear();
        tablesNumber.clear();
        reservationPrice.clear();
        textArea.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void navigateBackToOwnerDashboard(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("owner_dashboard.fxml"))));
        stage.show();
    }
}
