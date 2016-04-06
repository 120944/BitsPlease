package Test;

import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

class globals {
    static String DEFAULTSTRING = "I AM ERROR";
}

public class Program extends Application {

    public void start(Stage stage) {
        /** Add stuff here */
        Text text = new Text(0,0, globals.DEFAULTSTRING);
        text.setFont(new Font(40));
        Rectangle2D bounds = new Rectangle2D(0,0,360,480);
        Group grouped = new Group(text);

        /** Don't need to change these */
        // Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(new StackPane(grouped), bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.show();
        scene.setFill(Color.CORNFLOWERBLUE);

        /** EventHandlers and their usage */

        class setText implements EventHandler<MouseEvent> {
            private String changeTo;
            private setText(String changeTo) {
                this.changeTo = changeTo;
            }

            @Override
            public void handle(MouseEvent event) {
                text.setText(changeTo);
            }
        }

        scene.setOnMousePressed(new setText("Click"));
        scene.setOnMouseReleased(new setText(globals.DEFAULTSTRING));
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}

