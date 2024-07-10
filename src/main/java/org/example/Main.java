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
import java.util.stream.Collectors;

public class Main {
    public static List<Vacation> vacations = new ArrayList<>();
    public static Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        loadVacation();
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/createVacation", new Handler());
        server.createContext("/getVacations", new Handler());
        server.createContext("/getVacation", new Handler());
        server.createContext("/deleteVacation", new Handler());
        server.setExecutor(null);
        server.start();
//http://127.0.0.1:8000/getUser?id=5
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
                // Extract fields from JSON object
                Long id = jsonObject.get("id").getAsLong();
                String title = jsonObject.get("title").getAsString();
                String country = jsonObject.get("country").getAsString();
                String city = jsonObject.get("city").getAsString();
                String season = jsonObject.get("season").getAsString();
//                String url = jsonObject.get("url").getAsString();
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
                vacations.add(vacation);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleVacation(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = queryToMap(query);

        String title = params.get("title");
        String country = params.get("country");
        String city = params.get("city");
        String season = params.get("season");
        double price = Double.parseDouble(params.get("price"));
        String description = params.get("description");
        Vacation vacation = new Vacation(title, country, city, season, new String[]{}, price, description);
        Main.vacations.add(vacation);
        saveVacations();
        String response = "Vacation has been created successfully";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8), URLDecoder.decode(entry[1], StandardCharsets.UTF_8));
            } else {
                result.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8), "");
            }
        }
        return result;
    }

    public static void saveVacations() {
        try (FileWriter writer = new FileWriter("Vacations.json")) {
            gson.toJson(vacations, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
