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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import static softwareii.CustomerWindowController.isModify;

/**
 * FXML Controller class
 *
 * @author reggie.washington
 */
public class AppointmentController implements Initializable {

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableColumn<Appointment, String> appointmentIdCol;

    @FXML
    private TableColumn<Appointment, String> titleCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> startCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> endCol;

    private Appointment selectedAppointment;

    @FXML
    private Button returnButton;
    @FXML
    static boolean isModify = false;

    ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    @FXML
    private void returnButtonAction(ActionEvent event) {
        
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

    @FXML
    private void addAppointmentButtonAction(ActionEvent event) {
        Parent main = null;

        try {
            main = FXMLLoader.load(getClass().getResource("addAppWindow.fxml"));

            Scene scene = new Scene(main);

            Stage stage = SoftwareII.getStage();

            stage.setScene(scene);

            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void modifyAppointmentButtonAction(ActionEvent event) {
        Parent main = null;
        Appointment selectedAppointment = (appointmentTable.getSelectionModel().getSelectedItem());

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("addAppWindow.fxml"));
            main = loader.load();
            AddAppWindowController controller = loader.getController();
            controller.initAppointment(appointmentTable.getSelectionModel().getSelectedItem());
            isModify = true;

            Scene scene = new Scene(main);

            Stage stage = SoftwareII.getStage();

            stage.setScene(scene);

            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void filterAppointmentButtonAction(ActionEvent event) {
        LocalDate now = LocalDate.now();
        LocalDate nowPlus7 = now.plusDays(7);
        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentList);
        filteredData.setPredicate(row -> {

            Object objRowDate = row.getStart().getValue();
            String strRowDate = objRowDate.toString().substring(0, 10);

            LocalDate rowDate = LocalDate.parse(strRowDate, DateTimeFormatter.ISO_LOCAL_DATE);
            return rowDate.isAfter(now) && rowDate.isBefore(nowPlus7);
        });
        appointmentTable.setItems(filteredData);
    }

    @FXML
    private void filterAppointmentButtonAction2(ActionEvent event) {
        LocalDate now = LocalDate.now();
        LocalDate nowPlusMonth = now.plusMonths(1);
        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentList);
        filteredData.setPredicate(row -> {

            Object objRowDate = row.getStart().getValue();
            String strRowDate = objRowDate.toString().substring(0, 10);

            LocalDate rowDate = LocalDate.parse(strRowDate, DateTimeFormatter.ISO_LOCAL_DATE);
            return rowDate.isAfter(now) && rowDate.isBefore(nowPlusMonth);
        });
        appointmentTable.setItems(filteredData);
    }

    @Override
    public void initialize(URL l, ResourceBundle resources) {

        appointmentIdCol.setCellValueFactory(cellData -> {
            return cellData.getValue().getAppointmentId();
        });
        titleCol.setCellValueFactory(cellData -> {
            return cellData.getValue().getTitle();
        });
        startCol.setCellValueFactory(cellData -> {
            return cellData.getValue().getStart();
        });
        endCol.setCellValueFactory(cellData -> {
            return cellData.getValue().getEnd();
        });

        String sqlAppointment = "SELECT appointmentid, customerid, title, start, end FROM appointment WHERE customerId = ?";

        try {
            PreparedStatement ps = Database.getConn().prepareStatement(sqlAppointment);
            ps.setString(1, CustomerWindowController.customer);
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst(); //this is needed because the result set was looped through in accessDB.  We need to reset the cursor! 
            while (rs.next()) {
                String appointmentId = rs.getString("appointmentId");
                String customerId = rs.getString("customerId");//parameter is the column name in the database
                String title = rs.getString("title");
                Timestamp start = rs.getTimestamp("start");
                Timestamp end = rs.getTimestamp("end");

                ZoneId newzid = ZoneId.systemDefault();

                ZonedDateTime newzdtStart = start.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime newzdtEnd = end.toLocalDateTime().atZone(ZoneId.of("UTC"));

                ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(newzid);
                ZonedDateTime newLocalEnd = newzdtEnd.withZoneSameInstant(newzid);

                Appointment ap = new Appointment(new ReadOnlyStringWrapper(appointmentId),
                        new ReadOnlyStringWrapper(title),
                        new ReadOnlyObjectWrapper(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(newLocalStart)),
                        new ReadOnlyObjectWrapper(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(newLocalEnd)),
                        new ReadOnlyStringWrapper(customerId));
                appointmentList.add(ap);
            }
            //initAppointment(selectedAppointment);
            appointmentTable.setItems(appointmentList);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(AppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
