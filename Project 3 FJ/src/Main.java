//GMapsFX
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.DirectionsRequest;
import com.lynden.gmapsfx.service.directions.TravelModes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import netscape.javascript.JSObject;

//Directions
import com.lynden.gmapsfx.javascript.object.DirectionsPane;
import com.lynden.gmapsfx.service.directions.DirectionStatus;
import com.lynden.gmapsfx.service.directions.DirectionsRenderer;
import com.lynden.gmapsfx.service.directions.DirectionsResult;
import com.lynden.gmapsfx.service.directions.DirectionsService;
import com.lynden.gmapsfx.service.directions.DirectionsServiceCallback;
import com.lynden.gmapsfx.service.directions.DirectionsWaypoint;

//JavaFX
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

//JSON
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//ImageHandling
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

//GeoIP
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

/**
 * Created by floris-jan on 05-04-16.
 */

public class Main extends Application implements MapComponentInitializedListener, DirectionsServiceCallback {

    public static String origin;
    public static String destination;

    public static URL imageUrl;
    public static Image mapImage;
    public ImageView image2;

    public static int zoom;
    public static int width;
    public static int height;
    public static int scale;
    public static Scene scene;
    public static Location ipLocation;
    public static boolean staticMap;
    public static JSONArray garageArray;

    GoogleMapView mapView;
    GoogleMap map;
    protected DirectionsPane directions;
    DirectionsRenderer renderer;

    public static void main(String args[]) throws MalformedURLException {
        staticMap = false;
        launch();
    }

