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
public class PieChartX {

    //Draws the Pie Chart scene
    static VBox getScene(ChartInfo chartInfo, Boolean splitScreen) {
        VBox sceneView = new VBox();
        sceneView.setPadding(new Insets(10,10,10,10));

        final PieChart pieChart = new PieChart();

        ComboBox pickAreaComboBox = new ComboBox();
        pickAreaComboBox.setPromptText("Select a filter");
        pickAreaComboBox.setEditable(false);

        Text sceneTitle = new Text(chartInfo.getRangeSelector());
        sceneTitle.setFont(Font.font("null", FontWeight.MEDIUM, 30));

        ResultSet resultSet = SQL.getChartData(chartInfo);
        ResultSetMetaData resultSetMetaData;
        try {
            resultSetMetaData = resultSet.getMetaData();
            if (pickAreaComboBox.getItems().size() == 0){
                for (int i = 2; i < resultSetMetaData.getColumnCount() + 1; i++) {
                    pickAreaComboBox.getItems().add(resultSetMetaData.getColumnName(i).replaceAll("\n", ""));
                }
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        if(splitScreen) {
            pickAreaComboBox.setOnAction((q) -> {
                ChartInfo oldChartInfo = new ChartInfo(chartInfo.getDbURL(), chartInfo.getDbUsername(), chartInfo.getDbPassword(), chartInfo.getDbQuery(), chartInfo.getRangeSelector());
                chartInfo.setRangeSelector(pickAreaComboBox.getSelectionModel().getSelectedItem().toString());
                VBox vbox = new VBox();
                Text text = new Text();
                text.setText("Compare the piecharts here.");
                text.setFont(Font.font("null", FontWeight.MEDIUM, 40));
                text.setWrappingWidth(Main.scene.getWidth());
                vbox.getChildren().addAll(text, PieChartX.getScene(chartInfo, true), PieChartX.getScene(oldChartInfo, true));
                if (Main.openInNewWindow) {
                    Main.pieChart2Scene.setRoot(vbox);
                } else {
                    Main.borderPane.setCenter(vbox);
                }
            });
        } else {
            pickAreaComboBox.setOnAction((q) -> {
                chartInfo.setRangeSelector(pickAreaComboBox.getSelectionModel().getSelectedItem().toString());
                if (Main.openInNewWindow) {
                    Main.pieChartScene.setRoot(getScene(chartInfo, false));
                } else {
                    Main.borderPane.setCenter(getScene(chartInfo, false));
                }
            });
        }
        pickAreaComboBox.setValue(chartInfo.getRangeSelector());

        VBox subScene = getSubScene(resultSet, pieChart, chartInfo);

        sceneView.getChildren().addAll(sceneTitle, pickAreaComboBox, subScene);
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

    public static VBox getSubScene(String areaName) {
        VBox sceneView = new VBox();
        sceneView.setPrefSize(Main.width, Main.height);
        PieChart pieChart = new PieChart();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        String areaSafeName = null;
        if (areaName.contains(" ")) { areaSafeName = "{" + areaName + "}"; }
        else { areaSafeName = areaName; }
        ResultSet results = SQL.getDBResults("jdbc:mysql://127.0.0.1:3306/" + Main.DatabaseName, "root", "root", "select Crime, " + areaSafeName + " from all_crimes_transposed");

        try {
            while (results.next()) {
                for(int i = 2; i<3; i++) {
                    if(results.getString(i) != null) {
                        Double doubleNum = Double.parseDouble(results.getString(i).replaceAll(",", "."));
                        try {
                            pieChartData.add(new PieChart.Data(results.getString("Crime"), doubleNum));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        pieChart.setData(pieChartData);
        pieChart.setTitle("Crime in " + areaName);
        sceneView.getChildren().add(pieChart);
        return sceneView;
    }
}
