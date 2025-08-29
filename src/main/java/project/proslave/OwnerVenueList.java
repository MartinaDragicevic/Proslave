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
        venueTextArea.setEditable(false);
        activeCelebrations.setEditable(false);
        previousCelebration.setEditable(false);
        aboutCelebration.setEditable(false);
        venueTextArea.setCursor(Cursor.HAND);
        venueTextArea.setStyle("-fx-text-fill: black;");
        venueTextArea.setOnMouseClicked(this::handleVenueSelection);
        activeCelebrations.setOnMouseClicked(this::handleCelebrationSelection);
        previousCelebration.setOnMouseClicked(this::handleCelebrationSelection);
        refreshVenueTextArea();
    }

    private void refreshVenueTextArea() {
        StringBuilder venuesText = new StringBuilder();
        if (Database.venues.isEmpty()) {
            venuesText.append("There are no available venues.");
        } else {
            for (Venue venue : Database.venues) {
                if (venue.getStatus().equals("ODOBREN")) {
                    venuesText.append("ID: ").append(venue.getId())
                            .append(", Name: ").append(venue.getNaziv())
                            .append(", City: ").append(venue.getGrad())
                            .append(", Address: ").append(venue.getAdresa())
                            .append("\n");
                }
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
            selectedCelebrationId = -1;
            updateVenueDetails();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            venueTextArea.deselect();
            selectedVenueId = -1;
            clearDetails();
        }
    }

    private void updateVenueDetails() {
        if (selectedVenueId != -1) {
            Venue venue = Database.venues.stream()
                    .filter(v -> v.getId() == selectedVenueId)
                    .findFirst().orElse(null);
            if (venue != null) {
                String datesText = venue.getDatumi() != null ? "Booked: " + String.join(", ", venue.getDatumi().stream().map(LocalDate::toString).toArray(String[]::new)) : "All dates available";
                double earnings = calculateEarnings(selectedVenueId);
                earnedMoney.setText(String.format("$%.2f", earnings));
                updateCelebrations();
            } else {
                clearDetails();
            }
        }
    }

    private double calculateEarnings(int venueId) {
        return Database.celebrations.stream()
                .filter(c -> c.getObjekat().getId() == venueId && c.getUplacenIznos() > 0)
                .mapToDouble(Celebration::getUplacenIznos)
                .sum();
    }

    private void updateCelebrations() {
        if (selectedVenueId != -1) {
            LocalDate currentDate = LocalDate.now();
            List<Celebration> allCelebrations = Database.celebrations.stream()
                    .filter(c -> c.getObjekat().getId() == selectedVenueId)
                    .collect(Collectors.toList());
            activeCelebrations.setText(allCelebrations.stream()
                    .filter(c -> c.getDatum().isAfter(currentDate))
                    .map(c -> String.format("ID: %d, Date: %s, Guests: %d, Price: $%.2f, Paid: $%.2f",
                            c.getId(), c.getDatum(), c.getBrojGostiju(),
                            c.getUkupnaCijena(), c.getUplacenIznos()))
                    .collect(Collectors.joining("\n")));
            previousCelebration.setText(allCelebrations.stream()
                    .filter(c -> c.getDatum().isBefore(currentDate))
                    .map(c -> String.format("ID: %d, Date: %s, Guests: %d, Price: $%.2f, Paid: $%.2f",
                            c.getId(), c.getDatum(), c.getBrojGostiju(),
                            c.getUkupnaCijena(), c.getUplacenIznos()))
                    .collect(Collectors.joining("\n")));
            aboutCelebration.setText(allCelebrations.isEmpty() ? "No celebrations for this venue." : "Click a celebration for details.");
        }
    }

    private void handleCelebrationSelection(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            TextArea source = (TextArea) event.getSource();
            String selectedText = source.getSelectedText();
            if (selectedText != null && !selectedText.isEmpty()) {
                try {
                    int celebrationId = Integer.parseInt(selectedText.split(",")[0].replace("ID: ", "").trim());
                    Celebration celebration = Database.celebrations.stream()
                            .filter(c -> c.getId() == celebrationId)
                            .findFirst().orElse(null);
                    if (celebration != null) {
                        selectedCelebrationId = celebrationId;
                        Menu menu = celebration.getMeni();
                        aboutCelebration.setText(String.format(
                                "Date: %s\n" +
                                "Guests: %d\n" +
                                "Price: $%.2f\n" +
                                "Menu: %s\n",
                                celebration.getDatum(),
                                celebration.getBrojGostiju(),
                                celebration.getUplacenIznos(),
                                menu != null ? menu.getOpis() : "No menu"));
                    }
                } catch (NumberFormatException e) {
                    aboutCelebration.setText("Invalid selection.");
                }
            }
        }
    }

    private void clearDetails() {
        earnedMoney.setText("$0.00");
        activeCelebrations.setText("");
        previousCelebration.setText("");
        aboutCelebration.setText("");
        selectedVenueId = -1;
        selectedCelebrationId = -1;
    }

    public void backToDashboard(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("owner_dashboard.fxml"))));
        stage.show();
    }
}