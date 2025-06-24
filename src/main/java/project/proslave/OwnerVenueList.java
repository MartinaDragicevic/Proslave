package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Menu;
import SistemZaPlaniranjeProslava.Notification;
import SistemZaPlaniranjeProslava.Venue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OwnerVenueList implements Initializable {
    @FXML private TextArea venueTextArea;
    private int selectedVenueId = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Database.loadNotifications();
        refreshVenueTextArea();

        venueTextArea.setCursor(Cursor.HAND);
        venueTextArea.setStyle("-fx-text-fill: black;");

        venueTextArea.setOnMouseClicked(this::handleVenueSelection);
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
                        .append(", Status: ").append(venue.getStatus());

                if (venue.getStatus().equals("ODBIJEN")) {
                    for (Notification notification : Database.notifications) {
                        if (notification.getObjekatId() == venue.getId()) {
                            venuesText.append(", Razlog: ").append(notification.getText());
                        }
                    }
                }

                venuesText.append("\n");
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

            venueTextArea.selectRange(lineStart, lineEnd - 1);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri odabiru objekta. Kliknite na validan red.");
            venueTextArea.deselect();
            selectedVenueId = -1;
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