package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Celebration;
import SistemZaPlaniranjeProslava.Menu;
import SistemZaPlaniranjeProslava.Notification;
import SistemZaPlaniranjeProslava.Venue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class OwnerVenueList implements Initializable {

    @FXML private TextArea aboutCelebration;
    @FXML private TextArea activeCelebrations;
    @FXML private Text earnedMoney;
    @FXML private TextArea previousCelebration;
    @FXML private TextArea venueTextArea;

    private int selectedVenueId = -1;
    private int selectedCelebrationId = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Database.loadNotifications();
        setupTextAreas();
        updateVenueListDisplay();
    }

    private void setupTextAreas() {
        venueTextArea.setEditable(false);
        activeCelebrations.setEditable(false);
        previousCelebration.setEditable(false);
        aboutCelebration.setEditable(false);

        venueTextArea.setCursor(Cursor.HAND);
        venueTextArea.setStyle("-fx-text-fill: black;");

        venueTextArea.setOnMouseClicked(this::onVenueSelected);
        activeCelebrations.setOnMouseClicked(this::onCelebrationSelected);
        previousCelebration.setOnMouseClicked(this::onCelebrationSelected);
    }

    private void updateVenueListDisplay() {
        String venuesText = Database.venues.isEmpty()
                ? "There are no available venues."
                : Database.venues.stream()
                .filter(v -> v.getStatus().equals("ODOBREN"))
                .map(v -> String.format("ID: %d, Name: %s, City: %s, Address: %s",
                        v.getId(), v.getNaziv(), v.getGrad(), v.getAdresa()))
                .collect(Collectors.joining("\n"));

        venueTextArea.setText(venuesText);
    }

    private void onVenueSelected(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY || event.getClickCount() != 1){
            return;
        }

        String selectedLine = extractSelectedVenueLine(event.getSource(), venueTextArea.getCaretPosition());
        if (selectedLine == null) {
            clearVenueDetails();
            return;
        }

        try {
            selectedVenueId = Integer.parseInt(selectedLine.split(",")[0].replace("ID: ", "").trim());
            selectedCelebrationId = -1;
            displayVenueDetails();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            clearVenueDetails();
        }
    }

    private String extractSelectedVenueLine(Object source, int caretPosition) {
        String[] lines = venueTextArea.getText().split("\n");
        int currentPos = 0;
        for (String line : lines) {
            int lineEnd = currentPos + line.length() + 1;
            if (caretPosition >= currentPos && caretPosition <= lineEnd) {
                return line.trim();
            }
            currentPos = lineEnd;
        }
        return null;
    }

    private void displayVenueDetails() {
        Venue venue = Database.venues.stream()
                .filter(v -> v.getId() == selectedVenueId)
                .findFirst().orElse(null);

        if (venue == null) {
            clearVenueDetails();
            return;
        }

        earnedMoney.setText(String.format("$%.2f", calculateVenueEarnings(selectedVenueId)));
        displayVenueCelebrations();
    }

    private double calculateVenueEarnings(int venueId) {
        return Database.celebrations.stream()
                .filter(c -> c.getObjekat().getId() == venueId && c.getUplacenIznos() > 0)
                .mapToDouble(Celebration::getUplacenIznos)
                .sum();
    }

    private void displayVenueCelebrations() {
        LocalDate today = LocalDate.now();
        List<Celebration> celebrations = Database.celebrations.stream()
                .filter(c -> c.getObjekat().getId() == selectedVenueId)
                .collect(Collectors.toList());

        activeCelebrations.setText(celebrations.stream()
                .filter(c -> c.getDatum().isAfter(today))
                .map(this::formatCelebrationDetails)
                .collect(Collectors.joining("\n")));

        previousCelebration.setText(celebrations.stream()
                .filter(c -> c.getDatum().isBefore(today))
                .map(this::formatCelebrationDetails)
                .collect(Collectors.joining("\n")));

        aboutCelebration.setText(celebrations.isEmpty()
                ? "No celebrations for this venue."
                : "Click a celebration for details.");
    }

    private String formatCelebrationDetails(Celebration c) {
        return String.format("ID: %d, Date: %s, Guests: %d, Price: $%.2f, Paid: $%.2f",
                c.getId(), c.getDatum(), c.getBrojGostiju(),
                c.getUkupnaCijena(), c.getUplacenIznos());
    }

    private void onCelebrationSelected(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) return;

        TextArea source = (TextArea) event.getSource();
        String selectedText = source.getSelectedText();

        if (selectedText == null || selectedText.isEmpty()) return;

        try {
            int celebrationId = Integer.parseInt(selectedText.split(",")[0].replace("ID: ", "").trim());
            Celebration celebration = Database.celebrations.stream()
                    .filter(c -> c.getId() == celebrationId)
                    .findFirst().orElse(null);

            if (celebration != null) {
                selectedCelebrationId = celebrationId;
                Menu menu = celebration.getMeni();
                aboutCelebration.setText(String.format(
                        "Date: %s\nGuests: %d\nPrice: $%.2f\nMenu: %s\n",
                        celebration.getDatum(),
                        celebration.getBrojGostiju(),
                        celebration.getUplacenIznos(),
                        menu != null ? menu.getOpis() : "No menu"));
            }
        } catch (NumberFormatException e) {
            aboutCelebration.setText("Invalid selection.");
        }
    }

    private void clearVenueDetails() {
        earnedMoney.setText("$0.00");
        activeCelebrations.clear();
        previousCelebration.clear();
        aboutCelebration.clear();
        selectedVenueId = -1;
        selectedCelebrationId = -1;
    }

    public void navigateToOwnerDashboard(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("owner_dashboard.fxml"))));
        stage.show();
    }
}
