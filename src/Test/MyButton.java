package Test;

import javafx.scene.control.Button;

public class MyButton extends Button{
    private int timesClicked;

    public MyButton(String text) {
        super(text);
    }

    public void increment() {
        timesClicked++;
    }

    public int getTimesClicked() {
        return timesClicked;
    }
}
