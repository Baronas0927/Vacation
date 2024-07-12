package org.example;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static List<Vacation> vacations = new ArrayList<>();
    public static Gson gson = new Gson();

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/createVacation", new Handler());
        server.createContext("/getVacations", new Handler());
        server.createContext("/getVacation", new Handler());
        server.createContext("/deleteVacation", new Handler());
        server.createContext("/updateVacation", new Handler());
        server.setExecutor(null);
        server.start();
        loadVacation();
//http://127.0.0.1:8000/getVacation?id=5
        //saveVacations();
    }
    public static void loadVacation() throws IOException {
        try (FileReader reader = new FileReader("Vacations.json")) {
            // Parse the JSON file
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            // Iterate through the JSON array
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                System.out.println(jsonObject.toString());
                // Extract fields from JSON object
                Long id = jsonObject.get("id").getAsLong();
                String title = jsonObject.get("title").getAsString();
                String country = jsonObject.get("country").getAsString();
                String city = jsonObject.get("city").getAsString();
                String season = jsonObject.get("season").getAsString();
                double price = jsonObject.get("price").getAsDouble();
                int[] ratings = new Gson().fromJson(jsonObject.get("rating").getAsJsonArray(), int[].class);
                String[] photos = new Gson().fromJson(jsonObject.get("photos").getAsJsonArray(), String[].class);

                Vacation vacation = new Vacation();
                vacation.setId(Math.toIntExact(id));
                vacation.setTitle(title);
                vacation.setCountry(country);
                vacation.setCity(city);
                vacation.setSeason(season);
                vacation.setPhotos(photos);
                System.out.println(vacation);
                vacations.add(vacation);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveVacations() {
        try (FileWriter writer = new FileWriter("Vacations.json")) {
            System.out.println(vacations);
            gson.toJson(vacations, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
