import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Lucas on 4/18/2016.
 */
public abstract class IAreaChart {
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
