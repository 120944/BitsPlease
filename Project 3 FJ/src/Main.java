//GMapsFX
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.DirectionsRequest;
import com.lynden.gmapsfx.service.directions.TravelModes;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;

//JSON
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import netscape.javascript.JSObject;

//SQL
import java.sql.*;

//ImageHandling
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//GeoIP
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

/**
 * Created by floris-jan on 05-04-16.
 */

public class Main extends Application implements MapComponentInitializedListener, DirectionsServiceCallback {

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
    public static String origin;
    public static String destination;
    GoogleMapView mapView;
    GoogleMap map;
    protected DirectionsPane directions;
    DirectionsRenderer renderer;

    public int number;
    public int passed;
    public int average;
    public String averageYear;
    public BorderPane borderPane;

    public static void main(String args[]) throws MalformedURLException {
        staticMap = false;
        launch();
    }

    public static ResultSet getDBResults(String dataBaseUrl, String user, String password, String query) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(dataBaseUrl, user, password);
            Statement statement =  connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        startTextSub.setText("Start using this application by selecting something from the menu.");
        startTextSub.setFont(Font.font("null", FontWeight.LIGHT, 30));
        if(scene != null) { startTextSub.setWrappingWidth(startText.getWrappingWidth()); }
        else { startTextSub.setWrappingWidth(width); }
        startTextSub.setTextAlignment(TextAlignment.CENTER);

