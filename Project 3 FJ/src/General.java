import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.io.File;
import java.util.Optional;

/**
 * Created by floris-jan on 14-04-16.
 */
public class General {
    public static String backgroundImageFileString;
    //Returns the Welcome-scene
    public static VBox getStartScene() {
        VBox startView = new VBox();
        startView.setPrefSize(Main.width, Main.height);
        startView.setAlignment(Pos.CENTER);

        Text startText = new Text();
        startText.setText("Welcome!");
        startText.setFont(Font.font("null", FontWeight.BOLD, 120));

        Text startTextSub = new Text();
        startTextSub.setText("Start using this application by selecting something from the menu.");
        startTextSub.setFont(Font.font("null", FontWeight.LIGHT, 30));
        if(Main.scene != null) { startTextSub.setWrappingWidth(startText.getWrappingWidth()); }
        else { startTextSub.setWrappingWidth(Main.width); }
        startTextSub.setTextAlignment(TextAlignment.CENTER);

        startView.setBackground(getBackground(startView.getWidth(), startView.getHeight()));
        startView.getChildren().addAll(startText, startTextSub);
        return startView;
    }

    //Returns the Help-scene
    public static VBox getHelpScene() {
        VBox sceneView = new VBox();
        sceneView.setAlignment(Pos.CENTER);

        Text sceneText = new Text();
        sceneText.setText("Help");
        sceneText.setFont(Font.font("null", FontWeight.LIGHT, 40));

        Text sceneTextSub = new Text();
        sceneTextSub.setText("You can view the different graphical representations of our data using the Statistics submenu in the menubar.");
        sceneTextSub.setFont(Font.font("null", FontWeight.LIGHT, 12));
        if(Main.scene != null) { sceneTextSub.setWrappingWidth(sceneText.getWrappingWidth()); }
        else { sceneTextSub.setWrappingWidth(Main.width); }
        sceneTextSub.setTextAlignment(TextAlignment.CENTER);

        sceneView.setBackground(getBackground(sceneView.getWidth(), sceneView.getHeight()));
        sceneView.getChildren().addAll(sceneText, sceneTextSub);
        return sceneView;
    }

    //Returns the Preferences-scene
    public static VBox getPreferencesScene() {
        VBox sceneView = new VBox();
        sceneView.setPrefSize(Main.width, Main.height);
        sceneView.setSpacing(10);
        sceneView.setAlignment(Pos.CENTER);

        Text sceneText = new Text();
        sceneText.setText("Preferences");
        sceneText.setFont(Font.font("null", FontWeight.LIGHT, 40));

        /* changed to use lambda, removed redundant if/else statement */
        CheckBox staticMapBox = new CheckBox();
        staticMapBox.setText("Enable Static Maps");
        if(Main.staticMap) { staticMapBox.setSelected(true); }
        staticMapBox.setOnAction((q) -> {
            Main.staticMap = !Main.staticMap;
        });

        /* changed to use lambda, removed redundant if/else statement */
        CheckBox openInNewWindowBox = new CheckBox();
        openInNewWindowBox.setText("Open every chart in a seperate window");
        if(Main.openInNewWindow) { openInNewWindowBox.setSelected(true); }
        openInNewWindowBox.setOnAction((p) -> {
            Main.openInNewWindow = !Main.openInNewWindow;
        });

        ComboBox backgroundImageComboBox = new ComboBox();
        backgroundImageComboBox.getItems().addAll(
                "None",
                "Background 1.png",
                "Background 2.png",
                "Background 3.png",
                "Background 4.png"
        );
        backgroundImageComboBox.setPromptText("Select a background");
        backgroundImageComboBox.setEditable(true);
        backgroundImageComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                backgroundImageFileString = backgroundImageComboBox.getSelectionModel().getSelectedItem().toString();
                Main.borderPane.setCenter(getPreferencesScene());
            }
        });
        backgroundImageComboBox.setValue(backgroundImageFileString);

        sceneView.setBackground(getBackground(sceneView.getWidth(), sceneView.getHeight()));
        sceneView.getChildren().addAll(sceneText, staticMapBox, openInNewWindowBox, backgroundImageComboBox);
        return sceneView;
    }

    //Returns the About-scene
    public static VBox getAboutScene() {
        VBox sceneView = new VBox();
        sceneView.setAlignment(Pos.CENTER);

        Text sceneText = new Text();
        sceneText.setText("Copyright© BitsPlease 2016");
        sceneText.setFont(Font.font("null", FontWeight.LIGHT, 50));

        Text sceneTextSub = new Text();
        sceneTextSub.setText("J.W. Taylor-Parkins, M. de Klein, L. Pekelharing, F.J. Willemsen");
        sceneTextSub.setFont(Font.font("null", FontWeight.LIGHT, 30));
        if(Main.scene != null) { sceneTextSub.setWrappingWidth(sceneText.getWrappingWidth()); }
        else { sceneTextSub.setWrappingWidth(Main.width); }
        sceneTextSub.setTextAlignment(TextAlignment.CENTER);

        sceneView.setBackground(getBackground(sceneView.getWidth(), sceneView.getHeight()));
        sceneView.getChildren().addAll(sceneText, sceneTextSub);
        return sceneView;
    }

    //Returns the Quit-confirmation dialog
    public static Alert getQuitAlertBox() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Quit");
        alert.setHeaderText("You're about to quit the application.");
        alert.setContentText("Are you sure you want to quit?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            System.exit(0);
        } else {
            alert.close();
        }
        return alert;
    }

    //Returns the background for images
    public static Background getBackground(Double sceneWidth, Double sceneHeight) {
        File backgroundImageFile = new File("Backgrounds/" + backgroundImageFileString);
        BackgroundImage backgroundImage = new BackgroundImage(new Image(backgroundImageFile.toURI().toString(), sceneWidth, sceneHeight, false, true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        return new Background(backgroundImage);
    }

}