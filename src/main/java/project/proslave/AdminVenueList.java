package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Menu;
import SistemZaPlaniranjeProslava.Venue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        setupVenueTextArea();
        refreshVenueTextArea();
        acceptButton.setOnAction(e -> handleAccept());
        declineButton.setOnAction(e -> handleDecline());
    }

    private void setupVenueTextArea() {
        venueTextArea.setCursor(Cursor.HAND);
        venueTextArea.setStyle("-fx-text-fill: black;");
        venueTextArea.setOnMouseClicked(this::handleVenueSelection);
    }

    private void refreshVenueTextArea() {
        StringBuilder builder = new StringBuilder();

        Database.venues.stream()
                .filter(v -> "NA CEKANJU".equals(v.getStatus()))
                .forEach(v -> builder.append(String.format("ID: %d, Name: %s, City: %s, Address: %s, Status: %s%n",
                        v.getId(), v.getNaziv(), v.getGrad(), v.getAdresa(), v.getStatus())));

        if (builder.length() == 0) {
            builder.append("There is no available venues.");
        }

        venueTextArea.setText(builder.toString());
        menuTextArea.clear();
    }

    private void handleVenueSelection(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY || event.getClickCount() != 1){
            return;
        }

        int caretPos = venueTextArea.getCaretPosition();
        String[] lines = venueTextArea.getText().split("\n");

        int start = 0;
        String selectedLine = null;
        for (String line : lines) {
            int end = start + line.length() + 1;
            if (caretPos >= start && caretPos <= end) {
                selectedLine = line.trim();
                break;
            }
            start = end;
        }

        try {
            selectedVenueId = Integer.parseInt(selectedLine.split(",")[0].replace("ID:", "").trim());
            showVenueMenus();
            highlightSelectedLine(lines, selectedLine);
        } catch (Exception ex) {
            selectedVenueId = -1;
            venueTextArea.deselect();
        }
    }

    private void showVenueMenus() {
        StringBuilder menus = new StringBuilder();
        for (Menu menu : Database.menus) {
            if (menu.getObjekat() != null && menu.getObjekat().getId() == selectedVenueId) {
                menus.append(menu.getOpis())
                        .append(", ")
                        .append(menu.getCijenaPoOsobi())
                        .append("\n");
            }
        }
        menuTextArea.setText(menus.length() > 0 ? menus.toString() : "No menu for this venue.");
    }

    private void highlightSelectedLine(String[] lines, String selectedLine) {
        int pos = venueTextArea.getText().indexOf(selectedLine);
        venueTextArea.selectRange(pos, pos + selectedLine.length());
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

        Alert alert = createDeclineDialog();
        alert.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) processDecline(alert);
        });
    }

    private Alert createDeclineDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Odbijanje objekta");
        alert.setHeaderText(null);

        CheckBox cb1 = new CheckBox("Maksimalan broj mijesta u salonu se ne podudara sa brojem mijesta koji se dobijaju kada se svi stolovi popune,");
        CheckBox cb2 = new CheckBox("Cijene menija nisu usklađene sa cijenama u ostalim objektima");

        cb1.selectedProperty().addListener((o, ov, nv) -> { if (nv) cb2.setSelected(false); });
        cb2.selectedProperty().addListener((o, ov, nv) -> { if (nv) cb1.setSelected(false); });

        VBox content = new VBox(10, new Label("Enter reason for decline:"), cb1, cb2);
        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setUserData(new CheckBox[]{cb1, cb2});
        return alert;
    }

    private void processDecline(Alert alert) {
        CheckBox[] checkBoxes = (CheckBox[]) alert.getDialogPane().getUserData();
        CheckBox cb1 = checkBoxes[0];
        CheckBox cb2 = checkBoxes[1];

        String reason = cb1.isSelected() ? cb1.getText() : (cb2.isSelected() ? cb2.getText() : null);

        if (reason == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "You must choose the reason for decline.");
            return;
        }

        try {
            Database.updateVenueStatus(selectedVenueId, "ODBIJEN");
            Database.addDeclineTextNotification(selectedVenueId, reason);
            Database.venues = Database.retrieveDataFromTable("objekat", Venue.class);
            refreshVenueTextArea();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Venue declined.");
            selectedVenueId = -1;
            venueTextArea.deselect();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error with decline venue: " + e.getMessage());
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
        Scene newScene = new Scene(FXMLLoader.load(getClass().getResource("admin_dashboard.fxml")));
        stage.setScene(newScene);
        stage.show();
    }
}