        startView.getChildren().addAll(startText, startTextSub);
        return startView;
    }

    //Draws the Help-scene
    public VBox getHelpScene() {
        VBox sceneView = new VBox();

        Text sceneText = new Text();
        sceneText.setText("Help");
        sceneText.setFont(Font.font("null", FontWeight.LIGHT, 40));

        Text sceneTextSub = new Text();
        sceneTextSub.setText("You can view the different graphical representations of our data using the Statistics submenu in the menubar.");
        sceneTextSub.setFont(Font.font("null", FontWeight.LIGHT, 12));
        if(scene != null) { sceneTextSub.setWrappingWidth(sceneText.getWrappingWidth()); }
        else { sceneTextSub.setWrappingWidth(width); }
        sceneTextSub.setTextAlignment(TextAlignment.CENTER);

        sceneView.getChildren().addAll(sceneText, sceneTextSub);
        return sceneView;
    }

    //Draws the Preferences-scene
    public VBox getPreferencesScene() {
        VBox sceneView = new VBox();
        sceneView.setPrefSize(width, height);
//        startView.setAlignment(Pos.CENTER);

        Text sceneText = new Text();
        sceneText.setText("Preferences");
        sceneText.setFont(Font.font("null", FontWeight.LIGHT, 40));

        CheckBox staticMapBox = new CheckBox();
        staticMapBox.setText("Enable Static Maps");
        if(staticMap) { staticMapBox.setSelected(true); }
        staticMapBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(staticMap) { staticMap=false; }
                else { staticMap = true; }
            }
        });

        sceneView.getChildren().addAll(sceneText, staticMapBox);
        return sceneView;
    }

    //Draws the About-scene
    public VBox getAboutScene() {
        VBox sceneView = new VBox();
        sceneView.setAlignment(Pos.CENTER);

        Text sceneText = new Text();
        sceneText.setText("Copyright© BitsPlease 2016");
        sceneText.setFont(Font.font("null", FontWeight.LIGHT, 50));

        Text sceneTextSub = new Text();
        sceneTextSub.setText("J.W. Taylor-Parkins, M. de Klein, L. Pekelharing, F.J. Willemsen");
        sceneTextSub.setFont(Font.font("null", FontWeight.LIGHT, 30));
        if(scene != null) { sceneTextSub.setWrappingWidth(sceneText.getWrappingWidth()); }
        else { sceneTextSub.setWrappingWidth(width); }
        sceneTextSub.setTextAlignment(TextAlignment.CENTER);

        sceneView.getChildren().addAll(sceneText, sceneTextSub);
        return sceneView;
    }

    //Draws the Map-scene
    public VBox getStat1Scene() {
        map();
        VBox mapViewVBox = new VBox(8);

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
            mapViewVBox.getChildren().addAll(zoomInButton, zoomOutButton, originLabel, destinationLabel, image2);

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
            mapViewVBox.getChildren().addAll(mapView);
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
                        mapView.setPrefSize((Double) newSceneWidth, height);
                    }
                }
            });
            scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldSceneHeight, Number newSceneHeight) {
                    if ((Double) newSceneHeight > scene.widthProperty().getValue()) {
                        mapView.setPrefSize(width, (Double) newSceneHeight);
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
        pieChart.setTitle("Awesome movies");
        sceneView.getChildren().addAll(sceneText, pieChart);
        return sceneView;
    }

    //Draws the Area Chart-scene
    public VBox getStat3Scene(int minASCII, int maxASCII) {
        VBox sceneView = new VBox();
        sceneView.setPrefSize(width, height);
        ResultSet results = getDBResults("jdbc:mysql://127.0.0.1:3306/indice", "root", "root", "select * from diefstal_uit_auto");

        Text sceneText = new Text();
        sceneText.setText("Autodiefstal over verschillende jaren in Rotterdam");
        sceneText.setFont(Font.font("null", FontWeight.MEDIUM, 40));
        sceneText.setWrappingWidth(scene.getWidth());

        CategoryAxis XAxis = new CategoryAxis();
        NumberAxis YAxis = new NumberAxis();
        XAxis.setLabel("Jaar");
        YAxis.setLabel("Cijfer");

        final AreaChart<String, Number> areaChart = new AreaChart(XAxis, YAxis);
        areaChart.setPrefHeight(900);

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
                    System.out.println("Changed" + (int) newValue.toLowerCase().charAt(0) + (int) newValue.toLowerCase().charAt(2));
                    borderPane.setCenter(getStat3Scene((int) newValue.toLowerCase().charAt(0), (int) newValue.toLowerCase().charAt(2)));
                    pickRangeList.getSelectionModel().select(newValue);
                });

        number = 0;
        passed = 0;
        try {
            while (results.next()) {
                String name = results.getString("Gebied");
                char firstLetter = name.toLowerCase().charAt(0);
                int firstASCIILetter = (int) firstLetter;
                number++;
                for(int i = 2; i < 7; i++) {
                    average += results.getInt(i);
                    averageYear = "" + (i + 2004);
                }
                if(firstASCIILetter >= minASCII && firstASCIILetter <= maxASCII) {
                    passed++;
                    XYChart.Series series = new XYChart.Series();
                    for (int i = 2; i < 7; i++) {
                        String year = "" + (i + 2004);
                        try {
                            series.getData().add(new XYChart.Data(year, results.getInt(i)));
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
        sceneView.getChildren().addAll(sceneText, sceneSubText, pickRangeList, areaChart);
        return sceneView;
    }

    //Draws the Area Chart-scene
    public VBox getStat4Scene(int minASCII, int maxASCII) {
        VBox sceneView = new VBox();
        sceneView.setPrefSize(width, height);
        ResultSet results = getDBResults("jdbc:mysql://127.0.0.1:3306/indice", "root", "root", "select * from beschadiging_aan_auto");

        Text sceneText = new Text();
        sceneText.setText("Autobeschadiging over verschillende jaren in Rotterdam");
        sceneText.setFont(Font.font("null", FontWeight.MEDIUM, 40));
        sceneText.setWrappingWidth(scene.getWidth());

        CategoryAxis XAxis = new CategoryAxis();
        NumberAxis YAxis = new NumberAxis();
        XAxis.setLabel("Jaar");
        YAxis.setLabel("Cijfer");

        final AreaChart<String, Number> areaChart = new AreaChart(XAxis, YAxis);
        areaChart.setPrefHeight(900);

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
                    System.out.println("Changed" + (int) newValue.toLowerCase().charAt(0) + (int) newValue.toLowerCase().charAt(2));
                    borderPane.setCenter(getStat4Scene((int) newValue.toLowerCase().charAt(0), (int) newValue.toLowerCase().charAt(2)));
                    pickRangeList.getSelectionModel().select(newValue);
                });

        number = 0;
        passed = 0;
        try {
            while (results.next()) {
                String name = results.getString("Gebied");
                char firstLetter = name.toLowerCase().charAt(0);
                int firstASCIILetter = (int) firstLetter;
                number++;
                for(int i = 2; i < 7; i++) {
                    average += results.getInt(i);
                    averageYear = "" + (i + 2004);
                }
                if(firstASCIILetter >= minASCII && firstASCIILetter <= maxASCII) {
                    passed++;
                    XYChart.Series series = new XYChart.Series();
                    for (int i = 2; i < 7; i++) {
                        String year = "" + (i + 2004);
                        try {
                            series.getData().add(new XYChart.Data(year, results.getInt(i)));
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
        sceneView.getChildren().addAll(sceneText, sceneSubText, pickRangeList, areaChart);
        return sceneView;
    }

    //Draws the Barchart-scene
    public VBox getStat5Scene() {
        VBox sceneView = new VBox();

        Text sceneText = new Text();
        sceneText.setText("It's the Empire's bar chart. Foo Bar.");
        sceneText.setFont(Font.font("null", FontWeight.MEDIUM, 40));
        sceneText.setWrappingWidth(scene.getWidth());

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

    //Draws the Stacked Barchart-scene
    public VBox getStat6Scene(int minASCII, int maxASCII) {
        VBox sceneView = new VBox();
        sceneView.setPrefSize(1440, 900);
        ResultSet results = getDBResults("jdbc:mysql://127.0.0.1:3306/Crime_per_area", "root", "root", "select * from all_crimes");

        Text sceneText = new Text();
        sceneText.setText("Aandelen in buurtproblemen per buurt");
        sceneText.setFont(Font.font("null", FontWeight.MEDIUM, 40));
        sceneText.setWrappingWidth(scene.getWidth());

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
                    System.out.println("Changed" + (int) newValue.toLowerCase().charAt(0) + (int) newValue.toLowerCase().charAt(2));
                    borderPane.setCenter(getStat6Scene((int) newValue.toLowerCase().charAt(0), (int) newValue.toLowerCase().charAt(2)));
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

    //Creates, initiates and draws the window and all of its elements
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Statistics");
        width = 1000;
        height = 800;
        borderPane = new BorderPane();

        //Creates HBox-menubar layout and buttons
        MenuBar menuBar = new MenuBar();
        Menu general = new Menu("General");
        Menu statistics = new Menu("Statistics");

        MenuItem help = new MenuItem("Help");
        MenuItem preferences = new MenuItem("Preferences");
        MenuItem about = new MenuItem("About");
        MenuItem quit = new MenuItem("Quit");

        MenuItem start = new MenuItem("Start");
        MenuItem stat1 = new MenuItem("Map Stats");
        MenuItem stat2 = new MenuItem("Pie Chart");
        MenuItem stat3 = new MenuItem("Autodiefstal");
        MenuItem stat4 = new MenuItem("Autobeschadiging");
        MenuItem stat5 = new MenuItem("Barchart");
        MenuItem stat6 = new MenuItem("Stacked Barchart");

        general.getItems().addAll(help, preferences, about, quit);
        statistics.getItems().addAll(start, stat1,stat2,stat3,stat4,stat5,stat6);
        menuBar.getMenus().addAll(general, statistics);
        borderPane.setTop(menuBar);
        borderPane.setCenter(getStartScene());

        //Sets listeners for the General menubar-buttons
        help.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(getHelpScene());
            }
        });

        preferences.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(getPreferencesScene());
            }
        });

        about.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(getAboutScene());
            }
        });

        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        //Sets listeners for the Statistics menubar-buttons
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

        stat3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(getStat3Scene(0, 127));
            }
        });

        stat4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(getStat4Scene(0, 127));
            }
        });

        stat5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borderPane.setCenter(getStat5Scene());
            }
        });

        stat6.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) { borderPane.setCenter(getStat6Scene(0, 127)); }
        });

        //Creates and instantiates the scene
        scene = new Scene(borderPane, width, height);
        scene.setRoot(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}