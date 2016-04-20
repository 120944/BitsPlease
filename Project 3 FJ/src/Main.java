import javafx.application.Application;
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

    public static Stage primaryStage;

    public static Stage mapStage;
    public static Scene mapScene;

    public static Stage pieChartStage;
    public static Scene pieChartScene;

    public static Stage areaChart1Stage;
    public static Scene areaChart1Scene;

    public static Stage areaChart2Stage;
    public static Scene areaChart2Scene;

    public static Stage barChartStage;
    public static Scene barChartScene;

    public static Stage stackBarChartStage;
    public static Scene stackBarChartScene;

    // Setting if db name gets changed
    public static String DatabaseName = "Crime_per_area";

    public static void main(String args[]) throws MalformedURLException {
        //Default preferences
        staticMap = false;
        openInNewWindow = false;
        General.backgroundImageFileString = "Background 1.png";

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
        help.setOnAction((q) -> borderPane.setCenter(General.getHelpScene()));

        preferences.setOnAction((q) -> borderPane.setCenter(General.getPreferencesScene()));

        about.setOnAction((q) -> borderPane.setCenter(General.getAboutScene()));

        quit.setOnAction((q) -> General.getQuitAlertBox());

        //Sets listeners for the Statistics menubar-buttons
        start.setOnAction((q) -> borderPane.setCenter(General.getStartScene()));

        stat1.setOnAction((q) -> {
            if(openInNewWindow) {
                mapStage = new Stage();
                mapStage.setTitle("Map");
                mapScene = new Scene(MapChart1.getScene(), width, height);
                mapScene.setRoot(MapChart1.getScene());
                mapStage.setScene(mapScene);
                mapStage.show();
            }
            else {
                borderPane.setCenter(MapChart1.getScene());
            }
        });

        stat2.setOnAction((q) -> {
            if(openInNewWindow) {
                pieChartStage = new Stage();
                pieChartStage.setTitle("Pie Chart");
                pieChartScene = new Scene(PieChart1.getScene("Afrikaanderwijk"), width, height);
                pieChartScene.setRoot(PieChart1.getScene("Afrikaanderwijk"));
                pieChartStage.setScene(pieChartScene);
                pieChartStage.show();
            }
            else {
                borderPane.setCenter(PieChart1.getScene("Afrikaanderwijk"));
            }
        });

        stat3.setOnAction((q) -> {
            String[] chartInfo = {"jdbc:mysql://127.0.0.1:3306/" + DatabaseName, "root", "root",
                    "select * from diefstal_uit_auto", "Autodiefstal over verschillende jaren in Rotterdam",
                    "Jaar", "Cijfer"};
            if(openInNewWindow) {
                areaChart1Stage = new Stage();
                areaChart1Stage.setTitle("Area Chart 1");
                areaChart1Scene = new Scene(MyAreaChart.getScene(chartInfo,"All"), width, height);
                areaChart1Scene.setRoot(MyAreaChart.getScene(chartInfo,"All"));
                areaChart1Stage.setScene(areaChart1Scene);
                areaChart1Stage.show();
            }
            else {
                borderPane.setCenter(MyAreaChart.getScene(chartInfo,"All"));
            }
        });

        stat4.setOnAction((q) -> {
            String[] chartInfo = {"jdbc:mysql://127.0.0.1:3306/" + DatabaseName, "root", "root",
                    "select * from beschadiging_aan_auto",
                    "Autobeschadiging over verschillende jaren in Rotterdam",
                    "Jaar", "Cijfer"};
            if(openInNewWindow) {
                areaChart2Stage = new Stage();
                areaChart2Stage.setTitle("Area Chart 2");
                areaChart2Scene = new Scene(MyAreaChart.getScene(chartInfo,"All"), width, height);
                areaChart2Scene.setRoot(MyAreaChart.getScene(chartInfo,"All"));
                areaChart2Stage.setScene(areaChart2Scene);
                areaChart2Stage.show();
            }
            else {
                borderPane.setCenter(MyAreaChart.getScene(chartInfo,"All"));
            }
        });

        stat5.setOnAction((q) -> {
            if(openInNewWindow) {
                barChartStage = new Stage();
                barChartStage.setTitle("Bar Chart");
                barChartScene = new Scene(BarChart1.getScene(), width, height);
                barChartScene.setRoot(BarChart1.getScene());
                barChartStage.setScene(barChartScene);
                barChartStage.show();
            }
            else {
                borderPane.setCenter(BarChart1.getScene());
            }
        });

        stat6.setOnAction((q) -> {
            if(openInNewWindow) {
                stackBarChartStage = new Stage();
                stackBarChartStage.setTitle("Stacked Bar Chart");
                stackBarChartScene = new Scene(StackBarChart1.getScene(0,127), width, height);
                stackBarChartScene.setRoot(StackBarChart1.getScene(0,127));
                stackBarChartStage.setScene(stackBarChartScene);
                stackBarChartStage.show();
            }
            else {
                borderPane.setCenter(StackBarChart1.getScene(0, 127));
            }
        });

        //Creates and instantiates the scene
        scene = new Scene(borderPane, width, height);
        scene.setRoot(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}