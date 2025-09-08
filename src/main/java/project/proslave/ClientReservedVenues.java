package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import SistemZaPlaniranjeProslava.Celebration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClientReservedVenues {
    @FXML private TextArea active;
    @FXML private TextArea canceled;
    @FXML private TextArea previous;

    private int clientId;
    private static ClientReservedVenues instance;

    @FXML
    public void initialize() {
        instance = this;
        String loggedInUsername = Login.getClientUsername();
        if (loggedInUsername != null) {
            for (Client client : Database.clients) {
                if (client.getKorisnickoIme().equals(loggedInUsername)) {
                    clientId = client.getId();
                    break;
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška");
            alert.setHeaderText(null);
            alert.setContentText("Korisnik nije prijavljen.");
            alert.showAndWait();
            return;
        }
        active.setOnMouseClicked(this::handleCelebrationClick);
        previous.setOnMouseClicked(this::handleCelebrationClick);
        loadReservations();
    }

    private void loadReservations() {
        List<String> activeList = new ArrayList<>();
        List<String> previousList = new ArrayList<>();
        List<String> canceledList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        Database.celebrations = Database.retrieveDataFromTable("proslava", Celebration.class);

        for (Celebration reservation : Database.celebrations) {
            if (reservation.getKlijent().getId() != clientId) {
                continue;
            }
            String reservationInfo = String.format("Objekat: %s, Datum: %s, Gosti: %d, Cijena: %.2f",
                    reservation.getObjekat().getNaziv(),
                    reservation.getDatum().toString(),
                    reservation.getBrojGostiju(),
                    reservation.getUkupnaCijena());

            if (reservation.getProslavacol().equals("OTKAZANA")) {
                canceledList.add(reservationInfo);
            } else if (reservation.getDatum().isBefore(today)) {
                previousList.add(reservationInfo);
            } else {
                activeList.add(reservationInfo);
            }
        }

        active.setText(activeList.isEmpty() ? "Nema aktivnih rezervacija" : String.join("\n", activeList));
        previous.setText(previousList.isEmpty() ? "Nema proteklih rezervacija" : String.join("\n", previousList));
        canceled.setText(canceledList.isEmpty() ? "Nema otkazanih rezervacija" : String.join("\n", canceledList));
    }

    public void handleCelebrationClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            TextArea source = (TextArea) event.getSource();
            String selectedText = source.getSelectedText();
            if (selectedText != null && !selectedText.isEmpty()) {
                try {
                    int caretPosition = source.getCaretPosition();
                    String text = source.getText();
                    String[] lines = text.split("\n");
                    int currentPos = 0;
                    String selectedLine = null;
                    for (String line : lines) {
                        int lineStart = currentPos;
                        int lineEnd = currentPos + line.length();
                        if (caretPosition >= lineStart && caretPosition <= lineEnd) {
                            selectedLine = line.trim();
                            break;
                        }
                        currentPos = lineEnd + 1;
                    }

                    if (selectedLine != null) {
                        Celebration celebration = findCelebrationByLine(selectedLine);
                        if (celebration != null && celebration.getKlijent().getId() == clientId) {
                            openEditVenue(event, celebration);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Greška");
                            alert.setHeaderText(null);
                            alert.setContentText("Proslava nije pronađena ili ne pripada trenutnom klijentu.");
                            alert.showAndWait();
                        }
                    }
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Greška");
                    alert.setHeaderText(null);
                    alert.setContentText("Greška prilikom otvaranja proslave: " + e.getMessage());
                    alert.showAndWait();
                    e.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Upozorenje");
                alert.setHeaderText(null);
                alert.setContentText("Nije odabran tekst za dvostruki klik.");
                alert.showAndWait();
            }
        }
    }

    private Celebration findCelebrationByLine(String line) {
        for (Celebration celebration : Database.celebrations) {
            String reservationInfo = String.format("Objekat: %s, Datum: %s, Gosti: %d, Cijena: %.2f",
                    celebration.getObjekat().getNaziv(),
                    celebration.getDatum().toString(),
                    celebration.getBrojGostiju(),
                    celebration.getUkupnaCijena());
            if (reservationInfo.equals(line)) {
                return celebration;
            }
        }
        return null;
    }

    private void openEditVenue(MouseEvent event, Celebration celebration) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("client_editVenue.fxml"));
        Parent root = loader.load();
        ClientEditVenue controller = loader.getController();
        controller.setCelebration(celebration);
        controller.setCurrentClient(Database.clients.stream()
                .filter(c -> c.getId() == clientId)
                .findFirst()
                .orElse(null));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public void backToDashboard(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_dashboard.fxml"))));
        stage.show();
    }

    public static void refreshCanceled(Celebration canceledCelebration) {
        if (instance != null && instance.canceled != null && instance.active != null) {
            String canceledInfo = String.format("Objekat: %s, Datum: %s, Gosti: %d, Cijena: %.2f",
                    canceledCelebration.getObjekat().getNaziv(),
                    canceledCelebration.getDatum().toString(),
                    canceledCelebration.getBrojGostiju(),
                    canceledCelebration.getUkupnaCijena());
            String currentCanceledText = instance.canceled.getText();
            if (!currentCanceledText.contains(canceledInfo)) {
                if (currentCanceledText.equals("Nema otkazanih rezervacija")) {
                    instance.canceled.setText(canceledInfo + "\n");
                } else {
                    instance.canceled.appendText(canceledInfo + "\n");
                }
            }
            String activeText = instance.active.getText();
            if (activeText.contains(canceledInfo)) {
                instance.active.setText(activeText.replaceAll(canceledInfo + "\n", ""));
                if (instance.active.getText().isEmpty()) {
                    instance.active.setText("Nema aktivnih rezervacija");
                }
            }
        } else {
            System.out.println("Instance or TextArea is null: instance=" + instance + ", canceled=" + instance.canceled + ", active=" + instance.active);
        }
    }
}