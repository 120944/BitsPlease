import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.ResultSet;

/**
 * Created by floris-jan on 14-04-16.
 */
public class AreaChart2 extends MyAreaChart {

    //Draws the Area Chart-scene
    public static VBox getScene(int minASCII, int maxASCII) {
        VBox sceneView = new VBox();
        sceneView.setPrefSize(Main.width, Main.height);
        ResultSet results = SQL.getDBResults("jdbc:mysql://127.0.0.1:3306/indice", "root", "root", "select * from beschadiging_aan_auto");

        Text sceneText = new Text();
        sceneText.setText("Autobeschadiging over verschillende jaren in Rotterdam");
        sceneText.setFont(Font.font("null", FontWeight.MEDIUM, 40));
        sceneText.setWrappingWidth(Main.scene.getWidth());

        CategoryAxis XAxis = new CategoryAxis();
        NumberAxis YAxis = new NumberAxis();
        XAxis.setLabel("Jaar");
        YAxis.setLabel("Cijfer");

        final AreaChart<String, Number> areaChart = new AreaChart(XAxis, YAxis);
        areaChart.setPrefHeight(900);

        Text sceneSubText = new Text();
        sceneSubText.setText("Pick the range you like.");

        //You can simply change the letters in these Strings to change the ranges
        ComboBox pickRangeComboBox = new ComboBox();
        pickRangeComboBox.getItems().addAll(
                "A-J",
                "K-O",
                "P-Z",
                "A-Z"
        );
        pickRangeComboBox.setPromptText("Select a range");
        pickRangeComboBox.setEditable(true);
        pickRangeComboBox.setOnAction((q) -> {
                if(Main.openInNewWindow) {
                    Main.stackBarChartScene.setRoot(getScene((int) pickRangeComboBox.getSelectionModel().getSelectedItem().toString().toLowerCase().charAt(0), (int) pickRangeComboBox.getSelectionModel().getSelectedItem().toString().toLowerCase().charAt(2)));
                }
                else {
                    Main.borderPane.setCenter(getScene((int) pickRangeComboBox.getSelectionModel().getSelectedItem().toString().toLowerCase().charAt(0), (int) pickRangeComboBox.getSelectionModel().getSelectedItem().toString().toLowerCase().charAt(2)));
                }
        });
        if(minASCII == 0 && maxASCII == 127) { pickRangeComboBox.setValue("A-Z"); }
        else { pickRangeComboBox.setValue((char) (minASCII - 32) + "-" + (char) (maxASCII - 32)); }

        DataToChart(results,minASCII,maxASCII,areaChart);

        sceneView.getChildren().addAll(sceneText, sceneSubText, pickRangeComboBox, areaChart);
        return sceneView;
    }

}
