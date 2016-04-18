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
                General.getQuitAlertBox();
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
            public void handle(ActionEvent event) {
                if(openInNewWindow) {
                    mapStage = new Stage();
                    mapStage.setTitle("Map");
                    mapScene = new Scene(Map.getScene(), width, height);
                    mapScene.setRoot(Map.getScene());
                    mapStage.setScene(mapScene);
                    mapStage.show();
                }
                else {
                    borderPane.setCenter(Map.getScene());
                }
            }
        });

        stat2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(openInNewWindow) {
                    pieChartStage = new Stage();
                    pieChartStage.setTitle("Pie Chart");
                    pieChartScene = new Scene(PieChart1.getScene(), width, height);
                    pieChartScene.setRoot(PieChart1.getScene());
                    pieChartStage.setScene(pieChartScene);
                    pieChartStage.show();
                }
                else {
                    borderPane.setCenter(PieChart1.getScene());
                }
            }
        });

        stat3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(openInNewWindow) {
                    areaChart1Stage = new Stage();
                    areaChart1Stage.setTitle("Area Chart 1");
                    areaChart1Scene = new Scene(AreaChart1.getScene(0,127), width, height);
                    areaChart1Scene.setRoot(AreaChart1.getScene(0,127));
                    areaChart1Stage.setScene(areaChart1Scene);
                    areaChart1Stage.show();
                }
                else {
                    borderPane.setCenter(AreaChart1.getScene(0, 127));
                }
            }
        });

        stat4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(openInNewWindow) {
                    areaChart2Stage = new Stage();
                    areaChart2Stage.setTitle("Area Chart 2");
                    areaChart2Scene = new Scene(AreaChart2.getScene(0,127), width, height);
                    areaChart2Scene.setRoot(AreaChart2.getScene(0,127));
                    areaChart2Stage.setScene(areaChart2Scene);
                    areaChart2Stage.show();
                }
                else {
                    borderPane.setCenter(AreaChart2.getScene(0, 127));
                }
            }
        });

        stat5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
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
            }
        });

        stat6.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
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
            }
        });

        //Creates and instantiates the scene
        scene = new Scene(borderPane, width, height);
        scene.setRoot(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}