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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
public class AddAppWindowController implements Initializable {

    @FXML
    private TextField idField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField startField;
    @FXML
    private TextField endField;
    @FXML
    private Button submitButton;

    PreparedStatement pstmt = null;

    boolean modify = false;

    private Appointment selectedAppointment;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void submitButtonAction(ActionEvent event) throws SQLException {

        String start = startField.getText();

        start = start.substring(11, 16);

        String end = endField.getText();

        end = end.substring(11, 16);

        LocalTime localStart = LocalTime.parse(start, DateTimeFormatter.ISO_LOCAL_TIME);

        LocalTime localEnd = LocalTime.parse(end, DateTimeFormatter.ISO_LOCAL_TIME);

        LocalTime opening = LocalTime.of(9, 0);

        LocalTime closing = LocalTime.of(17, 0);

        if (titleField.getText().equals("") || startField.getText().equals("") || endField.getText().equals("")) {
            Alert ErrorAlert1 = new Alert(Alert.AlertType.ERROR);
            ErrorAlert1.setContentText("Please complete all fields.");
            ErrorAlert1.setHeaderText(null);
            ErrorAlert1.showAndWait().ifPresent((response -> {
                if (response == ButtonType.OK) {

                    ErrorAlert1.close();
                    return;
                }
            }));

        } else {

            if (AppointmentController.isModify) {
                
                String start1 = startField.getText();
                String end1 = endField.getText();

                ZoneId zid = ZoneId.systemDefault();
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss");

                LocalDateTime ldtStart = LocalDateTime.parse(start1, df);
                LocalDateTime ldtEnd = LocalDateTime.parse(end1, df);

                ZonedDateTime zdtStart = ldtStart.atZone(zid);
                ZonedDateTime zdtEnd = ldtEnd.atZone(zid);

                ZonedDateTime utcStart = zdtStart.withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime utcEnd = zdtEnd.withZoneSameInstant(ZoneId.of("UTC"));

                ldtStart = utcStart.toLocalDateTime();
                ldtEnd = utcEnd.toLocalDateTime();

                Timestamp startsqlts = Timestamp.valueOf(ldtStart);
                Timestamp endsqlts = Timestamp.valueOf(ldtEnd);

                String updateAppointment = "UPDATE appointment set title =?, start =?, end =? WHERE appointmentid = ?";
                pstmt = Database.getConn().prepareStatement(updateAppointment, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, titleField.getText());
                pstmt.setString(2, startsqlts.toString());
                pstmt.setString(3, endsqlts.toString());
                pstmt.setString(4, idField.getText());

                if (localStart.isBefore(opening) || localStart.isAfter(closing)) {
                    Alert ErrorAlert2 = new Alert(Alert.AlertType.CONFIRMATION, "Appointment is outside business hours.", ButtonType.OK);
                    ErrorAlert2.showAndWait();
                    if (ErrorAlert2.getResult() == ButtonType.OK) {
                        ErrorAlert2.close();
                     //   return;

                    }
                } else if (localEnd.isAfter(closing) || localEnd.isBefore(opening)) {
                    Alert ErrorAlert3 = new Alert(Alert.AlertType.CONFIRMATION, "Appointment is outside business hours.", ButtonType.OK);
                    ErrorAlert3.showAndWait();
                    if (ErrorAlert3.getResult() == ButtonType.OK) {
                        ErrorAlert3.close();
                      //  return;
                    }
                }
                appointmentOverlap();
                pstmt.executeUpdate();
                handleSceneChange1();

            } else {
                if (localStart.isBefore(opening) || localStart.isAfter(closing)) {
                    Alert ErrorAlert4 = new Alert(Alert.AlertType.CONFIRMATION, "Appointment is outside business hours.", ButtonType.OK);
                    ErrorAlert4.showAndWait();
                    if (ErrorAlert4.getResult() == ButtonType.OK) {
                        ErrorAlert4.close();
                        //    return;
                    }
                } else if (localEnd.isAfter(closing) || localEnd.isBefore(opening)) {
                    Alert ErrorAlert5 = new Alert(Alert.AlertType.CONFIRMATION, "Appointment is outside business hours.", ButtonType.OK);
                    ErrorAlert5.showAndWait();
                    if (ErrorAlert5.getResult() == ButtonType.OK) {
                        ErrorAlert5.close();
                        //   return;

                    }
                }
                appointmentOverlap();
                insertDBAppointment();
                handleSceneChange1();
            }
        }
    }

