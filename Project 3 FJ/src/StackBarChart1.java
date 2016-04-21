import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by floris-jan on 14-04-16.
 */
public class StackBarChart1 {
    //Draws the Stacked Barchart-scene
    public static VBox getScene(String problemName, Boolean average) {
        VBox sceneView = new VBox();
        sceneView.setPadding(new Insets(10,10,10,10));
        ResultSet results;

        ComboBox<String> pickAreaComboBox = new ComboBox<>();
        pickAreaComboBox.setPromptText("Select a filter");
        pickAreaComboBox.setEditable(false);

        CheckBox averageBox = null;
        if(problemName == "Alle situaties") {
            results = SQL.getDBResults("jdbc:mysql://127.0.0.1:3306/" + Main.DatabaseName, "root", "root", "SELECT * FROM all_crimes_transposed");
            averageBox = new CheckBox();
            averageBox.setText("Average");
            if(average) { averageBox.setSelected(true); }
            averageBox.setOnAction((q) -> {
                if(average) {
                    if (Main.openInNewWindow) {
                        Main.stackBarChartScene.setRoot(getScene(pickAreaComboBox.getSelectionModel().getSelectedItem().toString(), false));
                    } else {
                        Main.borderPane.setCenter(getScene(pickAreaComboBox.getSelectionModel().getSelectedItem().toString(), false));
                    }
                } else {
                    if (Main.openInNewWindow) {
                        Main.stackBarChartScene.setRoot(getScene(pickAreaComboBox.getSelectionModel().getSelectedItem().toString(), true));
                    } else {
                        Main.borderPane.setCenter(getScene(pickAreaComboBox.getSelectionModel().getSelectedItem().toString(), true));
                    }
                }
            });
        } else {
            results = SQL.getDBResults("jdbc:mysql://127.0.0.1:3306/" + Main.DatabaseName, "root", "root", "SELECT * FROM all_crimes_transposed WHERE Crime='" + problemName + "'");
        }

        Text sceneText = new Text();
        sceneText.setText("Aandelen in situaties per buurt");
        sceneText.setFont(Font.font("null", FontWeight.MEDIUM, 40));
        sceneText.setWrappingWidth(Main.scene.getWidth());

        CategoryAxis XAxis = new CategoryAxis();
        NumberAxis YAxis = new NumberAxis();
        final StackedBarChart<String, Number> barChart = new StackedBarChart(XAxis, YAxis);
        barChart.setMinSize(Main.scene.getWidth(), 750);
        XAxis.setLabel("Wijk");
        YAxis.setLabel("Index");
        YAxis.maxHeight(200);
        ArrayList<String> arrayList = new ArrayList<>();

        ResultSet allResults = SQL.getDBResults("jdbc:mysql://127.0.0.1:3306/" + Main.DatabaseName, "root", "root", "select * from all_crimes");
        ResultSetMetaData allResultsMetaData;
        ResultSetMetaData resultsMetaData;

        try {
            allResultsMetaData = allResults.getMetaData();
            resultsMetaData = results.getMetaData();
            pickAreaComboBox.getItems().add("Alle situaties");

            if(problemName == "Alle situaties") {
                for(int i = 2; i < results.getMetaData().getColumnCount(); i++) {
                    int count = 0;
                    int sum = 0;
                    while(results.next()) {
                        count++;
                        sum += results.getInt(i);
                    }
                    results.beforeFirst();
                    XYChart.Series series = new XYChart.Series();
                    String name = resultsMetaData.getColumnName(i);
                    arrayList.add("" + i);
                    if(average) { series.getData().add(new XYChart.Data("" + i, sum/count)); }
                    else { series.getData().add(new XYChart.Data("" + i, sum)); }
                    series.setName(i + ", " + name);
                    barChart.getData().add(series);
                }
            }

            for(int i = 2; i < allResultsMetaData.getColumnCount(); i++) {
                pickAreaComboBox.getItems().add(allResultsMetaData.getColumnName(i).replaceAll("\n", ""));
            }

            if(problemName != "Alle situaties") {
                while (results.next()) {
                    for (int i = 2; i < resultsMetaData.getColumnCount(); i++) {
                        XYChart.Series series = new XYChart.Series();
                        String name = resultsMetaData.getColumnName(i);
                        arrayList.add("" + i);
                        series.getData().add(new XYChart.Data("" + i, results.getInt(i)));
                        series.setName(i + ", " + name);
                        barChart.getData().add(series);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        XAxis.setCategories(FXCollections.observableArrayList(arrayList));

        pickAreaComboBox.setOnAction((q) -> {
                if(Main.openInNewWindow) {
                    Main.stackBarChartScene.setRoot(getScene(pickAreaComboBox.getSelectionModel().getSelectedItem().toString(), average));
                }
                else {
                    Main.borderPane.setCenter(getScene(pickAreaComboBox.getSelectionModel().getSelectedItem().toString(), average));
                }
        });
        pickAreaComboBox.setValue(problemName);

        sceneView.getChildren().addAll(sceneText, pickAreaComboBox, barChart);
        if(problemName == "Alle situaties") { sceneView.getChildren().add(averageBox); }
        return sceneView;
    }
}