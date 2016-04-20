import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Lucas on 4/20/2016.
 */
public class MyAreaChart {

    private static String selectedRange;
    public static ArrayList<String> nameList = new ArrayList<>();

    /**
     * @param chartInfo
     *  0 -> URL
     *  1 -> User
     *  2 -> Password
     *  3 -> Query
     *  4 -> Chart description
     *  5 -> xLabel
     *  6 -> yLabel
     * @param _selectedRange
     * String containing initial or selected range
     * @return
     * Returns the sceneView to caller
     */

    static VBox getScene(String[] chartInfo, String _selectedRange) {
        VBox sceneView = new VBox();
        ComboBox<String> pickRangeComboBox = new ComboBox<>();
        ResultSet resultSet = SQL.getDBResults(chartInfo[0],chartInfo[1],chartInfo[2],chartInfo[3]);

        Text sceneTitle = new Text(chartInfo[4]);
        sceneTitle.setFont(Font.font("null", FontWeight.MEDIUM, 40));
        sceneTitle.setWrappingWidth(Main.scene.getWidth());

        Text rangeBoxText = new Text("Pick the filter you like.");

        CategoryAxis XAxis = new CategoryAxis();
        NumberAxis YAxis = new NumberAxis();
        XAxis.setLabel(chartInfo[5]);
        YAxis.setLabel(chartInfo[6]);

        final AreaChart<String, Number> areaChart = new AreaChart<>(XAxis,YAxis);
        areaChart.setPrefHeight(900);

        selectedRange = _selectedRange;

        pickRangeComboBox.setPromptText("Select a filter");
        pickRangeComboBox.setEditable(false);
        pickRangeComboBox.setOnAction((q) -> {
            if (Main.openInNewWindow) {
                Main.areaChart1Scene.setRoot(getScene(chartInfo, pickRangeComboBox.getSelectionModel().getSelectedItem()));
            }
            else {
                Main.borderPane.setCenter(getScene(chartInfo,pickRangeComboBox.getSelectionModel().getSelectedItem()));
            }
        });

        pickRangeComboBox.setValue(selectedRange);

        DataToChart(resultSet, areaChart, pickRangeComboBox);
        for(int i = 0; i < nameList.size(); i++) {
            pickRangeComboBox.getItems().add(nameList.get(i));
        }

        sceneView.getChildren().addAll(sceneTitle,rangeBoxText,pickRangeComboBox,areaChart);
        return sceneView;
    }

    /**
     * @param sqlData
     *  Data to be showed on the chart
     * @param areaChart
     *  Original areachart, which will be overwritten (or painted over) or changed with the passed sqlData
     */

    static void DataToChart(ResultSet sqlData, AreaChart areaChart, ComboBox pickRangeComboBox) {
        int number = 0, passed = 0, average = 0;
        String averageYear = "";

        try {
            while (sqlData.next()) {
                String name = sqlData.getString("Gebied");
                nameList.add(name);
                number++;

                for (int i = 2; i < 7; i++) {
                    average += sqlData.getInt(i);
                    if (i<6) {
                        averageYear = "" + (i + 2004);
                    }
                    else if (i == 6) {averageYear = "2011";}
                }
                if (name.toLowerCase().equals(selectedRange.toLowerCase())) {
                    passed++;
                    XYChart.Series series = new XYChart.Series<>();
                    for (int j = 2; j < 7; j++) {
                        String year = "";
                        if (j<6) {
                            year = "" + (j + 2004);
                        }
                        else if (j == 6) {year = "2011";}

                        try {
                            series.getData().add(new XYChart.Data(year, sqlData.getInt(j)));
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    series.setName(name);
                    areaChart.getData().add(series);
                }
            }
            average /= number;
            XYChart.Series averageSeries = new XYChart.Series<>();
            averageSeries.getData().add(new XYChart.Data(averageYear, average));
            averageSeries.setName("Rotterdam Average");
            areaChart.getData().add(averageSeries);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Total number of options: " + number + "\nSelected number of options: " + passed);
    }
}
