package us.malfeasant.ldapspoof;

import java.io.IOException;

import org.tinylog.Logger;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    private static int LDAP_PORT = 389;
    private final ObjectProperty<Listen> listener = new SimpleObjectProperty<>();

    @Override
    public void start(Stage stage) {
        var portLabel = new Label("Port:");
        
        var portField = new TextField(Integer.toString(LDAP_PORT));
        portField.setMaxWidth(50);
        portField.setTextFormatter(new TextFormatter<>(change -> {
            // accept only digits
            return change.getText().matches("\\d*") ? change : null;
        }));

        var button = new Button("Listen");
        button.disableProperty().bind(listener.isNotNull());
        portField.disableProperty().bind(listener.isNotNull());
        button.setOnAction(e -> {
            try {
                listener.set(new Listen(
                    portField.getText().equals("") ? 0 :
                    Integer.parseInt(portField.getText())
                ));
                portField.textProperty().bind(listener.get().portProperty.asString());
            } catch (NumberFormatException e1) {
                // This shouldn't happen with the TextFormatter, but just in case...
                Logger.error(e1, "Non-number entered in port field.");
            } catch (IOException e1) {
                // TODO Something went wrong opening socket...
                // what to do depends on what can go wrong...
                e1.printStackTrace();
            }
        });

        var layout = new GridPane();
        layout.addRow(0, portLabel, portField, button);
        layout.setVgap(5);
        layout.setHgap(5);
        layout.setPadding(new Insets(5));
        
        var scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}