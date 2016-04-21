import com.lynden.gmapsfx.GoogleMapView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MapChart2 {
    //Draws the Map-scene

    /**
     *
     * @param yearString
     * @return
     */
    public static VBox getScene(String yearString) {
        int year = Integer.parseInt(yearString);
        ComboBox<String> pickYearComboBox = new ComboBox<>();

        Text text = new Text();
        text.setText("Heatmap: the circles are representive for car-related crimes");
        text.setFont(Font.font("null", FontWeight.MEDIUM, 20));
        text.setWrappingWidth(Main.scene.getWidth());

        pickYearComboBox.setPromptText("Select a year");
        pickYearComboBox.setEditable(false);
        pickYearComboBox.setValue(year + "");
        pickYearComboBox.getItems().addAll(
                2006 + "",
                2007 + "",
                2008 + "",
                2009 + "",
                2011 + ""
        );
        pickYearComboBox.setOnAction((q) -> {
            if(Main.openInNewWindow) {
                Main.mapScene.setRoot(getScene(pickYearComboBox.getSelectionModel().getSelectedItem().toString()));
            }
            else {
                Main.borderPane.setCenter(getScene(pickYearComboBox.getSelectionModel().getSelectedItem().toString()));
            }
        });

        Map2.mapViewVBox = new VBox(8);
        Map2.map(year);

        if(Main.staticMap) {
            //Sets up the buttons and labels
            Map2.mapViewVBox.setPadding(new Insets(10, 10, 10, 10));
            Button zoomInButton = new Button();
            Button zoomOutButton = new Button();
            zoomInButton.setText("+");
            zoomOutButton.setText("-");

            Label originLabel = new Label();
            Label destinationLabel = new Label();
            originLabel.setText(Map2.geoIPLookUp().city);
            destinationLabel.setText(Map.destination);

            //Creates and instantiates the ImageView, sets the image in the view
            final ImageView image2 = new ImageView();
            image2.setPreserveRatio(true);
            image2.setFitWidth(Main.width);
            image2.setSmooth(true);
            image2.setCache(true);
            if (Map2.scene != null) {
                image2.setViewport(new Rectangle2D(0, 0, Map2.scene.getWidth(), Map2.scene.getHeight()));
            }
            image2.setImage(Map2.mapImage);
            Map2.mapViewVBox.getChildren().addAll(zoomInButton, zoomOutButton, originLabel, destinationLabel, image2);

            //Zooms the ImageMapView in
            zoomInButton.setOnAction((q) -> {
                Map2.zoom++;
                Map2.map(year);
                image2.setImage(Map2.mapImage);
            });

            //Zooms the ImageMapView out
            zoomOutButton.setOnAction((q) -> {
                Map2.zoom--;
                Map2.map(year);
                image2.setImage(Map2.mapImage);
            });
        }
        else {
            Map2.mapView = new GoogleMapView();
            Map2.mapView.addMapInializedListener(new Map2());
            Map2.mapView.setPrefSize(Main.width, Main.height);
            Map2.mapViewVBox.setAlignment(Pos.CENTER);
            Map2.mapViewVBox.getChildren().addAll(text, pickYearComboBox, Map2.mapView);
        }

        //Listeners for screen resize events
        if(Main.staticMap) {
            Main.scene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldSceneWidth, Number newSceneWidth) {
                    if ((Double) newSceneWidth > Main.scene.heightProperty().getValue()) {
                        Map2.image2.setFitWidth((Double) newSceneWidth);
                    }
                }
            });
            Main.scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldSceneHeight, Number newSceneHeight) {
                    if ((Double) newSceneHeight > Main.scene.widthProperty().getValue()) {
                        Map2.image2.setFitHeight((Double) newSceneHeight);
                    }
                }
            });
        }
        else {
            Main.scene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldSceneWidth, Number newSceneWidth) {
                    if ((Double) newSceneWidth > Main.scene.heightProperty().getValue()) {
                        Map2.mapView.setPrefSize((Double) newSceneWidth, Main.height);
                    }
                }
            });
            Main.scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldSceneHeight, Number newSceneHeight) {
                    if ((Double) newSceneHeight > Main.scene.widthProperty().getValue()) {
                        Map2.mapView.setPrefSize(Main.width, (Double) newSceneHeight);
                    }
                }
            });
        }
        return Map2.mapViewVBox;
    }
}
