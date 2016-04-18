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

/**
 * Created by Lucas on 4/18/2016.
 */
public class MyAreaChart {
    /**
     * @param chartInfo
     *  0 -> URL
     *  1 -> User
     *  2 -> Password
     *  3 -> Query
     *  4 -> Chart description
     *  5 -> xLabel
     *  6 -> yLabel
     * @param minASCII
     * @param maxASCII
     * @return
     * Returns the scene to be showed on screen
     */

    static VBox getScene(String[] chartInfo, int minASCII, int maxASCII) {
        VBox sceneView = new VBox();

        ResultSet resultSet = SQL.getDBResults(chartInfo[0],chartInfo[1],chartInfo[2],chartInfo[3]);

        Text sceneText = new Text();
        sceneText.setText(chartInfo[4]);
        sceneText.setFont(Font.font("null", FontWeight.MEDIUM, 40));
        sceneText.setWrappingWidth(Main.scene.getWidth());

        Text sceneSubText = new Text();
        sceneSubText.setText("Pick the range you like.");

        CategoryAxis XAxis = new CategoryAxis();
        NumberAxis YAxis = new NumberAxis();
        XAxis.setLabel(chartInfo[5]);
        YAxis.setLabel(chartInfo[6]);

        final AreaChart<String,Number> areaChart = new AreaChart<>(XAxis,YAxis);
        areaChart.setPrefHeight(900);

        ComboBox<String> pickRangeComboBox = new ComboBox<String>();
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
                Main.stackBarChartScene.setRoot(getScene(chartInfo, (int) pickRangeComboBox.getSelectionModel().getSelectedItem().toLowerCase().charAt(0), (int) pickRangeComboBox.getSelectionModel().getSelectedItem().toLowerCase().charAt(2)));
            }
            else {
                Main.borderPane.setCenter(getScene(chartInfo, (int) pickRangeComboBox.getSelectionModel().getSelectedItem().toLowerCase().charAt(0), (int) pickRangeComboBox.getSelectionModel().getSelectedItem().toLowerCase().charAt(2)));
            }
        });

        if(minASCII == 0 && maxASCII == 127) { pickRangeComboBox.setValue("A-Z"); }
        else { pickRangeComboBox.setValue((char) (minASCII - 32) + "-" + (char) (maxASCII - 32)); }

        DataToChart(resultSet,minASCII,maxASCII,areaChart);

        sceneView.getChildren().addAll(sceneText, sceneSubText, pickRangeComboBox, areaChart);
        return sceneView;
    }

    static void DataToChart(ResultSet sqlData, int minASCII, int maxASCII, AreaChart areaChart) {
        int number = 0;
        int passed = 0;
        int average = 0;
        String averageYear = "";
        try {
            while (sqlData.next()) {
                String name = sqlData.getString("Gebied");
                char firstLetter = name.toLowerCase().charAt(0);
                int firstASCIILetter = (int) firstLetter;
                number++;
                for(int i = 2; i < 7; i++) {
                    average += sqlData.getInt(i);
                    averageYear = "" + (i + 2004);
                }
                if(firstASCIILetter >= minASCII && firstASCIILetter <= maxASCII) {
                    passed++;
                    XYChart.Series series = new XYChart.Series();
                    for (int i = 2; i < 7; i++) {
                        String year = "" + (i + 2004);
                        try {
                            series.getData().add(new XYChart.Data(year, sqlData.getInt(i)));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    series.setName(name);
                    areaChart.getData().add(series);
                }
            }
            average /= number;
            XYChart.Series averageSeries = new XYChart.Series();
            averageSeries.getData().add(new XYChart.Data(averageYear, average));
            averageSeries.setName("Rotterdam Average");
            areaChart.getData().add(averageSeries);
            average = 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Total number of options: " + number + "\nSelected number of options: " + passed);
    }
}
