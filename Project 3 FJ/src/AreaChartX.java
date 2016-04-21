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
public class AreaChartX {
    public static ArrayList<String> nameList = new ArrayList<>();

    /**
     * @param chartInfo
     *  R   -> URL
     *  R   -> User
     *  R   -> Password
     *  R   -> Query
     *  R   -> Chart description
     *  R   -> xLabel
     *  R   -> yLabel
     *  R/W -> rangeSelector
     *
     * @return
     * Returns the sceneView to caller
     */

    static VBox getScene(ChartInfo chartInfo) {
        VBox sceneView = new VBox();
        ComboBox<String> pickRangeComboBox = new ComboBox<>();
        ResultSet resultSet = SQL.getChartData(chartInfo);

        Text sceneTitle = new Text(chartInfo.getChartDesc());
        sceneTitle.setFont(Font.font("null", FontWeight.MEDIUM, 40));
        sceneTitle.setWrappingWidth(Main.scene.getWidth());

        Text rangeBoxText = new Text("Pick the filter you like.");

        CategoryAxis XAxis = new CategoryAxis();
        NumberAxis YAxis = new NumberAxis();
        XAxis.setLabel(chartInfo.getxLabel());
        YAxis.setLabel(chartInfo.getyLabel());

        final AreaChart<String, Number> areaChart = new AreaChart<>(XAxis,YAxis);
        areaChart.setPrefHeight(900);

        pickRangeComboBox.setPromptText("Select a filter");
        pickRangeComboBox.setEditable(false);
        pickRangeComboBox.setOnAction((q) -> {
            chartInfo.setRangeSelector(pickRangeComboBox.getSelectionModel().getSelectedItem());
            if (Main.openInNewWindow) {
                Main.areaChart1Scene.setRoot(getScene(chartInfo));
            }
            else {
                Main.borderPane.setCenter(getScene(chartInfo));
            }
        });

        pickRangeComboBox.setValue(chartInfo.getRangeSelector());

        DataToChart(resultSet, areaChart, chartInfo);

        if (pickRangeComboBox.getItems().size() == 0) {
            for (String name : nameList) {
                pickRangeComboBox.getItems().add(name);
            }
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

    private static void DataToChart(ResultSet sqlData, AreaChart areaChart, ChartInfo chartInfo) {
        int number = 0, passed = 0;
        String[] averageYear = {"","","","",""};
        int[] averagePerYear = {0,0,0,0,0};

        try {
            while (sqlData.next()) {
                String name = sqlData.getString("Gebied");
                if (nameList.size() <= 74) {
                    if (!nameList.contains("All")) {
                        nameList.add(0, "All");
                    }
                    nameList.add(name);
                }
                number++;

                for (int i = 2; i < 7; i++) {
                    averagePerYear[i-2] += sqlData.getInt(i);
                    if (i<6) {
                        averageYear[i-2] = "" + (i + 2004);
                    }
                    else if (i == 6) {averageYear[i-2] = "" +(i+1+2004);}
                }
                if (name.toLowerCase().equals(chartInfo.getRangeSelector().toLowerCase()) || chartInfo.getRangeSelector().equals("All")) {
                        passed++;
                        XYChart.Series series = new XYChart.Series<>();
                        for (int j = 2; j < 7; j++) {
                            String year = "";
                            if (j < 6) {
                                year = "" + (j + 2004);
                            } else if (j == 6) {
                                year = "" + (j + 1 + 2004);
                            }

                            try {
                                series.getData().add(new XYChart.Data(year, sqlData.getInt(j)));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        series.setName(name);
                        areaChart.getData().add(series);
                    }
            }
            XYChart.Series averageSeries = new XYChart.Series<>();
            for (int i = 0; i < averagePerYear.length; i++) {
                float average = averagePerYear[i]/number;
                averageSeries.getData().add(new XYChart.Data(averageYear[i], average));
            }
            averageSeries.setName("Rotterdam Average");
            areaChart.getData().add(averageSeries);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
//        System.out.println("Total number of options: " + number + "\nSelected number of options: " + passed);
    }
}
