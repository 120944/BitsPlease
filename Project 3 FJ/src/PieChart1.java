import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;

/**
 * Created by floris-jan on 14-04-16.
 */
public class PieChart1 {
    //Draws the Pie Chart-scene
    public static VBox getStat2Scene() {
        VBox sceneView = new VBox();
        sceneView.setPrefSize(Main.width, Main.height);

        Text sceneText = new Text();
        sceneText.setText("Behold, our magnificent pie chart!");
        sceneText.setFont(Font.font("null", FontWeight.MEDIUM, 50));

        PieChart pieChart = new PieChart();
        ObservableList<Data> pieChartData = FXCollections.observableArrayList();
        pieChartData.addAll(
                new Data("Star Wars", 99.9),
                new Data("Batman", 78.56));
        pieChart.setData(pieChartData);
        pieChart.setTitle("Awesome movies");
        sceneView.getChildren().addAll(sceneText, pieChart);
        return sceneView;
    }
}