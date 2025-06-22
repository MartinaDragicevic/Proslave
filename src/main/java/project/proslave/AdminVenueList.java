package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Menu;
import SistemZaPlaniranjeProslava.Venue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.scene.Cursor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminVenueList implements Initializable {
    @FXML private TextArea menuTextArea;
    @FXML private TextArea venueTextArea;
    @FXML private Button acceptButton;
    @FXML private Button declineButton;
    private int selectedVenueId = -1; // Track selected venue ID

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshVenueTextArea();

        // Set cursor to pointer and default style
        venueTextArea.setCursor(Cursor.HAND);
        venueTextArea.setStyle("-fx-text-fill: black;");

        venueTextArea.setOnMouseClicked(this::handleVenueSelection);

        // Bind button actions
        acceptButton.setOnAction(event -> handleAccept());
        declineButton.setOnAction(event -> handleDecline());
    }

    private void refreshVenueTextArea() {
        StringBuilder venuesText = new StringBuilder();
        if (Database.venues.isEmpty()) {
            venuesText.append("Nema dostupnih objekata.");
        } else {
            for (Venue venue : Database.venues) {
                venuesText.append("ID: ").append(venue.getId())
                        .append(", Naziv: ").append(venue.getName())
                        .append(", Grad: ").append(venue.getPlace())
                        .append(", Adresa: ").append(venue.getAddress())
                        .append(", Status: ").append(venue.getStatus())
                        .append("\n");
            }
        }
        venueTextArea.setText(venuesText.toString());
    }

    private void handleVenueSelection(MouseEvent event) {
        if (event.getClickCount() != 1 || event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        int caretPosition = venueTextArea.getCaretPosition();
        String text = venueTextArea.getText();
        if (text == null || text.isEmpty() || text.equals("Nema dostupnih objekata.")) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Nema dostupnih objekata.");
            venueTextArea.deselect();
            selectedVenueId = -1;
            return;
        }

        String[] lines = text.split("\n");
        int currentPos = 0;
        String selectedLine = null;
        int lineStart = 0;
        int lineEnd = 0;
        for (String line : lines) {
            lineStart = currentPos;
            lineEnd = currentPos + line.length() + 1;
            if (caretPosition >= lineStart && caretPosition <= lineEnd) {
                selectedLine = line.trim();
                break;
            }
            currentPos = lineEnd;
        }

        if (selectedLine == null || selectedLine.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Kliknite na validan objekat.");
            venueTextArea.deselect();
            selectedVenueId = -1;
            return;
        }

        try {
            String idPart = selectedLine.split(",")[0].trim();
            selectedVenueId = Integer.parseInt(idPart.replace("ID: ", "").trim());

            StringBuilder menusText = new StringBuilder();
            for (Menu menu : Database.menus) {
                if (menu.getObjekatId() == selectedVenueId) {
                    menusText.append(menu.getDescription()).append(", ")
                            .append(menu.getPrice()).append("\n");
                }
            }
            menuTextArea.setText(menusText.length() > 0 ? menusText.toString() : "Nema menija za ovaj objekat.");

            venueTextArea.selectRange(lineStart, lineEnd - 1);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri odabiru objekta. Kliknite na validan red.");
            venueTextArea.deselect();
            selectedVenueId = -1;
        }
    }

    private void handleAccept() {
        if (selectedVenueId == -1) {
            showAlert(Alert.AlertType.INFORMATION, "Informacija", "Selektujte objekat.");
            return;
        }

        try {
            Database.updateVenueStatus(selectedVenueId, "ODOBREN");
            Database.venues = Database.retrieveDataFromTable("objekat", Venue.class); // Refresh venues list
            refreshVenueTextArea();
            showAlert(Alert.AlertType.INFORMATION, "Uspjeh", "Objekat odobren.");
            selectedVenueId = -1; // Reset selection
            venueTextArea.deselect();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri odobravanju objekta: " + e.getMessage());
        }
    }

    private void handleDecline() {
        if (selectedVenueId == -1) {
            showAlert(Alert.AlertType.INFORMATION, "Informacija", "Selektujte objekat.");
            return;
        }

        try {
            Database.updateVenueStatus(selectedVenueId, "ODBIJEN");
            Database.venues = Database.retrieveDataFromTable("objekat", Venue.class); // Refresh venues list
            refreshVenueTextArea();
            showAlert(Alert.AlertType.INFORMATION, "Uspjeh", "Objekat odbijen.");
            selectedVenueId = -1; // Reset selection
            venueTextArea.deselect();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri odbijanju objekta: " + e.getMessage());
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
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("admin_dashboard.fxml"))));
        stage.show();
    }
}