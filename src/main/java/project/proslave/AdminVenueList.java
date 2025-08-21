package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Menu;
import SistemZaPlaniranjeProslava.Venue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
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
    private int selectedVenueId = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshVenueTextArea();

        venueTextArea.setCursor(Cursor.HAND);
        venueTextArea.setStyle("-fx-text-fill: black;");

        venueTextArea.setOnMouseClicked(this::handleVenueSelection);

        acceptButton.setOnAction(event -> handleAccept());
        declineButton.setOnAction(event -> handleDecline());
    }

    private void refreshVenueTextArea() {
        StringBuilder venuesText = new StringBuilder();
        if (Database.venues.isEmpty()) {
            venuesText.append("There is no available venues.");
        } else {
            for (Venue venue : Database.venues) {
                if (venue.getStatus().equals("NA CEKANJU")) {
                    venuesText.append("ID: ").append(venue.getId())
                            .append(", Name: ").append(venue.getNaziv())
                            .append(", City: ").append(venue.getGrad())
                            .append(", Address: ").append(venue.getAdresa())
                            .append(", Status: ").append(venue.getStatus())
                            .append("\n");
                }
            }
        }
        venueTextArea.setText(venuesText.toString());
        menuTextArea.clear();
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
                if (menu.getObjekat() != null && menu.getObjekat().getId() == selectedVenueId) {
                    menusText.append(menu.getOpis()).append(", ")
                            .append(menu.getCijenaPoOsobi()).append("\n");
                }
            }
            menuTextArea.setText(menusText.length() > 0 ? menusText.toString() : "No menu for this venue.");

            venueTextArea.selectRange(lineStart, lineEnd - 1);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            venueTextArea.deselect();
            selectedVenueId = -1;
        }
    }

    private void handleAccept() {
        try {
            Database.updateVenueStatus(selectedVenueId, "ODOBREN");
            Database.venues = Database.retrieveDataFromTable("objekat", Venue.class);
            refreshVenueTextArea();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Venue Accepted.");
            selectedVenueId = -1;
            venueTextArea.deselect();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error with approving the venue: " + e.getMessage());
        }
    }

    private void handleDecline() {
        if (selectedVenueId == -1) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No venue is selected.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Odbijanje objekta");
        alert.setHeaderText(null);

        CheckBox cb1 = new CheckBox("Maksimalan broj mijesta u salonu se ne podudara sa brojem mijesta koji se dobijaju kada se svi stolovi popune,");
        CheckBox cb2 = new CheckBox("Cijene menija nisu usklađene sa cijenama u ostalim objektima");

        cb1.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) cb2.setSelected(false);
        });
        cb2.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) cb1.setSelected(false);
        });

        VBox content = new VBox(10, new Label("Enter reason for decline:"), cb1, cb2);
        alert.getDialogPane().setContent(content);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String selectedReason = null;
                if (cb1.isSelected()) selectedReason = cb1.getText();
                else if (cb2.isSelected()) selectedReason = cb2.getText();

                if (selectedReason != null) {
                    try {
                        Database.updateVenueStatus(selectedVenueId, "ODBIJEN");
                        Database.addDeclineTextNotification(selectedVenueId, selectedReason);
                        Database.venues = Database.retrieveDataFromTable("objekat", Venue.class);
                        refreshVenueTextArea();
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Venue declined.");
                        selectedVenueId = -1;
                        venueTextArea.deselect();
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Error with decline venue: " + e.getMessage());
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Warning", "You must choose the reason for decline.");
                }
            }
        });
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