    public boolean appointmentOverlap() throws SQLException {
        String selectStart = "SELECT customerid, start, end FROM appointment WHERE customerid =?";

        try {
            PreparedStatement ps = Database.getConn().prepareStatement(selectStart);
            ps.setString(1, CustomerWindowController.customer);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                LocalDateTime existingStart = rs.getTimestamp(2).toLocalDateTime();
                LocalDateTime existingEnd = rs.getTimestamp(3).toLocalDateTime();

                String pendingStart = startField.getText();
                String pendingEnd = endField.getText();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                LocalDateTime localStart = LocalDateTime.parse(pendingStart, formatter);
                LocalDateTime localEnd = LocalDateTime.parse(pendingEnd, formatter);

                if (localStart.isAfter(existingStart) && localStart.isBefore(existingEnd)) {

                    //existingStart.isBefore(localEnd)) {
                    Alert ErrorAlert1 = new Alert(Alert.AlertType.ERROR);
                    ErrorAlert1.setContentText("Appointment start is between existing appointment.");
                    ErrorAlert1.setHeaderText(null);
                    ErrorAlert1.showAndWait().ifPresent((response -> {
                        if (response == ButtonType.OK) {

                            ErrorAlert1.close();
                            return;
                        }
                    }));

                } else if (localEnd.isAfter(existingStart) && localEnd.isBefore(existingEnd)) {

                    //existingEnd.isAfter(localStart)) {
                    Alert ErrorAlert2 = new Alert(Alert.AlertType.ERROR, "Appointment end is between existing appointment.", ButtonType.OK);
                    ErrorAlert2.showAndWait();
                    if (ErrorAlert2.getResult() == ButtonType.OK) {
                        ErrorAlert2.close();
                        return true;
                    }

                } else if (localStart.isBefore(existingStart) && localEnd.isAfter(existingEnd)) {
                    Alert ErrorAlert2 = new Alert(Alert.AlertType.ERROR, "Appointment is overlapping existing appointment.", ButtonType.OK);
                    ErrorAlert2.showAndWait();
                    if (ErrorAlert2.getResult() == ButtonType.OK) {
                        ErrorAlert2.close();
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(AddModWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
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

    private void handleSceneChange1() {
        Parent main = null;
        try {
            main = FXMLLoader.load(getClass().getResource("appointmentWindow.fxml"));
            Scene scene = new Scene(main);

            Stage stage = SoftwareII.getStage();

            stage.setScene(scene);

            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void initAppointment(Appointment app) {
        selectedAppointment = app;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        idField.setText(selectedAppointment.getAppointmentId().getValue());
        titleField.setText(selectedAppointment.getTitle().getValue());
        Object start = selectedAppointment.getStart().getValue();
        startField.setText(start.toString());
        Object end = selectedAppointment.getEnd().getValue();
        endField.setText(end.toString());
    }

    String insertDBAppointment() {
        //Note that this assumes an autoincrement on the country id column! 
        String sqlAppointment = "INSERT INTO appointment (customerId,title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy)"
                + " VALUES (?,?,'','','user1','',?,?,now(),'user1',now(),'user1')";
        String customerId = CustomerWindowController.getSelectedCustomer().getCustomerID().getValue();
        String title = titleField.getText();
        String start = startField.getText();
        String end = endField.getText();
        String appointmentID = null;

        ZoneId zid = ZoneId.systemDefault();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss");

        LocalDateTime ldtStart = LocalDateTime.parse(start, df);
        LocalDateTime ldtEnd = LocalDateTime.parse(end, df);

        ZonedDateTime zdtStart = ldtStart.atZone(zid);
        ZonedDateTime zdtEnd = ldtEnd.atZone(zid);

        ZonedDateTime utcStart = zdtStart.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime utcEnd = zdtEnd.withZoneSameInstant(ZoneId.of("UTC"));

        ldtStart = utcStart.toLocalDateTime();
        ldtEnd = utcEnd.toLocalDateTime();

        Timestamp startsqlts = Timestamp.valueOf(ldtStart);
        Timestamp endsqlts = Timestamp.valueOf(ldtEnd);

        try {
            PreparedStatement ps = Database.getConn().prepareStatement(sqlAppointment);
            ps.setString(1, customerId);
            ps.setString(2, title);
            ps.setString(3, startsqlts.toString());
            ps.setString(4, endsqlts.toString());

            ps.execute();
            ps = Database.getConn().prepareStatement("SELECT LAST_INSERT_ID() FROM appointment"); //retrieve newly assigned customer id
            ResultSet rs = ps.executeQuery();
            rs.next();
            appointmentID = rs.getString(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger
                    .getLogger(AddAppWindowController.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

        return appointmentID;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
