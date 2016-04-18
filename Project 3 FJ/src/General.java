import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Created by floris-jan on 14-04-16.
 */
public class General {
    //Draws the Welcome-scene
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

        startView.getChildren().addAll(startText, startTextSub);
        return startView;
    }

    //Draws the Help-scene
    public static VBox getHelpScene() {
        VBox sceneView = new VBox();

        Text sceneText = new Text();
        sceneText.setText("Help");
        sceneText.setFont(Font.font("null", FontWeight.LIGHT, 40));

        Text sceneTextSub = new Text();
        sceneTextSub.setText("You can view the different graphical representations of our data using the Statistics submenu in the menubar.");
        sceneTextSub.setFont(Font.font("null", FontWeight.LIGHT, 12));
        if(Main.scene != null) { sceneTextSub.setWrappingWidth(sceneText.getWrappingWidth()); }
        else { sceneTextSub.setWrappingWidth(Main.width); }
        sceneTextSub.setTextAlignment(TextAlignment.CENTER);

        sceneView.getChildren().addAll(sceneText, sceneTextSub);
        return sceneView;
    }

    //Draws the Preferences-scene
    public static VBox getPreferencesScene() {
        VBox sceneView = new VBox();
        sceneView.setPrefSize(Main.width, Main.height);
        sceneView.setSpacing(10);
//        startView.setAlignment(Pos.CENTER);

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

        sceneView.getChildren().addAll(sceneText, staticMapBox, openInNewWindowBox);
        return sceneView;
    }

    //Draws the About-scene
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

        sceneView.getChildren().addAll(sceneText, sceneTextSub);
        return sceneView;
    }

}