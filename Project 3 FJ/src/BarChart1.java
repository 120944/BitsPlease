import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.chart.BarChart;

/**
 * Created by floris-jan on 14-04-16.
 */
public class BarChart1 {
    //Draws the Barchart-scene
    public static VBox getStat5Scene() {
        VBox sceneView = new VBox();

        Text sceneText = new Text();
        sceneText.setText("It's the Empire's bar chart. Foo Bar.");
        sceneText.setFont(Font.font("null", FontWeight.MEDIUM, 40));
        sceneText.setWrappingWidth(Main.scene.getWidth());

        CategoryAxis XAxis = new CategoryAxis();
        NumberAxis YAxis = new NumberAxis();
        final BarChart<String, Number> barChart = new BarChart(XAxis, YAxis);
        XAxis.setLabel("Event");
        YAxis.setLabel("Stress");
        barChart.setTitle("Sith Stuff. Because Sith rock.");

        XYChart.Series deathStar = new XYChart.Series();
        XYChart.Series deathStar2 = new XYChart.Series();
        XYChart.Series starKillerBase = new XYChart.Series();
        deathStar.setName("Death Star 1 costs");
        deathStar2.setName("Death Star 2 costs");
        starKillerBase.setName("Starkiller Base costs");

        String item1 = "Armour";
        String item2 = "Weaponry";
        String item3 = "Supplies";
        String item4 = "Personnel";

        deathStar.getData().addAll(
                new XYChart.Data(item1, 10),
                new XYChart.Data(item2, 40),
                new XYChart.Data(item3, 100),
                new XYChart.Data(item4, 0));

        deathStar2.getData().addAll(
                new XYChart.Data(item1, 20),
                new XYChart.Data(item2, 100),
                new XYChart.Data(item3, 50),
                new XYChart.Data(item4, -10));

        starKillerBase.getData().addAll(
                new XYChart.Data(item1, 60),
                new XYChart.Data(item2, 150),
                new XYChart.Data(item3, 10),
                new XYChart.Data(item4, 10));

        barChart.getData().addAll(deathStar, deathStar2, starKillerBase);
        sceneView.getChildren().addAll(sceneText, barChart);
        return sceneView;
    }

}