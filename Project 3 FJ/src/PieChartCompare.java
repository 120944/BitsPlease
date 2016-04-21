import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by Lucas on 4/21/2016.
 */

/**
 * @TODO
 * Needs to show 2 pie charts next to one another
 */
public class PieChartCompare {
    static VBox getScene(ChartInfo chartInfo) {
        VBox sceneView = new VBox();
        sceneView.setPadding(new Insets(10,10,10,10));

        final PieChart pieChart = new PieChart();

        ComboBox pickArea1ComboBox = new ComboBox();
        pickArea1ComboBox.setPromptText("Select a filter");
        pickArea1ComboBox.setEditable(false);

        ComboBox pickArea2ComboBox = new ComboBox();
        pickArea2ComboBox.setPromptText("Select a filter");
        pickArea2ComboBox.setEditable(false);

        Text sceneTitle = new Text(chartInfo.getRangeSelector());
        sceneTitle.setFont(Font.font("null", FontWeight.MEDIUM, 30));

        ResultSet resultSet = SQL.getChartData(chartInfo);
        ResultSetMetaData resultSetMetaData;

        try {
            resultSetMetaData = resultSet.getMetaData();
            if (pickArea1ComboBox.getItems().size() == 0 && pickArea2ComboBox.getItems().size() == 0){
                for (int i = 2; i < resultSetMetaData.getColumnCount() + 1; i++) {
                    pickArea1ComboBox.getItems().add(resultSetMetaData.getColumnName(i).replaceAll("\n", ""));
                    pickArea2ComboBox.getItems().add(resultSetMetaData.getColumnName(i).replaceAll("\n", ""));
                }
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }

        for (ComboBox combobox: new ComboBox[] {pickArea1ComboBox,pickArea2ComboBox}) {
            chartInfo.setRangeSelector(combobox.getSelectionModel().getSelectedItem().toString());
            if (Main.openInNewWindow) {
                Main.pieChartScene.setRoot(getScene(chartInfo));
            } else {
                Main.borderPane.setCenter(getScene(chartInfo));
            }
        }

        pickArea1ComboBox.setValue(chartInfo.getRangeSelector());

        VBox subScene = getSubScene(resultSet, pieChart, chartInfo);

        sceneView.getChildren().addAll(sceneTitle, pickArea1ComboBox, subScene);
        return sceneView;
    }

    public static VBox getSubScene(ResultSet resultSet, PieChart pieChart, ChartInfo chartInfo) {
        VBox sceneView = new VBox();
        sceneView.setPrefSize(Main.width, Main.height);
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try {
            while (resultSet.next()) {
                for (int i = 1; i < resultSet.getMetaData().getColumnCount(); i++) {
                    int col = i+1;
                    String number = resultSet.getString(col);
                    if (number == null || number.equals("")) {
                        number = "0";
                    }
                    if (resultSet.getMetaData().getColumnName(col).equals(chartInfo.getRangeSelector())) {
                        Double doubleNum = Double.parseDouble(number.replaceAll(",", "."));
                        try {
                            pieChartData.add(new PieChart.Data(resultSet.getString("Crime"), doubleNum));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        pieChart.setData(pieChartData);
        pieChart.setTitle("Crime in " + chartInfo.getRangeSelector());
        sceneView.getChildren().add(pieChart);
        return sceneView;
    }
}
