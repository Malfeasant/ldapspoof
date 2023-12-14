package us.malfeasant.ldapspoof;

import org.tinylog.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    private static int LDAP_PORT = 389;
    @Override
    public void start(Stage stage) {
        var portLabel = new Label("Port:");
        var portField = new TextField(Integer.toString(LDAP_PORT));
        var button = new Button("Listen");
        button.setOnAction(e -> {
            try {
                var listen = new Listen(
                    Integer.parseInt(portField.getText())
                );
                button.disableProperty().bind(listen.isRunning);
            } catch (NumberFormatException e1) {
                Logger.error(e1, "Non-number entered in port field.");
            }
        });
        var layout = new GridPane();
        layout.addRow(0, portLabel, portField, button);
        var scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}