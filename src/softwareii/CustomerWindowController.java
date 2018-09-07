/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareii;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author reggie.washington
 */
public class CustomerWindowController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TableView<TableRow> customerTable;

    @FXML
    private TableColumn<TableRow, String> customerIDCol;

    @FXML
    private TableColumn<TableRow, String> customerNameCol;

    @FXML
    private TableColumn<TableRow, String> addressCol;

    @FXML
    private TableColumn<TableRow, String> cityCol;

    @FXML
    private TableColumn<TableRow, String> postalCodeCol;

    @FXML
    private TableColumn<TableRow, String> countryCol;

    @FXML
    private TableColumn<TableRow, String> phoneCol;

    @FXML
    private Button addCustomer;
    @FXML
    private Button modifyCustomer;

    @FXML
    private Button deleteCustomer;

    @FXML
    private Button appointments;

    static TableRow addModCustomer;

    static TableRow addNewCustomer;

    static TableRow selectedCustomer = null;

    static String customer = "0";

    @FXML
    static boolean isAppointment = false;
    @FXML
    static boolean isModify = false;

    private ObservableList<TableRow> customerList = FXCollections.observableArrayList();

    @FXML
    private void addButtonAction(ActionEvent event) {

        handleSceneChange();
    }

    public static TableRow getSelectedCustomer() {
        return selectedCustomer;
    }

    @FXML
    private void modifyButtonAction(ActionEvent event) throws SQLException {

        TableRow selectedCustomer = (customerTable.getSelectionModel().getSelectedItem());
        handleSceneChange1();

    }

    public void deleteButtonAction() throws SQLException {

        TableRow selectedCustomer = (customerTable.getSelectionModel().getSelectedItem());
        customerList.remove(selectedCustomer);
        customerTable.getItems().remove(selectedCustomer);
        String customerID = selectedCustomer.getCustomerID().getValue();
        String sqlDelete = "delete from customer where customerid='" + customerID + "'";

        try {
            // prepare statement
            PreparedStatement ps = Database.getConn().prepareStatement(sqlDelete);

            // set param
            ps.execute();

            // execute SQL
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(CustomerWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void appointmentsButtonAction(ActionEvent event) {

        if (customerTable.getSelectionModel().getSelectedItem() == null) {

            Alert ErrorAlert2 = new Alert(Alert.AlertType.ERROR, "Select a customer", ButtonType.OK);
            ErrorAlert2.showAndWait();
            if (ErrorAlert2.getResult() == ButtonType.OK) {
                ErrorAlert2.close();
                return;
            }

        } else {
            selectedCustomer = (customerTable.getSelectionModel().getSelectedItem());

            customer = selectedCustomer.getCustomerID().getValue();

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("appointmentWindow.fxml"));
                Parent main = loader.load();
                AppointmentController controller = loader.getController();
                //   controller.initAppointment(appointmentList.getSelectionModel().getSelectedItem());
                Scene scene = new Scene(main);

                Stage stage = SoftwareII.getStage();

                stage.setScene(scene);

                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleSceneChange() {
        Parent main = null;

        try {
            main = FXMLLoader.load(getClass().getResource("addModWindow.fxml"));

            Scene scene = new Scene(main);

            Stage stage = SoftwareII.getStage();

            stage.setScene(scene);

            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void handleSceneChange1() {
        // Parent main = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("addModWindow.fxml"));
            Parent main = loader.load();
            AddModWindowController controller = loader.getController();
            controller.initCustomer(customerTable.getSelectionModel().getSelectedItem());
            isModify = true;

            Scene scene = new Scene(main);

            Stage stage = SoftwareII.getStage();

            stage.setScene(scene);

            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static TableRow getAddModCustomer() {
        return addModCustomer;
    }

    public TableRow getAddNewCustomer() {
        return addNewCustomer;
    }

    public static boolean isAppointment() {
        return isAppointment;
    }

    public void generateAlerts() {

        String sqlAlarm = "SELECT start FROM appointment WHERE createdBy =?";

        ZoneId zid = ZoneId.systemDefault();

        LocalDateTime now = LocalDateTime.now();

        ZonedDateTime localNow = now.atZone(zid);

        ZonedDateTime UTCNow = localNow.withZoneSameInstant(ZoneId.of("UTC"));

        now = UTCNow.toLocalDateTime();

        try {
            // prepare statement
            PreparedStatement ps = Database.getConn().prepareStatement(sqlAlarm);
            ps.setString(1, LoginWindowController.user);
            ResultSet rs = ps.executeQuery();
           
            while (rs.next()) {

                LocalDateTime appointmentStart = rs.getTimestamp(1).toLocalDateTime();
System.out.println(now + " " + appointmentStart);
                if (appointmentStart.plusMinutes(15).isAfter(now)) {

                    Alert AppointmentAlert = new Alert(Alert.AlertType.INFORMATION, "You have an upcoming appointment at " + appointmentStart.toString(), ButtonType.OK);
                    AppointmentAlert.showAndWait();
                    if (AppointmentAlert.getResult() == ButtonType.OK) {
                        AppointmentAlert.close();

                    }

                }
                
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(CustomerWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //REPORTS GENERATED AUTOMATICALLY WHEN USER LOGS IN
    //Generates a report of the total number of customers. Txt document can be found in file location. Open with WordPad.
    public void generateCustomers() {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(
                    new File("Customers.txt"),
                    true /* append = true */));

            String sqlCustomers = "SELECT count(*) FROM customer";

            try {
                // prepare statement
                PreparedStatement ps = Database.getConn().prepareStatement(sqlCustomers);
                ResultSet rs = ps.executeQuery();
                rs.beforeFirst(); //this is needed because the result set was looped through in accessDB.  We need to reset the cursor! 
                pw.append("Total Customers: ");
                while (rs.next()) {

                    pw.append(rs.getString(1));
                    pw.close();

                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                Logger.getLogger(CustomerWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CustomerWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    //REPORTS GENERATED AUTOMATICALLY WHEN USER LOGS IN
    //Generates appointment count of each type by month when user logs in. Txt document can be found in file location. Open with WordPad
    public void generateAppointments() {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(
                    new File("Appointments.txt"),
                    true ));

            String sqlCustomers = "SELECT title, count(title),month(start) FROM appointment GROUP BY title ORDER BY month(start)";

            try {
                // prepare statement
                PreparedStatement ps = Database.getConn().prepareStatement(sqlCustomers);

                ResultSet rs = ps.executeQuery();
                rs.beforeFirst(); 
                pw.append("Type\tCount\tMonth\n");
                while (rs.next()) {
                   

                    pw.append(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\n"); 
                    

                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                Logger.getLogger(CustomerWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CustomerWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    //REPORTS GENERATED AUTOMATICALLY WHEN USER LOGS IN
    //Generates schedule of each consultant by title, start, and end times. Txt document can be found in file location. Open with WordPad
    public void generateSchedule() {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(
                    new File("Schedule.txt"),
                    true ));

            String sqlCustomers = "SELECT title, start, end, contact FROM appointment ORDER BY contact;";

            try {
                // prepare statement
                PreparedStatement ps = Database.getConn().prepareStatement(sqlCustomers);

                ResultSet rs = ps.executeQuery();
                rs.beforeFirst(); 
                pw.append("Type\tStart\tEnd\tConsultant\n");
                while (rs.next()) {
                   

                    pw.append(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4)+"\n"); 
                    

                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                Logger.getLogger(CustomerWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CustomerWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        generateAlerts();
        generateCustomers();
        generateAppointments();
        generateSchedule();

        customerIDCol.setCellValueFactory(cellData -> {
            return cellData.getValue().getCustomerID();
        });
        customerNameCol.setCellValueFactory(cellData -> {
            return cellData.getValue().getCustomerName();
        });
        addressCol.setCellValueFactory(cellData -> {
            return cellData.getValue().getAddress();
        });
        cityCol.setCellValueFactory(cellData -> {
            return cellData.getValue().getCity();
        });
        countryCol.setCellValueFactory(cellData -> {
            return cellData.getValue().getCountry();
        });
        postalCodeCol.setCellValueFactory(cellData -> {
            return cellData.getValue().getPostalCode();
        });
        phoneCol.setCellValueFactory(cellData -> {
            return cellData.getValue().getPhone();
        });

        String sqlCustomer = "SELECT customerid, customerName, address, city, country, postalCode, phone FROM customer, address, city, country  WHERE city.countryID = country.countryID AND address.cityId = city.cityId AND customer.addressId = address.addressId";

        try {
            PreparedStatement ps = Database.getConn().prepareStatement(sqlCustomer);
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst(); //this is needed because the result set was looped through in accessDB.  We need to reset the cursor! 
            while (rs.next()) {
                String customerID = rs.getString("customerID"); //parameter is the column name in the database
                String customerName = rs.getString("customerName");
                String address = rs.getString("address");
                String city = rs.getString("city");
                String country = rs.getString("country");
                String postalCode = rs.getString("postalCode");
                String phone = rs.getString("phone");
                TableRow tr = new TableRow(new ReadOnlyStringWrapper(customerID),
                        new ReadOnlyStringWrapper(customerName),
                        new ReadOnlyStringWrapper(address),
                        new ReadOnlyStringWrapper(city),
                        new ReadOnlyStringWrapper(country),
                        new ReadOnlyStringWrapper(postalCode),
                        new ReadOnlyStringWrapper(phone));
                customerList.add(tr);
            }
            customerTable.setItems(customerList);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(CustomerWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