    //Gets the Interactive or Static Google Map, applies all of the necessary elements (Markers, waypoints, routes etc.), possibly from external sources (internet)
    public static void map() {
        //default
        BufferedImage bufferedImage = null;
        ipLocation = geoIPLookUp();
        try {
            bufferedImage = ImageIO.read(new File("maps-icon.png")); //default fallback
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Parser for the JSON from a URL
        if(ipLocation != null) { origin = ipLocation.latitude + "," + ipLocation.longitude; }
        else { origin = "Rotterdam"; }
        destination = "Middelharnis";
        String mapUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&key=AIzaSyCPwbsx2oWVLVbZJnlbEgEFtj0mQy4ES1c";
        String garageUrl = "http://opendata.technolution.nl/opendata/parkingdata/v1";

        //Process for generating the ImageMapView
        if (zoom == 0) {
            zoom = 11;
        }
        if (scene != null) {
            width = (int) scene.getWidth();
            height = (int) scene.getHeight();
        } else {
            width = 1000;
            height = 800;
        }
        scale = 2;
        String maptype = "roadmap";

        try {
            //Gets all of the garages and sets them as markers
            String markers = "";
            String parsedGarage = IOUtils.toString((new URL(garageUrl)));
            JSONObject parsedGarageObject = (JSONObject) JSONValue.parseWithException(parsedGarage);
            garageArray = (JSONArray) parsedGarageObject.get("parkingFacilities");
            for(int i = 0; i < garageArray.size(); i++){
                JSONObject garage = (JSONObject) garageArray.get(i);
                String name = (String) garage.get("name");
                JSONObject displayLocation = (JSONObject) garage.get("locationForDisplay");
                Double latitude = (Double) displayLocation.get("latitude");
                Double longitude = (Double) displayLocation.get("longitude");
                if(latitude > 51.8 && latitude < 52 && longitude > 4 && longitude < 4.75) {
                    String location = latitude + "," + longitude + "|";
                    markers += location;
                }
            }

            //Gets all of the waypoints in the route to the destination
            String route = "|" + ipLocation.latitude + "," + ipLocation.longitude + "|";
            String parsedMap = IOUtils.toString(new URL(mapUrl));
            JSONObject parsedMapObject = (JSONObject) JSONValue.parseWithException(parsedMap);
            JSONArray routesArray = (JSONArray) parsedMapObject.get("routes");
            JSONObject legsArray = (JSONObject) routesArray.get(0);
            JSONArray legs = (JSONArray) legsArray.get("legs");
            JSONObject stepsArray = (JSONObject) legs.get(0);
            JSONArray steps = (JSONArray) stepsArray.get("steps");

            for (int j = 0; j < steps.size(); j++) {
                JSONObject startArray = (JSONObject) steps.get(j);
                JSONObject start = (JSONObject) startArray.get("start_location");
                Double startLat = (Double) start.get("lat");
                Double startLng = (Double) start.get("lng");
                String startLoc = startLat + "," + startLng;
                route += startLoc + "|";
            }

            JSONObject endArray = (JSONObject) steps.get(steps.size() -1);
            JSONObject end = (JSONObject) endArray.get("end_location");
            Double endLat = (Double) end.get("lat");
            Double endLng = (Double) end.get("lng");
            String endLoc = endLat + "," + endLng;
            route += endLoc;

            //Retrieves the correct image                                    //center=" + ipLocation.latitude + "," + ipLocation.longitude + "&zoom=" + zoom + "&
            imageUrl = new URL("https://maps.googleapis.com/maps/api/staticmap?zoom=" + zoom + "&size=" + width + "x" + height + "&scale=" + scale + "&maptype=" + maptype + "&markers=" + ipLocation.latitude + "," + ipLocation.longitude + "|" + markers + "&markers=color:blue%7Clabel:Destination%7C" + endLoc + "&path=color:0x6464FF|weight:5" + route);
            bufferedImage = ImageIO.read(imageUrl);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        mapImage = SwingFXUtils.toFXImage(bufferedImage, null);
    }

    //Converts the coordinates into an address
    public String coordinatesToAddress(Float lat, Float lng) throws IOException, org.json.simple.parser.ParseException {

        URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String formattedAddress = "";

        try {
            InputStream in = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String result, line = reader.readLine();
            result = line;
            while ((line = reader.readLine()) != null) { result += line; }

            JSONParser parser = new JSONParser();
            JSONObject rsp = (JSONObject) parser.parse(result);

            if (rsp.containsKey("results")) {
                JSONArray matches = (JSONArray) rsp.get("results");
                JSONObject data = (JSONObject) matches.get(0);
                formattedAddress = (String) data.get("formatted_address");
            }
            return formattedAddress;
        }
        finally {
            urlConnection.disconnect();
            return formattedAddress;
        }
    }

    //Adds Markers to the map
    @Override
    public void mapInitialized() {
        MapOptions mapOptions = new MapOptions();
        mapOptions.center(new LatLong(ipLocation.latitude, ipLocation.longitude))
            .mapType(MapTypeIdEnum.ROADMAP)
            .overviewMapControl(true)
            .panControl(false)
            .rotateControl(false)
            .scaleControl(true)
            .streetViewControl(false)
            .zoomControl(true)
            .zoom(12);
        map = mapView.createMap(mapOptions);
        Marker userLocation = new Marker(new MarkerOptions().position(new LatLong(ipLocation.latitude, ipLocation.longitude)).title("User").visible(true));
        map.addMarker(userLocation);
        LatLong latLongOrigin = new LatLong(ipLocation.latitude, ipLocation.longitude);

        directions = mapView.getDirec();
        DirectionsService ds = new DirectionsService();
        renderer = new DirectionsRenderer(true, map, directions);

        DirectionsWaypoint[] dw = new DirectionsWaypoint[0];
//        dw[0] = new DirectionsWaypoint("Utrecht");
//        dw[1] = new DirectionsWaypoint("Eindhoven");

        for(int i = 0; i < garageArray.size(); i++){
            JSONObject garage = (JSONObject) garageArray.get(i);
            String name = (String) garage.get("name");
            JSONObject displayLocation = (JSONObject) garage.get("locationForDisplay");
            Double latitude = (Double) displayLocation.get("latitude");
            Double longitude = (Double) displayLocation.get("longitude");
            if(latitude > 51.8 && latitude < 52 && longitude > 4 && longitude < 4.75) {
                LatLong latLongDestination = new LatLong(latitude, longitude);
                Marker marker = new Marker(new MarkerOptions().position(new LatLong(latitude, longitude)).title(name).visible(true));
                map.addMarker(marker);
                InfoWindow info = new InfoWindow(new InfoWindowOptions().content("<p>"+name+"</p"));
                info.setPosition(new LatLong(latitude, longitude));
                map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
                    info.open(map);
                    try {
                        DirectionsRequest dr = new DirectionsRequest(
                                coordinatesToAddress(ipLocation.latitude, ipLocation.longitude),
                                coordinatesToAddress(latitude.floatValue(), longitude.floatValue()),
                                TravelModes.DRIVING,
                                dw);
                        ds.getRoute(dr, this, renderer);
                    }
                    catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                });
            }
        }


    }

