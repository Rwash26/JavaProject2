/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareii;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author reggie.washington
 */
public class SoftwareII extends Application {

    static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        Database.accessDB();
        Parent root = FXMLLoader.load(getClass().getResource("loginWindow.fxml"));

        Scene scene = new Scene(root);
        this.stage = stage;
        stage.setScene(scene);
        stage.show();

        //  String sql = "jdbc:mysql://52.206.157.109/U04cUB";
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /* 
        Server name:  52.206.157.109 
        Database name:  U04cUB
        Username:  U04cUB
        Password:  53688204428
        
        Make appointment window next
         */
        launch(args);

    }

    public static Stage getStage() {
        return stage;
    }

}
