package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Celebration;
import SistemZaPlaniranjeProslava.Client;
import SistemZaPlaniranjeProslava.Menu;
import SistemZaPlaniranjeProslava.Table;
import SistemZaPlaniranjeProslava.Venue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ClientEditVenue implements Initializable {
    @FXML private ChoiceBox<String> menuChoiceBox;
    @FXML private Button cancelReservation;
    @FXML private Button saveChanges;
    @FXML private Text venueName;
    @FXML private Circle circle;
    @FXML private Pane venuePane;
    @FXML private ChoiceBox<String> tables;

    private Celebration selectedCelebration;
    private Client currentClient;
    private List<Table> venueTables;
    private List<TextField> currentTextFields;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentTextFields = new ArrayList<>();
        if (venuePane == null) {
            System.err.println("Greška: venuePane je null. Provjerite FXML fajl (fx:id=\"venuePane\").");
            showAlert(Alert.AlertType.ERROR, "Greška", "Pane za prikaz stolova nije inicijaliziran.");
            return;
        }
        if (circle == null) {
            System.err.println("Greška: circle je null. Provjerite FXML fajl (fx:id=\"circle\").");
            showAlert(Alert.AlertType.ERROR, "Greška", "Krug za pozicioniranje mjesta nije inicijaliziran.");
            return;
        }
        if (tables == null) {
            System.err.println("Greška: tables ChoiceBox je null. Provjerite FXML fajl (fx:id=\"tables\").");
            showAlert(Alert.AlertType.ERROR, "Greška", "ChoiceBox za stolove nije inicijaliziran.");
            return;
        }

        if (selectedCelebration != null && selectedCelebration.getObjekat() != null) {
            handleVenueMenu();
            venueName.setText(selectedCelebration.getObjekat().getNaziv());

            try {
                venueTables = Database.getTablesByVenueId(selectedCelebration.getObjekat().getId());
                System.out.println("Dohvaćeni stolovi: " + venueTables);
                populateTablesChoiceBox();

                tables.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && !newValue.equals("Nema dostupnih stolova")) {
                        int tableIndex = Integer.parseInt(newValue.split(" ")[1]) - 1;
                        Table selectedTable = venueTables.get(tableIndex);
                        int numberOfSeats = selectedTable.getBrojMjesta();
                        System.out.println("Odabran sto: " + newValue + ", broj mjesta: " + numberOfSeats);
                        populateTextFields(circle, numberOfSeats, selectedTable.getId());
                    }
                });

                if (!venueTables.isEmpty()) {
                    tables.getSelectionModel().selectFirst();
                } else {
                    System.out.println("Nema dostupnih stolova za objekat ID: " + selectedCelebration.getObjekat().getId());
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Greška",
                        "Ne mogu dohvatiti podatke o stolovima: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            venueName.setText("Nema dostupnih podataka o objektu");
            tables.getItems().add("Nema dostupnih stolova");
            tables.setDisable(true);
        }

        cancelReservation.setOnAction(event -> cancelVenueReservation());
        saveChanges.setOnAction(event -> saveVenueChanges());
    }

    private void populateTablesChoiceBox() {
        tables.getItems().clear();
        if (venueTables == null || venueTables.isEmpty()) {
            tables.getItems().add("Nema dostupnih stolova");
            tables.setDisable(true);
        } else {
            for (int i = 0; i < venueTables.size(); i++) {
                tables.getItems().add("Sto " + (i + 1));
            }
            tables.setDisable(false);
        }
    }

    private void populateTextFields(Circle circle, int numberOfSeats, int tableId) {
        if (venuePane == null || circle == null) {
            System.err.println("Greška: venuePane ili circle je null u populateTextFields.");
            return;
        }

        venuePane.getChildren().removeIf(node -> node instanceof TextField);
        currentTextFields.clear();
        System.out.println("Generisanje " + numberOfSeats + " TextField elemenata za sto ID: " + tableId);

        List<String> guestList = new ArrayList<>();
        try {
            guestList = Database.getGuestsForTableAndCelebration(tableId, selectedCelebration.getId());
            System.out.println("Dohvaćeni gosti: " + guestList);
        } catch (SQLException e) {
            System.err.println("Greška prilikom dohvatanja gostiju: " + e.getMessage());
            e.printStackTrace();
        }

        // Provjeri broj gostiju i uskladi s brojem sjedala
        if (guestList.size() > numberOfSeats) {
            System.err.println("Upozorenje: Broj gostiju u bazi (" + guestList.size() + ") veći je od broja sjedala (" + numberOfSeats + ") za sto ID: " + tableId);
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Broj gostiju u bazi premašuje broj sjedala za ovaj sto.");
            guestList = guestList.subList(0, numberOfSeats); // Ograniči na broj sjedala
        }

        // Popuni praznim stringovima ako je lista kraća od broja sjedala
        while (guestList.size() < numberOfSeats) {
            guestList.add("");
        }

        double centerX = circle.getLayoutX();
        double centerY = circle.getLayoutY();
        double radius = circle.getRadius();
        System.out.println("Centar kruga: (" + centerX + ", " + centerY + "), Poluprečnik: " + radius);

        List<Point2D> points = CircleHelper.getEquallySpacedPoints(
                centerX,
                centerY,
                radius,
                numberOfSeats
        );

        for (int i = 0; i < numberOfSeats; i++) {
            Point2D p = points.get(i);
            TextField tf = new TextField();
            tf.setPrefWidth(80);
            tf.setPrefHeight(30);
            tf.setPromptText("Gost " + (i + 1));
            tf.setText(guestList.get(i)); // Postavi gosta na točnoj poziciji
            tf.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");

            tf.setLayoutX(p.getX() - tf.getPrefWidth() / 2);
            tf.setLayoutY(p.getY() - tf.getPrefHeight() / 2);

            venuePane.getChildren().add(tf);
            currentTextFields.add(tf);
            System.out.println("Dodan TextField na poziciju: (" + tf.getLayoutX() + ", " + tf.getLayoutY() + ") sa tekstom: " + tf.getText());
        }
    }

    public void saveVenueChanges() {
        if (selectedCelebration == null || menuChoiceBox.getValue() == null ||
                menuChoiceBox.getValue().equals("No menu for this venue.") ||
                menuChoiceBox.getValue().equals("Nema dostupnih podataka o objektu")) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Odaberite validan meni.");
            return;
        }

        String selectedMenuText = menuChoiceBox.getValue();
        Menu newMenu = Database.menus.stream()
                .filter(m -> m.getObjekat() != null && m.getObjekat().getId() == selectedCelebration.getObjekat().getId()
                        && ("Description: " + m.getOpis() + " | Price: " + m.getCijenaPoOsobi() + " KM").equals(selectedMenuText))
                .findFirst().orElse(null);

        if (newMenu != null) {
            try {
                // Ažuriraj meni
                Database.updateCelebrationMenu(selectedCelebration.getId(), newMenu.getId());
                selectedCelebration.setMeni(newMenu);

                // Spremi goste za trenutno odabrani sto
                String selectedTable = tables.getSelectionModel().getSelectedItem();
                if (selectedTable != null && !selectedTable.equals("Nema dostupnih stolova")) {
                    int tableIndex = Integer.parseInt(selectedTable.split(" ")[1]) - 1;
                    Table selectedTableObj = venueTables.get(tableIndex);
                    int tableId = selectedTableObj.getId();
                    // Prikupljaj sve TextField unose, uključujući prazne, prema njihovom redoslijedu
                    List<String> guests = new ArrayList<>();
                    for (int i = 0; i < currentTextFields.size(); i++) {
                        String guest = currentTextFields.get(i).getText().trim();
                        guests.add(guest.isEmpty() ? "" : guest);
                    }
                    System.out.println("Spremam goste za sto ID: " + tableId + ", proslava ID: " + selectedCelebration.getId() + ", gosti: " + guests);
                    Database.saveGuestsForTableAndCelebration(tableId, selectedCelebration.getId(), guests);
                }

                showAlert(Alert.AlertType.INFORMATION, "Uspjeh", "Promjene uspješno sačuvane.");
                navigateToReservedVenues(null);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Greška", "Greška prilikom čuvanja promjena: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška: Odabrani meni nije validan.");
        }
    }

    public void setCelebration(Celebration celebration) {
        this.selectedCelebration = celebration;
        if (celebration != null && celebration.getObjekat() != null) {
            handleVenueMenu();
            venueName.setText(celebration.getObjekat().getNaziv());

            try {
                venueTables = Database.getTablesByVenueId(celebration.getObjekat().getId());
                System.out.println("Dohvaćeni stolovi u setCelebration: " + venueTables);
                populateTablesChoiceBox();

                tables.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && !newValue.equals("Nema dostupnih stolova")) {
                        int tableIndex = Integer.parseInt(newValue.split(" ")[1]) - 1;
                        Table selectedTable = venueTables.get(tableIndex);
                        int numberOfSeats = selectedTable.getBrojMjesta();
                        System.out.println("Odabran sto u setCelebration: " + newValue + ", broj mjesta: " + numberOfSeats);
                        populateTextFields(circle, numberOfSeats, selectedTable.getId());
                    }
                });

                if (!venueTables.isEmpty()) {
                    tables.getSelectionModel().selectFirst();
                } else {
                    System.out.println("Nema dostupnih stolova za objekat ID: " + celebration.getObjekat().getId());
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Greška",
                        "Ne mogu dohvatiti podatke o stolovima: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            venueName.setText("Nema dostupnih podataka o objektu");
            tables.getItems().add("Nema dostupnih stolova");
            tables.setDisable(true);
        }
    }

    public void setCurrentClient(Client client) {
        this.currentClient = client;
    }

    public void handleVenueMenu() {
        menuChoiceBox.getItems().clear();

        if (selectedCelebration == null || selectedCelebration.getObjekat() == null) {
            menuChoiceBox.getItems().add("Nema dostupnih podataka o objektu");
            menuChoiceBox.setDisable(true);
            return;
        }

        int venueId = selectedCelebration.getObjekat().getId();
        boolean menuFound = false;

        for (Menu menu : Database.menus) {
            if (menu.getObjekat() != null && menu.getObjekat().getId() == venueId) {
                menuFound = true;
                String menuEntry = "Description: " + menu.getOpis() + " | Price: " + menu.getCijenaPoOsobi() + " KM";
                menuChoiceBox.getItems().add(menuEntry);

                if (selectedCelebration.getMeni() != null
                        && menu.getOpis().equals(selectedCelebration.getMeni().getOpis())
                        && menu.getCijenaPoOsobi() == selectedCelebration.getMeni().getCijenaPoOsobi()) {
                    menuChoiceBox.getSelectionModel().select(menuEntry);
                }
            }
        }

        if (!menuFound) {
            menuChoiceBox.getItems().add("No menu for this venue.");
            menuChoiceBox.setDisable(true);
        } else {
            menuChoiceBox.setDisable(false);
        }
    }

    public void cancelVenueReservation() {
        LocalDate today = LocalDate.now();
        LocalDate celebrationDate = selectedCelebration.getDatum();

        try {
            double clientBalance = Database.getAccountBalance(currentClient.getBrojRacuna());
            double ownerBalance = Database.getAccountBalance(selectedCelebration.getObjekat().getVlasnik().getBrojRacuna());
            double uplacenIznos = selectedCelebration.getUplacenIznos();
            String message;

            if (celebrationDate.isBefore(today.plusDays(3))) {
                message = "Rezervacija otkazana, ali je proslava za manje od 3 dana. Vlasnik zadržava sav uplaćeni novac (" + uplacenIznos + " KM). Stanje na računu: " + clientBalance + " KM";
            } else {
                if (ownerBalance < uplacenIznos) {
                    showAlert(Alert.AlertType.ERROR, "Greška", "Vlasnik nema dovoljno sredstava za povrat novca: " + ownerBalance + " KM dostupno, potrebno: " + uplacenIznos + " KM");
                    return;
                }
                Database.transferMoney(selectedCelebration.getObjekat().getVlasnik().getBrojRacuna(),
                        currentClient.getBrojRacuna(),
                        uplacenIznos);
                clientBalance = Database.getAccountBalance(currentClient.getBrojRacuna());
                message = "Rezervacija otkazana. Vraćeno vam je " + uplacenIznos + " KM. Stanje na računu: " + clientBalance + " KM";
            }

            Venue venue = selectedCelebration.getObjekat();
            if (venue != null && venue.getDatumi() != null) {
                List<LocalDate> datumi = new ArrayList<>(venue.getDatumi());
                datumi.remove(selectedCelebration.getDatum());
                venue.setDatumi(datumi);
                String datumiString = datumi.isEmpty() ? "" : String.join(",", datumi.stream().map(LocalDate::toString).toArray(String[]::new));
                Database.updateVenueDates(venue.getId(), datumiString);
            }

            Database.updateCelebrationProslavacol(selectedCelebration.getId(), "OTKAZANA");
            selectedCelebration.setProslavacol("OTKAZANA");

            showAlert(Alert.AlertType.INFORMATION, "Otkazivanje rezervacije", message);

            ClientReservedVenues.updateCanceledReservations(selectedCelebration);
            navigateToReservedVenues(null);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška prilikom otkazivanja rezervacije: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void navigateToReservedVenues(MouseEvent event) {
        try {
            Stage stage = (Stage) cancelReservation.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_reservedVenues.fxml"))));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška prilikom povratka na dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}