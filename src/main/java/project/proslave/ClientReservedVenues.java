package project.proslave;

import Database.Database;
import SistemZaPlaniranjeProslava.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
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

    @FXML
    public void initialize() {
        String loggedInUsername = Login.getClientUsername();
        if (loggedInUsername != null) {
            for (Client client : Database.clients) {
                if (client.getKorisnickoIme().equals(loggedInUsername)) {
                    clientId = client.getId();
                    break;
                }
            }
        } else {
            return;
        }
        loadReservations();
    }

    private void loadReservations() {
        List<String> activeList = new ArrayList<>();
        List<String> previousList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        Database.celebrations = Database.retrieveDataFromTable("proslava", Celebration.class);

        for (Celebration reservation : Database.celebrations) {
            if (reservation.getKlijent() == null) {
                System.out.println("Null klijent for celebration ID: " + reservation.getId());
                continue;
            }
            if (reservation.getKlijent().getId() != clientId) {
                continue;
            }
            if (reservation.getDatum() == null) {
                System.out.println("Null datum for celebration ID: " + reservation.getId());
                continue;
            }
            if (reservation.getObjekat() == null) {
                System.out.println("Null objekat for celebration ID: " + reservation.getId());
                continue;
            }

            String reservationInfo = String.format("ID: %d, Objekat: %s, Datum: %s, Gosti: %d, Cijena: %.2f",
                    reservation.getId(),
                    reservation.getObjekat().getNaziv(),
                    reservation.getDatum().toString(),
                    reservation.getBrojGostiju(),
                    reservation.getUkupnaCijena());

            if (reservation.getDatum().isBefore(today)) {
                previousList.add(reservationInfo);
            } else {
                activeList.add(reservationInfo);
            }
        }

        active.setText(activeList.isEmpty() ? "Nema aktivnih rezervacija" : String.join("\n", activeList));
        previous.setText(previousList.isEmpty() ? "Nema proteklih rezervacija" : String.join("\n", previousList));
        canceled.setText("");
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public void backToDashboard(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("client_dashboard.fxml"))));
        stage.show();
    }
}