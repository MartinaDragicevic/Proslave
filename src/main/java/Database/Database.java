package Database;

import SistemZaPlaniranjeProslava.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;

public class Database {
    private static String DB_user = "root";
    private static String DB_password = "";
    private static String connectionUrl;
    private static int port = 3306;
    private static String DB_name = "sistemzaplaniranjeproslava";
    private static Connection connection;
    public static List<Admin> admins = retrieveDataFromTable("admin", Admin.class);
    public static List<Owner> owners = retrieveDataFromTable("vlasnik", Owner.class);
    public static List<Client> clients = retrieveDataFromTable("klijent", Client.class);
    public static List<BankAccount> bankAccounts = retrieveDataFromTable("`bankovni racun`", BankAccount.class);
    public static List<Venue> venues = retrieveDataFromTable("objekat", Venue.class);
    public static List<Celebration> celebrations = retrieveDataFromTable("proslava", Celebration.class);
    public static List<Menu> menus = retrieveDataFromTable("meni", Menu.class);
    public static List<Table> tables = retrieveDataFromTable("sto", Table.class);
    public static List<TableLayout> schedules = retrieveDataFromTable("raspored", TableLayout.class);
    public static List<Notification> notifications = retrieveDataFromTable("obavjestenje", Notification.class);

    public static void DBConnect() throws SQLException {
        connectionUrl = "jdbc:mysql://localhost" + ":" + port + "/" + DB_name;
        connection = DriverManager.getConnection(connectionUrl, DB_user, DB_password);
    }

