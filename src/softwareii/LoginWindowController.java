/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareii;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author reggie.washington
 */
public class LoginWindowController implements Initializable {

    @FXML
    private Label label;
    @FXML
    private Button login;
    @FXML
    private TextField userName;

    protected static String user;

    static boolean isLogin = false;
    @FXML
    private PasswordField password;

    Logger log = Logger.getLogger("log.txt");
    
    ResourceBundle rb;

    //SELECT * FROM user WHERE userName = ? AND password =?
    public void authenticateUser() {
        String authUser = "SELECT * FROM user WHERE userName =? AND password =?";

        try {
            PreparedStatement ps = Database.getConn().prepareStatement(authUser);
            ps.setString(1, userName.getText());
            ps.setString(2, password.getText());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                
            isLogin = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(AddModWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

        @FXML
        private void handleButtonAction
        (ActionEvent event
        
            ) {
            authenticateUser();
        if (isLogin == true) {
                user = userName.getText();
                log.info(user + " logged in");
                handleSceneChange();
            } else {
                Alert ErrorAlert1 = new Alert(Alert.AlertType.CONFIRMATION, rb.getString("errorMsg"), ButtonType.OK); //change to spanish
                ErrorAlert1.showAndWait();
                user = userName.getText();
                log.info(user + "logged in");
                if (ErrorAlert1.getResult() == ButtonType.OK) {
                    ErrorAlert1.close();
                    return;
                }
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

    public static boolean isLogin() {
        return isLogin;
    }

    public void loginlogger() {
        try {
            FileHandler fh = new FileHandler("log.txt");
            SimpleFormatter sf = new SimpleFormatter();
            fh.setFormatter(sf);
            log.addHandler(fh);

        } catch (IOException ex) {
            Logger.getLogger(LoginWindowController.class.getName()).log(Level.INFO, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(LoginWindowController.class.getName()).log(Level.INFO, null, ex);
        }

        log.setLevel(Level.INFO);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       // Locale.setDefault(new Locale("en", "EN"));
       
        Locale.setDefault(new Locale("es", "ES"));
        rb = ResourceBundle.getBundle("language_files/rb");
        userName.setPromptText(rb.getString("username"));
        password.setPromptText(rb.getString("password"));      
        login.setText(rb.getString("login"));
        System.out.println(rb.getString("intro"));
        loginlogger();
        authenticateUser();

    }

    /**
     * @return the userName
     */
    public static String getUserName() {

        return user;
    }

}
