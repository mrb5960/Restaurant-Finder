import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;


//import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by mrb5960 on 3/2/2017.
 */
public class Maps {

    /*
    This methods accepts the api url as the parameter and provides a json string as output
     */
    public String APIcall(String api_url){
        try {
            URL url = new URL(api_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String str;
            StringBuffer buffer = new StringBuffer();
            while ((str = reader.readLine()) != null) {
                buffer.append(str);
                buffer.append("\n");
            }
            str = buffer.toString();
            //System.out.println(str);
            return str;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String args[]) {
        Maps instance = new Maps();
        try {
            // accepts an address as an input
            System.out.println("Enter your location: ");
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();
            System.out.println();
            //String input = "marketplace mall henrietta";
            // tokenizes the string
            String[] input_address = input.split(" ");
            String address = "";
            //System.out.println(input_address.length);

            // creates parameters for the address url where each word is seperated by a '+' sign
            for(int i = 0; i < input_address.length; i++){
                //System.out.println(i);
                address = address + input_address[i];
                if(i != input_address.length-1)
                    address = address + "+";
            }
            //System.out.println(address);

            // url for google maps geocoding api which returns the latitude and longitude of the address provided
            String maps_url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=AIzaSyBlbGTH8E-93uvky-K742eMfvWrbtse8OA";
            String maps_output = instance.APIcall(maps_url);
            //System.out.println(str);
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(maps_output);
            JSONArray msg = (JSONArray) object.get("results");
            Iterator<?> iterator = msg.iterator();
            JSONObject a;
            String lat="",lng="", source_id = "";

            // latitude and longitude are retrieved from the json string
            while(iterator.hasNext()){
                a = (JSONObject)iterator.next();
                JSONObject b = (JSONObject) a.get("geometry");
                JSONObject c = (JSONObject) b.get("location");
                source_id = a.get("place_id").toString();
                lat = c.get("lat").toString();
                lng = c.get("lng").toString();
                //System.out.println(lat + " " + lng + " " + source_id);
                break;
            }

            // url for the google maps places api which returns nearby places
            // accepts latitude and longitude as the parameters
            String places_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lng+"&radius=1000&type=restaurant&key=AIzaSyCOmZdpYVcEgNpiV-_sL6CaKPOvb82J2Y0";
            //System.out.println(places_url);
            String places_output = instance.APIcall(places_url);
            parser = new JSONParser();
            object = (JSONObject) parser.parse(places_output);
            msg = (JSONArray) object.get("results");
            iterator = msg.iterator();

            // hashmap to store places and their ids so that they can be provided as an input to the directions api
            Map<String,String> destination_ids = new HashMap();
            while(iterator.hasNext()){
                a = (JSONObject)iterator.next();
                String name =  a.get("name").toString();
                //String rating = a.get("rating").toString();
                String place_id = a.get("place_id").toString();
                destination_ids.put(name.toLowerCase(), place_id);
                System.out.println(name);
            }

            System.out.println("\nEnter one of the locations to get directions: ");
            String destination = sc.nextLine().toLowerCase();

            // get the place_id for the destination
            String destination_id = destination_ids.get(destination);

            // provides the distance to the destination and the time duration to reach the destination
            String directions_url = "https://maps.googleapis.com/maps/api/directions/json?origin=place_id:" + source_id + "&destination=place_id:" + destination_id + "&mode=walking&key=AIzaSyCvIBTSgUF-YqjiV8PpGYnTMCvN0JaSCS8";
            //System.out.println(directions_url);
            String directions_output = instance.APIcall(directions_url);

            //System.out.println(directions_output);

            parser = new JSONParser();
            object = (JSONObject) parser.parse(directions_output);

            String distance_text="", duration_text="";
            JSONArray routes = (JSONArray) object.get("routes");
            //System.out.println(routes.toString());
            Iterator<?> iterator1 = routes.iterator();
            JSONObject temp;
            while (iterator1.hasNext()){
                //System.out.println("In while");
                temp = (JSONObject) iterator1.next();
                //System.out.println(temp.toString());
                JSONArray temp1 = (JSONArray) temp.get("legs");
                //System.out.println(temp1);
                Iterator<?> iterator2 = temp1.iterator();
                JSONObject inside_temp;

                // get the duration and distance from the json string
                while(iterator2.hasNext()){
                    inside_temp = (JSONObject) iterator2.next();
                    //System.out.println(inside_temp.get("duration") + " " + inside_temp.get("duration"));
                    JSONObject duration = (JSONObject) inside_temp.get("duration");
                    duration_text = duration.get("text").toString();
                    JSONObject distance = (JSONObject) inside_temp.get("distance");
                    distance_text = distance.get("text").toString();
                }
            }

            // print the distance and the duration
            System.out.println("\nDistance: " + distance_text + " Duration: " + duration_text);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

