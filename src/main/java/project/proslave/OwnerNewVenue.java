package project.proslave;

import Database.Database;
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

public class OwnerNewVenue {

    @FXML private TextField venueName;
    @FXML private TextField venueAddress;
    @FXML private TextField venuePlace;
    @FXML private TextField maxSeatsNumber;
    @FXML private TextField seatsPerTable;
    @FXML private TextField tablesNumber;
    @FXML private TextField reservationPrice;
    @FXML private TextArea menuTextArea;

    private int currentVlasnikId = 1;

    public void addNewVenue() {
        String name = venueName.getText().trim();
        String address = venueAddress.getText().trim();
        String place = venuePlace.getText().trim();
        String maxSeats = maxSeatsNumber.getText().trim();
        String seatsPerTableStr = seatsPerTable.getText().trim();
        String tables = tablesNumber.getText().trim();
        String price = reservationPrice.getText().trim();
        String menuInput = menuTextArea.getText().trim();

        if (name.isEmpty() || address.isEmpty() || place.isEmpty() || maxSeats.isEmpty() || seatsPerTableStr.isEmpty() || tables.isEmpty() || price.isEmpty() || menuInput.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill out all fields!"); return;
        }else if (!isPositiveInteger(maxSeats) || !isPositiveInteger(seatsPerTableStr) || !isPositiveInteger(tables) || !isPositiveDecimal(price)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ensure numeric fields contain positive numbers."); return;
        }

        int maxSeatsInt = Integer.parseInt(maxSeats);
        int seatsPerTableInt = Integer.parseInt(seatsPerTableStr);
        int tablesInt = Integer.parseInt(tables);
        double priceDouble = Double.parseDouble(price);

        if (maxSeatsInt < seatsPerTableInt * tablesInt) {
            showAlert(Alert.AlertType.ERROR, "Error", "Total seats cannot be less than seats per table times number of tables.");
            return;
        }

        String[] menuItems = menuInput.split(";");
        for (String menuItem : menuItems) {
            String[] delovi = menuItem.trim().split(":");
            if (delovi.length != 2 || !isPositiveDecimal(delovi[1].trim())) {
                showAlert(Alert.AlertType.ERROR, "Greška", "Neispravan format menija. Koristite 'opis:cjena;opis:cjena'.");
                return;
            }
        }

        /*try {
            Database.addNewVenueToDatabase(int currentVlasnikId, String name, double priceDouble, String place, String address, int maxSeatsInt, int tablesInt, int seatsPerTableInt, menuItems);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Venue added successfully!");
            venueName.clear();
            venueAddress.clear();
            venuePlace.clear();
            maxSeatsNumber.clear();
            seatsPerTable.clear();
            tablesNumber.clear();
            reservationPrice.clear();
            menuTextArea.clear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add venue: " + e.getMessage());
        }*/
    }

    private boolean isPositiveInteger(String str) {
        try {
            int value = Integer.parseInt(str);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isPositiveDecimal(String str) {
        try {
            double value = Double.parseDouble(str);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void backToDashboard(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("owner_dashboard.fxml"))));
        stage.show();
    }
}