    //Handles received directions
    @Override
    public void directionsReceived(DirectionsResult directionsResult, DirectionStatus directionStatus) {
        if(1==1){
            DirectionsResult e = directionsResult;
            DirectionStatus f = directionStatus;
        }
    }

    //Tries to find out the user location based on IP, returns a Location object
    public static Location geoIPLookUp() {
        LookupService cl = null;
        Location location = null;
        try {
            for(int j = 0; j < 2; j++) { //Tries to use the City database first. If the IP is not found, it falls back to the Country database.
                String db = null;
                if (j == 0) { db = "GeoLiteCity.dat"; }
                if (j == 1) { db = "GeoLiteCountry.dat"; }
                cl = new LookupService(db, LookupService.GEOIP_STANDARD);
                for (int i = 0; i < 3; i++) { //Tries getting the IP locally to locate the device. If it fails, it falls back to two webservices for the external IP.
                    if (location == null) {
                        String ip = null;
                        if (i > 0) {
                            URL checkIp = null;
                            if (i == 1) {
                                checkIp = new URL("http://ifconfig.me/ip");
                            }
                            if (i == 2) {
                                checkIp = new URL("http://checkip.amazonaws.com");
                            }
                            BufferedReader in = new BufferedReader(new InputStreamReader(checkIp.openStream()));
                            ip = in.readLine();
                        }
                        if (i == 0) {
                            ip = InetAddress.getLocalHost().getHostAddress();
                        }
                        location = cl.getLocation(ip);
                        System.out.println(i);
                    }
                }
                cl.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

    //Draws the Welcome-scene
    public VBox getStartScene() {
        VBox startView = new VBox();
        startView.setPrefSize(width, height);
        startView.setAlignment(Pos.CENTER);

        Text startText = new Text();
        startText.setText("Welcome!");
        startText.setFont(Font.font("null", FontWeight.BOLD, 120));

        Text startTextSub = new Text();
        startTextSub.setText("Start using this application by selecting something from the menu in the topleft corner.");
        startTextSub.setFont(Font.font("null", FontWeight.LIGHT, 30));
        if(scene != null) { startTextSub.setWrappingWidth(startText.getWrappingWidth()); }
        else { startTextSub.setWrappingWidth(width); }
        startTextSub.setTextAlignment(TextAlignment.CENTER);

        startView.getChildren().addAll(startText, startTextSub);
        return startView;
    }

    //Draws the Map-scene
    public VBox getStat1Scene() {
        map();
        VBox mapViewVBox = new VBox(8);

        CheckBox staticMapBox = new CheckBox();
        staticMapBox.setText("Enable Static Maps");
        staticMapBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(staticMap) { staticMap=false; }
                else { staticMap = true; }
            }
        });

        if(staticMap) {
            //Sets up the buttons and labels
            mapViewVBox.setPadding(new Insets(10, 10, 10, 10));
            Button zoomInButton = new Button();
            Button zoomOutButton = new Button();
            zoomInButton.setText("+");
            zoomOutButton.setText("-");

            Label originLabel = new Label();
            Label destinationLabel = new Label();
            originLabel.setText(geoIPLookUp().city);
            destinationLabel.setText(destination);

            //Creates and instantiates the ImageView, sets the image in the view
            final ImageView image2 = new ImageView();
            image2.setPreserveRatio(true);
            image2.setFitWidth(width);
            image2.setSmooth(true);
            image2.setCache(true);
            if (scene != null) { image2.setViewport(new Rectangle2D(0, 0, scene.getWidth(), scene.getHeight())); }
            image2.setImage(mapImage);
            mapViewVBox.getChildren().addAll(staticMapBox, zoomInButton, zoomOutButton, originLabel, destinationLabel, image2);

            //Zooms the ImageMapView in
            zoomInButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    zoom++;
                    map();
                    image2.setImage(mapImage);
                }
            });

            //Zooms the ImageMapView out
            zoomOutButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    zoom--;
                    map();
                    image2.setImage(mapImage);
                }
            });
        }

        else {
            mapView = new GoogleMapView();
            mapView.addMapInializedListener(this);
            mapView.setPrefSize(width, height);
            mapViewVBox.setAlignment(Pos.CENTER);
            mapViewVBox.getChildren().addAll(staticMapBox, mapView);
        }
        //Listeners for screen resize events
        if(staticMap) {
            scene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldSceneWidth, Number newSceneWidth) {
                    if ((Double) newSceneWidth > scene.heightProperty().getValue()) {
                        image2.setFitWidth((Double) newSceneWidth);
                    }
                }
            });
            scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldSceneHeight, Number newSceneHeight) {
                    if ((Double) newSceneHeight > scene.widthProperty().getValue()) {
                        image2.setFitHeight((Double) newSceneHeight);
                    }
                }
            });
        }
        else {
            scene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldSceneWidth, Number newSceneWidth) {
                    if ((Double) newSceneWidth > scene.heightProperty().getValue()) {
                        System.out.println("Changed Width!" + oldSceneWidth + newSceneWidth);
                        mapView.setPrefSize((Double) newSceneWidth, height);
                        mapViewVBox.getChildren().addAll(staticMapBox, mapView);
                    }
                }
            });
            scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldSceneHeight, Number newSceneHeight) {
                    if ((Double) newSceneHeight > scene.widthProperty().getValue()) {
                        System.out.println("Changed Height! " + oldSceneHeight + " , " + newSceneHeight);
                        mapView.setPrefSize(width, (Double) newSceneHeight);
                        mapViewVBox.getChildren().addAll(staticMapBox, mapView);
                    }
                }
            });
        }
        return mapViewVBox;
    }

    //Draws the Pie Chart-scene
    public VBox getStat2Scene() {
        VBox sceneView = new VBox();
        sceneView.setPrefSize(width, height);

        Text sceneText = new Text();
        sceneText.setText("Behold, our magnificent pie chart!");
        sceneText.setFont(Font.font("null", FontWeight.MEDIUM, 50));

        PieChart pieChart = new PieChart();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        pieChartData.addAll(
                new PieChart.Data("Star Wars", 99.9),
                new PieChart.Data("Batman", 78.56));
        pieChart.setData(pieChartData);
        sceneView.getChildren().addAll(sceneText, pieChart);
        return sceneView;
    }

    //Creates, initiates and draws the window and all of its elements
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Statistics");
        width = 1000;
        height = 800;
        BorderPane borderPane = new BorderPane();

        //Creates HBox-menubar layout and buttons
        MenuBar menuBar = new MenuBar();
        Menu statistics = new Menu("Statistiscs");
        MenuItem start = new MenuItem("Start");
        MenuItem stat1 = new MenuItem("Map Stats");
        MenuItem stat2 = new MenuItem("Graph");
        MenuItem stat3 = new MenuItem("Statistic 3");
        MenuItem stat4 = new MenuItem("Statistic 4");
        MenuItem stat5 = new MenuItem("Statistic 5");
        statistics.getItems().addAll(start, stat1,stat2,stat3,stat4,stat5);
        menuBar.getMenus().addAll(statistics);
        borderPane.setTop(menuBar);
        borderPane.setCenter(getStartScene());

        //Sets listeners for the menubar-buttons
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(getStartScene());
            }
        });

        stat1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(getStat1Scene());
            }
        });

        stat2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(getStat2Scene());
            }
        });

        //Creates and instantiates the scene
        scene = new Scene(borderPane, width, height);
        scene.setRoot(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}