    public static void main(String[] args) {
        try {
            DBConnect();
            System.out.println("Uspjesno ste se konektovali na bazu:" + connectionUrl);
            ResultSet resultSet = null;
            Statement statement = connection.createStatement();
            String SQLQuery = "SELECT * FROM admin";
            resultSet = statement.executeQuery(SQLQuery);
            System.out.println("--------------------------------------------");
            while (resultSet.next()) {
                String result = resultSet.getString(1) + ", " + resultSet.getString(2)
                        + ", " + resultSet.getString(3) + ", " + resultSet.getString(4)
                        + ", " + resultSet.getString(5);
                System.out.println(result);
                System.out.println("--------------------------------------------");
            }

            statement.close();
            connection.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void addUserToDatabase(int id, String ime, String prezime, String jmbg, String brojRacuna, String korisnickoIme, String lozinka, String tableName, String userType){
        String query = "INSERT INTO " + tableName + " (id, ime, prezime, jmbg, broj_racuna, korisnicko_ime, lozinka) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            DBConnect();
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, ime);
            preparedStatement.setString(3, prezime);
            preparedStatement.setString(4, jmbg);
            preparedStatement.setString(5, brojRacuna);
            preparedStatement.setString(6, korisnickoIme);
            preparedStatement.setString(7, lozinka);
            preparedStatement.executeUpdate();

            if (userType.equals("klijent")) {
                clients.add(new Client(id, ime, prezime, jmbg, brojRacuna, korisnickoIme, lozinka));
            } else if (userType.equals("vlasnik")) {
                owners.add(new Owner(id, ime, prezime, jmbg, brojRacuna, korisnickoIme, lozinka));
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void changePassword(String newPassword, String username, String tableName){
        try {
            DBConnect();
            String updateQuery = "UPDATE " + tableName + " SET lozinka = ? WHERE korisnicko_ime = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean checkCurrentPassword(String username, String inputPassword) {
        try {
            DBConnect();

            String adminQuery = "SELECT lozinka FROM admin WHERE korisnicko_ime = ?";
            PreparedStatement adminPs = connection.prepareStatement(adminQuery);
            adminPs.setString(1, username);
            ResultSet adminRs = adminPs.executeQuery();

            if (adminRs.next()) {
                String storedPassword = adminRs.getString("lozinka");
                return storedPassword.equals(inputPassword);
            }

            String clientQuery = "SELECT lozinka FROM klijent WHERE korisnicko_ime = ?";
            PreparedStatement clientPs = connection.prepareStatement(clientQuery);
            clientPs.setString(1, username);
            ResultSet clientRs = clientPs.executeQuery();

            if (clientRs.next()) {
                String storedPassword = clientRs.getString("lozinka");
                return storedPassword.equals(inputPassword);
            }

            String ownerQuery = "SELECT lozinka FROM vlasnik WHERE korisnicko_ime = ?";
            PreparedStatement ownerPs = connection.prepareStatement(ownerQuery);
            ownerPs.setString(1, username);
            ResultSet ownerRs = ownerPs.executeQuery();

            if (ownerRs.next()) {
                String storedPassword = ownerRs.getString("lozinka");
                return storedPassword.equals(inputPassword);
            }

            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static double getAccountBalance(String brojRacuna) {
        double balance = 0.0;
        try {
            DBConnect();
            String query = "SELECT stanje FROM `bankovni racun` WHERE broj_racuna = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, brojRacuna);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                balance = rs.getDouble("stanje");
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    public static void addVenue(Venue venue, int tablesNumber, int seatsPerTable, List<Menu> menus) throws SQLException {
        Connection conn = null;
        PreparedStatement venueStmt = null;
        PreparedStatement menuStmt = null;
        PreparedStatement tableStmt = null;
        ResultSet generatedKeys = null;

        try {
            DBConnect();
            conn = connection;
            conn.setAutoCommit(false);

            String datumiString = venue.getDatumi() != null ? String.join(",", venue.getDatumi().stream().map(LocalDate::toString).toArray(String[]::new)) : "";
            String venueQuery = "INSERT INTO objekat (Vlasnik_id, naziv, cijena_rezervacije, grad, adresa, broj_mjesta, broj_stolova, datumi, zarada, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            venueStmt = conn.prepareStatement(venueQuery, Statement.RETURN_GENERATED_KEYS);
            venueStmt.setInt(1, venue.getVlasnik().getId());
            venueStmt.setString(2, venue.getNaziv());
            venueStmt.setDouble(3, venue.getCijenaRezervacije());
            venueStmt.setString(4, venue.getGrad());
            venueStmt.setString(5, venue.getAdresa());
            venueStmt.setInt(6, venue.getBrojMjesta());
            venueStmt.setInt(7, tablesNumber);
            venueStmt.setString(8, datumiString);
            venueStmt.setDouble(9, venue.getZarada());
            venueStmt.setString(10, "NA ČEKANJU");
            venueStmt.executeUpdate();

            generatedKeys = venueStmt.getGeneratedKeys();
            int venueId;
            if (generatedKeys.next()) {
                venueId = generatedKeys.getInt(1);
                venue.setId(venueId);
            } else {
                throw new SQLException("Failed to retrieve venue ID.");
            }

            String menuQuery = "INSERT INTO meni (opis, cijena_po_osobi, Objekat_id) VALUES (?, ?, ?)";
            menuStmt = conn.prepareStatement(menuQuery);
            for (Menu menu : menus) {
                menuStmt.setString(1, menu.getOpis());
                menuStmt.setDouble(2, menu.getCijenaPoOsobi());
                menuStmt.setInt(3, venueId);
                menuStmt.executeUpdate();
            }

            String tableQuery = "INSERT INTO sto (broj_mjesta, Objekat_id) VALUES (?, ?)";
            tableStmt = conn.prepareStatement(tableQuery);
            for (int i = 0; i < tablesNumber; i++) {
                tableStmt.setInt(1, seatsPerTable);
                tableStmt.setInt(2, venueId);
                tableStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    //
                }
            }
            throw e;
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (venueStmt != null) venueStmt.close();
            if (menuStmt != null) menuStmt.close();
            if (tableStmt != null) tableStmt.close();
            if (conn != null) conn.close();
        }
    }

    public static void updateVenueStatus(int venueId, String status) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DBConnect();
            conn = connection;
            String query = "UPDATE objekat SET status = ? WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setInt(2, venueId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public static void addDeclineTextNotification(int objekatId, String text) {
        String query = "INSERT INTO obavjestenje (Objekat_id, tekst) VALUES (?, ?)";

        try {
            DBConnect();
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, objekatId);
            stmt.setString(2, text);
            stmt.executeUpdate();

            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadNotifications() {
        notifications.clear();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            DBConnect();
            conn = connection;
            String query = "SELECT * FROM obavjestenje";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                int objekatId = rs.getInt("Objekat_id");
                String tekst = rs.getString("tekst");
                Venue venue = venues.stream().filter(v -> v.getId() == objekatId).findFirst().orElse(null);
                notifications.add(new Notification(id, venue, tekst));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addCelebration(Celebration celebration) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            DBConnect();
            conn = connection;
            String query = "INSERT INTO proslava (Objekat_id, Klijent_id, Meni_id, proslavacol, datum, broj_gostiju, ukupna_cijena, uplacen_iznos) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, celebration.getObjekat().getId());
            pstmt.setInt(2, celebration.getKlijent().getId());
            pstmt.setInt(3, celebration.getMeni().getId());
            pstmt.setString(4, celebration.getProslavacol());
            pstmt.setObject(5, celebration.getDatum());
            pstmt.setInt(6, celebration.getBrojGostiju());
            pstmt.setDouble(7, celebration.getUkupnaCijena());
            pstmt.setDouble(8, celebration.getUplacenIznos());
            pstmt.executeUpdate();

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                celebration.setId(generatedKeys.getInt(1));
            }
            celebrations.add(celebration);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public static void updateVenueDates(int venueId, String datumiString) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DBConnect();
            conn = connection;
            String query = "UPDATE objekat SET datumi = ? WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, datumiString);
            stmt.setInt(2, venueId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public static void updateCelebrationProslavacol(int celebrationId, String proslavacol) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DBConnect();
            conn = connection;
            String query = "UPDATE proslava SET proslavacol = ? WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, proslavacol);
            stmt.setInt(2, celebrationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public static void transferMoney(String fromAccount, String toAccount, double amount) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtFrom = null;
        PreparedStatement stmtTo = null;
        try {
            DBConnect();
            conn = connection;
            conn.setAutoCommit(false);

            double fromBalance = getAccountBalance(fromAccount);
            if (fromBalance < amount) {
                throw new SQLException("Nedovoljno sredstava na računu pošiljaoca (" + fromAccount + "): " + fromBalance + " KM, potrebno: " + amount + " KM");
            }

            String checkAccountQuery = "SELECT COUNT(*) FROM `bankovni racun` WHERE broj_racuna = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkAccountQuery);
            checkStmt.setString(1, fromAccount);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next() || rs.getInt(1) == 0) {
                throw new SQLException("Račun pošiljaoca ne postoji: " + fromAccount);
            }
            checkStmt.setString(1, toAccount);
            rs = checkStmt.executeQuery();
            if (!rs.next() || rs.getInt(1) == 0) {
                throw new SQLException("Račun primaoca ne postoji: " + toAccount);
            }
            rs.close();
            checkStmt.close();

            String queryFrom = "UPDATE `bankovni racun` SET stanje = stanje - ? WHERE broj_racuna = ?";
            stmtFrom = conn.prepareStatement(queryFrom);
            stmtFrom.setDouble(1, amount);
            stmtFrom.setString(2, fromAccount);
            int rowsAffectedFrom = stmtFrom.executeUpdate();
            if (rowsAffectedFrom == 0) {
                throw new SQLException("Ažuriranje stanja pošiljaoca nije uspjelo za račun: " + fromAccount);
            }

            String queryTo = "UPDATE `bankovni racun` SET stanje = stanje + ? WHERE broj_racuna = ?";
            stmtTo = conn.prepareStatement(queryTo);
            stmtTo.setDouble(1, amount);
            stmtTo.setString(2, toAccount);
            int rowsAffectedTo = stmtTo.executeUpdate();
            if (rowsAffectedTo == 0) {
                throw new SQLException("Ažuriranje stanja primaoca nije uspjelo za račun: " + toAccount);
            }

            conn.commit();
            System.out.println("Transfer uspješan: " + amount + " KM sa računa " + fromAccount + " na račun " + toAccount);
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transakcija poništena zbog greške: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (stmtFrom != null) stmtFrom.close();
            if (stmtTo != null) stmtTo.close();
            if (conn != null) conn.close();
        }
    }

    public static void updateClientBalance(String accountNumber, double newBalance) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DBConnect();
            conn = connection;
            conn.setAutoCommit(false);

            double currentBalance = getAccountBalance(accountNumber);
            if (newBalance < 0 && currentBalance + newBalance < 0) {
                throw new SQLException("Insufficient funds: Balance cannot go below zero.");
            }

            String query = "UPDATE `bankovni racun` SET stanje = ? WHERE broj_racuna = ?";
            stmt = conn.prepareStatement(query);
            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Account not found or update failed.");
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public static void updateCelebrationMenu(int celebrationId, int menuId) throws SQLException {
        String updateQuery = "UPDATE proslava SET Meni_id = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sistemzaplaniranjeproslava", "root", "");
             PreparedStatement preparedStatement = conn.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, menuId);
            preparedStatement.setInt(2, celebrationId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No rows updated for celebrationId: " + celebrationId + ", menuId: " + menuId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }


    public static <T> List<T> retrieveDataFromTable(String tableName, Class<T> clazz) {
        List<T> list = new ArrayList<>();

        try {
            DBConnect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);

            Field[] fields = clazz.getDeclaredFields();

            while (resultSet.next()) {
                T obj = clazz.getDeclaredConstructor().newInstance();

                for (Field field : fields) {
                    field.setAccessible(true);
                    String columnName = field.getName();
                    if (tableName.equals("proslava")) {
                        if (columnName.equals("id")) {
                            field.setInt(obj, resultSet.getInt("id"));
                        } else if (columnName.equals("objekat")) {
                            int venueId = resultSet.getInt("Objekat_id");
                            Venue venue = (venues != null) ? venues.stream().filter(v -> v.getId() == venueId).findFirst().orElse(null) : null;
                            if (venue == null && venues == null) {
                                System.out.println("Venues list is null, cannot map Objekat_id: " + venueId);
                            } else if (venue == null) {
                                System.out.println("No venue found for Objekat_id: " + venueId);
                            }
                            field.set(obj, venue);
                        } else if (columnName.equals("klijent")) {
                            int clientId = resultSet.getInt("Klijent_id");
                            Client client = (clients != null) ? clients.stream().filter(c -> c.getId() == clientId).findFirst().orElse(null) : null;
                            if (client == null && clients == null) {
                                System.out.println("Clients list is null, cannot map Klijent_id: " + clientId);
                            } else if (client == null) {
                                System.out.println("No client found for Klijent_id: " + clientId);
                            }
                            field.set(obj, client);
                        } else if (columnName.equals("meni")) {
                            int menuId = resultSet.getInt("Meni_id");
                            Menu menu = (menus != null) ? menus.stream().filter(m -> m.getId() == menuId).findFirst().orElse(null) : null;
                            if (menu == null && menus == null) {
                                //System.out.println("Menus list is null, cannot map Meni_id: " + menuId);
                            } else if (menu == null) {
                                //System.out.println("No menu found for Meni_id: " + menuId);
                            }
                            field.set(obj, menu);
                        } else if (columnName.equals("datum")) {
                            java.sql.Date sqlDate = resultSet.getDate("datum");
                            field.set(obj, sqlDate != null ? sqlDate.toLocalDate() : null);
                        } else if (columnName.equals("proslavacol")) {
                            field.set(obj, resultSet.getString("proslavacol"));
                        } else if (columnName.equals("brojGostiju")) {
                            field.setInt(obj, resultSet.getInt("broj_gostiju"));
                        } else if (columnName.equals("ukupnaCijena")) {
                            field.setDouble(obj, resultSet.getDouble("ukupna_cijena"));
                        } else if (columnName.equals("uplacenIznos")) {
                            field.setDouble(obj, resultSet.getDouble("uplacen_iznos"));
                        }
                    } else if (tableName.equals("objekat")) {
                        if (columnName.equals("id")) columnName = "id";
                        else if (columnName.equals("vlasnik")) {
                            int ownerId = resultSet.getInt("Vlasnik_id");
                            Owner owner = (owners != null) ? owners.stream().filter(o -> o.getId() == ownerId).findFirst().orElse(null) : null;
                            if (owner == null && owners == null) {
                                System.out.println("Owners list is null, cannot map Vlasnik_id: " + ownerId);
                            } else if (owner == null) {
                                System.out.println("No owner found for Vlasnik_id: " + ownerId);
                            }
                            field.set(obj, owner);
                        } else if (columnName.equals("naziv")) columnName = "naziv";
                        else if (columnName.equals("cijena_rezervacije")) columnName = "cijena_rezervacije";
                        else if (columnName.equals("grad")) columnName = "grad";
                        else if (columnName.equals("adresa")) columnName = "adresa";
                        else if (columnName.equals("broj_mjesta")) columnName = "broj_mjesta";
                        else if (columnName.equals("broj_stolova")) columnName = "broj_stolova";
                        else if (columnName.equals("datumi")) {
                            String datumiString = resultSet.getString("datumi");
                            if (datumiString != null && !datumiString.isEmpty()) {
                                List<LocalDate> datumi = new ArrayList<>();
                                for (String dateStr : datumiString.split(",")) {
                                    datumi.add(LocalDate.parse(dateStr.trim()));
                                }
                                field.set(obj, datumi);
                            }
                        } else if (columnName.equals("zarada")) columnName = "zarada";
                        else if (columnName.equals("status")) columnName = "status";
                    } else if (tableName.equals("sto")) {
                        if (columnName.equals("capacity")) columnName = "broj_mjesta";
                        else if (columnName.equals("objekatId")) columnName = "Objekat_id";
                    } else if (tableName.equals("meni")) {
                        if (columnName.equals("description")) columnName = "opis";
                        else if (columnName.equals("price")) columnName = "cijena_po_osobi";
                        else if (columnName.equals("objekatId")) columnName = "Objekat_id";
                    }
                    try {
                        if (field.getType() == int.class) {
                            field.setInt(obj, resultSet.getInt(columnName));
                        } else if (field.getType() == String.class) {
                            String value = resultSet.getString(columnName);
                            field.set(obj, value != null ? value : "");
                        } else if (field.getType() == LocalDate.class) {
                            java.sql.Date sqlDate = resultSet.getDate(columnName);
                            field.set(obj, sqlDate != null ? sqlDate.toLocalDate() : null);
                        } else if (field.getType() == double.class) {
                            field.setDouble(obj, resultSet.getDouble(columnName));
                        } else if (field.getType() == Venue.class && tableName.equals("meni")) {
                            int venueId = resultSet.getInt("Objekat_id");
                            Venue venue = (venues != null) ? venues.stream().filter(v -> v.getId() == venueId).findFirst().orElse(null) : null;
                            if (venue == null && venues == null) {
                                System.out.println("Venues list is null, cannot map Objekat_id: " + venueId);
                            } else if (venue == null) {
                                System.out.println("No venue found for Objekat_id: " + venueId);
                            }
                            field.set(obj, venue);
                        }
                    } catch (SQLException e) {
                        continue;
                    }
                }
                list.add(obj);
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}