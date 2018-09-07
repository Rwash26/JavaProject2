/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareii;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import static softwareii.CustomerWindowController.addModCustomer;
import static softwareii.LoginWindowController.isLogin;

/**
 * FXML Controller class
 *
 * @author reggie.washington
 */
public class AddModWindowController implements Initializable {

    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private ComboBox<String> cityField;
    @FXML
    private ComboBox<String> countryField;
    @FXML
    private TextField postalCodeField;
    @FXML
    private TextField phoneField;
    @FXML
    private Button submitButton;

    PreparedStatement pstmt = null;

    boolean modify = false;

    private TableRow selectedCustomer;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void submitButtonAction(ActionEvent event) throws SQLException {

        if (nameField.equals("") || addressField.equals("") || cityField.equals("") || countryField.equals("") || postalCodeField.equals("") || phoneField.equals("")) {
            Alert ErrorAlert1 = new Alert(Alert.AlertType.ERROR);
            ErrorAlert1.setContentText("Please complete all fields.");
            ErrorAlert1.setHeaderText(null);
            ErrorAlert1.showAndWait().ifPresent((response -> {
                if (response == ButtonType.OK) {

                    ErrorAlert1.close();
                    return;
                }
            }));
        } else if (phoneField.getText().length() != 10) {
            Alert ErrorAlert2 = new Alert(Alert.AlertType.ERROR);
            ErrorAlert2.setContentText("Phone number must be 10 digits");
            ErrorAlert2.setHeaderText(null);
            ErrorAlert2.showAndWait().ifPresent((response -> {
                if (response == ButtonType.OK) {

                    ErrorAlert2.close();
                    return;
                }
            }));
        } else if (CustomerWindowController.isModify) {
            String updateCustomer = "UPDATE customer set customerName =?, addressId =? WHERE customerID=? ";
            pstmt = Database.getConn().prepareStatement(updateCustomer, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, nameField.getText());
            pstmt.setString(2, idField.getText());
            pstmt.setString(3, idField.getText());
            pstmt.executeUpdate();
            String updateAddress = "UPDATE address set address =?, postalCode =?, phone =? WHERE addressId=?";
            pstmt = Database.getConn().prepareStatement(updateAddress, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, addressField.getText());
            pstmt.setString(2, postalCodeField.getText());
            pstmt.setString(3, phoneField.getText());
            pstmt.setString(4, idField.getText());
            pstmt.executeUpdate();
            String country = countryField.getValue();

            String sql = "SELECT city.city "
                    + "FROM city, country "
                    + "WHERE city.countryId = country.countryId "
                    + "AND country.country = \"" + country + "\"";

            ResultSet rs = pstmt.executeQuery(sql);
            cityField.getItems().clear();

            try {
                while (rs.next()) {

                    cityField.getItems().add(rs.getString(1));

                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                Logger.getLogger(AddModWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
            ResultSet rs1 = pstmt.executeQuery("SELECT country FROM country");
            try {

                while (rs1.next()) {

                    countryField.getItems().add(rs1.getString(1));

                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                Logger.getLogger(AddModWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
            pstmt.executeUpdate();
            handleSceneChange();
        } else {
            insertDBAddress();
            insertDBCustomer();
            handleSceneChange();
        }
    }

    private void handleSceneChange() {
        Parent main = null;
        try {
            main = FXMLLoader.load(getClass().getResource("customerWindow.fxml"));
            Scene scene = new Scene(main);

            Stage stage = SoftwareII.getStage();

            stage.setScene(scene);

            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void initCustomer(TableRow row) {
        selectedCustomer = row;
        idField.setText(selectedCustomer.getCustomerID().getValue());
        nameField.setText(selectedCustomer.getCustomerName().getValue());
        addressField.setText(selectedCustomer.getAddress().getValue());
        cityField.getSelectionModel().select(selectedCustomer.getCity().getValue());
        countryField.getSelectionModel().select(selectedCustomer.getCountry().getValue());
        postalCodeField.setText(selectedCustomer.getPostalCode().getValue());
        phoneField.setText(selectedCustomer.getPhone().getValue());

    }

    String insertDBCustomer() {

        //Note that this assumes an autoincrement on the country id column!
        String sqlCustomer = "INSERT INTO customer (customerName,addressid, active, createDate, createdBy, lastUpdate, lastUpdateBy)"
                + " VALUES (?,LAST_INSERT_ID(),0,now(),'user1',now(),'user1')";
        String customerName = nameField.getText();
        String customerID = null;
        try {
            PreparedStatement ps = Database.getConn().prepareStatement(sqlCustomer);
            ps.setString(1, customerName);

            ps.execute();
            ps = Database.getConn().prepareStatement("SELECT LAST_INSERT_ID() FROM customer"); //retrieve newly assigned customer id
            ResultSet rs = ps.executeQuery();
            rs.next(); //only one record, so no need for a loop.
            customerID = rs.getString(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(AddModWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return customerID;
    }

    String insertDBAddress() {
        //select cityid from city where city =?
        String cityName = cityField.getSelectionModel().getSelectedItem();
        String sqlCity = "SELECT cityid FROM city WHERE city ='" + cityName + "'";

        String address = addressField.getText();
        String postalCode = postalCodeField.getText();
        String phone = phoneField.getText();
        String sqlAddress = "INSERT INTO address (address,address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)"
                + " VALUES (?,'',?,?,?,now(),'user1',now(),'user1')";

        String addressId = null;
        try {
            PreparedStatement ps = Database.getConn().prepareStatement(sqlCity);

            ResultSet rs = ps.executeQuery();
            rs.next();
            String cityId = rs.getString(1);

            ps = Database.getConn().prepareStatement(sqlAddress);

            ps.setString(1, address);
            ps.setString(2, cityId);
            ps.setString(3, postalCode);
            ps.setString(4, phone);

            ps.execute();

            ps = Database.getConn().prepareStatement("SELECT LAST_INSERT_ID() FROM address"); //retrieve newly assigned address id
            rs = ps.executeQuery();
            rs.next(); //only one record, so no need for a loop.
            addressId = rs.getString(1);

        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(AddModWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return addressId;
    }

    @FXML
    private void initializeCity() {
        String country = countryField.getValue();

        String sqlCountry = "SELECT city.city "
                + "FROM city, country "
                + "WHERE city.countryId = country.countryId "
                + "AND country.country = \"" + country + "\"";
        cityField.getItems().clear();
        try {
            PreparedStatement ps = Database.getConn().prepareStatement(sqlCountry);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                cityField.getItems().add(rs.getString(1));

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(AddModWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String sqlCountry = "SELECT country FROM country";
        cityField.getItems().clear();

        try {
            pstmt = Database.getConn().prepareStatement(sqlCountry);
            ResultSet rs1 = pstmt.executeQuery();

            while (rs1.next()) {

                countryField.getItems().add(rs1.getString(1));

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(AddModWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
