package testing.model;

import testing.controller.MainTestController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Created by MTs on 25/08/16.
 *
 *
 */

public class TestApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(new StackPane());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/testing/view/MainTestWindow.fxml"));
        scene.setRoot(loader.load());
        MainTestController controller = loader.getController();
        controller.init();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {


        launch(args);
    }
}
