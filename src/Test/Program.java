package Test;

import javafx.application.Application;
import javafx.event.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Program extends Application {

    public void start(Stage stage) {
        Text text = new Text(10,40, "Hello World");
        text.setFont(new Font(40));
        MyButton button = new MyButton("8==D");
        button.relocate(10,10);
        Scene scene = new Scene(new Group(text,button));

        stage.setTitle("not so test");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        scene.setFill(Color.CORNFLOWERBLUE);

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

        class ButtonAction implements EventHandler<MouseEvent> {
            @Override
            public void handle(MouseEvent event) {
                if (button.getTimesClicked() < 10) {
                    button.increment();
                }
                else {
                    button.setText("Stop clicking me");
                }
            }
        }
        button.setOnMouseClicked(new ButtonAction());
        scene.setOnMouseEntered(new setText("In Border"));
        scene.setOnMouseExited(new setText("Out of Border"));
        scene.setOnMousePressed(new setText("Click"));
        scene.setOnMouseReleased(new setText("Clack"));
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}

