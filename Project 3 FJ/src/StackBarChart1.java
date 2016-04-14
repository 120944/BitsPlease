import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by floris-jan on 14-04-16.
 */
public class StackBarChart1 {
    //Draws the Stacked Barchart-scene
    public static VBox getScene(int minASCII, int maxASCII) {
        VBox sceneView = new VBox();
        sceneView.setPrefSize(1440, 900);
        ResultSet results = SQL.getDBResults("jdbc:mysql://127.0.0.1:3306/Crime_per_area", "root", "root", "select * from all_crimes");

        Text sceneText = new Text();
        sceneText.setText("Aandelen in buurtproblemen per buurt");
        sceneText.setFont(Font.font("null", FontWeight.MEDIUM, 40));
        sceneText.setWrappingWidth(Main.scene.getWidth());

        String item1 = "buurtprobleem fietsendiefstal";
        String item2 = "buurtprobleem diefstal uit de auto";
        String item3 = "buurtprobleem beschadiging / diefstal auto";
        String item4 = "slachtofferschap autodiefstal";

        CategoryAxis XAxis = new CategoryAxis();
        NumberAxis YAxis = new NumberAxis();
        final StackedBarChart<String, Number> barChart = new StackedBarChart(XAxis, YAxis);
        barChart.setTitle("Sith Stuff. Because Sith rock.");
        XAxis.setLabel("Probleem");
        YAxis.setLabel("Index");
        XAxis.setCategories(FXCollections.observableArrayList(Arrays.asList(
                item1,
                item2,
                item3,
                item4
        )));

        //You can simply change the letters in these Strings to change the ranges
        String range1 = "A-J";
        String range2 = "K-O";
        String range3 = "P-Z";
        String range4 = "A-Z";
        Text sceneSubText = new Text();
        sceneSubText.setText("Pick the range you like.");
        ListView<String> pickRangeList = new ListView<>();
        ObservableList<String> items =FXCollections.observableArrayList(range1, range2, range3, range4);
        pickRangeList.setItems(items);

        pickRangeList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> ov, String oldValue,
                 String newValue) -> {
                    if(Main.openInNewWindow) {
                        Main.stackBarChartScene.setRoot(getScene((int) newValue.toLowerCase().charAt(0), (int) newValue.toLowerCase().charAt(2)));
                    }
                    else {
                        Main.borderPane.setCenter(getScene((int) newValue.toLowerCase().charAt(0), (int) newValue.toLowerCase().charAt(2)));
                    }
                    pickRangeList.getSelectionModel().select(newValue);
                });

//        try {
//            for(int i = 2; i < 6; i++) {
//                XYChart.Series series = new XYChart.Series();
//                System.out.println(XAxis.getCategories().get(i-2));
//                while (results.next()) {
//                    if(results.getInt(i) < 5) {
//                        String name = results.getString("Gebied");
//                        series.getData().add(new XYChart.Data(XAxis.getCategories().get(i-2), results.getInt(i)));
////                    System.out.println(results.getRow() + ", " + i);
//                        System.out.println(results.getInt(i));
//                    }
//                }
//                System.out.println(series.getData());
//                results.first();
//                series.setName(XAxis.getCategories().get(i-2));
//                barChart.getData().add(series);
//            }
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }

        try {
            while (results.next()) {
                String name = results.getString("Gebied").replaceAll("\n", "");
                char firstLetter = name.toLowerCase().charAt(0);
                int firstASCIILetter = (int) firstLetter;
                if(firstASCIILetter >= minASCII && firstASCIILetter <= maxASCII) {
                    XYChart.Series series = new XYChart.Series();
                    for (int i = 2; i < 6; i++) {
                        try {
                            series.getData().add(new XYChart.Data(XAxis.getCategories().get(i - 2), results.getInt(i)));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    series.setName(name);
                    barChart.getData().add(series);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        sceneView.getChildren().addAll(sceneText, pickRangeList, barChart);
        return sceneView;
    }
}