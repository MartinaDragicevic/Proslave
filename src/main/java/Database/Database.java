package Database;

import SistemZaPlaniranjeProslava.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

            String venueQuery = "INSERT INTO objekat (Vlasnik_id, naziv, cijena_rezervacije, grad, adresa, broj_mjesta, broj_stolova, datumi, zarada, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            venueStmt = conn.prepareStatement(venueQuery, Statement.RETURN_GENERATED_KEYS);
            venueStmt.setInt(1, venue.getOwnerId());
            venueStmt.setString(2, venue.getName());
            venueStmt.setDouble(3, venue.getReservationPrice());
            venueStmt.setString(4, venue.getPlace());
            venueStmt.setString(5, venue.getAddress());
            venueStmt.setInt(6, venue.getCapacity());
            venueStmt.setInt(7, venue.getBrojStolova());
            venueStmt.setString(8, venue.getDatumi());
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
                menuStmt.setString(1, menu.getDescription());
                menuStmt.setDouble(2, menu.getPrice());
                menuStmt.setInt(3, venueId);
                menuStmt.executeUpdate();
            }

            String tableQuery = "INSERT INTO sto (broj_mjesta, Objekat_id) VALUES (?, ?)";
            tableStmt = conn.prepareStatement(tableQuery);
            tableStmt.setInt(1, seatsPerTable);
            tableStmt.setInt(2, venueId);
            tableStmt.executeUpdate();

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
                    if (tableName.equals("objekat")) {
                        if (columnName.equals("ownerId")) columnName = "Vlasnik_id";
                        else if (columnName.equals("name")) columnName = "naziv";
                        else if (columnName.equals("reservationPrice")) columnName = "cijena_rezervacije";
                        else if (columnName.equals("place")) columnName = "grad";
                        else if (columnName.equals("adress")) columnName = "adresa";
                        else if (columnName.equals("capacity")) columnName = "broj_mjesta";
                        else if (columnName.equals("brojStolova")) columnName = "broj_stolova";
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
                        } else if (field.getType() == Date.class) {
                            field.set(obj, resultSet.getDate(columnName));
                        } else if (field.getType() == BigDecimal.class) {
                            field.set(obj, resultSet.getBigDecimal(columnName));
                        } else if (field.getType() == double.class) {
                            field.setDouble(obj, resultSet.getDouble(columnName));
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
