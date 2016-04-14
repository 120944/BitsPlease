import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import java.net.*;

/**
 * Created by floris-jan on 05-04-16.
 */

public class Main extends Application {

    public static int width;
    public static int height;
    public static Scene scene;
    public static boolean staticMap;
    public static boolean openInNewWindow;
    public static BorderPane borderPane;

    public static void main(String args[]) throws MalformedURLException {
        staticMap = false;
        openInNewWindow = false;
        launch();
    }

    //Creates, initiates and draws the window and all of its elements
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Statistics");
        width = 1000;
        height = 800;
        borderPane = new BorderPane();

        //Creates HBox-menubar layout and buttons
        MenuBar menuBar = new MenuBar();
        Menu general = new Menu("General");
        Menu statistics = new Menu("Statistics");

        MenuItem help = new MenuItem("Help");
        MenuItem preferences = new MenuItem("Preferences");
        MenuItem about = new MenuItem("About");
        MenuItem quit = new MenuItem("Quit");

        MenuItem start = new MenuItem("Start");
        MenuItem stat1 = new MenuItem("Map Stats");
        MenuItem stat2 = new MenuItem("Pie Chart");
        MenuItem stat3 = new MenuItem("Autodiefstal");
        MenuItem stat4 = new MenuItem("Autobeschadiging");
        MenuItem stat5 = new MenuItem("Barchart");
        MenuItem stat6 = new MenuItem("Stacked Barchart");

        general.getItems().addAll(help, preferences, about, quit);
        statistics.getItems().addAll(start, stat1,stat2,stat3,stat4,stat5,stat6);
        menuBar.getMenus().addAll(general, statistics);
        borderPane.setTop(menuBar);
        borderPane.setCenter(General.getStartScene());

        //Sets listeners for the General menubar-buttons
        help.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(General.getHelpScene());
            }
        });

        preferences.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(General.getPreferencesScene());
            }
        });

        about.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(General.getAboutScene());
            }
        });

        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        //Sets listeners for the Statistics menubar-buttons
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(General.getStartScene());
            }
        });

        stat1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) { borderPane.setCenter(Map.getStat1Scene()); }
        });

        stat2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(PieChart1.getStat2Scene());
            }
        });

        stat3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(AreaChart1.getStat3Scene(0, 127));
            }
        });

        stat4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(AreaChart2.getStat4Scene(0, 127));
            }
        });

        stat5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(BarChart1.getStat5Scene());
            }
        });

        stat6.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) { borderPane.setCenter(StackerBarChart1.getStat6Scene(0, 127)); }
        });

        //Creates and instantiates the scene
        scene = new Scene(borderPane, width, height);
        scene.setRoot(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}