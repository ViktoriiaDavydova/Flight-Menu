package application;

import controllers.MainWindowController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Entry point for application.
 *
 * @author Nick Hamnett
 * 
 * @version 2019-06-16
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load the FXML file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainWindow.fxml"));

        // Get the parent node of the FXML file.
        Parent root = loader.load();

        // Sets the title of the window.
        primaryStage.setTitle("Ticket To Ride Travel");

        // Sets the scene to the parent node and the width and height of the window.
        primaryStage.setScene(new Scene(root, 605, 597));

        // Displays the window.
        primaryStage.show();

        // Get the main window controller.
        MainWindowController mainWindowController = loader.getController();

        // Call the onCreated method since the window has been created.
        mainWindowController.onCreated();

        // Attach listener for when window is closed.
        primaryStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // Call onClosing method when window is closed.
                mainWindowController.onClosing();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
