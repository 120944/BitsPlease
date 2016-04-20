import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by floris-jan on 14-04-16.
 */
public class PieChart1 {
    private static PieChart pieChart;

    public static VBox getScene(String areaName) {
        VBox sceneView = new VBox();
        sceneView.setPadding(new Insets(10,10,10,10));

        ComboBox<String> pickAreaComboBox = new ComboBox<>();
        pickAreaComboBox.setPromptText("Select a filter");
        pickAreaComboBox.setEditable(false);

        Text sceneText = new Text();
        sceneText.setText(areaName);
        sceneText.setFont(Font.font("null", FontWeight.MEDIUM, 30));

        ResultSet allResults = SQL.getDBResults("jdbc:mysql://127.0.0.1:3306/Crime_per_area", "root", "root", "select * from all_crimes_transposed");
        ResultSetMetaData allResultsMetaData;
        try {
            allResultsMetaData = allResults.getMetaData();
            for(int i = 2; i < allResultsMetaData.getColumnCount()+1; i++) {
                pickAreaComboBox.getItems().add(allResultsMetaData.getColumnName(i).replaceAll("\n", ""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        pickAreaComboBox.setOnAction((q) -> {
            if (Main.openInNewWindow) {
                Main.pieChartScene.setRoot(getScene(pickAreaComboBox.getSelectionModel().getSelectedItem()));
            } else {
                Main.borderPane.setCenter(getScene(pickAreaComboBox.getSelectionModel().getSelectedItem()));
            }
        });

        pickAreaComboBox.setValue(areaName);
        sceneView.getChildren().addAll(sceneText, pickAreaComboBox, getSubScene(areaName));
        return sceneView;
    }

    //Draws the Pie Chart-Subscene
    public static VBox getSubScene(String areaName) {
        VBox sceneView = new VBox();
        sceneView.setPrefSize(Main.width, Main.height);
        pieChart = new PieChart();
        ObservableList<Data> pieChartData = FXCollections.observableArrayList();

        String areaSafeName = null;
        if (areaName.contains(" ")) { areaSafeName = "{" + areaName + "}"; }
        else { areaSafeName = areaName; }
        ResultSet results = SQL.getDBResults("jdbc:mysql://127.0.0.1:3306/Crime_per_area", "root", "root", "select Crime, " + areaSafeName + " from all_crimes_transposed");

        try {
            while (results.next()) {
                for(int i = 2; i<3; i++) {
                    if(results.getString(i) != null) {
                        Double doubleNum = Double.parseDouble(results.getString(i).replaceAll(",", "."));
                        try {
                            pieChartData.add(new Data(results.getString("Crime"), doubleNum));
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