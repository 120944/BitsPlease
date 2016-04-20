import com.lynden.gmapsfx.GoogleMapView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Created by floris-jan on 21-04-16.
 */
public class MapChart1 {
    //Draws the Map-scene
    public static VBox getScene() {
        Map.mapViewVBox = new VBox(8);
        Map.map();

        if(Main.staticMap) {
            //Sets up the buttons and labels
            Map.mapViewVBox.setPadding(new Insets(10, 10, 10, 10));
            Button zoomInButton = new Button();
            Button zoomOutButton = new Button();
            zoomInButton.setText("+");
            zoomOutButton.setText("-");

            Label originLabel = new Label();
            Label destinationLabel = new Label();
            originLabel.setText(Map.geoIPLookUp().city);
            destinationLabel.setText(Map.destination);

            //Creates and instantiates the ImageView, sets the image in the view
            final ImageView image2 = new ImageView();
            image2.setPreserveRatio(true);
            image2.setFitWidth(Main.width);
            image2.setSmooth(true);
            image2.setCache(true);
            if (Map.scene != null) {
                image2.setViewport(new Rectangle2D(0, 0, Map.scene.getWidth(), Map.scene.getHeight()));
            }
            image2.setImage(Map.mapImage);
            Map.mapViewVBox.getChildren().addAll(zoomInButton, zoomOutButton, originLabel, destinationLabel, image2);

            //Zooms the ImageMapView in
            zoomInButton.setOnAction((q) -> {
                Map.zoom++;
                Map.map();
                image2.setImage(Map.mapImage);
            });

            //Zooms the ImageMapView out
            zoomOutButton.setOnAction((q) -> {
                Map.zoom--;
                Map.map();
                image2.setImage(Map.mapImage);
            });
        }
        else {
            Map.mapView = new GoogleMapView();
            Map.mapView.addMapInializedListener(new Map());
            Map.mapView.setPrefSize(Main.width, Main.height);
            Map.mapViewVBox.setAlignment(Pos.CENTER);
            Map.mapViewVBox.getChildren().addAll(Map.mapView);
        }

        //Listeners for screen resize events
        if(Main.staticMap) {
            Main.scene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldSceneWidth, Number newSceneWidth) {
                    if ((Double) newSceneWidth > Main.scene.heightProperty().getValue()) {
                        Map.image2.setFitWidth((Double) newSceneWidth);
                    }
                }
            });
            Main.scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldSceneHeight, Number newSceneHeight) {
                    if ((Double) newSceneHeight > Main.scene.widthProperty().getValue()) {
                        Map.image2.setFitHeight((Double) newSceneHeight);
                    }
                }
            });
        }
        else {
            Main.scene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldSceneWidth, Number newSceneWidth) {
                    if ((Double) newSceneWidth > Main.scene.heightProperty().getValue()) {
                        Map.mapView.setPrefSize((Double) newSceneWidth, Main.height);
                    }
                }
            });
            Main.scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldSceneHeight, Number newSceneHeight) {
                    if ((Double) newSceneHeight > Main.scene.widthProperty().getValue()) {
                        Map.mapView.setPrefSize(Main.width, (Double) newSceneHeight);
                    }
                }
            });
        }
        return Map.mapViewVBox;
    }
}
